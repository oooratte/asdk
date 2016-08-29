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

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.advisory.DestinationSource;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.jms.pool.PooledConnectionFactory;

import net.as_development.asdk.jms.core.JMSEnv;

//=============================================================================
public class AMQContext
{
	//-------------------------------------------------------------------------
	public static final String JNDI_CONTEXT_FACTORY = "org.apache.activemq.jndi.ActiveMQInitialContextFactory";

	//-------------------------------------------------------------------------
	public static final String JNDI_CONNECTION_FACTORY = "ConnectionFactory";

	//-------------------------------------------------------------------------
	public AMQContext ()
	{}
	
	//-------------------------------------------------------------------------
	public synchronized void setEnv (final JMSEnv aEnv)
		throws Exception
	{
		m_aEnv = aEnv;
	}

	//-------------------------------------------------------------------------
	public synchronized Session newSession ()
		throws Exception
	{
		final Connection aConnection = impl_openConnection ();
    	final Session    aSession    = aConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    	return aSession;
	}

	//-------------------------------------------------------------------------
	public synchronized List< String > listQueues ()
		throws Exception
	{
		final List< String > lQueues     = new ArrayList< String > ();
		final Connection     aConnection = impl_openConnection ();
		
		if ( ! (aConnection instanceof ActiveMQConnection))
			return lQueues;
		
		final ActiveMQConnection   aAMQCon    = (ActiveMQConnection) aConnection;
		final DestinationSource    aDestSrc   = aAMQCon.getDestinationSource();
		final Set< ActiveMQQueue > lAMQQueues = aDestSrc.getQueues();
		
		for (final ActiveMQQueue aAMQQueue : lAMQQueues)
		{
			final String sQueue = aAMQQueue.getQueueName();
			lQueues.add (sQueue);
		}
		
		return lQueues;
	}

	//-------------------------------------------------------------------------
	private Connection impl_openConnection ()
	    throws Exception
	{
    	final ConnectionFactory aConnectionFactory = mem_ConnectionFactory ();

    	// Obtain a JMS connection from the factory
    	final Connection aConnection = aConnectionFactory.createConnection();
    	aConnection.start();

    	return aConnection;
	}
	
	//-------------------------------------------------------------------------
	private void impl_closeConnection ()
	    throws Exception
	{
		if (m_aConnection == null)
			return;

		throw new UnsupportedOperationException ("not implemented yet.");
	}

	//-------------------------------------------------------------------------
	// Create and initialize a JNDI context
	private synchronized Context mem_Context ()
	    throws Exception
	{
		if (m_aContext != null)
			return m_aContext;

		final String     sContextFactory = m_aEnv.getContextFactoryName ();
		      String     sConnectionURL  = m_aEnv.getServerURI          ().toASCIIString();
    	final boolean    bEnableFailover = m_aEnv.isFailOver            ();
    	final Properties aConnectionCfg  = new Properties ();
    	
    	if (bEnableFailover)
    		sConnectionURL = "failover:("+sConnectionURL+")";
    	
    	aConnectionCfg.setProperty(Context.INITIAL_CONTEXT_FACTORY, sContextFactory);
    	aConnectionCfg.setProperty(Context.PROVIDER_URL           , sConnectionURL );

    	m_aContext = new InitialContext(aConnectionCfg);
		return m_aContext;
	}
	
	//-------------------------------------------------------------------------
	private synchronized ConnectionFactory mem_ConnectionFactory ()
	    throws Exception
	{
		if (m_aConnectionFactory != null)
			return m_aConnectionFactory;
		
		final Context                   aContext       = mem_Context ();
    	final ActiveMQConnectionFactory aFactory       = (ActiveMQConnectionFactory) aContext.lookup(JNDI_CONNECTION_FACTORY);
    	final PooledConnectionFactory   aPooledFactory = new PooledConnectionFactory ();
    	
    	aFactory      .setUseAsyncSend                     (true    );
    	aFactory      .setAlwaysSyncSend                   (false   );
    	aFactory      .setCopyMessageOnSend                (false   );
    	aFactory      .setDispatchAsync                    (true    );
    	aFactory      .setUseCompression                   (true    );
    	
    	aPooledFactory.setConnectionFactory                (aFactory);
    	aPooledFactory.setMaxConnections                   (100     );
    	aPooledFactory.setMaximumActiveSessionPerConnection(200     );
    	
    	m_aConnectionFactory = aPooledFactory;
		return m_aConnectionFactory;
	}
	
	//-------------------------------------------------------------------------
	private Context m_aContext = null;

	//-------------------------------------------------------------------------
	private Connection m_aConnection = null;

	//-------------------------------------------------------------------------
	private ConnectionFactory m_aConnectionFactory = null;

	//-------------------------------------------------------------------------
	private JMSEnv m_aEnv = null;
}
