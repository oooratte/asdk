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
package net.as_development.asdk.jms.core.impl;

import java.util.List;

import javax.jms.Session;

import net.as_development.asdk.jms.core.IJMSClient;
import net.as_development.asdk.jms.core.JMSEnv;

public class AMQClient implements IJMSClient
{
	//-------------------------------------------------------------------------
	public AMQClient ()
	{}

	//-------------------------------------------------------------------------
	@Override
	public synchronized void setEnv (final JMSEnv aEnv)
	    throws Exception
	{
		m_aEnv = aEnv;
	}

	//-------------------------------------------------------------------------
	@Override
	public synchronized Session newSession ()
	    throws Exception
	{
		final Session aSession = mem_Context ().newSession();
		return aSession;
	}

	//-------------------------------------------------------------------------
	@Override
	public synchronized List< String > listQueues ()
	    throws Exception
	{
		return mem_Context ().listQueues();
	}

	//-------------------------------------------------------------------------
	private synchronized AMQContext mem_Context ()
	    throws Exception
	{
		if (m_aContext == null)
		{
			final AMQContext aContext = new AMQContext ();
			aContext.setEnv (m_aEnv);
			m_aContext = aContext;
		}
		return m_aContext;
	}

	//-------------------------------------------------------------------------
	private AMQContext m_aContext = null;

	//-------------------------------------------------------------------------
	private JMSEnv m_aEnv = null;
}
