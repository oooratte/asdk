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
import java.util.Map.Entry;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
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

import net.as_development.asdk.persistence.ISimplePersistenceImpl;
import net.as_development.asdk.persistence.ISimplePersistenceLock;
import net.as_development.asdk.persistence.SimplePersistenceConfig;
import net.as_development.asdk.tools.common.CollectionUtils;
import net.as_development.asdk.tools.reflection.SerializationUtils;

//=============================================================================
public class HazelcastPersistence implements ISimplePersistenceImpl
										   , ISimplePersistenceLock
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
	public HazelcastPersistence ()
	    throws Exception
	{}

	//-------------------------------------------------------------------------
	@Override
	public synchronized void configure (final String... lConfig)
		throws Exception
	{
		m_lConfig = CollectionUtils.flat2MappedArguments(lConfig);
		
		final String sScope = m_lConfig.get(SimplePersistenceConfig.CFG_PERSISTENCE_SCOPE);
		Validate.notEmpty (sScope, "Miss config item '"+SimplePersistenceConfig.CFG_PERSISTENCE_SCOPE+"'.");
		m_sScope = new AtomicReference< String > (sScope);

		final String sStoreType = m_lConfig.get(HazelcastPersistence.CFG_STORE_TYPE);
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
	public synchronized ISimplePersistenceImpl getSubSet (final String sSubSet)
		throws Exception
	{
		final HazelcastPersistence aSubSet  = new HazelcastPersistence ();
		aSubSet.m_aCore   = m_aCore  ;
		aSubSet.m_lConfig = m_lConfig;
		aSubSet.m_sScope    .set(m_sScope.get());
		aSubSet.m_sSubSet   .set(KeyHelper.nameKey(m_sSubSet.get(), sSubSet));
		aSubSet.m_eStoreType.set(m_eStoreType.get());
		return aSubSet;
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
	public /*no synchronized*/ void set (final Map< String, Object > lChanges)
	    throws Exception
	{
		final EHZStoreType eStoreType = m_eStoreType.get();

		final Iterator< Entry< String, Object > > rChanges = lChanges.entrySet().iterator();
		while (rChanges.hasNext())
		{
			final Entry< String, Object > aChange = rChanges.next    ();
			final String                  sKey    = aChange .getKey  ();
			final Object                  aValue  = aChange .getValue();
			
			if (eStoreType == EHZStoreType.E_MAP)
				impl_setOnMAP (sKey, aValue);
			else
			if (eStoreType == EHZStoreType.E_DISTRIBUTED_OBJECT)
				impl_setOnREF (sKey, aValue);
			else
				throw new UnsupportedOperationException ("No support for '"+eStoreType+"' implemented yet.");
		}
	}
	
	//-------------------------------------------------------------------------
	@Override
	public /*no synchronized*/ Object get (final String sKey)
	    throws Exception
	{
		final EHZStoreType eStoreType = m_eStoreType.get();
		      Object aValue     = null;

		if (eStoreType == EHZStoreType.E_MAP)
			aValue = impl_getOnMAP (sKey);
		else
		if (eStoreType == EHZStoreType.E_DISTRIBUTED_OBJECT)
			aValue = impl_getOnREF (sKey);
		else
			throw new UnsupportedOperationException ("No support for '"+eStoreType+"' implemented yet.");
		
		return aValue;
	}
	
//	//-------------------------------------------------------------------------
//	@Override
//	public synchronized void begin ()
//		throws Exception
//	{
//		if (m_aTransaction != null)
//			return;
//
//		final FailureAwareHZClient aCore        = mem_Core ();
//		final TransactionOptions   aOption      = new TransactionOptions()
//	                      							.setTransactionType(TransactionType.LOCAL);
//		final TransactionContext   aTransaction = aCore.newTransactionContext(aOption);
//
//		aTransaction.beginTransaction();
//		m_aTransaction = aTransaction;
//	}
//
//	//-------------------------------------------------------------------------
//	@Override
//	public synchronized void commit ()
//		throws Exception
//	{
//		Validate.notNull(m_aTransaction, "No transaction started. Commit not possible.");
//
//		m_aTransaction.commitTransaction();
//		m_aTransaction = null;
//	}
//
//	//-------------------------------------------------------------------------
//	@Override
//	public synchronized void rollback ()
//		throws Exception
//	{
//		Validate.notNull(m_aTransaction, "No transaction started. Rollback not possible.");
//
//		m_aTransaction.rollbackTransaction();
//		m_aTransaction = null;
//	}
	

	//-------------------------------------------------------------------------
	@Override
	public /* no synchronized */ ILock lock(final String sId)
		throws Exception
	{
		Validate.isTrue( ! StringUtils.isEmpty(sId), "Invalid argument 'id'.");

		final com.hazelcast.core.ILock iHZLock = mem_Core().getLock(sId);
		iHZLock.lock();
		
		final Lock aLock = new Lock ();
		aLock.sId     = sId;
		aLock.iHZLock = iHZLock;
		return aLock;
	}

	//-------------------------------------------------------------------------
	@Override
	public /* no synchronized */ ILock tryLock(final String   sId      ,
									  		   final int      nTimeOut ,
									  		   final TimeUnit aTimeUnit)
		throws Exception
	{
		Validate.isTrue( ! StringUtils.isEmpty(sId), "Invalid argument 'id'."                   );
		Validate.isTrue(   nTimeOut > 0            , "Invalid argument 'timeout'. Needs to > 0.");

		final com.hazelcast.core.ILock iHZLock = mem_Core().getLock(sId);
		final boolean                  bOK     = iHZLock.tryLock(nTimeOut, aTimeUnit);

		if ( ! bOK)
			return null;
		
		final Lock aLock = new Lock ();
		aLock.sId     = sId;
		aLock.iHZLock = iHZLock;
		return aLock;
	}

	//-------------------------------------------------------------------------
	@Override
	public /* no synchronized */ boolean unlock(final ILock iLock)
		throws Exception
	{
		Validate.isTrue(iLock != null, "Invalid argument 'lock'.");

		if ( ! Lock.class.isAssignableFrom(iLock.getClass ()))
			throw new IllegalMonitorStateException ("This is not a lock owned by this persistence instance. (wrong type)");

		final Lock aLock = (Lock) iLock;

		try
		{
			aLock.iHZLock.unlock();
		}
		catch (final IllegalMonitorStateException exIllegalMonitor)
		{
			throw exIllegalMonitor;
		}
		catch (final Throwable exAny)
		{
			return false;
		}

		return true;
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
	private synchronized void impl_setOnMAP (final String sKey  ,
											 final Object aValue)
	    throws Exception
	{
		final IMap< Object, Object > aMap = mem_Map();
		
		if (aValue == null)
		{
			aMap.remove(sKey);
		}
		else
		{
			final String sValue = SerializationUtils.mapObject2String((Serializable)aValue);
			aMap.put(sKey, sValue);
		}
	}

	//-------------------------------------------------------------------------
	private synchronized void impl_setOnREF (final String sKey  ,
											 final Object aValue)
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

			final String sValue = SerializationUtils.mapObject2String((Serializable)aValue);
			aRef.set ((Object)sValue);
		}
	}

	//-------------------------------------------------------------------------
	private synchronized Object impl_getOnMAP (final String sKey)
	    throws Exception
	{
		final IMap< Object, Object > aMap   = mem_Map ();
		      String                 sValue = null; 
		      Object           aValue = null;
		
		if (aMap.containsKey(sKey))
			sValue = (String) aMap.get (sKey);
		
		aValue = SerializationUtils.mapString2Object (sValue);
		
		return aValue;
	}
	
	//-------------------------------------------------------------------------
	private synchronized Object impl_getOnREF (final String sKey)
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
		
		final Object aValue = SerializationUtils.mapString2Object (sValue);
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
	private class Lock implements ISimplePersistenceLock.ILock
	{
		protected String                   sId     = null;
		protected com.hazelcast.core.ILock iHZLock = null;
	}

	//-------------------------------------------------------------------------
	private FailureAwareHZClient m_aCore = null;

//	//-------------------------------------------------------------------------
//	private TransactionContext m_aTransaction = null;
	
	//-------------------------------------------------------------------------
	private AtomicReference< String > m_sScope = null;

	//-------------------------------------------------------------------------
	private AtomicReference< String > m_sSubSet = null;

	//-------------------------------------------------------------------------
	private AtomicReference< EHZStoreType > m_eStoreType = null;

	//-------------------------------------------------------------------------
	private Map< String, String > m_lConfig = null;

	//-------------------------------------------------------------------------
	private IMap< Object, Object > m_iMap = null;

	//-------------------------------------------------------------------------
	private Map< Object, IAtomicReference< Object > > m_iRefs = null;
}
