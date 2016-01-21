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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.jcraft.jsch.Channel;

//==============================================================================
public class SSHStdOutErrSinkV1
{
    //--------------------------------------------------------------------------
	public SSHStdOutErrSinkV1 ()
	    throws Exception
	{}

	//--------------------------------------------------------------------------
	private class LogStream extends OutputStream
	{
		public LogStream(final OutputStream aWrapStream)
		    throws Exception
		{
			m_aWrapStream = aWrapStream;
		}
		
		public OutputStream m_aWrapStream = null;

		@Override
		public void write(int b)
			throws IOException
		{
			m_aWrapStream.write(b);
			
			System.out.print(b);
		}
	}
	
	//--------------------------------------------------------------------------
	public synchronized void bind(final Channel aChannel)
		throws Exception
	{
		m_aStdOut = new ByteArrayOutputStream ();
		aChannel.setExtOutputStream(new LogStream (m_aStdOut));
	}

	//--------------------------------------------------------------------------
	public synchronized String getStdOut ()
	    throws Exception
	{
		if (m_aStdOut != null)
			return m_aStdOut.toString();
		else
			return "";
	}
	
	//--------------------------------------------------------------------------
	public synchronized String getStdOutAndClear ()
	    throws Exception
	{
		String sOut = "";
		if (m_aStdOut != null)
		{
			sOut = m_aStdOut.toString();
			m_aStdOut.reset ();
		}
		return sOut;
	}

	//--------------------------------------------------------------------------
	public synchronized String getStdErr ()
	    throws Exception
	{
		if (m_aStdErr != null)
			return m_aStdErr.toString();
		else
			return "";
	}

	//--------------------------------------------------------------------------
	public synchronized String getStdErrAndClear ()
	    throws Exception
	{
		String sErr = "";
		if (m_aStdErr != null)
		{
			sErr = m_aStdErr.toString();
			m_aStdErr.reset ();
		}
		return sErr;
	}

	//--------------------------------------------------------------------------
	private ByteArrayOutputStream m_aStdOut = null;

	//--------------------------------------------------------------------------
	private ByteArrayOutputStream m_aStdErr = null;
}
