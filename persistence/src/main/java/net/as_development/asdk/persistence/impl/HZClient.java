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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientNetworkConfig;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IAtomicReference;
import com.hazelcast.core.IMap;
import com.hazelcast.instance.GroupProperties;
import com.hazelcast.transaction.TransactionContext;
import com.hazelcast.transaction.TransactionOptions;
import com.hazelcast.transaction.TransactionOptions.TransactionType;

import net.as_development.asdk.persistence.ISimplePersistence;
import net.as_development.asdk.persistence.ISimplePersistenceTransacted;
import net.as_development.asdk.tools.common.CollectionUtils;
import net.as_development.asdk.tools.reflection.SerializationUtils;

//=============================================================================
public class HZClient implements ISimplePersistenceTransacted
{
	//-------------------------------------------------------------------------
	public static final String CFG_SERVER_HOST     = "server.host"    ;
	public static final String CFG_SERVER_PORT     = "server.port"    ;
	public static final String CFG_SERVER_ID       = "server.id"      ;
	public static final String CFG_SERVER_PASSWORD = "server.password";
	public static final String CFG_STORE_TYPE      = "store.type"     ;
	
	//-------------------------------------------------------------------------
	public static EHZStoreType DEFAULT_HZ_STORE_TYPE = EHZStoreType.E_DISTRIBUTED_OBJECT;
	
	//-------------------------------------------------------------------------
	public HZClient ()
	    throws Exception
	{}

	//-------------------------------------------------------------------------
	@Override
	public synchronized void configure (final String... lConfig)
		throws Exception
	{
		m_lConfig = CollectionUtils.flat2MappedArguments(lConfig);
		
		final String sScope = m_lConfig.get(ISimplePersistence.CFG_PERSISTENCE_SCOPE);
		Validate.notEmpty (sScope, "Miss config item '"+ISimplePersistence.CFG_PERSISTENCE_SCOPE+"'.");
		m_sScope = new AtomicReference< String > (sScope);

		final String sStoreType = m_lConfig.get(HZClient.CFG_STORE_TYPE);
		if (StringUtils.isEmpty(sStoreType))
			m_eStoreType = new AtomicReference< EHZStoreType > (DEFAULT_HZ_STORE_TYPE);
		else
			m_eStoreType = new AtomicReference< EHZStoreType > (EHZStoreType.fromString(sStoreType));

		// TRICKY and DIRTY
		// HZ will join the cluster in case an HazelcastInstance exists only (of course).
		// So this line ensure this persistence instance will join the cluster and
		// distributed data of other processes reach our own process ;-)
		// TODO think about : do we need an explicit start/stop API ?
		mem_Core ();
	}

	//-------------------------------------------------------------------------
	@Override
	public synchronized void clear ()
		throws Exception
	{
		// TODO implement me
	}

	//-------------------------------------------------------------------------
	public synchronized List< String > listKeys ()
		throws Exception
	{
		      List< String > lKeys      = null;
		final EHZStoreType   eStoreType = m_eStoreType.get();

		if (eStoreType == EHZStoreType.E_MAP)
			lKeys = impl_listKeysOnMAP ();
		else
		if (eStoreType == EHZStoreType.E_DISTRIBUTED_OBJECT)
			lKeys = impl_listKeysOnREF ();
		else
			throw new UnsupportedOperationException ("No support for '"+eStoreType+"' implemented yet.");
		
		return lKeys;
	}

	//-------------------------------------------------------------------------
	@Override
	public /*no synchronized*/ < T extends Serializable > void set (final String sKey  ,
			                                                        final T      aValue)
	    throws Exception
	{
		final EHZStoreType eStoreType = m_eStoreType.get();

		if (eStoreType == EHZStoreType.E_MAP)
			impl_setOnMAP (sKey, aValue);
		else
		if (eStoreType == EHZStoreType.E_DISTRIBUTED_OBJECT)
			impl_setOnREF (sKey, aValue);
		else
			throw new UnsupportedOperationException ("No support for '"+eStoreType+"' implemented yet.");
	}
	
	//-------------------------------------------------------------------------
	@Override
	@SuppressWarnings("unchecked")
	public /*no synchronized*/ < T extends Serializable > T get (final String sKey)
	    throws Exception
	{
		final EHZStoreType eStoreType = m_eStoreType.get();
		      T            aValue     = null;

		if (eStoreType == EHZStoreType.E_MAP)
			aValue = (T) impl_getOnMAP (sKey);
		else
		if (eStoreType == EHZStoreType.E_DISTRIBUTED_OBJECT)
			aValue = (T) impl_getOnREF (sKey);
		else
			throw new UnsupportedOperationException ("No support for '"+eStoreType+"' implemented yet.");
		
		return aValue;
	}
	
	//-------------------------------------------------------------------------
	@Override
	public synchronized void begin ()
		throws Exception
	{
		if (m_aTransaction != null)
			return;

		final FailureAwareHZClient aCore        = mem_Core ();
		final TransactionOptions   aOption      = new TransactionOptions()
	                      							.setTransactionType(TransactionType.LOCAL);
		final TransactionContext   aTransaction = aCore.newTransactionContext(aOption);

		aTransaction.beginTransaction();
		m_aTransaction = aTransaction;
	}

	//-------------------------------------------------------------------------
	@Override
	public synchronized void commit ()
		throws Exception
	{
		Validate.notNull(m_aTransaction, "No transaction started. Commit not possible.");

		m_aTransaction.commitTransaction();
		m_aTransaction = null;
	}

	//-------------------------------------------------------------------------
	@Override
	public synchronized void rollback ()
		throws Exception
	{
		Validate.notNull(m_aTransaction, "No transaction started. Rollback not possible.");

		m_aTransaction.rollbackTransaction();
		m_aTransaction = null;
	}
	
	//-------------------------------------------------------------------------
	private synchronized List< String > impl_listKeysOnMAP ()
		throws Exception
	{
		final IMap< Object, Object > aMap   = mem_Map ();
		final Iterator< Object >     rKeys  = aMap.keySet().iterator();
		final List< String >         lKeys  = new Vector< String > ();
		
		while (rKeys.hasNext())
		{
			final Object aKey = rKeys.next();
			
			if (aKey instanceof String)
				lKeys.add ((String)aKey);
			else
				throw new UnsupportedOperationException ("Key '"+aKey+"' is not a string.");
		}

		return lKeys;
	}

	//-------------------------------------------------------------------------
	private synchronized List< String > impl_listKeysOnREF ()
		throws Exception
	{
		final FailureAwareHZClient                      aCore = mem_Core ();
		final Map< Object, IAtomicReference< Object > > lRefs = mem_Refs ();
		final Iterator< Object >                        rKeys = lRefs.keySet().iterator();
		final List< String >                            lKeys = new Vector< String > ();
		
		while (rKeys.hasNext())
		{
			final Object aKey = rKeys.next();
			
			if (aKey instanceof String)
				lKeys.add ((String)aKey);
			else
				throw new UnsupportedOperationException ("Key '"+aKey+"' is not a string.");
		}
		
		final List< String > lKeysInScope = aCore.listKeysInScope (m_sScope.get());
		for (final String sKey : lKeysInScope)
		{
			if ( ! lKeys.contains(sKey))
				lKeys.add (sKey);
		}

		return lKeys;
	}

	//-------------------------------------------------------------------------
	private synchronized < T extends Serializable > void impl_setOnMAP (final String sKey  ,
																	    final T      aValue)
	    throws Exception
	{
		final IMap< Object, Object > aMap = mem_Map();
		
		if (aValue == null)
		{
			aMap.remove(sKey);
		}
		else
		{
			final String sValue = SerializationUtils.mapObject2String(aValue);
			aMap.put(sKey, sValue);
		}
	}

	//-------------------------------------------------------------------------
	private synchronized < T extends Serializable > void impl_setOnREF (final String sKey  ,
												                        final T      aValue)
		throws Exception
	{
		final FailureAwareHZClient                      aCore = mem_Core ();
		final Map< Object, IAtomicReference< Object > > lRefs = mem_Refs ();
			  IAtomicReference< Object >                aRef  = lRefs.get(sKey);
		
		if (aValue == null && aRef != null)
		{
			aRef .set   (null);
			lRefs.remove(sKey);
		}
		else
		{
			if (aRef == null)
			{
				aRef = aCore.getObject(m_sScope.get(), sKey);
				lRefs.put(sKey, aRef);
			}

			final String sValue = SerializationUtils.mapObject2String(aValue);
			aRef.set ((Object)sValue);
		}
	}

	//-------------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	private synchronized < T extends Serializable > T impl_getOnMAP (final String sKey)
	    throws Exception
	{
		final IMap< Object, Object > aMap   = mem_Map ();
		      String                 sValue = null; 
		      T                      aValue = null;
		
		if (aMap.containsKey(sKey))
			sValue = (String) aMap.get (sKey);
		
		aValue = (T) SerializationUtils.mapString2Object (sValue);
		
		return aValue;
	}
	
	//-------------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	private synchronized < T extends Serializable > T impl_getOnREF (final String sKey)
	    throws Exception
	{
		final FailureAwareHZClient                      aCore = mem_Core ();
		final Map< Object, IAtomicReference< Object > > lRefs = mem_Refs ();
		
		IAtomicReference< Object > aRef = lRefs.get(sKey);
		if (aRef == null)
		{
			aRef = aCore.getObject(m_sScope.get(), sKey);
			lRefs.put (sKey, aRef);
		}

		String sValue = null; 
		if (aRef != null)
			sValue = (String) aRef.get();
		
		T aValue = (T) SerializationUtils.mapString2Object (sValue);
		return aValue;
	}
	
	//-------------------------------------------------------------------------
	private ClientConfig impl_configClientStatic ()
		throws Exception
	{
		final ClientConfig aConfig = new ClientConfig ();

		aConfig.setProperty(GroupProperties.PROP_PREFER_IPv4_STACK          , "true" );
	  //aConfig.setProperty(GroupProperties.PROP_CLIENT_ENGINE_THREAD_COUNT , "20"   );
		aConfig.setProperty(GroupProperties.PROP_ENABLE_JMX                 , "false");
		aConfig.setProperty(GroupProperties.PROP_ENABLE_JMX_DETAILED        , "false");
		aConfig.setProperty(GroupProperties.PROP_VERSION_CHECK_ENABLED      , "false");
		aConfig.setProperty(GroupProperties.PROP_LOGGING_TYPE               , "none" );
		aConfig.setProperty(GroupProperties.PROP_SYSTEM_LOG_ENABLED         , "false");
		aConfig.setProperty(GroupProperties.PROP_SHUTDOWNHOOK_ENABLED       , "false");

		final ClientNetworkConfig aNetwork        = aConfig.getNetworkConfig ();
		final GroupConfig         aGroup          = aConfig.getGroupConfig   ();
		
		final String              sServerHost     =                  m_lConfig.get (CFG_SERVER_HOST);
		final int                 nServerPort     = Integer.parseInt(m_lConfig.get (CFG_SERVER_PORT));
		final String              sServerAddress  = sServerHost + ":" + nServerPort;
		
		final String              sServerId       = m_lConfig.get (CFG_SERVER_ID      );
		final String              sServerPassword = m_lConfig.get (CFG_SERVER_PASSWORD);
		
		aNetwork.addAddress                (sServerAddress);
		aNetwork.setConnectionAttemptLimit (30            );
		aNetwork.setConnectionAttemptPeriod(1000          );
		aNetwork.setConnectionTimeout      (30000         );
		
		aGroup  .setName    (sServerId      );
		aGroup  .setPassword(sServerPassword);

//		NearCacheConfig aNearCache = aConfig.getNearCacheConfig("*");
//		if (aNearCache == null)
//		{
//			aNearCache = new NearCacheConfig ();
//			aConfig.addNearCacheConfig(aNearCache);
//		}
//		aNearCache.setMaxSize           (1000                 );
//		aNearCache.setInMemoryFormat    (InMemoryFormat.OBJECT);
//		aNearCache.setCacheLocalEntries (true                 );
//		aNearCache.setInvalidateOnChange(true                 );
//		aNearCache.setTimeToLiveSeconds (60                   );

		return aConfig;
	}

	//-------------------------------------------------------------------------
	private FailureAwareHZClient mem_Core ()
	    throws Exception
	{
		if (
			(m_aCore != null    ) &&
			(m_aCore.hasErrors())
		   )
		{
			m_aCore = null;
		}

		if (m_aCore == null)
		{
	    	final ClientConfig      aConfig = impl_configClientStatic ();
	    	final HazelcastInstance aCore   = HazelcastClient.newHazelcastClient(aConfig);
	    	m_aCore = FailureAwareHZClient.create(aCore);             
		}
		
		return m_aCore;
	}

	//-------------------------------------------------------------------------
	private IMap< Object, Object > mem_Map ()
	    throws Exception
	{
		if (m_iMap == null)
		{
			final FailureAwareHZClient   aCore = mem_Core ();
			final IMap< Object, Object > iMap  = aCore.getMap(m_sScope.get());
			m_iMap = iMap;
		}
		return m_iMap;
	}

	//-------------------------------------------------------------------------
	private Map< Object, IAtomicReference< Object > > mem_Refs ()
		throws Exception
	{
		if (m_iRefs == null)
			m_iRefs = new HashMap< Object, IAtomicReference< Object > > ();
		return m_iRefs;
	}

	//-------------------------------------------------------------------------
	private FailureAwareHZClient m_aCore = null;

	//-------------------------------------------------------------------------
	private TransactionContext m_aTransaction = null;
	
	//-------------------------------------------------------------------------
	private AtomicReference< String > m_sScope = null;

	//-------------------------------------------------------------------------
	private AtomicReference< EHZStoreType > m_eStoreType = null;

	//-------------------------------------------------------------------------
	private Map< String, String > m_lConfig = null;

	//-------------------------------------------------------------------------
	private IMap< Object, Object > m_iMap = null;

	//-------------------------------------------------------------------------
	private Map< Object, IAtomicReference< Object > > m_iRefs = null;
}
