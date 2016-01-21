/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
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
