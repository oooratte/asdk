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
package net.as_development.asdk.db_service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import net.as_development.asdk.api.db.EQueryPartBinding;
import net.as_development.asdk.api.db.EQueryPartOperation;
import net.as_development.asdk.api.db.IDB;
import net.as_development.asdk.api.db.IDBQuery;
import net.as_development.asdk.api.db.IDBSchema;
import net.as_development.asdk.api.db.IEntity;
import net.as_development.asdk.api.db.IPersistenceUnit;
import net.as_development.asdk.db_service.EntityBase;
import net.as_development.asdk.db_service.IDBBackend;
import net.as_development.asdk.db_service.IDBBackendQuery;

import org.apache.commons.lang3.StringUtils;

//==============================================================================
/** Base for all implementations of the interface IDB.
 *  It provides the basic functionality which is used by all those derived
 *  classes ... as e.g. handling of the persistence unit.
 */
public class DB implements IDB
						 , IDBSchema
{
    //--------------------------------------------------------------------------
    /** create new instance.
     */
    public DB ()
    {}

    //--------------------------------------------------------------------------
    @Override
    public synchronized void setPersistenceUnit(String sUnit)
        throws Exception
    {
        if (StringUtils.equals(m_sPersistenceUnit, sUnit))
            return;

        m_sPersistenceUnit = sUnit;

        // make sure such internal resources will be create new next time
        // somehwere ask for it .-)
        m_aPersistenceUnit = null;
        m_aMetaProvider    = null;
    }

    //--------------------------------------------------------------------------
    @Override
    public synchronized void setPersistenceUnit(IPersistenceUnit iUnit)
        throws Exception
    {
        m_aPersistenceUnit = (PersistenceUnit) iUnit;
        m_sPersistenceUnit = iUnit.getName();

        // make sure such internal resources will be create new next time
        // somewhere ask for it .-)
        m_aMetaProvider = null;
    }

    //--------------------------------------------------------------------------
    @Override
    public synchronized <TEntity extends IEntity> void storeEntities(TEntity... lEntities)
        throws Exception
    {
        List< Row > lInsertRows = new ArrayList< Row >(10);
        List< Row > lUpdateRows = new ArrayList< Row >(10);

        for (TEntity aNextEntity : lEntities)
        {
        	EntityBase aEntity = (EntityBase)aNextEntity;
        	
        	// Check if entity is already in sync with DB back end (means if it is persistent already)
            boolean bInSync = aEntity.isPersistent();

            // if it is not persistent already ... generate a new ID ...
            // But be tricky. Generate a special PreId. If all values from aEntity
            // will be copied to aRow (on calling EntityHelper.createRowFromEntity())
            // this PreId will be copied as ID value. Further aEntity will be set on aRow.
            // The backend is asked then to call aRow.setInSync () if those row was
            // stored successfully. And that will call aEntity.setInSync() ... where
            // aEntity.ID will be filled from aEntity.PreId.
            // After this process we will have all real persistent entities with an ID ...
            // all non persistent entities with ID=null .-)

            // Pre condition: Row objects generated here has not to be cached !

            if ( ! bInSync)
            	aEntity.PreId = impl_generateId ((IEntity)aEntity);

            // mark entity as 'modified' (needed for backup solution)
            aEntity.setModifyStamp();
            
            // make sure removed ... but now added again ... entities will be handled right .-)
            aEntity.setRemoved(false);
            
            EntityMetaInfo         aMeta    = mem_MetaProvider ().getMetaInforForEntity(aEntity);
            Row                    aRow     = EntityHelper.createRowFromEntity(aMeta, aEntity);
            PersistentStateHandler aHandler = new PersistentStateHandler (aEntity);
            aRow.setPersistentStateHandler(aHandler);

            if (bInSync)
                lUpdateRows.add(aRow);
            else
                lInsertRows.add(aRow);
        }

        if ( ! lInsertRows.isEmpty())
            mem_Backend ().insertRows(lInsertRows);

        if ( ! lUpdateRows.isEmpty())
            mem_Backend ().updateRows(lUpdateRows);
    }

    //--------------------------------------------------------------------------
    @Override
    public synchronized <TEntity extends IEntity> void removeEntities(TEntity... lEntities)
        throws Exception
    {
        List< Row > lRows = new ArrayList< Row >(lEntities.length);
        for (TEntity aNextEntity : lEntities)
        {
        	EntityBase aEntity = (EntityBase)aNextEntity;
        	
            String sId = aEntity.Id;
            if (StringUtils.isEmpty(sId))
                throw new RuntimeException ("Entity cant be removed. Primary key value not valid.");

            aEntity.setRemoved(true);
            
            EntityMetaInfo         aMeta    = mem_MetaProvider ().getMetaInforForEntity(aEntity);
            Row                    aRow     = new Row (aMeta);
            PersistentStateHandler aHandler = new PersistentStateHandler (aEntity);

            aRow.setPersistentStateHandler(aHandler);
            aRow.setIdValue    (sId); // its enough to define id here ... we must not copy all values of entity .-)
            aRow.setColumnValue(EntityBase.COLUMN_NAME_REMOVED, aEntity.Removed);

            lRows.add (aRow);
        }

        if (impl_isFeatureSpecialRemove())
        	mem_Backend ().updateRows (lRows);
        else
        	mem_Backend ().deleteRows (lRows);
    }

    //--------------------------------------------------------------------------
    @Override
	public synchronized < TEntity extends IEntity > void removeEntitiesById (Class< TEntity > aType,
	                                                                            String...        lIds )
    	throws Exception
    {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    //--------------------------------------------------------------------------
    @Override
    public synchronized <TEntity extends IEntity> void removeAllEntitiesOfType (Class< TEntity > aType)
        throws Exception
    {
        EntityMetaInfo aMeta    = mem_MetaProvider ().getMetaInfoForEntityClass(aType);
        Row            aRowInfo = new Row (aMeta);
        
        if (impl_isFeatureSpecialRemove())
        	throw new UnsupportedOperationException ("Not implemented yet.");
        else
        	mem_Backend ().deleteAllRows(aRowInfo);
    }

    //--------------------------------------------------------------------------
    @SuppressWarnings("unchecked")
	@Override
    public synchronized < TEntity extends IEntity > TEntity getEntityById(Class< TEntity > aType,
                                                                          String           sId  )
        throws Exception
    {
        EntityMetaInfo         aMeta    = mem_MetaProvider ().getMetaInfoForEntityClass(aType);
        Row                    aRow     = new Row (aMeta);
        PersistentStateHandler aHandler = new PersistentStateHandler ();
        TEntity                aEntity  = null;

        aRow.setIdValue               (sId     );
        aRow.setPersistentStateHandler(aHandler);

        try
        {
	        mem_Backend ().getRowById (aRow);
	        if (aHandler.isPersistent())
	        	aEntity = (TEntity) EntityHelper.createEntityFromRow(aMeta, aRow);
	        if (impl_isRemoved(aEntity))
	        	aEntity = null;
        }
        catch (Throwable ex)
        {
        	// If an entity does not exists we have to return NULL !
        }

        return aEntity;
    }

    //--------------------------------------------------------------------------
    @Override
	public synchronized < TEntity extends IEntity > String getEntitiesById (Class< TEntity > aType     ,
	                                                                           String           sNextToken,
	                                                                           List< TEntity >  lResults  ,
	                                                                           String...        lIds      )
    	throws Exception
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    //--------------------------------------------------------------------------
    public synchronized < TEntity extends IEntity > String getAllEntitiesOfType (Class< TEntity > aType     ,
                                                                                    String           sNextToken,
                                                                                    List< TEntity >  lResults  )
        throws Exception
    {
        EntityMetaInfo         aMeta    = mem_MetaProvider ().getMetaInfoForEntityClass(aType);
        Row                    aRow     = new Row (aMeta);
        PersistentStateHandler aHandler = new PersistentStateHandler ();

        aRow.setPersistentStateHandler(aHandler);

        List< Row > lRows          = new Vector< Row >();
        String      sNextNextToken = mem_Backend ().getAllRows (aRow, sNextToken, lRows);

        lResults.clear ();
        for (Row aResult : lRows)
        {
            aHandler = aResult.getPersistentStateHandler();
            if ( ! aHandler.isPersistent())
                continue;
	        if (impl_isRemoved(aResult))
	        	continue;
            
            @SuppressWarnings("unchecked")
            TEntity aEntity = (TEntity) EntityHelper.createEntityFromRow(aMeta, aResult);
            lResults.add (aEntity);
        }

        return sNextNextToken;
    }
    
    //--------------------------------------------------------------------------
    @SuppressWarnings("unchecked")
	@Override
    public synchronized < TEntity extends IEntity > String query (Class< TEntity >    aType     ,
                                                                  String              sNextToken,
                                                                  List< TEntity >     lResults  ,
                                                                  IDBQuery< TEntity > iQuery    )
    	throws Exception
    {
    	// We can do that ... because those queries was created by ourself .-)
    	DBQuery< TEntity > aQuery = (DBQuery< TEntity >)iQuery;

    	if (impl_isFeatureSpecialRemove())
    	{
    		int nEndPos = (aQuery.getPartCount()-1);
    		if (nEndPos < 1)
    			throw new Exception ("Query without any parameter ?");
    		aQuery.setQueryPart(nEndPos, EQueryPartBinding.E_AND, EQueryPartOperation.E_MATCH, EntityBase.ATTRIBUTE_NAME_REMOVED, false);
    	}

    	// Compile query ... can be used as often as you want ...
    	// but do it one times only. See implementation of that method.
    	aQuery.getTemplate().compile ();

    	// Clean results so they are in the right state even if an exception is thrown here.
    	// On the other side the back end knows - WE already cleared that list .-)
    	lResults.clear ();

        EntityMetaInfo         aMeta       = mem_MetaProvider ().getMetaInfoForEntityClass(aType);
        Row                    aRowInfo    = new Row (aMeta);
        PersistentStateHandler aHandler    = new PersistentStateHandler ();
    	List< Row >            lResultRows = new ArrayList< Row > (10);

        aRowInfo.setPersistentStateHandler(aHandler);

    	String          sNextNextToken = mem_Backend ().queryRows (aRowInfo, sNextToken, lResultRows, (IDBBackendQuery) iQuery);
        Iterator< Row > pResults       = lResultRows.iterator();

        while (pResults.hasNext())
        {
            Row aResult = pResults.next();

            if ( ! aResult.getPersistentStateHandler().isPersistent())
            	continue;

            EntityBase aEntity = EntityHelper.createEntityFromRow(aMeta, aResult);
            if (aEntity.isExpired()) /// TODO think about removing those entities from DB real ...
            	continue;

            lResults.add ((TEntity) aEntity);
        }

    	return sNextNextToken;
    }

    //--------------------------------------------------------------------------
    @Override
    public synchronized < TEntity extends IEntity > TEntity queryOne (Class< TEntity >    aType ,
                                                                         IDBQuery< TEntity > iQuery)
        throws Exception
    {
        List< TEntity > lResults = new ArrayList< TEntity >(10);
        query (aType, null, lResults, iQuery);

        if (lResults.size () > 0)
            return lResults.get(0);

        return null;
    }

    //--------------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	@Override
	public synchronized < TEntity extends IEntity > IDBQuery< TEntity > prepareQuery(Class< TEntity > aType   ,
	                                                                                    String           sQueryId)
        throws Exception
    {
	    Map< String, DBQueryTemplate< ? > > aPool     = mem_QueryPool ();
	    DBQueryTemplate< TEntity >          aTemplate = null;

	    if (aPool.containsKey(sQueryId))
	    	aTemplate = (DBQueryTemplate< TEntity >) aPool.get(sQueryId);
	    else
	    {
	        EntityMetaInfo aMeta  = mem_MetaProvider ().getMetaInfoForEntityClass(aType);
	                       aTemplate = new DBQueryTemplate< TEntity > (sQueryId, aMeta);
	        aPool.put(sQueryId, aTemplate);
	    }
	    
	    DBQuery< TEntity > aQuery = new DBQuery< TEntity > ();
	    aQuery.bindToQueryTemplate(aTemplate);
	    
        return aQuery;
	}

    //--------------------------------------------------------------------------
    @Override
    public synchronized < TEntity extends IEntity > void createEntitySchema (Class< TEntity > aType)
        throws Exception
    {
        EntityMetaInfo aMeta    = mem_MetaProvider ().getMetaInfoForEntityClass(aType);
        Row            aRowInfo = new Row (aMeta);
        IDBBackend     iBackend = mem_Backend ();

        iBackend.createDB   (aRowInfo);
        iBackend.createTable(aRowInfo);
    }

    //--------------------------------------------------------------------------
    @Override
    public synchronized < TEntity extends IEntity > void removeEntitySchema(Class< TEntity > aType)
        throws Exception
    {
        EntityMetaInfo aMeta    = mem_MetaProvider ().getMetaInfoForEntityClass(aType);
        Row            aRowInfo = new Row (aMeta);
        IDBBackend     iBackend = mem_Backend ();

        iBackend.removeTable(aRowInfo);
        // do not remove DB as we dont know how many entities are bound to it ;-(
    }
    
    //--------------------------------------------------------------------------
    private String impl_generateId (IEntity aEntity)
        throws Exception
    {
        EntityMetaInfo aMeta = mem_MetaProvider ().getMetaInfoForEntityClass(aEntity.getClass());
        String         sId   = IdStrategy.newId(aMeta.getIdGenerationStrategy());
        return sId;
    }

    //--------------------------------------------------------------------------
    private boolean impl_isFeatureSpecialRemove ()
    	throws Exception
    {
    	      PersistenceUnit aPU                    = mem_PersistenceUnit();
    	final boolean         DEFAULT_SPECIAL_REMOVE = false;
    	      boolean         bState                 = aPU.getBooleanProperty(PersistenceUnitConst.FEATURE_SPECIAL_REMOVE, DEFAULT_SPECIAL_REMOVE);
    	return bState;
    }
    
    //--------------------------------------------------------------------------
    private static < TEntity extends IEntity > boolean impl_isRemoved (TEntity aEntity)
    	throws Exception
    {
        return ((EntityBase)aEntity).isRemoved();
    }
    
    //--------------------------------------------------------------------------
    private static boolean impl_isRemoved (Row aRow)
    	throws Exception
    {
    	return ((Boolean) aRow.getColumnValue(EntityBase.ATTRIBUTE_NAME_REMOVED)).booleanValue();
    }

    //--------------------------------------------------------------------------
    /** provides access to member m_aEntityProvider.
     *
     *  If those member was not initialized or "cleared" before ...
     *  we create a new one. Those instance will be created and(!) initialized.
     *  So we can make sure a valid unit will be returned where even it's
     *  configuration seems to be valid.
     *
     *  @return the one time created and initialized entity provider instance.
     *
     *  @throws Exception in case entity provider couldnt be created
     *          successfull ... e.g. because corresponding configuration
     *          seems to be invalid.
     */
    private EntityMetaInfoProvider mem_MetaProvider ()
        throws Exception
    {
        if (m_aMetaProvider == null)
        {
            EntityMetaInfoProvider aProvider = new EntityMetaInfoProvider ();
            PersistenceUnit        aUnit     = mem_PersistenceUnit ();

            aProvider.setPersistenceUnit(aUnit);
            aProvider.retrieveMetaInfo  ();

            m_aMetaProvider = aProvider;
        }
        return m_aMetaProvider;
    }

    //--------------------------------------------------------------------------
    private PersistenceUnit mem_PersistenceUnit ()
        throws Exception
    {
        if (m_aPersistenceUnit == null)
        {
            PersistenceUnit aUnit = new PersistenceUnit ();
            aUnit.setName (m_sPersistenceUnit);
            PersistenceXml.readXml(aUnit);

            m_aPersistenceUnit = aUnit;
        }
        return m_aPersistenceUnit;
    }

    //--------------------------------------------------------------------------
	private IDBBackend mem_Backend ()
        throws Exception
    {
        if (m_iBackend == null)
        {
            EntityMetaInfoProvider aMetaProvider    = mem_MetaProvider ();
            PersistenceUnit        aPersistenceUnit = aMetaProvider.getPersistenceUnit();

            String     sProviderClass = aPersistenceUnit.getProvider();
            Class< ? >      aClass         = Class.forName(sProviderClass);
            IDBBackend iBackend       = (IDBBackend) aClass.newInstance();

            iBackend.setEntityMetaInfoProvider (aMetaProvider);

            m_iBackend = iBackend;
        }
        return m_iBackend;
    }

    //--------------------------------------------------------------------------
    private Map< String, DBQueryTemplate< ? > > mem_QueryPool ()
    	throws Exception
    {
    	if (m_lQueryPool == null)
    		m_lQueryPool = new HashMap< String, DBQueryTemplate< ? > >(10);
    	return m_lQueryPool;
    }

    //--------------------------------------------------------------------------
    private String m_sPersistenceUnit = null;

    //--------------------------------------------------------------------------
    private PersistenceUnit m_aPersistenceUnit = null;

    //--------------------------------------------------------------------------
    /** those provider knows all configured entity classes
     *  and her list of attributes.
     */
    private EntityMetaInfoProvider m_aMetaProvider = null;

    //--------------------------------------------------------------------------
    private IDBBackend m_iBackend = null;

    //--------------------------------------------------------------------------
    private Map< String, DBQueryTemplate< ? > > m_lQueryPool = null;
}
