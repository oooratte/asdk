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

import org.apache.commons.io.IOUtils;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

//=============================================================================
public class I2CUSBSerial extends USBSerial
{
	/*
	' Die häufigsten I²C-Bus-Adressen:
	' &h20 - &h2E    MAX7311 16Bit Portexpander
	' &h30 - &h3E    MAX7311 16Bit Portexpander
	' &h40 - &h4E    MAX7311 16Bit Portexpander
	' &h40 - &h4E    PCF8574 Portexpander
	' &h50 - &h5E    MAX7311 16Bit Portexpander
	' &h50 - &h5E    MAX520 4ch. 8Bit DA-Wandler
	' &h68 - &h6E    MAX4572 Analog-Schalter (write only)
	' &h70 - &h7E    PCF8574A Portexpander
	' &h90 - &h9E    PCF8591 4ch. 8Bit AD-Wandler
	' &h90 - &h9E    DS1631 12Bit Temperatursensor
	' &hA0 - &hA2    PCF8583 Uhrenbaustein
	' &hA0 - &hA6    serielle EEProms 24C128 - 24C512
	' &hA0 - &hAE    serielle EEProms 24C01 - 24C64
	' &hA0 - &hAE    MAX7311 16Bit Portexpander
	' &hB0 - &hBE    MAX7311 16Bit Portexpander
	' &hC0 - &hCE    MAX7311 16Bit Portexpander
	' &hD0 - &hDE    MAX7311 16Bit Portexpander
	*/
	
	//-------------------------------------------------------------------------
	public static final short DEVICE_MAX520 = 0x50;

	//-------------------------------------------------------------------------
	public I2CUSBSerial()
    	throws Exception
	{
		setBaudrate (USBSerial.BAUDRATE_19200);
		setDataBits (USBSerial.DATABITS_8    );
		setStopBits (USBSerial.STOPBITS_2    );
		setParity   (USBSerial.PARITY_NONE   );
    }
    
	//-------------------------------------------------------------------------
	protected boolean writeByteRequest (final int nDevice  ,
									    final int nRegister,
									    final int nByte    )
	    throws Exception
	{
		final byte[] lRaw = new byte[] {0x55, (byte)nDevice, (byte)nRegister, 0x01, (byte)nByte};
//		final byte[] lRaw = new byte[] {0x55, 0x50         , 0x01           , 0x01, 0x00       };
//    	System.out.println ("... write ("+IOUtils.toString(lRaw)+")");

    	writeRaw (lRaw);
		
		final int nResult = impl_getResult ();
		if (nResult == 1)
			return true;
		else
			return false;
	}
	
	//-------------------------------------------------------------------------
	private byte impl_getResult ()
		throws Exception
	{
		final byte[] lIn     = readRaw(1);
        final byte   nResult = lIn[0];
        return nResult;
	}
}