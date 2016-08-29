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
package net.as_development.asdk.jms.core;

import java.util.concurrent.CountDownLatch;

import javax.jms.Message;

//=============================================================================
public class JMSRequestResponsePair extends CountDownLatch
{
	//-------------------------------------------------------------------------
	public JMSRequestResponsePair()
	{
		super(1);
	}

	//-------------------------------------------------------------------------
	public synchronized void setRequest (final Message aRequest)
		throws Exception
	{
		m_aRequest = aRequest;
	}
	
	//-------------------------------------------------------------------------
	public synchronized Message getRequest ()
		throws Exception
	{
		return m_aRequest;
	}

	//-------------------------------------------------------------------------
	public synchronized Message getResponse ()
		throws Exception
	{
		return m_aResponse;
	}

	//-------------------------------------------------------------------------
	public synchronized void notifyResponse (final Message aResponse)
	    throws Exception
	{
		m_aResponse = aResponse;
		countDown ();
		notifyAll ();
	}
	
	//-------------------------------------------------------------------------
	private Message m_aRequest = null;

	//-------------------------------------------------------------------------
	private Message m_aResponse = null;
}