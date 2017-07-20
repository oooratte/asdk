package net.as_development.camel.core;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.jms.pool.PooledConnectionFactory;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.component.jms.JmsConfiguration;

import net.as_development.tools.configuration.ConfigurationFactory;
import net.as_development.tools.configuration.IInlineConfiguration;

//=============================================================================
public class CamelJmsComponent
{
	//-------------------------------------------------------------------------
	public static final String CFG_SERVER_HOST       = "server.host"       ;
	public static final String CFG_SERVER_PORT       = "server.port"       ;
	public static final String CFG_ENABLE_FAILOVER   = "enable.failover"   ;
	public static final String CFG_TIME_4_SEND_IN_MS = "time.4.send.in-ms" ;
	
	//-------------------------------------------------------------------------
	public CamelJmsComponent ()
	    throws Exception
	{}

	//-------------------------------------------------------------------------
	public static /* no synchronized */ CamelJmsComponent create (final String sId)
		throws Exception
	{
		final CamelJmsComponent aComponent = new CamelJmsComponent ();
		aComponent.setId(sId);
		return aComponent;
	}
	
	//-------------------------------------------------------------------------
	public synchronized void setId (final String sId)
		throws Exception
	{
		m_sId = sId;
	}

	//-------------------------------------------------------------------------
	public synchronized String getId ()
		throws Exception
	{
		return m_sId;
	}

	//-------------------------------------------------------------------------
	public /* no synchronized */ < T > CamelJmsComponent config (final String sKey  ,
															    final T       aValue)
	    throws Exception
	{
		mem_Config ().set(sKey, aValue);
		return this;
	}
	
	//-------------------------------------------------------------------------
	public synchronized JmsComponent getJmsComponent ()
	    throws Exception
	{
		if (m_aJmsComponent != null)
			return m_aJmsComponent;
		
		final JmsConfiguration aJmsConfig    = mem_JmsConfig    ();
		final JmsComponent     aJmsComponent = new JmsComponent ();

		aJmsComponent.setConfiguration(aJmsConfig);
		
		m_aJmsComponent = aJmsComponent;
		return m_aJmsComponent;
	}

	//-------------------------------------------------------------------------
	private synchronized JmsConfiguration mem_JmsConfig ()
	    throws Exception
	{
		if (m_aJmsConfig != null)
			return m_aJmsConfig;

		final PooledConnectionFactory aJmsConnectionFactoryPool = mem_JmsConnectionFactoryPool ();
		final JmsConfiguration        aJmsConfig                = new JmsConfiguration         ();

		aJmsConfig.setConnectionFactory      (aJmsConnectionFactoryPool);
		aJmsConfig.setAcknowledgementModeName("AUTO_ACKNOWLEDGE"       );
		aJmsConfig.setConcurrentConsumers    (10                       );
		aJmsConfig.setDeliveryPersistent     (true                     );
		aJmsConfig.setRequestTimeout         (10000                    );
		aJmsConfig.setCacheLevelName         ("CACHE_AUTO"             );
		
		m_aJmsConfig = aJmsConfig;
		return aJmsConfig;
	}

	//-------------------------------------------------------------------------
	private synchronized PooledConnectionFactory mem_JmsConnectionFactoryPool ()
	    throws Exception
	{
		if (m_aJmsConnectionFactoryPool != null)
			return m_aJmsConnectionFactoryPool;

		final ActiveMQConnectionFactory aJmsConnectionFactory     = mem_JmsConnectionFactory    ();
		final PooledConnectionFactory   aJmsConnectionFactoryPool = new PooledConnectionFactory ();
		
		aJmsConnectionFactoryPool.setConnectionFactory                (aJmsConnectionFactory);
		aJmsConnectionFactoryPool.setMaxConnections                   (100);
		aJmsConnectionFactoryPool.setMaximumActiveSessionPerConnection(200);
		
		m_aJmsConnectionFactoryPool = aJmsConnectionFactoryPool;
		return m_aJmsConnectionFactoryPool;
	}

	//-------------------------------------------------------------------------
	private synchronized ActiveMQConnectionFactory mem_JmsConnectionFactory ()
	    throws Exception
	{
		if (m_aJmsConnectionFactory != null)
			return m_aJmsConnectionFactory;
		
		final IInlineConfiguration      iConfig               = mem_Config ();
		final ActiveMQConnectionFactory aJmsConnectionFactory = new ActiveMQConnectionFactory();
		final String                    sServerHost           = iConfig.get(CFG_SERVER_HOST      , String .class, "localhost");
		final int                       nServerPort           = iConfig.get(CFG_SERVER_PORT      , int    .class, 61616      );
		final int                       nTimeout4SendInMS     = iConfig.get(CFG_TIME_4_SEND_IN_MS, int    .class, 60000      );
		final boolean                   bEnableFailOver       = iConfig.get(CFG_ENABLE_FAILOVER  , boolean.class, true       );
		final StringBuffer              sBrokerURL            = new StringBuffer (256);

		if (bEnableFailOver)
			sBrokerURL.append("failover:(");

		sBrokerURL.append("tcp://"   );
		sBrokerURL.append(sServerHost);
		sBrokerURL.append(":"        );
		sBrokerURL.append(nServerPort);
		
		if (bEnableFailOver)
			sBrokerURL.append(")");
		
		aJmsConnectionFactory.setBrokerURL                          (sBrokerURL.toString());
		aJmsConnectionFactory.setUseAsyncSend                       (true                 );
		aJmsConnectionFactory.setWarnAboutUnstartedConnectionTimeout(-1                   ); // -1 disable that feature ... we rely on our timeout for sending messages always
		aJmsConnectionFactory.setSendTimeout                        (nTimeout4SendInMS    );

		m_aJmsConnectionFactory = aJmsConnectionFactory;
		return m_aJmsConnectionFactory;
	}

	//-------------------------------------------------------------------------
	private synchronized IInlineConfiguration mem_Config ()
	    throws Exception
	{
		if (m_iConfig == null)
			m_iConfig = ConfigurationFactory.createInlineConfiguration();
		return m_iConfig;
	}

	//-------------------------------------------------------------------------
	private String m_sId = null;

	//-------------------------------------------------------------------------
	private IInlineConfiguration m_iConfig = null;
	
	//-------------------------------------------------------------------------
	private ActiveMQConnectionFactory m_aJmsConnectionFactory = null;
	
	//-------------------------------------------------------------------------
	private PooledConnectionFactory m_aJmsConnectionFactoryPool = null;

	//-------------------------------------------------------------------------
	private JmsConfiguration m_aJmsConfig = null;

	//-------------------------------------------------------------------------
	private JmsComponent m_aJmsComponent = null;
}
