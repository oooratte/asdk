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
package net.as_development.asdk.db_service.impl.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import net.as_development.asdk.api.db.BetweenQueryRange;
import net.as_development.asdk.api.db.EQueryPartOperation;
import net.as_development.asdk.api.db.IDBUser;
import net.as_development.asdk.db_service.IDBBackend;
import net.as_development.asdk.db_service.IDBBackendQuery;
import net.as_development.asdk.db_service.ISqlGenerator;
import net.as_development.asdk.db_service.impl.EntityMetaInfoProvider;
import net.as_development.asdk.db_service.impl.NextToken;
import net.as_development.asdk.db_service.impl.PersistenceUnit;
import net.as_development.asdk.db_service.impl.PersistenceUnitConst;
import net.as_development.asdk.db_service.impl.QueryPart;
import net.as_development.asdk.db_service.impl.QueryPartValue;
import net.as_development.asdk.db_service.impl.Row;
import net.as_development.asdk.db_service.impl.sql.generator.AnsiSqlGenerator;

//==============================================================================
/**
 */
public class SqlProvider implements IDBBackend
								  , IDBUser
{
    //--------------------------------------------------------------------------
    /** create new instance.
     */
    public SqlProvider ()
    {}

    //--------------------------------------------------------------------------
    public void setSqlGenerator (final Class< ? extends ISqlGenerator > aImplClass)
        throws Exception
    {
    	m_aSqlGeneratorImpl = aImplClass;
    }
    
    //--------------------------------------------------------------------------
    @Override
    public void setEntityMetaInfoProvider(EntityMetaInfoProvider aProvider)
        throws Exception
    {
        m_aMetaProvider = aProvider;
    }

    //-------------------------------------------------------------------------
    @Override
    public synchronized void createUser (final String    sName                ,
							    		 final String    sPassword            ,
							    		 final boolean   bAdministrativeRights,
							    		 final boolean   bAllowRemote         ,
							    		 final String... lSchemas             )
		throws Exception
	{
    	final boolean bUserExists = impl_isUser (sName, bAllowRemote);
    	if (bUserExists)
    		return;
    	
    	final Map< String, Object > lArgs = new HashMap< String, Object > ();
    	lArgs.put(AnsiSqlGenerator.ARG_CREATE_USER_NAME                 , sName                );
    	lArgs.put(AnsiSqlGenerator.ARG_CREATE_USER_PASSWORD             , sPassword            );
    	lArgs.put(AnsiSqlGenerator.ARG_CREATE_USER_ADMINISTRATIVE_RIGHTS, bAdministrativeRights);
    	lArgs.put(AnsiSqlGenerator.ARG_CREATE_USER_DB_SCHEMAS           , lSchemas             );
    	lArgs.put(AnsiSqlGenerator.ARG_CREATE_USER_ALLOW_REMOTE         , bAllowRemote         );
    	
    	final List< String > lSqls = mem_SqlGenerator ().createSql (ISqlGenerator.EStatementType.E_CREATE_USER, lArgs);
    	for (final String sSql : lSqls)
    	{
    		PreparedStatement aSql = null;
            try
            {
            	aSql = mem_Connection ().prepareStatement(sSql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            	aSql.execute();
            }
            finally
            {
            	impl_closeStatementIfNotCached (aSql);
            }
    	}
	}

	//-------------------------------------------------------------------------
    private boolean impl_isUser (final String  sName       ,
   		 						 final boolean bAllowRemote)
    	throws Exception
    {
    	final Map< String, Object > lArgs = new HashMap< String, Object > ();
    	lArgs.put(AnsiSqlGenerator.ARG_CREATE_USER_NAME        , sName       );
    	lArgs.put(AnsiSqlGenerator.ARG_CREATE_USER_ALLOW_REMOTE, bAllowRemote);
    	
    	final List< String > lSqls = mem_SqlGenerator ().createSql (ISqlGenerator.EStatementType.E_QUERY_USER, lArgs);
    	for (final String sSql : lSqls)
    	{
    		PreparedStatement aSql    = null;
    		ResultSet         aResult = null;
            try
            {
            	aSql    = mem_Connection ().prepareStatement(sSql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            	aResult = aSql.executeQuery();
            	
    	        if (
		            (aResult != null) &&
		            (aResult.next() )
		           )
		        {
    	        	if ( ! aResult.getBoolean(1))
    	        		return false;
		        }
            }
            finally
            {
            	if (aResult != null)
                    aResult.close();
            	impl_closeStatementIfNotCached (aSql);
            }
    	}
    	
    	return true;
    }
    
	//-------------------------------------------------------------------------
    @Override
	public synchronized void removeUser (final String sName)
		throws Exception
	{
    	throw new UnsupportedOperationException ("not implemented yet");
	}

    //--------------------------------------------------------------------------
    @Override
    public void createDB (Row aMeta)
        throws Exception
    {
        final PreparedStatement aSql = impl_getSql (ISqlGenerator.EStatementType.E_CREATE_SCHEMA, aMeta, null);
        try
        {
        	aSql.executeUpdate ();
        }
        finally
        {
        	impl_closeStatementIfNotCached (aSql);
        }
    }

    //--------------------------------------------------------------------------
    @Override
    public void createTable(Row aMeta)
        throws Exception
    {
        PreparedStatement aSql = impl_getSql (ISqlGenerator.EStatementType.E_CREATE_TABLE, aMeta, null);
        try
        {
        	aSql.execute ();
        }
        finally
        {
        	impl_closeStatementIfNotCached (aSql);
        }
    }

    //--------------------------------------------------------------------------
    @Override
    public void removeTable(Row aMeta)
        throws Exception
    {
        PreparedStatement aSql = impl_getSql (ISqlGenerator.EStatementType.E_REMOVE_TABLE, aMeta, null);
        try
        {
            aSql.execute ();
        }
        catch (SQLException exSql)
        {
            ISqlGenerator.ESqlError eError = mem_SqlGenerator ().mapSqlExceptionToError(exSql);
            if (eError != ISqlGenerator.ESqlError.E_RESOURCE_DO_NOT_EXISTS)
                throw new SQLException (exSql);
        }
	    finally
	    {
	    	impl_closeStatementIfNotCached (aSql);
	    }
    }
    
    //--------------------------------------------------------------------------
	@Override
    public void insertRows(List< Row > lRows)
        throws Exception
    {
        for (Row aRow : lRows)
        {
            PreparedStatement  aSql     = impl_getSql (ISqlGenerator.EStatementType.E_INSERT, aRow, null);
            Iterator< String > pColumns = aRow.listColumns();
            int                nColumn  = 1;

            while (pColumns.hasNext())
            {
                String     sColumn = pColumns.next();
                Class< ? > aType   = aRow.getColumnType(sColumn);
                Object     aValue  = aRow.getColumnValue(sColumn);

                mem_SqlGenerator ().setValueOnPreparedStatement (aSql, nColumn, aType, aValue);
                ++nColumn;
            }

            try
            {
	            aSql.executeUpdate();
	            aRow.getPersistentStateHandler().setPersistent();
            }
    	    finally
    	    {
    	    	impl_closeStatementIfNotCached (aSql);
    	    }
        }
    }

    //--------------------------------------------------------------------------
	@Override
    public void getRowById(Row aRow)
        throws Exception
    {
        Class< ? > aIdType  = aRow.getIdType ();
        Object     aIdValue = aRow.getIdValue();

        PreparedStatement aSql = impl_getSql (ISqlGenerator.EStatementType.E_QUERY_BY_ID, aRow, null);
        mem_SqlGenerator ().setValueOnPreparedStatement (aSql, 1, aIdType, aIdValue);

        ResultSet aResult = null;
        try
        {
	        aResult = aSql.executeQuery();
	        if (
	            (aResult != null) &&
	            (aResult.next() )
	           )
	        {
	            impl_mapResultSetToRow (aResult, aRow);
	            aRow.getPersistentStateHandler().setPersistent();
	        }
        }
        finally
        {
            if (aResult != null)
                aResult.close();
	    	impl_closeStatementIfNotCached (aSql);
        }
    }
	
    //--------------------------------------------------------------------------
    @Override
    public String getAllRows (Row         aMeta     ,
                              String      sNextToken,
                              List< Row > lResults  )
        throws Exception
    {
        PreparedStatement aSql = impl_getSql (ISqlGenerator.EStatementType.E_GET_ALL, aMeta, null);
        return impl_executeQuery (aSql, aMeta, sNextToken, lResults);
    }

    //--------------------------------------------------------------------------
    @Override
    public void updateRows(List< Row > lRows)
        throws Exception
    {
        for (Row aRow : lRows)
        {
            PreparedStatement  aSql      = impl_getSql (ISqlGenerator.EStatementType.E_UPDATE, aRow, null);
            Iterator< String > pColumns  = aRow.listColumns();
            int                nColumn   = 1;
            String             sIdColumn = aRow.getIdColumn();

            while (pColumns.hasNext())
            {
                String     sColumn = pColumns.next();
                Class< ? > aType   = aRow.getColumnType(sColumn);
                Object     aValue  = aRow.getColumnValue(sColumn);

                if (StringUtils.equals(sColumn, sIdColumn))
                	continue;

            	// TODO ignore all further columns where meta say they are not allowed within update !

                mem_SqlGenerator ().setValueOnPreparedStatement (aSql, nColumn, aType, aValue);
                ++nColumn;
            }

            Class< ? > aType   = aRow.getIdType ();
            Object     aValue  = aRow.getIdValue();
            mem_SqlGenerator ().setValueOnPreparedStatement (aSql, nColumn, aType, aValue);

            try
            {
	            aSql.executeUpdate();
	            aRow.getPersistentStateHandler().setPersistent();
	        }
		    finally
		    {
		    	impl_closeStatementIfNotCached (aSql);
		    }
        }
    }

    //--------------------------------------------------------------------------
	@Override
    public void deleteRows(List< Row > lRows)
        throws Exception
    {
        for (Row aRow : lRows)
        {
            PreparedStatement aSql     = impl_getSql (ISqlGenerator.EStatementType.E_DELETE, aRow, null);
            Class< ? >        aIdType  = aRow.getIdType();
            Object            aIdValue = aRow.getIdValue();

            mem_SqlGenerator ().setValueOnPreparedStatement (aSql, 1, aIdType, aIdValue);

            try
            {
	            aSql.executeUpdate();
	            aRow.getPersistentStateHandler().setTransient();
            }
    	    finally
    	    {
    	    	impl_closeStatementIfNotCached (aSql);
    	    }
        }
    }

    //--------------------------------------------------------------------------
    @Override
    public void deleteAllRows(Row aRowInfo)
        throws Exception
    {
        PreparedStatement aSql = impl_getSql (ISqlGenerator.EStatementType.E_DELETE_ALL, aRowInfo, null);
        try
        {
        	aSql.executeUpdate();
        }
	    finally
	    {
	    	impl_closeStatementIfNotCached (aSql);
	    }
    }

    //--------------------------------------------------------------------------
	@Override
	public String queryRows(Row             aMetaRow  ,
	                        String          sNextToken,
	                        List< Row >     lResults  ,
	                        IDBBackendQuery iQuery    )
    	throws Exception
    {
        PreparedStatement aSql         = impl_getSql (ISqlGenerator.EStatementType.E_QUERY_BY_PROPS, aMetaRow, iQuery);
        QueryPartValue[]  lParts       = iQuery.getQueryParts();
        int               nPlaceHolder = 1;
        int               nPartIndex   = 0;
        int               c            = lParts.length;

        for (nPartIndex=0; nPartIndex<c; ++nPartIndex)
        {
        	QueryPartValue      aValuePart = lParts[nPartIndex];
        	QueryPart           aPart      = aValuePart.getPart();
        	String              sColumn    = aPart.getColumn();
            Class< ? >          aType      = aMetaRow.getColumnType(sColumn);
        	Object              aValue     = aValuePart.getValue();
        	EQueryPartOperation eOperation = aPart.getOperation();

        	if (eOperation == EQueryPartOperation.E_MATCH)
        	{
        		mem_SqlGenerator ().setValueOnPreparedStatement (aSql, nPlaceHolder++, aType, aValue);
        	}
        	else
        	if (eOperation == EQueryPartOperation.E_LIKE)
        	{
        		String sValue = (String) aValue;
     		           sValue = StringUtils.replaceChars(sValue, '*', '%'); // TODO use db-dialect-config (or something similar) to know which chars has to be used here for which real DB .-)
    		           sValue = StringUtils.replaceChars(sValue, '?', '_');
    		    mem_SqlGenerator ().setValueOnPreparedStatement (aSql, nPlaceHolder++, aType, sValue);
        	}
        	else
        	if (eOperation == EQueryPartOperation.E_BETWEEN)
        	{
        		BetweenQueryRange aRange = (BetweenQueryRange) aValue;
        		mem_SqlGenerator ().setValueOnPreparedStatement (aSql, nPlaceHolder++, aType, aRange.MinRange);
        		mem_SqlGenerator ().setValueOnPreparedStatement (aSql, nPlaceHolder++, aType, aRange.MaxRange);
        	}
        	else
        	if (eOperation == EQueryPartOperation.E_LESS_THAN)
        	{
        		mem_SqlGenerator ().setValueOnPreparedStatement (aSql, nPlaceHolder++, aType, aValue);
        	}
        	else
        	if (eOperation == EQueryPartOperation.E_GREATER_THAN)
        	{
        		mem_SqlGenerator ().setValueOnPreparedStatement (aSql, nPlaceHolder++, aType, aValue);
        	}
        	else
        		throw new RuntimeException ("Unknown operation. Did you extend enum EQueryPartOperation but forgot to change this line of code ?");
        }

        return impl_executeQuery (aSql, aMetaRow, sNextToken, lResults);
    }

    //--------------------------------------------------------------------------
    @Override
    public String dumpStatement4Create (Row aMeta)
    	throws Exception
    {
    	final List< String > lSqls = mem_SqlGenerator ().createSql(ISqlGenerator.EStatementType.E_CREATE_TABLE, aMeta, null);
    	final StringBuffer   sSql  = new StringBuffer (256);

    	for (final String sNextSql : lSqls)
    	{
    		sSql.append(sNextSql);
    		sSql.append("\n"    );
    	}

    	return sSql.toString();
    }

    //--------------------------------------------------------------------------
	private String impl_executeQuery (PreparedStatement aSql      ,
	                                  Row               aMeta     ,
	                                  String            sNextToken,
	                                  List< Row >       lResults  )
	    throws Exception
	{
        NextToken aNextToken   = new NextToken (sNextToken);
        int       nLimit       = aNextToken.getPageSize();
        int       nOffset      = aNextToken.getOffset();
        int       nMaxResults  = 0;
        ResultSet aResult      = null;

        // Important ! set fetch size here to reach something useful related
        // to the limit we need.
        //
        // But dont use setMaxRows() instead !!!
        //
        // Some lines below we use a tricky piece of code to get the max count of
        // ALL results matching the current query. We step to the end of the result set
        // and get the current row number then. But last row in result set will
        // be not the right one in case setMaxRows() was called .-)
        aSql.setFetchSize(nLimit);
        
        try
        {
            aResult = aSql.executeQuery();
            
            if (aResult != null)
            {
                // If documentation isn't wrong ...
                // absolute() goes to the end of the result set
                // if offset is to high.
                aResult.absolute(nOffset);
                
                // Read as long you can and as long
                // current page isn't full. Decrease limit value
                // to know the fill-state of current page ...
                // It's checked some lines below too to know
                // if there can be a following page and a suitable
                // next token must be generated.
                while (
                       (aResult.next()) &&
                       (nLimit > 0    )
                      )
                {
                    Row aRow = aMeta.newRow();
                    impl_mapResultSetToRow (aResult, aRow);
                    lResults.add (aRow);
                    aRow.getPersistentStateHandler().setPersistent();
                    
                    --nLimit;
                }
                
                // Here is the tricky part where we retrieve the count of
                // ALL possible results matching the current query .-)
                aResult.last();
                nMaxResults = aResult.getRow();
            }
        }
        finally
        {
            if (aResult != null)
                aResult.close ();
        }
        
        return aNextToken.stepNext(nMaxResults);
	}
	
    //--------------------------------------------------------------------------
    private PreparedStatement impl_getSql (ISqlGenerator.EStatementType eStatement,
                                           Row                          aMetaRow  ,
                                           IDBBackendQuery              iQuery    )
        throws Exception
    {
    	boolean           bCacheStatements = impl_hasStatementsToBeCached ();
    	SqlStatementCache aSqlCache        = mem_SqlCache ();
    	Connection        aConnection      = mem_Connection ();
        PreparedStatement aSql             = null;
        String 			  sCacheId         = null;

        if (
        	(bCacheStatements                ) &&
        	(aSqlCache.containsKey (sCacheId))
           )
        {
        	sCacheId = aSqlCache.buildCacheId(eStatement, aMetaRow, iQuery);
        	aSql     = aSqlCache.get (sCacheId);

        	if (aSql.isClosed ())
                aSql = null;
        }
        
        if (aSql == null)
        {
            final List< String > lSqls = mem_SqlGenerator ().createSql(eStatement, aMetaRow, iQuery);
            for (final String sSql : lSqls)
            {
            	if (aSql == null)
            		aSql = aConnection.prepareStatement(sSql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            	else
            		aSql.addBatch(sSql);
            }
            
            if (bCacheStatements)
            {
            	aSql.setPoolable(true);
	            aSqlCache.put(sCacheId, aSql);
            }
            else
            {
            	aSql.setPoolable      (false);
            	aSql.closeOnCompletion(     );
            }
        }

        aSql.clearParameters();
        return aSql;
    }

    //--------------------------------------------------------------------------
	private void impl_closeStatementIfNotCached (final PreparedStatement aSql)
		throws Exception
	{
		if (aSql == null)
			return;
		
    	final boolean bCacheStatements = impl_hasStatementsToBeCached ();
		if ( ! bCacheStatements)
			aSql.close();
	}
	
    //--------------------------------------------------------------------------
	private void impl_mapResultSetToRow (ResultSet aResultSet,
                                         Row       aRow      )
        throws Exception
    {
        Iterator< String > pColumns = aRow.listColumns();

        while (pColumns.hasNext())
        {
            String     sColumn = pColumns.next();
            Class< ? > aType   = aRow.getColumnType(sColumn);
            Object     aValue  = mem_SqlGenerator ().getValueFromResultSet (aResultSet, sColumn, aType);
            aRow.setColumnValue(sColumn, aValue);
        }
    }

    //--------------------------------------------------------------------------
    /** reset the DB connection member in case it seams not be usable any longer.
     *
     *  It checks if the connection is not valid any longer and
     *  set the member m_aConnection to null then.
     *  Next call to mem_Connection () will recreate the connection member ...
     *  .-)
     *
     *  Note	Don't forget to clean up our internal SQL statement cache.
     *          Those PreparedStatement objects are bound to this connection and
     *          will throw exception in case they are used AFTER closing the connection object.
     */
    private void impl_resetConnectionIfBroken ()
    	throws Exception
    {
    	if (m_aConnection == null)
    		return;

		if (m_aConnection.isClosed())
		{
			mem_SqlCache ().clear ();
			m_aConnection = null;
			return;
		}

		try
		{
    		// TODO get timeout from anywhere else ...
    		if ( ! m_aConnection.isValid (5000))
    		{
    			mem_SqlCache ().clear ();
				m_aConnection.close();
    			m_aConnection = null;
    			return;
			}
		}
		catch (Throwable ex)
		{
			m_aConnection = null;
		}
    }

    //--------------------------------------------------------------------------
    private Connection mem_Connection ()
        throws Exception
    {
    	// external connections are not checked if they are closed or broken or ...
    	// It's used as it is !
        PersistenceUnit aPu                 = m_aMetaProvider.getPersistenceUnit();
        Connection      aExternalConnection = aPu.getObjectProperty(PersistenceUnitConst.JDBC_EXTERNAL_CONNECTION);
        if (aExternalConnection != null)
        {
        	aExternalConnection.setAutoCommit(true);
        	return aExternalConnection;
        }

        // will reset member m_aConnection to null ... might be .-)
    	impl_resetConnectionIfBroken ();

        if (m_aConnection == null)
        {
            String          sDriver   = aPu.getStringProperty(PersistenceUnitConst.JDBC_DRIVER       );
            String          sUrl      = aPu.getStringProperty(PersistenceUnitConst.JDBC_CONNECTIONURL);
            String          sUser     = aPu.getStringProperty(PersistenceUnitConst.DB_USER         );
            String          sPassword = aPu.getStringProperty(PersistenceUnitConst.DB_PASSWORD     );

            // load the driver
            // Can be called as often as you want.
            // Driver will be loaded one times only .-)
            JdbcDriver.load(sDriver);

            Connection aConnection = JdbcDriver.getConnection(sUrl, sUser, sPassword);
            aConnection.setAutoCommit(true);
            m_aConnection = aConnection;
        }
        return m_aConnection;
    }

    //--------------------------------------------------------------------------
    private SqlStatementCache mem_SqlCache ()
    	throws Exception
    {
    	if (m_aSqlCache == null)
    		m_aSqlCache = new SqlStatementCache ();
    	return m_aSqlCache;
    }

    //--------------------------------------------------------------------------
    private ISqlGenerator mem_SqlGenerator ()
        throws Exception
    {
        if (m_iSqlGenerator == null)
        {
        	final ISqlGenerator   iGenerator = m_aSqlGeneratorImpl.newInstance();
            final PersistenceUnit aPU        = m_aMetaProvider.getPersistenceUnit();

            if (aPU.hasProperty(PersistenceUnitConst.SQL_IDENTIFIER_QUOTE))
            {
	            final String sIdentifierQuote = aPU.getStringProperty(PersistenceUnitConst.SQL_IDENTIFIER_QUOTE);
	        	iGenerator.setIdentifierQuote(sIdentifierQuote);
            }

            if (aPU.hasProperty(PersistenceUnitConst.SQL_STRING_QUOTE))
            {
            	final String sStringQuote = aPU.getStringProperty(PersistenceUnitConst.SQL_STRING_QUOTE);
            	iGenerator.setStringQuote(sStringQuote);
            }
            
            if (aPU.hasProperty(PersistenceUnitConst.SQL_DUMP_STATEMENTS))
            {
            	final boolean bEnableDumps = aPU.getBooleanProperty(PersistenceUnitConst.SQL_DUMP_STATEMENTS, false);
            	iGenerator.enableSqlDumps(bEnableDumps);
            }

            m_iSqlGenerator = iGenerator;
        }
        return m_iSqlGenerator;
    }
    
    //--------------------------------------------------------------------------
    private boolean impl_hasStatementsToBeCached ()
    	throws Exception
    {
    	if (m_bCacheStatements == null)
    	{
            PersistenceUnit aPu          = m_aMetaProvider.getPersistenceUnit();
            boolean         bEnableCache = aPu.getBooleanProperty(PersistenceUnitConst.JDBC_CACHE_STATEMENTS, true);
            m_bCacheStatements = bEnableCache;
    	}
    	return m_bCacheStatements;
    }
    
    //--------------------------------------------------------------------------
    private Connection m_aConnection = null;

    //--------------------------------------------------------------------------
    private EntityMetaInfoProvider m_aMetaProvider = null;

    //--------------------------------------------------------------------------
    private SqlStatementCache m_aSqlCache = null;
    
    //--------------------------------------------------------------------------
    private Class< ? extends ISqlGenerator > m_aSqlGeneratorImpl = AnsiSqlGenerator.class;
    
    //--------------------------------------------------------------------------
    private ISqlGenerator m_iSqlGenerator = null;

    //--------------------------------------------------------------------------
    private Boolean m_bCacheStatements = null;
}
