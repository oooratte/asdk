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
package net.as_development.asdk.persistence.impl;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import com.hazelcast.config.AwsConfig;
import com.hazelcast.config.Config;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.config.InterfacesConfig;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MapStoreConfig;
import com.hazelcast.config.MapStoreConfig.InitialLoadMode;
import com.hazelcast.config.MulticastConfig;
import com.hazelcast.config.NearCacheConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.config.TcpIpConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.MapLoader;
import com.hazelcast.core.MapStoreFactory;
import com.hazelcast.instance.GroupProperties;

public class HZServer
{
	//-------------------------------------------------------------------------
	public HZServer ()
	    throws Exception
	{}

	//-------------------------------------------------------------------------
	public synchronized void setInterface (final String sInterface)
	    throws Exception
	{
		m_sInterface = sInterface;
	}

	//-------------------------------------------------------------------------
	public synchronized void setPort (final int nPort)
	    throws Exception
	{
		m_nPort = nPort;
	}
	
	//-------------------------------------------------------------------------
	public synchronized void setId (final String sId)
	    throws Exception
	{
		m_sId = sId;
	}

	//-------------------------------------------------------------------------
	public synchronized void setPassword (final String sPassword)
	    throws Exception
	{
		m_sPassword = sPassword;
	}

	//-------------------------------------------------------------------------
	public synchronized void enablePersistence (final String sDir)
		throws Exception
	{
		m_sPersistenceDir = sDir;
	}
	
	//-------------------------------------------------------------------------
	public synchronized void setCleanOnShutdown (final boolean bState)
	    throws Exception
	{
		m_bCleanOnShutdown = bState;
	}

	//-------------------------------------------------------------------------
	/** for testing purposes only !
	 *  Be careful by using that method in production code ,-)
	 */
	public synchronized void cleanPersistenceLayer ()
	    throws Exception
	{
		impl_cleanPersistenceLayer ();
	}
	
	//-------------------------------------------------------------------------
	public void start ()
		throws Exception
	{
		synchronized(this)
		{
			if (m_aRunSync != null)
				return;
			
			m_aRunSync = new CountDownLatch (1);
		}
		
		mem_Core ();
	}

	//-------------------------------------------------------------------------
	public void stop ()
		throws Exception
	{
		CountDownLatch    aSync = null;
		HazelcastInstance aCore = null;

		synchronized(this)
		{
			if (m_aRunSync == null)
				return;
			
			aSync      = m_aRunSync;
            m_aRunSync = null;
            
            aCore      = m_aCore;
            m_aCore    = null;
		}
		
		aCore.shutdown ();

		if (m_bCleanOnShutdown)
			impl_cleanPersistenceLayer ();

		aSync.countDown();
	}

	//-------------------------------------------------------------------------
	public void join ()
		throws Exception
	{
		CountDownLatch aSync  = null;
		synchronized(this)
		{
			if (m_aRunSync == null)
				return;

			aSync = m_aRunSync;
		}

		aSync.await();
	}

	//-------------------------------------------------------------------------
	@SuppressWarnings("rawtypes")
	private void impl_cleanPersistenceLayer ()
	    throws Exception
	{
		final Collection< RecoverableHZMapLoader > aRegistry = mem_LoaderRegistry ().values();
		for (final RecoverableHZMapLoader aLoader : aRegistry)
			aLoader.clear ();
		
		if (StringUtils.isEmpty(m_sPersistenceDir))
			return;
		
		final File aPersistenceDir = new File(m_sPersistenceDir);
		FileUtils.deleteQuietly(aPersistenceDir);
	}
	
	//-------------------------------------------------------------------------
	private HazelcastInstance mem_Core ()
	    throws Exception
	{
		if (m_aCore == null)
		{
		        final Config aConfig = impl_configClusterMemberStatic    ();
			  //final Config aConfig = impl_configClusterMemberMulticast ();
		    	             m_aCore = Hazelcast.newHazelcastInstance(aConfig);
		}
		return m_aCore;
	}

	//-------------------------------------------------------------------------
	private Config impl_configClusterMemberStatic ()
		throws Exception
	{
		Validate.notEmpty(m_sInterface, "Miss config value for 'interface'.");
		Validate.isTrue  (m_nPort > 0 , "Miss config value for 'port'."     );
		
		final Config aConfig = new Config();

		aConfig.setProperty(GroupProperties.PROP_PREFER_IPv4_STACK          , "true" );
	  //aConfig.setProperty(GroupProperties.PROP_CLIENT_ENGINE_THREAD_COUNT , "20"   );
		aConfig.setProperty(GroupProperties.PROP_ENABLE_JMX                 , "false");
		aConfig.setProperty(GroupProperties.PROP_ENABLE_JMX_DETAILED        , "false");
		aConfig.setProperty(GroupProperties.PROP_VERSION_CHECK_ENABLED      , "false");
		aConfig.setProperty(GroupProperties.PROP_LOGGING_TYPE               , "none" );
		aConfig.setProperty(GroupProperties.PROP_SYSTEM_LOG_ENABLED         , "false");
		aConfig.setProperty(GroupProperties.PROP_SHUTDOWNHOOK_ENABLED       , "false");
		aConfig.setProperty("hazelcast.immediate.backup.interval"           , "5"    );
		aConfig.setProperty("hazelcast.jmx", "true");

		final MapConfig aDefaultMapCfg = aConfig.getMapConfig("default");
		aDefaultMapCfg.setBackupCount   (1   ); // synchronous backup to (at least) one other node within the cluster
		aDefaultMapCfg.setReadBackupData(true);
		
//		final MapConfig aCEServerMapCfg = aConfig.getMapConfig("ce-server-context");
//		aCEServerMapCfg.setBackupCount   (1   ); // synchronous backup to (at least) one other node within the cluster
//		aCEServerMapCfg.setReadBackupData(true);

		final NearCacheConfig aDefaultMapCache = new NearCacheConfig();
		aDefaultMapCache.setCacheLocalEntries(false           );
		aDefaultMapCfg  .setNearCacheConfig  (aDefaultMapCache);

		final NetworkConfig    aNetwork   = aConfig .getNetworkConfig  ();
		final GroupConfig      aGroup     = aConfig .getGroupConfig    ();
		final JoinConfig       aJoin      = aNetwork.getJoin           ();
		final MulticastConfig  aMulticast = aJoin   .getMulticastConfig();
		final TcpIpConfig      aTcpIp     = aJoin   .getTcpIpConfig    ();
		final AwsConfig        aAws       = aJoin   .getAwsConfig      ();
		final InterfacesConfig aInterface = aNetwork.getInterfaces     ();
		
		aTcpIp    .setEnabled(true );
		aAws      .setEnabled(false);
		aMulticast.setEnabled(false);
		aInterface.setEnabled(true );

		aNetwork  .setPort             (m_nPort);
		aNetwork  .setPortAutoIncrement(true   );
		
		aInterface.addInterface(m_sInterface);
		
		if ( ! StringUtils.isEmpty(m_sId))
			aGroup.setName (m_sId);

		if ( ! StringUtils.isEmpty(m_sPassword))
			aGroup.setPassword (m_sPassword);
		
		if ( ! StringUtils.isEmpty(m_sPersistenceDir))
			impl_enabledPersistence (aConfig, m_sPersistenceDir);
	
		return aConfig;
	}

	//-------------------------------------------------------------------------
	private void impl_enabledPersistence (final Config aConfig        ,
										  final String sPersistenceDir)
		throws Exception
	{
		final MapConfig      aAllMapsConfig  = aConfig.getMapConfig( "*" );
	      	  MapStoreConfig aMapStoreConfig = aAllMapsConfig.getMapStoreConfig();
	
		if (aMapStoreConfig == null)
		{
			aMapStoreConfig = new MapStoreConfig();
			aAllMapsConfig.setMapStoreConfig(aMapStoreConfig);
		}
	
		aMapStoreConfig.setEnabled(true);
		aMapStoreConfig.setInitialLoadMode(InitialLoadMode.EAGER);
		aMapStoreConfig.setFactoryImplementation(new MapStoreFactory< Object, Object >()
		{
			@SuppressWarnings({ "rawtypes", "unchecked" })
			@Override
			public MapLoader< Object, Object > newMapStore(final String     sMapName,
					                                       final Properties lProps  )
			{
				try
				{
					final Map< String, RecoverableHZMapLoader > aRegistry = mem_LoaderRegistry ();
					      RecoverableHZMapLoader aLoader = aRegistry.get(sMapName);
					      
			        if (aLoader == null)
			        {
			        	aLoader = new RecoverableHZMapLoader< Object, Object > ();
						aLoader.setPersistenceDir (sPersistenceDir);
						aLoader.setMapId          (sMapName       );
						aRegistry.put (sMapName, aLoader);
			        }
					
					return aLoader;
				}
				catch (Throwable ex)
				{
					throw new RuntimeException (ex);
				}
			}
		});
	}

//	//-------------------------------------------------------------------------
//	private Config impl_configClusterMemberMulticast ()
//		throws Exception
//	{
//		final Config aConfig = new Config ();
//
//		// http://docs.hazelcast.org/docs/2.2/manual/html/ch12s06.html
//		aConfig.setProperty("hazelcast.jmx.detailed"                 , "false");
//		aConfig.setProperty("hazelcast.jmx"                          , "false");
//		aConfig.setProperty("hazelcast.prefer.ipv4.stack"            , "true" );
//		aConfig.setProperty("hazelcast.executor.client.thread.count" , "20"   );
//		aConfig.setProperty("hazelcast.version.check.enabled"        , "false");
//		aConfig.setProperty("hazelcast.logging.type"                 , "slf4j");
//		aConfig.setProperty("hazelcast.immediate.backup.interval"    , "5"    );
//		aConfig.setProperty(GroupProperties.PROP_SHUTDOWNHOOK_ENABLED, "false");
//		
//		final MapConfig aDefaultMapCfg = aConfig.getMapConfig("default");
//		aDefaultMapCfg.setBackupCount      (1   ); // synchronous backup to (at least) one other node within the cluster
//		aDefaultMapCfg.setReadBackupData   (true);
////		aDefaultMapCfg.setStatisticsEnabled(true);
//
//		final NearCacheConfig aDefaultMapCache = new NearCacheConfig();
//		aDefaultMapCache.setCacheLocalEntries(false);
//		aDefaultMapCfg.setNearCacheConfig(aDefaultMapCache);
//		
////		final MapConfig aCEServerMapCfg = aConfig.getMapConfig("ce-server-context");
////		aCEServerMapCfg.setBackupCount      (1   ); // synchronous backup to (at least) one other node within the cluster
////		aCEServerMapCfg.setReadBackupData   (true);
////		aCEServerMapCfg.setStatisticsEnabled(true);
////		
////		final NearCacheConfig aCEServerMapCache = new NearCacheConfig();
////		aCEServerMapCache.setCacheLocalEntries(false);
////		aDefaultMapCfg.setNearCacheConfig(aCEServerMapCache);
//		
//		final NetworkConfig    aNetwork   = aConfig .getNetworkConfig  ();
//		final GroupConfig      aGroup     = aConfig .getGroupConfig    ();
//		final JoinConfig       aJoin      = aNetwork.getJoin           ();
//		final MulticastConfig  aMulticast = aJoin   .getMulticastConfig();
//		final TcpIpConfig      aTcp       = aJoin   .getTcpIpConfig    ();
//		final AwsConfig        aAws       = aJoin   .getAwsConfig      ();
//		final InterfacesConfig aInterface = aNetwork.getInterfaces     ();
//		
//		aTcp      .setEnabled(false);
//		aAws      .setEnabled(false);
//		aMulticast.setEnabled(true );
//		
//		aNetwork  .setPort             (7000);
//		aNetwork  .setPortAutoIncrement(true);
//
//		aMulticast.setMulticastGroup("224.2.2.3");
//		aMulticast.setMulticastPort (54327      );
//		
//		aInterface.setEnabled  (true       );
//		aInterface.addInterface("127.0.0.1");
//		
//		aGroup.setName    ("dev");
//		aGroup.setPassword("xxx");
//
//		return aConfig;
//	}

	//-------------------------------------------------------------------------
	private Map< String, RecoverableHZMapLoader > mem_LoaderRegistry ()
	    throws Exception
	{
		if (m_lLoaderRegistry == null)
			m_lLoaderRegistry = new HashMap< String, RecoverableHZMapLoader > ();
		return m_lLoaderRegistry;
	}

	//-------------------------------------------------------------------------
	private HazelcastInstance m_aCore = null;

	//-------------------------------------------------------------------------
	private String m_sInterface = null;

	//-------------------------------------------------------------------------
	private int m_nPort = 0;

	//-------------------------------------------------------------------------
	private String m_sId = null;

	//-------------------------------------------------------------------------
	private String m_sPassword = null;

	//-------------------------------------------------------------------------
	private CountDownLatch m_aRunSync = null;

	//-------------------------------------------------------------------------
	private String m_sPersistenceDir = null;
	
	//-------------------------------------------------------------------------
	private boolean m_bCleanOnShutdown = true;
	
	//-------------------------------------------------------------------------
	private Map< String, RecoverableHZMapLoader > m_lLoaderRegistry = null;
}
