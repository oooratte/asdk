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
import java.util.Iterator;
import java.util.List;

import net.as_development.asdk.api.db.BetweenQueryRange;
import net.as_development.asdk.api.db.EQueryPartOperation;
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

import org.apache.commons.lang3.StringUtils;

//==============================================================================
/**
 */
public class SqlProvider implements IDBBackend
{
    //--------------------------------------------------------------------------
    /** create new instance.
     */
    public SqlProvider ()
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
    public void createTable(Row aMeta)
        throws Exception
    {
        PreparedStatement aSql = impl_getSql (ISqlGenerator.EStatementType.E_CREATE_TABLE, aMeta, null);
        aSql.execute ();
    }

    //--------------------------------------------------------------------------
    @Override
    public void removeTable(Row aMeta)
        throws Exception
    {
        try
        {
            PreparedStatement aSql = impl_getSql (ISqlGenerator.EStatementType.E_REMOVE_TABLE, aMeta, null);
            aSql.execute ();
        }
        catch (SQLException exSql)
        {
            ISqlGenerator.ESqlError eError = mem_SqlGenerator ().mapSqlExceptionToError(exSql);
            if (eError != ISqlGenerator.ESqlError.E_RESOURCE_DO_NOT_EXISTS)
                throw new SQLException (exSql);
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

            aSql.executeUpdate();
            aRow.getPersistentStateHandler().setPersistent();
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

            	// @todo ignore all further columns where meta say they are not allowed within update !

                mem_SqlGenerator ().setValueOnPreparedStatement (aSql, nColumn, aType, aValue);
                ++nColumn;
            }

            Class< ? > aType   = aRow.getIdType ();
            Object     aValue  = aRow.getIdValue();
            mem_SqlGenerator ().setValueOnPreparedStatement (aSql, nColumn, aType, aValue);

            aSql.executeUpdate();
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
            PreparedStatement aSql     = impl_getSql (ISqlGenerator.EStatementType.E_DELETE, aRow, null);
            Class< ? >        aIdType  = aRow.getIdType();
            Object            aIdValue = aRow.getIdValue();

            mem_SqlGenerator ().setValueOnPreparedStatement (aSql, 1, aIdType, aIdValue);

            aSql.executeUpdate();
            aRow.getPersistentStateHandler().setTransient();
        }
    }

    //--------------------------------------------------------------------------
    @Override
    public void deleteAllRows(Row aRowInfo)
        throws Exception
    {
        PreparedStatement aSql = impl_getSql (ISqlGenerator.EStatementType.E_DELETE_ALL, aRowInfo, null);
        aSql.executeUpdate();
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
     		           sValue = StringUtils.replaceChars(sValue, '*', '%'); // @todo use db-dialect-config (or something similar) to know which chars has to be used here for which real DB .-)
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
    	SqlStatementCache aSqlCache = mem_SqlCache ();
        PreparedStatement aSql      = null;
        String 			  sCacheId  = aSqlCache.buildCacheId(eStatement, aMetaRow, iQuery);

        if (aSqlCache.containsKey (sCacheId))
        {
        	aSql = aSqlCache.get (sCacheId);
            if (aSql.isClosed ())
                aSql = null;
        }
        
        if (aSql == null)
        {
            String sSql = mem_SqlGenerator ().createSql(eStatement, aMetaRow, iQuery);
                   aSql = mem_Connection   ().prepareStatement(sSql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            aSql.setPoolable(true);
            aSqlCache.put(sCacheId, aSql);
        }

        aSql.clearParameters();
        return aSql;
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
     *  @note	Don't forget to clean up our internal SQL statement cache.
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
    		// @todo get timeout from anywhere else ...
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
    	// will reset member m_aConnection to null ... might be .-)
    	impl_resetConnectionIfBroken ();

        if (m_aConnection == null)
        {
            PersistenceUnit aPu       = m_aMetaProvider.getPersistenceUnit();
            String          sDriver   = aPu.getProperty(PersistenceUnitConst.JDBC_DRIVER       );
            String          sUrl      = aPu.getProperty(PersistenceUnitConst.JDBC_CONNECTIONURL);
            String          sUser     = aPu.getProperty(PersistenceUnitConst.DB_USER         );
            String          sPassword = aPu.getProperty(PersistenceUnitConst.DB_PASSWORD     );

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
            m_iSqlGenerator = new AnsiSqlGenerator ();
        return m_iSqlGenerator;
    }
    
    //--------------------------------------------------------------------------
    private Connection m_aConnection = null;

    //--------------------------------------------------------------------------
    private EntityMetaInfoProvider m_aMetaProvider = null;

    //--------------------------------------------------------------------------
    private SqlStatementCache m_aSqlCache = null;
    
    //--------------------------------------------------------------------------
    private ISqlGenerator m_iSqlGenerator = null;
}
