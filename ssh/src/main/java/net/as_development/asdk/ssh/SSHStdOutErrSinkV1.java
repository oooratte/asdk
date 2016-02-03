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
