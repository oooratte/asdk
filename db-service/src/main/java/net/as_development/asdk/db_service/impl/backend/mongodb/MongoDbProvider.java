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
package net.as_development.asdk.db_service.impl.backend.mongodb;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.as_development.asdk.api.db.BetweenQueryRange;
import net.as_development.asdk.api.db.EQueryPartBinding;
import net.as_development.asdk.api.db.EQueryPartOperation;
import net.as_development.asdk.api.db.IPersistenceUnit;
import net.as_development.asdk.db_service.IDBBackend;
import net.as_development.asdk.db_service.IDBBackendQuery;
import net.as_development.asdk.db_service.impl.Column;
import net.as_development.asdk.db_service.impl.EntityMetaInfoProvider;
import net.as_development.asdk.db_service.impl.NextToken;
import net.as_development.asdk.db_service.impl.PersistenceUnit;
import net.as_development.asdk.db_service.impl.PersistenceUnitConst;
import net.as_development.asdk.db_service.impl.QueryPart;
import net.as_development.asdk.db_service.impl.QueryPartValue;
import net.as_development.asdk.db_service.impl.Row;

import org.apache.commons.lang3.StringUtils;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.Mongo;

//==============================================================================
/**
 * TODO document me
 */
public class MongoDbProvider implements IDBBackend
{
    //--------------------------------------------------------------------------
    /// property within persistence unit which define the MongoDB server ip
    public static final String PUPROP_DB_SERVER = "mongodb.server";
    
    //--------------------------------------------------------------------------
    /// property within persistence unit which define the MongoDB server port
    public static final String PUPROP_DB_PORT = "mongodb.port";
    
    //--------------------------------------------------------------------------
    private static final int INDEX_ASCENDING = 1;
    
    //--------------------------------------------------------------------------
    //private static final int INDEX_DESCENDING = -1;
    
    //--------------------------------------------------------------------------
    private static final String MONGO_OPERATION_OR = "$or";
    
    //--------------------------------------------------------------------------
    private static final String MONGO_OPERATION_REGEX = "$regex";
    
    //--------------------------------------------------------------------------
    private static final String MONGO_OPERATION_GREATER_THEN_OR_EQUAL_TO = "$gte";
    
    //--------------------------------------------------------------------------
    private static final String MONGO_OPERATION_LESS_THEN_OR_EQUAL_TO = "$lte";
    
    //--------------------------------------------------------------------------
    /** create new instance.
     */
    public MongoDbProvider ()
    {}

    //--------------------------------------------------------------------------
    @Override
    public void setEntityMetaInfoProvider(EntityMetaInfoProvider aProvider)
        throws Exception
    {
        m_aMetaProvider = aProvider;
    }

    //--------------------------------------------------------------------------
    @Override
    public void createDB (Row aMeta)
        throws Exception
    {
    	throw new UnsupportedOperationException ("Not implemented yet.");
    }

    //--------------------------------------------------------------------------
    @Override
    public void createTable(Row aMeta)
        throws Exception
    {
        String        sTable    = aMeta.getTable();
        DBCollection  aTable    = impl_getTable (sTable);
        String        sIdColumn = aMeta.getIdColumn();
        
        // TODO check existing index fields before creating new ones .-)
        BasicDBObject aIndex = new BasicDBObject ();
        aIndex.put   (sIdColumn, MongoDbProvider.INDEX_ASCENDING);
        aIndex.append("unique"  , "true");
        //aIndex.append("dropDups", "true"); this drops ALL .. why ? .-(
        
        aTable.ensureIndex(aIndex);
    }

    //--------------------------------------------------------------------------
    @Override
    public void removeTable(Row aMeta)
        throws Exception
    {
        String       sTable = aMeta.getTable();
        DBCollection aTable = impl_getTable (sTable);
        aTable.drop();
    }
    
    //--------------------------------------------------------------------------
    @Override
    public void insertRows(List< Row > lRows)
        throws Exception
    {
        for (Row aRow : lRows)
        {
            String        sTable = aRow.getTable();
            DBCollection  aTable = impl_getTable (sTable);
            BasicDBObject aNew   = impl_toMongo (aRow);
            
            aTable.insert(aNew);
            aRow.getPersistentStateHandler().setPersistent();
        }
    }
    
    //--------------------------------------------------------------------------
    @Override
    public void updateRows(List< Row > lRows)
        throws Exception
    {
        for (Row aRow : lRows)
        {
            String        sTable = aRow.getTable();
            DBCollection  aTable = impl_getTable (sTable);
            BasicDBObject aOld   = new BasicDBObject ();
            BasicDBObject aNew   = impl_toMongo (aRow);
            
            aOld.put(aRow.getIdColumn(), aRow.getIdValue());
            aTable.update(aOld, aNew);
            
            aRow.getPersistentStateHandler().setPersistent();
        }
    }

    //--------------------------------------------------------------------------
    @Override
    public void deleteRows(List< Row > lRows)
        throws Exception
    {
        for (Row aRow : lRows)
        {
            String        sTable     = aRow.getTable();
            DBCollection  aTable     = impl_getTable (sTable);
            BasicDBObject aRemovable = new BasicDBObject ();
            
            aRemovable.put(aRow.getIdColumn(), aRow.getIdValue());
            aTable.remove(aRemovable);
            
            aRow.getPersistentStateHandler().setTransient();
        }
    }

    //--------------------------------------------------------------------------
    @Override
    public void deleteAllRows(Row aRow)
        throws Exception
    {
        throw new UnsupportedOperationException ("Not implemented yet.");
    }

    //--------------------------------------------------------------------------
	@Override
    public void getRowById(Row aRow)
        throws Exception
    {
        String        sTable   = aRow.getTable();
        DBCollection  aTable   = impl_getTable (sTable);
        BasicDBObject aIdQuery = new BasicDBObject ();
        
        aIdQuery.put(aRow.getIdColumn(), aRow.getIdValue());
        BasicDBObject aResult = (BasicDBObject) aTable.findOne(aIdQuery);
        
        if (aResult == null)
            return;
        
        impl_fromMongo(aResult, aRow);
        aRow.getPersistentStateHandler().setPersistent();
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
    @SuppressWarnings("unchecked")
    @Override
	public String queryRows(Row             aMeta     ,
	                        String          sNextToken,
	                        List< Row >     lResults  ,
	                        IDBBackendQuery iQuery    )
    	throws Exception
    {
        String           sTable      = aMeta.getTable();
        DBCollection     aTable      = impl_getTable (sTable);
        BasicDBObject    aQuery      = new BasicDBObject ();
        QueryPartValue[] lValueParts = iQuery.getQueryParts();
        BasicDBObject    aFieldRef   = impl_toMongoFieldRef (aMeta);
        
        for (QueryPartValue aValuePart : lValueParts)
        {
            QueryPart           aPart      = aValuePart.getPart();
            EQueryPartBinding   eBinding   = aPart.getLogicBinding();
            EQueryPartOperation eOperation = aPart.getOperation();
            String              sColumn    = aPart.getColumn();
            Class< ? >          aType      = aMeta.getColumnType(sColumn);
            Object              aValue     = aValuePart.getValue();
            
            if (eBinding == EQueryPartBinding.E_AND)
            {
                if (aQuery.get(sColumn) != null)
                    throw new IllegalArgumentException ("Query using AND operator makes no sense for more then one query value.");
                
                if (eOperation == EQueryPartOperation.E_MATCH)
                {
                    String sValue = DatatypeConvert.toString(aType, aValue, false);
                    aQuery.put(sColumn, sValue);
                }
                else
                if (eOperation == EQueryPartOperation.E_LIKE)
                {
                    String        sValue = DatatypeConvert.toString(aType, aValue, false);
                    String        sRegEx = sValue;
                                  sRegEx = StringUtils.replace(sRegEx, "*", ".*");
                                  sRegEx = StringUtils.replace(sRegEx, "?", "." );
                    BasicDBObject aLike  = new BasicDBObject (MongoDbProvider.MONGO_OPERATION_REGEX, sRegEx);
                    aQuery.put(sColumn, aLike);
                }
                else
                if (eOperation == EQueryPartOperation.E_BETWEEN)
                {
                    BetweenQueryRange aRange   = (BetweenQueryRange) aValue;
                    String            sMin     = DatatypeConvert.toString(aType, aRange.MinRange, false);
                    String            sMax     = DatatypeConvert.toString(aType, aRange.MaxRange, false);
                    BasicDBObject     aBetween = new BasicDBObject ();
                    aBetween.put(MongoDbProvider.MONGO_OPERATION_GREATER_THEN_OR_EQUAL_TO, sMin);                    
                    aBetween.put(MongoDbProvider.MONGO_OPERATION_LESS_THEN_OR_EQUAL_TO   , sMax);                    
                    aQuery.put(sColumn, aBetween);
                }
            }
            else
            {
                List< Object > lOrParts = (List< Object >) aQuery.get(MongoDbProvider.MONGO_OPERATION_OR);
                if (lOrParts == null)
                {
                    lOrParts = new ArrayList< Object >();
                    aQuery.put(MongoDbProvider.MONGO_OPERATION_OR, lOrParts);
                }
                
                if (eOperation == EQueryPartOperation.E_MATCH)
                {
                    String        sValue = DatatypeConvert.toString(aType, aValue, false);
                    BasicDBObject aMatch = new BasicDBObject (sColumn, sValue);
                    lOrParts.add(aMatch);
                }
                else
                if (eOperation == EQueryPartOperation.E_LIKE)
                {
                    String        sValue = DatatypeConvert.toString(aType, aValue, false);
                    String        sRegEx = sValue;
                                  sRegEx = StringUtils.replace(sRegEx, "*", ".*");
                                  sRegEx = StringUtils.replace(sRegEx, "?", "." );
                    BasicDBObject aLike  = new BasicDBObject (MongoDbProvider.MONGO_OPERATION_REGEX, sRegEx);
                    lOrParts.add(aLike);
                }
                else
                if (eOperation == EQueryPartOperation.E_BETWEEN)
                {
                    BetweenQueryRange aRange   = (BetweenQueryRange) aValue;
                    String            sMin     = DatatypeConvert.toString(aType, aRange.MinRange, false);
                    String            sMax     = DatatypeConvert.toString(aType, aRange.MaxRange, false);
                    BasicDBObject     aBetween = new BasicDBObject ();
                    aBetween.put(MongoDbProvider.MONGO_OPERATION_GREATER_THEN_OR_EQUAL_TO, sMin);                    
                    aBetween.put(MongoDbProvider.MONGO_OPERATION_LESS_THEN_OR_EQUAL_TO   , sMax);                    
                    lOrParts.add(aBetween);
                }
            }
        }
        
        NextToken aNextToken  = new NextToken (sNextToken);
        int       nOffset     = aNextToken.getOffset  ();
        int       nLimit      = aNextToken.getPageSize();
        int       nMaxResults = 0;
        DBCursor  aResultSet  = null;
        
        try
        {
            aResultSet = aTable.find(aQuery, aFieldRef, nOffset, nLimit);
            
            if (aResultSet != null)
            {
                // max results knows ALL possible results independent
                // from current offset and page size !
                nMaxResults = aResultSet.count();
                
                while (aResultSet.hasNext())
                {
                    BasicDBObject aResult    = (BasicDBObject) aResultSet.next();
                    Row           aResultRow = aMeta.newRow();
                    
                    impl_fromMongo(aResult, aResultRow);
                    aResultRow.getPersistentStateHandler().setPersistent();
                    lResults.add(aResultRow);
                }
            }
        }
        finally
        {
            if (aResultSet != null)
                aResultSet.close();
        }

        return aNextToken.stepNext(nMaxResults);
    }
    
    //--------------------------------------------------------------------------
    private BasicDBObject impl_toMongoFieldRef (Row aSource)
        throws Exception
    {
        BasicDBObject      aTarget  = new BasicDBObject ();
        Iterator< String > pColumns = aSource.listColumns();
        
        while (pColumns.hasNext())
        {
            String sColumn = pColumns.next();
            aTarget.put(sColumn, 1);
        }
        
        return aTarget;
    }
    
    //--------------------------------------------------------------------------
    private BasicDBObject impl_toMongo (Row aSource)
        throws Exception
    {
        BasicDBObject      aTarget  = new BasicDBObject ();
        Iterator< String > pColumns = aSource.listColumns();
        
        while (pColumns.hasNext())
        {
            String sColumn = pColumns.next();
            Column aColumn = aSource.getColumn(sColumn);
            String sValue  = DatatypeConvert.toString(aColumn.Type, aColumn.Value, false);
            
            aTarget.put(sColumn, sValue);
        }
        
        return aTarget;
    }
    
    //--------------------------------------------------------------------------
    private void impl_fromMongo (BasicDBObject aSource,
                                 Row           aTarget)
        throws Exception
    {
        Iterator< String > pColumns = aSource.keySet().iterator();
        while (pColumns.hasNext())
        {
            String     sColumn = pColumns.next();
            Column     aColumn = aTarget.getColumn(sColumn);
            
            if (aColumn == null)
                continue;
            
            aColumn.Value = DatatypeConvert.fromString(aColumn.Type, aSource.getString(sColumn), false);
        }
    }
    
    //--------------------------------------------------------------------------
    private DBCollection impl_getTable (String sTable)
        throws Exception
    {
        return mem_DB ().getCollection(sTable);
    }
    
    //--------------------------------------------------------------------------
    /*
    public void impl_DbgList (Row aMeta)
        throws Exception
    {
        DB aDB = mem_DB ();
        System.out.println("tables = "+aDB.getCollectionNames());
        
        String        sTable = aMeta.getTable();
        DBCollection  aTable = impl_getTable (sTable);
        
        System.out.println ("dump table '"+sTable+"' ...");
        System.out.println ("count = "+aTable.count());
        
        DBCursor p = aTable.find();
        int i = 0;
        while (p.hasNext())
            System.out.println ("["+(i++)+"] "+p.next());
    }
    */
    
    //--------------------------------------------------------------------------
    private Mongo mem_Connection ()
        throws Exception
    {
        if (m_aConnection == null)
        {
            PersistenceUnit aConfig = m_aMetaProvider.getPersistenceUnit();
            String          sServer = aConfig.getProperty(MongoDbProvider.PUPROP_DB_SERVER);
            String          sPort   = aConfig.getProperty(MongoDbProvider.PUPROP_DB_PORT  );

            if (StringUtils.isEmpty (sServer))
                throw new RuntimeException ("Miss MongoDB server name. Please configure one inside peristence.xml.");
            if (StringUtils.isEmpty (sPort))
                throw new RuntimeException ("Miss MongoDB server port. Please configure one inside peristence.xml.");

            int   nPort  = Integer.parseInt(sPort);
            Mongo aMongo = new Mongo (sServer, nPort);
            
            m_aConnection = aMongo;
        }
        return m_aConnection;
    }

    //--------------------------------------------------------------------------
    private DB mem_DB ()
        throws Exception
    {
        if (m_aDB == null)
        {
            PersistenceUnit aConfig     = m_aMetaProvider.getPersistenceUnit();
            String          sSchema     = aConfig.getProperty(PersistenceUnitConst.DB_SCHEMA);
            Mongo           aConnection = mem_Connection ();
            DB              aDB         = aConnection.getDB(sSchema);
            
            m_aDB = aDB;
        }
        return m_aDB;
    }
    
    //--------------------------------------------------------------------------
    private Mongo m_aConnection = null;

    //--------------------------------------------------------------------------
    private DB m_aDB = null;
    
    //--------------------------------------------------------------------------
    //private static boolean m_bScrambleData = true;

    //--------------------------------------------------------------------------
    private EntityMetaInfoProvider m_aMetaProvider = null;
}
