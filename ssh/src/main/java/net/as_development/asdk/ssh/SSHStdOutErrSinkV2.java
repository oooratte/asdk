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
package net.as_development.asdk.ssh;

import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;

//==============================================================================
public class SSHStdOutErrSinkV2
{
    //--------------------------------------------------------------------------
    private static final int STREAM_BLOCKSIZE = 65565;

    //--------------------------------------------------------------------------
	public SSHStdOutErrSinkV2 ()
	    throws Exception
	{}

	//--------------------------------------------------------------------------
	public synchronized void bind(final Channel aChannel)
		throws Exception
	{
		m_aStdOut = aChannel.getInputStream();

		if (aChannel instanceof ChannelExec)
			m_aStdErr = ((ChannelExec) aChannel).getErrStream();
		
		start ();
	}

	//--------------------------------------------------------------------------
	public synchronized void unbind ()
		throws Exception
	{
		stop (10000);
		
		m_aStdOut = null;
		m_aStdErr = null;
		
		// do not clear string buffer !
	}
	
	//--------------------------------------------------------------------------
	public synchronized void start ()
		throws Exception
	{
		if (m_aAsync != null)
			return;

		m_sStdOut     = new StringBuffer (256);
		m_sStdErr     = new StringBuffer (256);
		
		m_aAsync      = Executors.newFixedThreadPool(2);
		
		m_aStdOutSync = impl_readStreamToBufferAsync (m_aStdOut, m_sStdOut, m_aAsync);
		m_aStdErrSync = impl_readStreamToBufferAsync (m_aStdErr, m_sStdErr, m_aAsync);
	}

	//--------------------------------------------------------------------------
	public synchronized void stop (final long nTimeoutInMS)
		throws Exception
	{
		if (m_aAsync == null)
			return;
		
		m_aAsync.shutdown();
		if (m_aStdOutSync != null)
			m_aStdOutSync.cancel(true);
		if (m_aStdErrSync != null)
			m_aStdErrSync.cancel(true);

		final boolean bOK = m_aAsync.awaitTermination(nTimeoutInMS, TimeUnit.MILLISECONDS);
		if ( ! bOK)
			throw new TimeoutException ("Termination of threads exceeded timeout of "+nTimeoutInMS+" ms.");
		
		m_aAsync      = null;
		m_aStdOutSync = null;
		m_aStdErrSync = null;
	}

	//--------------------------------------------------------------------------
	public synchronized String getStdOut ()
	    throws Exception
	{
		if (m_sStdOut != null)
			return m_sStdOut.toString ();
		else
			return "";
	}
	
	//--------------------------------------------------------------------------
	public synchronized String getStdOutAndClear ()
	    throws Exception
	{
		String sStdOut = "";
		if (m_sStdOut != null)
		{
			sStdOut = m_sStdOut.toString ();
			m_sStdOut.setLength(0);
		}
		return sStdOut;
	}

	//--------------------------------------------------------------------------
	public synchronized String getStdErr ()
	    throws Exception
	{
		if (m_sStdErr != null)
			return m_sStdErr.toString ();
		else
			return "";
	}

	//--------------------------------------------------------------------------
	public synchronized String getStdErrAndClear ()
	    throws Exception
	{
		String sStdErr = "";
		if (m_sStdErr != null)
		{
			sStdErr = m_sStdErr.toString ();
			m_sStdErr.setLength(0);
		}
		return sStdErr;
	}

	//--------------------------------------------------------------------------
	private Future< ? > impl_readStreamToBufferAsync (final InputStream     aStream,
										  		      final StringBuffer    sBuffer,
										  		      final ExecutorService aAsync )
	    throws Exception
	{
		if (aStream == null)
			return null;
		
	    final Future< ? > aSync = aAsync.submit(new Runnable ()
	    {
	    	@Override
	    	public void run ()
	    	{
	    		try
	    		{
		        	final Thread aThis   = Thread.currentThread();
	        	    final byte[] lBuffer = new byte[STREAM_BLOCKSIZE];
			        while(true)
			        {
			        	if (aThis.isInterrupted())
			        		break;

			        	final int nAvailable = aStream.available();
		        		if (nAvailable < 1)
		        			continue;
		        		
			            int nRead = aStream.read(lBuffer, 0, nAvailable);
			            if (nRead < 0)
			                break;
			
			            final String sRead = new String(lBuffer, 0, nRead);
			            sBuffer.append(sRead);
			            System.out.print(sRead);
			        }
	    		}
	    		catch (Throwable ex)
	    		{
	    			// TODO
	    		}
	    	}
	    });
	    
	    return aSync;
	}

	//--------------------------------------------------------------------------
	private InputStream m_aStdOut = null;

	//--------------------------------------------------------------------------
	private InputStream m_aStdErr = null;

	//--------------------------------------------------------------------------
	private StringBuffer m_sStdOut = null;

	//--------------------------------------------------------------------------
	private StringBuffer m_sStdErr = null;

	//--------------------------------------------------------------------------
	private ExecutorService m_aAsync = null;

	//--------------------------------------------------------------------------
	private Future< ? > m_aStdOutSync = null;

	//--------------------------------------------------------------------------
	private Future< ? > m_aStdErrSync = null;
}
