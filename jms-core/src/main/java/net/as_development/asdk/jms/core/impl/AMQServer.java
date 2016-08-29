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

import java.io.File;
import java.net.URI;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;

import net.as_development.asdk.jms.core.IJMSServer;
import net.as_development.asdk.jms.core.JMSEnv;

//=============================================================================
public class AMQServer implements IJMSServer
{
	//-------------------------------------------------------------------------
	public AMQServer ()
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
	public synchronized void start ()
	    throws Exception
	{
		if (m_aBroker != null)
			return;

		final BrokerService aBroker = new BrokerService ();
		impl_configureBroker (aBroker);
		aBroker.start();

		m_aBroker = aBroker;
	}
	
	//-------------------------------------------------------------------------
	@Override
	public synchronized void stop ()
	    throws Exception
	{
		if (m_aBroker == null)
			return;
		
		final BrokerService aBroker = m_aBroker;
		                  m_aBroker = null;

		if (aBroker != null)
			aBroker.stop();
	}

	//-------------------------------------------------------------------------
	private void impl_configureBroker (final BrokerService aBroker)
	    throws Exception
	{
		final URI                aConnectorURI = m_aEnv.getServerURI   ();
		final TransportConnector aConnector    = new TransportConnector();

		aConnector.setUri            (aConnectorURI);
		aBroker   .addConnector      (aConnector   );
		aBroker   .setUseShutdownHook(false        );
		
		if (m_aEnv.isPersistent())
		{
			final String sDataPath = m_aEnv.getLocalDataPath();
			final File   aDataPath = new File (sDataPath);
			
			aBroker.setPersistent        (true);
			aBroker.getPersistenceAdapter(    ).setDirectory(aDataPath);
		}
		else
		{
			aBroker.setPersistent        (false);
			aBroker.setPersistenceAdapter(null );
		}

		if (m_aEnv.isGCFrequency())
		{
			aBroker.setSchedulePeriodForDestinationPurge(m_aEnv.getGCFrequency());
		}

		if (m_aEnv.isJMX())
		{
			aBroker .setUseJmx           (true);
			aBroker .getManagementContext()
					.setConnectorPort    (m_aEnv.getJMXPort());
		}
		else
		{
			aBroker.setUseJmx(false);
		}
	}

	//-------------------------------------------------------------------------
	private BrokerService m_aBroker = null;

	//-------------------------------------------------------------------------
	private JMSEnv m_aEnv = null;
}
