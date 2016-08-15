/**
 * This is free and unencumbered software released into the public domain.
 *
 * Anyone is free to copy, modify, publish, use, compile, sell, or
 * distribute this software, either in source code form or as a compiled
 * binary, for any purpose, commercial or non-commercial, and by any
 * means.
 *
 * In jurisdictions that recognize copyright laws, the author or authors
 * of this software dedicate any and all copyright interest in the
 * software to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and
 * successors. We intend this dedication to be an overt act of
 * relinquishment in perpetuity of all present and future rights to this
 * software under copyright law.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 * For more information, please refer to <http://unlicense.org/>
 */
package net.as_development.asdk.tools.usbserial;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import net.as_development.asdk.tools.common.HexUtils;

//=============================================================================
public class USBSerial
{
	//-------------------------------------------------------------------------
	public static final int BAUDRATE_9600  =  9600;
	public static final int BAUDRATE_19200 = 19200;
	public static final int BAUDRATE_57600 = 57600;
	
	//-------------------------------------------------------------------------
	public static final int DATABITS_5     = SerialPort.DATABITS_5  ;
	public static final int DATABITS_6     = SerialPort.DATABITS_6  ;
	public static final int DATABITS_7     = SerialPort.DATABITS_7  ;
	public static final int DATABITS_8     = SerialPort.DATABITS_8  ;
	
	//-------------------------------------------------------------------------
	public static final int PARITY_NONE    = SerialPort.PARITY_NONE ;
	public static final int PARITY_ODD     = SerialPort.PARITY_ODD  ;
	public static final int PARITY_EVEN    = SerialPort.PARITY_EVEN ;
	public static final int PARITY_MARK    = SerialPort.PARITY_MARK ;
	public static final int PARITY_SPACE   = SerialPort.PARITY_SPACE;
	
	//-------------------------------------------------------------------------
	public static final int STOPBITS_1     = SerialPort.STOPBITS_1  ;
	public static final int STOPBITS_2     = SerialPort.STOPBITS_2  ;
	public static final int STOPBITS_1_5   = SerialPort.STOPBITS_1_5;

	//-------------------------------------------------------------------------
	public USBSerial()
    {}
    
	//-------------------------------------------------------------------------
	public void setPort (final String sPortName)
		throws Exception
	{
		m_sPortName = sPortName;
	}

	//-------------------------------------------------------------------------
	public void setBaudrate (final int nBaudrate)
	    throws Exception
	{
		m_nBaudrate = nBaudrate;
	}
	
	//-------------------------------------------------------------------------
	public void setDataBits (final int nBits)
	    throws Exception
	{
		m_nDataBits = nBits;
	}

	//-------------------------------------------------------------------------
	public void setStopBits (final int nBits)
	    throws Exception
	{
		m_nStopBits = nBits;
	}

	//-------------------------------------------------------------------------
	public void setParity (final int nParity)
	    throws Exception
	{
		m_nParity = nParity;
	}

	//-------------------------------------------------------------------------
    public void connect ()
    	throws Exception
    {
    	mem_Port  ();
    	onConnect ();
    }

	//-------------------------------------------------------------------------
    public void disconnect ()
    	throws Exception
    {
    	onDisconnect ();
    }

	//-------------------------------------------------------------------------
	public void writeRaw (final String sRaw)
	    throws Exception
	{
		Validate.isTrue( ! StringUtils.isEmpty (sRaw), "Invalid argument 'raw'. Has not to be null or empty.");
		final byte[] lRaw = HexUtils.convertStringToByteArray(sRaw);
		writeRaw (lRaw);
	}

	//-------------------------------------------------------------------------
	public void writeRaw (final byte[] lRaw)
	    throws Exception
	{
		Validate.isTrue(lRaw        != null, "Invalid argument 'raw'. Has not to be null." );
		Validate.isTrue(lRaw.length  > 0   , "Invalid argument 'raw'. Has not to be empty.");

		final OutputStream aOut = mem_Out ();
		aOut.write (lRaw);
		aOut.flush ();
	}
	
	//-------------------------------------------------------------------------
	public byte[] readRaw (final int nBytes)
		throws Exception
	{
		Validate.isTrue(nBytes > 0, "Invalid argument 'bytes'. Needs to be > 0.");

		final byte[]      lRaw = new byte[nBytes];
		final InputStream aIn  = mem_In ();
		
		aIn.read(lRaw, 0, nBytes);
		
		return lRaw;
	}
	
    //-------------------------------------------------------------------------
	public void addListener ()
		throws Exception
	{
		final SerialPort aPort = mem_Port ();
		aPort.addEventListener(new SerialPortEventListener ()
		{
			@Override
			public void serialEvent(final SerialPortEvent aEvent)
			{
				final StringBuffer sEvent = new StringBuffer (256);
				sEvent.append("--------------------------------\n");
				sEvent.append("type : "+aEvent.getEventType()+"\n");
				sEvent.append("src  : "+aEvent.getSource   ()+"\n");
				sEvent.append("old  : "+aEvent.getOldValue ()+"\n");
				sEvent.append("new  : "+aEvent.getNewValue ()+"\n");
				System.out.println(sEvent.toString());
			}
		});
	}
	
    //-------------------------------------------------------------------------
    public void setDataSink (final IUSBSerialDataSink iSink)
        throws Exception
    {
    	if (
    		(iSink       == null) &&
    		(m_aInReader == null)
    	   )
    		return;

    	if (
    		(iSink       != null) &&
    		(m_aInReader != null)
    	   )
    	{
    		throw new Error ("Data sink already set and running.");
    	}

    	if (
    		(iSink       == null) &&
    		(m_aInReader != null)
    	   )
    	{
    		m_aInReader.cancel(true);
    		m_aInReader = null;
    	}
    	
    	final InputStream     aIn     = mem_In ();
    	final ExecutorService aExec   = Executors.newSingleThreadExecutor();
    	final Future< ? >     aHandle = aExec.submit(new Runnable ()
    	{
    		@Override
    		public void run ()
    		{
				try
				{
		    	    int nPackLen = iSink.getPackageLength();
		    	    if (nPackLen < 1)
		    	    	nPackLen = 1;

	    			while (true)
	    			{
	    				final int nAvailable = aIn.available();
	    				if (nAvailable < nPackLen)
	    				{
	    					synchronized (this)
	    					{
	    						wait (25);
	    					}
	    					continue;
	    				}
	
	    				final byte[] lBuffer = new byte[nAvailable];
					    final int    nRead   = aIn.read  (lBuffer, 0, nAvailable);
					    final byte[] lData   = ArrayUtils.subarray(lBuffer, 0, nRead);

					    iSink.notifyData(lData);
					}
				}
				catch (Throwable ex)
				{
					ex.printStackTrace();
				}
    		}
    	});
    	m_aInReader = aHandle;
    }

	//-------------------------------------------------------------------------
    protected void onConnect ()
        throws Exception
    {}
    
	//-------------------------------------------------------------------------
    protected void onDisconnect ()
        throws Exception
    {}

    //-------------------------------------------------------------------------
	public static void listCOMs ()
		throws Exception
	{
		System.out.println("look for COM ports ...");
		final Enumeration< CommPortIdentifier > lPorts = CommPortIdentifier.getPortIdentifiers();
        while (lPorts.hasMoreElements()) 
        {
            final CommPortIdentifier aPort = lPorts.nextElement();
            System.out.println(aPort.getName()  +  " - " +  impl_getPortTypeName(aPort.getPortType()) );
        }		
		System.out.println("OK");
	}
    
	//-------------------------------------------------------------------------
	private static String impl_getPortTypeName (final int nPortType)
    {
        switch (nPortType)
        {
            case CommPortIdentifier.PORT_I2C:
                return "I2C";
            case CommPortIdentifier.PORT_PARALLEL:
                return "Parallel";
            case CommPortIdentifier.PORT_RAW:
                return "Raw";
            case CommPortIdentifier.PORT_RS485:
                return "RS485";
            case CommPortIdentifier.PORT_SERIAL:
                return "Serial";
            default:
                return "unknown type";
        }
    }
    
	//-------------------------------------------------------------------------
    private SerialPort mem_Port ()
    	throws Exception
    {
    	if (m_aPort != null)
    		return m_aPort;

        final CommPortIdentifier aPortIdent = CommPortIdentifier.getPortIdentifier(m_sPortName);
        final CommPort           aComPort   = aPortIdent.open(USBSerial.class.getName(),2000);
        final SerialPort         aPort      = (SerialPort) aComPort;

        aPort.setSerialPortParams(m_nBaudrate, m_nDataBits, m_nStopBits, m_nParity);

        m_aPort = aPort;
        return m_aPort;
    }

    //-------------------------------------------------------------------------
    private OutputStream mem_Out ()
        throws Exception
    {
    	if (m_aOut != null)
    		return m_aOut;
    	
    	final SerialPort   aPort = mem_Port ();
    	final OutputStream aOut  = aPort.getOutputStream();

    	m_aOut = aOut;
    	return m_aOut;
    }

    //-------------------------------------------------------------------------
    private InputStream mem_In ()
        throws Exception
    {
    	if (m_aIn != null)
    		return m_aIn;
    	
    	final SerialPort  aPort = mem_Port ();
    	final InputStream aIn   = aPort.getInputStream();

    	m_aIn = aIn;
    	return m_aIn;
    }

    //-------------------------------------------------------------------------
    private String m_sPortName = null;

    //-------------------------------------------------------------------------
    private int m_nBaudrate = BAUDRATE_9600;

    //-------------------------------------------------------------------------
    private int m_nDataBits = DATABITS_8;

    //-------------------------------------------------------------------------
    private int m_nStopBits = STOPBITS_1;

    //-------------------------------------------------------------------------
    private int m_nParity = PARITY_NONE;

    //-------------------------------------------------------------------------
    private SerialPort m_aPort = null;

    //-------------------------------------------------------------------------
    private OutputStream m_aOut = null;

    //-------------------------------------------------------------------------
    private InputStream m_aIn = null;

    //-------------------------------------------------------------------------
    private Future< ? > m_aInReader = null;
}