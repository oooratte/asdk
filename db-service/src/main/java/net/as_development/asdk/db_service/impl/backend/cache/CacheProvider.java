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
package net.as_development.asdk.db_service.impl.backend.cache;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.as_development.asdk.db_service.IDBBackend;
import net.as_development.asdk.db_service.IDBBackendQuery;
import net.as_development.asdk.db_service.impl.EntityMetaInfoProvider;
import net.as_development.asdk.db_service.impl.PersistenceUnit;
import net.as_development.asdk.db_service.impl.Row;
import net.as_development.asdk.service.env.ServiceEnv;

//==============================================================================
/** Wrap another IDBBackendImplementation and combine it with a
 *  cache. Those cache can exist local (in memory) or remote (e.g. memcache).
 *  The cache will be preferred if it is still working but ignored if it
 *  shows errors.
 */
public class CacheProvider implements IDBBackend
{
	// TODO get it from real cache API
	private static interface ICache
	{
		public < T > T getValue (final String sId);
		public void    setValue (final String sId, final Object aQuery);
	}
	
    //--------------------------------------------------------------------------
    private enum ECacheUpdateMode
    {
        E_REMOVE,
        E_UPDATE
    }
    
    //--------------------------------------------------------------------------
    public CacheProvider ()
    {}

    //--------------------------------------------------------------------------
    @Override
    public void setEntityMetaInfoProvider(EntityMetaInfoProvider aProvider)
        throws Exception
    {
        m_aMetaProvider = aProvider;
        mem_Db ().setEntityMetaInfoProvider(aProvider);
    }

    //--------------------------------------------------------------------------
    @Override
    public void createTable(Row aRow)
        throws Exception
    {
        // creating tables within cache makes no sense ...
        // so forward request to wrapped db backend only.
        mem_Db ().createTable(aRow);
    }

    //--------------------------------------------------------------------------
    @Override
    public void removeTable(Row aRow)
        throws Exception
    {
        // @todo think about me
        // ... how we can remove all items for this table from our cache
        // ... in an acceptable time frame .-)
        mem_Db ().removeTable(aRow);
    }
    
    //--------------------------------------------------------------------------
    @Override
    public void insertRows(List< Row > lRows)
        throws Exception
    {
        impl_updateCache (CacheProvider.ECacheUpdateMode.E_UPDATE, CacheProvider.impl_mapRowList2Array(lRows));
        mem_Db ().insertRows(lRows);
    }

    //--------------------------------------------------------------------------
    @Override
    public void updateRows(List< Row > lRows)
        throws Exception
    {
        impl_updateCache (CacheProvider.ECacheUpdateMode.E_UPDATE, CacheProvider.impl_mapRowList2Array(lRows));
        mem_Db ().updateRows(lRows);
    }

    //--------------------------------------------------------------------------
    @Override
    public void deleteRows(List< Row > lRows)
        throws Exception
    {
        impl_updateCache (CacheProvider.ECacheUpdateMode.E_REMOVE, CacheProvider.impl_mapRowList2Array(lRows));
        mem_Db ().deleteRows(lRows);
    }

    //--------------------------------------------------------------------------
    @Override
    public void deleteAllRows(Row aRow)
        throws Exception
    {
        // Because cache has no interface to iterate over all cache items we can't
        // implement that - at least not without loosing performance .-)
        
        // The given row object contains meta data about the table which has to be cleaned up
        // only .. no real entities. So we don't have an entity IDs here and can't use
        // the cache interface which require such IDs.
        
        // On the other side cache items will expire if they are not used for a certain time.
        // -> do nothing regarding cache here ...
        // -> forward request to DB backend only.
        
        mem_Db ().deleteAllRows(aRow);
    }

    //--------------------------------------------------------------------------
	@Override
    public void getRowById(Row aRow)
        throws Exception
    {
        String sId    = (String) aRow.getIdValue();
	    ICache iCache = mem_Cache ();
	    
	    // a) read from cache and return if it exists there.
	    //    Handle cache errors gracefully.
	    try
	    {
    	    CachedEntity aEntity = iCache.getValue(sId);
    	    if (aEntity != null)
    	    {
    	        aEntity.toRow(aRow);
    	        aRow.getPersistentStateHandler().setPersistent();
    	        return;
    	    }
	    }
	    catch (Throwable ex)
	    {
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.WARNING, "Cache read access faild on getRowById().", ex);
	    }
	    
	    // b) read it from DB ...
	    IDBBackend iDb = mem_Db ();
	    iDb.getRowById(aRow);
	    
	    // c) ... but cache it before you return
	    impl_updateCache (CacheProvider.ECacheUpdateMode.E_UPDATE, aRow);
    }

    //--------------------------------------------------------------------------
    @Override
    public String getAllRows (Row         aMeta     ,
                              String      sNextToken,
                              List< Row > lResults  )
        throws Exception
    {
        throw new UnsupportedOperationException ("Not implemented yet.");
    }
    
    //--------------------------------------------------------------------------
    @Override
	public String queryRows(Row 	  		aMetaRow  ,
	                        String          sNextToken,
	                        List< Row >     lResults  ,
	                        IDBBackendQuery iQuery    )
    	throws Exception
    {
        ICache iCache   = mem_Cache ();
        String sCacheID = impl_generateQueryCacheId (aMetaRow, sNextToken, iQuery);
        
        // read from cache first
        try
        {
            lResults.clear ();
            CachedQueryResult aCachedQuery = iCache.getValue(sCacheID);
            
            if (aCachedQuery != null)
            {
                List< String > lCachedEntityIDs = aCachedQuery.getIDs();
                for (String sCachedEntityID : lCachedEntityIDs)
                {
                    CachedEntity aCachedEntity = iCache.getValue(sCachedEntityID);
                    if (aCachedEntity != null)
                    {
                        Row aResult = aMetaRow.newRow();
                        aCachedEntity.toRow(aResult);
                        aResult.getPersistentStateHandler().setPersistent();
                        lResults.add (aResult);
                    }                        
                }
                
                return aCachedQuery.getNextToken();
            }
        }
        catch (Throwable ex)
        {
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.WARNING, "Cache read access faild on queryRows().", ex);
        }
        
        // ask real DB
        String sNextNextToken = mem_Db ().queryRows(aMetaRow, sNextToken, lResults, iQuery);
        
        // cache results
        // cache entities first (even if query isn't cached we read newest entity values next time) .-)
        impl_updateCache(CacheProvider.ECacheUpdateMode.E_UPDATE, CacheProvider.impl_mapRowList2Array(lResults));
        // cache query id's
        try
        {
            CachedQueryResult aCachedQuery = new CachedQueryResult ();
            aCachedQuery.setNextToken(sNextNextToken);
            
            for (Row aResult : lResults)
                aCachedQuery.putID ((String)aResult.getIdValue());
            
            iCache.setValue(sCacheID, aCachedQuery);
        }
        catch (Throwable ex)
        {
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.WARNING, "Cache write access faild on queryRows().", ex);
        }
        
        return sNextNextToken;
    }

    //--------------------------------------------------------------------------
    private String impl_generateQueryCacheId (Row             aMeta     ,
                                              String          sNextToken,
                                              IDBBackendQuery iQuery    )
        throws Exception
    {
        StringBuffer sQueryCacheID = new StringBuffer ();
        
        sQueryCacheID.append(aMeta.getTable());
        sQueryCacheID.append(iQuery.getId()  );
        sQueryCacheID.append(sNextToken      );
        
        return sQueryCacheID.toString ();
    }
    
    //--------------------------------------------------------------------------
    /**
        // but ignore errors here.
        // It's a cache only and errors here shouldn't affect
        // updating items within real DB !
        // Thats true even if we will be inconsistent for some seconds .-)
     */
    private void impl_updateCache (CacheProvider.ECacheUpdateMode eMode,
                                   Row...                         lRows)
    {
        try
        {
            ICache iCache = mem_Cache ();
            for (Row aRow : lRows)
            {
                String sId = (String) aRow.getIdValue();
                
                if (eMode == CacheProvider.ECacheUpdateMode.E_REMOVE)
                {
                    iCache.setValue(sId, null);
                }
                else
                if (eMode == CacheProvider.ECacheUpdateMode.E_UPDATE)
                {
                    CachedEntity aCacheValue = new CachedEntity ();
                    aCacheValue.fromRow(aRow);
                    iCache.setValue(sId, aCacheValue);
                }
                else
                    throw new IllegalArgumentException ("Please implement new cache update mode: "+eMode+" .-)");
            }
        }
        catch (Throwable ex)
        {
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.WARNING, "Cache update failed on impl_updateCache ().", ex);
        }
    }
    
    //--------------------------------------------------------------------------
    private static Row[] impl_mapRowList2Array (List< Row > lList)
        throws Exception
    {
        int   c      = lList.size();
        Row[] lArray = new Row[c];
        lList.toArray(lArray);
        return lArray;
    }
    
    //--------------------------------------------------------------------------
    private IDBBackend mem_Db ()
        throws Exception
    {
        if (m_iDb == null)
        {
            PersistenceUnit aPU   = m_aMetaProvider.getPersistenceUnit();
            String          sImpl = aPU.getProperty(CacheProviderConfigConst.PROP_DB_IMPLEMENTATION);
            IDBBackend      iDb   = ServiceEnv.get ().getService(sImpl);
            
            m_iDb = iDb; 
        }
        return m_iDb;
    }
    
    //--------------------------------------------------------------------------
    private ICache mem_Cache ()
        throws Exception
    {
        if (m_iCache == null)
        {
            PersistenceUnit aPU    = m_aMetaProvider.getPersistenceUnit();
            String          sImpl  = aPU.getProperty(CacheProviderConfigConst.PROP_CACHE_IMPLEMENTATION);
            ICache          iCache = ServiceEnv.get ().getService(sImpl);
            
            m_iCache = iCache;
        }
        return m_iCache;
    }
    
    //--------------------------------------------------------------------------
    private EntityMetaInfoProvider m_aMetaProvider = null;
    
    //--------------------------------------------------------------------------
    private IDBBackend m_iDb = null;
    
    //--------------------------------------------------------------------------
    private ICache m_iCache = null;
}
