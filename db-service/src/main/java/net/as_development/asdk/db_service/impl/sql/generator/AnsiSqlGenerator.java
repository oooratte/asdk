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
package net.as_development.asdk.db_service.impl.sql.generator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import net.as_development.asdk.api.db.EQueryPartBinding;
import net.as_development.asdk.api.db.EQueryPartOperation;
import net.as_development.asdk.db_service.IDBBackendQuery;
import net.as_development.asdk.db_service.ISqlGenerator;
import net.as_development.asdk.db_service.impl.Column;
import net.as_development.asdk.db_service.impl.EntityMetaInfo;
import net.as_development.asdk.db_service.impl.QueryPart;
import net.as_development.asdk.db_service.impl.QueryPartValue;
import net.as_development.asdk.db_service.impl.Row;

//==============================================================================
/**
 *  Generated sql statements are generic ones. They use real table and
 *  column names ... but place holder for values. Those statements can
 *  be used to create e.g. prepared statements then and reuse them by
 *  adding real entity values if required.
 */
public class AnsiSqlGenerator implements ISqlGenerator
{
    //--------------------------------------------------------------------------
    /// we 'simulate' boolean values as integer where FALSE Is set to 0
    public static final int BOOLEAN_FALSE = 0;
    
    //--------------------------------------------------------------------------
    /// we 'simulate' boolean values as integer where TRUE Is set to 1
    public static final int BOOLEAN_TRUE = 1;
    
    //--------------------------------------------------------------------------
    /// string columns will be limited here up to 256 letter
    public static final int MAX_STRING_LENGTH = 256;
    
    //--------------------------------------------------------------------------
    public static final String SQLTYPE_CHAR    = "varchar(1)";
    public static final String SQLTYPE_BOOLEAN = "numeric(1,0)";
    public static final String SQLTYPE_BYTE    = "numeric(3,0)";
    public static final String SQLTYPE_SHORT   = "numeric(5,0)";
    public static final String SQLTYPE_INT     = "numeric(10,0)";
    public static final String SQLTYPE_LONG    = "numeric(19,0)";
    public static final String SQLTYPE_FLOAT   = "float";
    public static final String SQLTYPE_DOUBLE  = "double precision";
    public static final String SQLTYPE_DATE    = AnsiSqlGenerator.SQLTYPE_LONG;
    
    //--------------------------------------------------------------------------
	public static final String ARG_CREATE_USER_NAME                  = "create.user.name"                 ;
	public static final String ARG_CREATE_USER_PASSWORD              = "create.user.password"             ;
	public static final String ARG_CREATE_USER_ADMINISTRATIVE_RIGHTS = "create.user.administrative.rights";
	public static final String ARG_CREATE_USER_DB_SCHEMAS            = "create.user.db.schemas"           ;
	public static final String ARG_CREATE_USER_ALLOW_REMOTE          = "create.user.allow.remote"         ;
	
    //--------------------------------------------------------------------------
    public AnsiSqlGenerator ()
    {}

    //--------------------------------------------------------------------------
    @Override
    public void setIdentifierQuote (final String sQuote)
    	throws Exception
    {
    	m_sIdentifierQuote = sQuote;
    }

    //--------------------------------------------------------------------------
    @Override
    public void setStringQuote (final String sQuote)
    	throws Exception
    {
    	m_sStringQuote = sQuote;
    }

    //--------------------------------------------------------------------------
    @Override
    public void enableSqlDumps (final boolean bEnabled)
    	throws Exception
    {
    	m_bDumpSqlStatements = bEnabled;
    }

    //--------------------------------------------------------------------------
    @Override
    public ESqlError mapSqlExceptionToError(SQLException exSQL)
        throws Exception
    {
        ESqlError eError = ESqlError.E_UNKNOWN;
        String    sState = exSQL.getSQLState();
        
        if (StringUtils.equalsIgnoreCase(sState, "42y55"))
            eError = ESqlError.E_RESOURCE_DO_NOT_EXISTS;
            
        return eError;
    }
    
    //--------------------------------------------------------------------------
    @Override
    public List< String > createSql (final ISqlGenerator.EStatementType eStatement,
    								 final Row                          aMeta     ,
    								 final IDBBackendQuery              iQuery    )
        throws Exception
    {
    	final List< String > lSqls = new ArrayList< String > ();

        if (eStatement == ISqlGenerator.EStatementType.E_CREATE_SCHEMA)
            impl_createSqlCreateSchema4Entity(aMeta, lSqls);
        else
        if (eStatement == ISqlGenerator.EStatementType.E_CREATE_TABLE)
            impl_createSqlCreateTable4Entity(aMeta, lSqls);
        else
        if (eStatement == ISqlGenerator.EStatementType.E_REMOVE_TABLE)
            impl_createSqlRemoveTable4Entity(aMeta, lSqls);
        else
        if (eStatement == ISqlGenerator.EStatementType.E_INSERT)
            impl_createSqlInsert4Entity(aMeta, lSqls);
        else
        if (eStatement == ISqlGenerator.EStatementType.E_DELETE)
            impl_createSqlDelete4Entity(aMeta, lSqls);
        else
        if (eStatement == ISqlGenerator.EStatementType.E_DELETE_ALL)
            impl_createSqlDeleteAll4Entity(aMeta, lSqls);
        else
        if (eStatement == ISqlGenerator.EStatementType.E_GET_ALL)
            impl_createSqlGetAll4Entity(aMeta, lSqls);
        else
        if (eStatement == ISqlGenerator.EStatementType.E_QUERY_BY_ID)
            impl_createSqlQueryById4Entity(aMeta, lSqls);
        else
        if (eStatement == ISqlGenerator.EStatementType.E_QUERY_BY_PROPS)
            impl_createSqlPropQueryAll4Entity(aMeta, iQuery, lSqls);
        else
        if (eStatement == ISqlGenerator.EStatementType.E_UPDATE)
            impl_createSqlUpdate4Entity(aMeta, lSqls);
        else
            throw new UnsupportedOperationException ("Not implemented yet. (createSql for '"+eStatement+"')");

		impl_dumpSqlStatementsIfConfigured (lSqls);
        return lSqls;
    }
    
    //--------------------------------------------------------------------------
    @Override
    public List< String > createSql (final ISqlGenerator.EStatementType eStatement,
    					             final Map< String, Object >        lArgs     )
	    throws Exception
	{
    	final List< String > lSqls = new ArrayList< String > ();

        if (eStatement == ISqlGenerator.EStatementType.E_CREATE_USER)
            impl_createSqlCreateUser(lArgs, lSqls);
        else
        if (eStatement == ISqlGenerator.EStatementType.E_QUERY_USER)
            impl_createSqlQueryUser(lArgs, lSqls);
        else
        	throw new UnsupportedOperationException ("Not implemented yet. (createSql for '"+eStatement+"')");

		impl_dumpSqlStatementsIfConfigured (lSqls);
        return lSqls;
	}

    //--------------------------------------------------------------------------
    @Override
    public String mapJavaTypeToSqlType (Column aColumn)
        throws Exception
    {
        String sType = null;

        if (aColumn.Type.equals(String.class))
        {
            if (aColumn.Length < 1)
                throw new RuntimeException ("No length specified for string column '"+aColumn.Name+"'.");
            
            if (aColumn.Length > AnsiSqlGenerator.MAX_STRING_LENGTH)
                throw new RuntimeException ("Max supported string length of "+AnsiSqlGenerator.MAX_STRING_LENGTH+" specified for string column '"+aColumn.Name+"' reached.");
            
            sType = "varchar("+aColumn.Length+")";
        }
        
        else
        if (
            (aColumn.Type.equals(char.class))
           )
            sType = AnsiSqlGenerator.SQLTYPE_CHAR;
        
        else
        if (
            (aColumn.Type.equals(boolean.class)) ||
            (aColumn.Type.equals(Boolean.class))
           )
            sType = AnsiSqlGenerator.SQLTYPE_BOOLEAN;
        
        else
        if (
            (aColumn.Type.equals(byte.class)) ||
            (aColumn.Type.equals(Byte.class))
           )
            sType = AnsiSqlGenerator.SQLTYPE_BYTE;
        
        else
        if (
            (aColumn.Type.equals(short.class)) ||
            (aColumn.Type.equals(Short.class))
           )
            sType = AnsiSqlGenerator.SQLTYPE_SHORT;
        
        else
        if (
            (aColumn.Type.equals(int.class)    ) ||
            (aColumn.Type.equals(Integer.class))
           )
            sType = AnsiSqlGenerator.SQLTYPE_INT;
        
        else
        if (
            (aColumn.Type.equals(long.class)) ||
            (aColumn.Type.equals(Long.class))
           )
            sType = AnsiSqlGenerator.SQLTYPE_LONG;
        
        else
        if (
            (aColumn.Type.equals(float.class)) ||
            (aColumn.Type.equals(Float.class))
           )
            sType = AnsiSqlGenerator.SQLTYPE_FLOAT;
        
        else
        if (
            (aColumn.Type.equals(double.class)) ||
            (aColumn.Type.equals(Double.class))
           )
            sType = AnsiSqlGenerator.SQLTYPE_DOUBLE;
        
        else
        if (
            (aColumn.Type.equals(java.util.Date.class))
           )
            sType = AnsiSqlGenerator.SQLTYPE_DATE;

        if (StringUtils.isEmpty(sType))
            throw new RuntimeException ("Unsupported data type found for creating sql create statement.");

        return sType;
    }

    //--------------------------------------------------------------------------
    @Override
    public void setValueOnPreparedStatement (PreparedStatement aSql  ,
                                             int               nIndex,
                                             Class< ? >        aType ,
                                             Object            aValue)
        throws Exception
    {
        if (aType.equals(String.class))
        {
            if (aValue == null)
                aSql.setNull(nIndex, Types.VARCHAR);
            else
                aSql.setString(nIndex, (String)aValue);
        }
        else
        if (
            (aType.equals(boolean.class)) ||
            (aType.equals(Boolean.class))
           )
        {
            if (aValue == null)
                aSql.setNull(nIndex, Types.NUMERIC);
            else
            {
                if (((Boolean)aValue).booleanValue())
                    aSql.setInt(nIndex, AnsiSqlGenerator.BOOLEAN_TRUE);
                else
                    aSql.setInt(nIndex, AnsiSqlGenerator.BOOLEAN_FALSE);  
            }
        }
        else
        if (
            (aType.equals(byte.class)) ||
            (aType.equals(Byte.class))
           )
        {
            if (aValue == null)
                aSql.setNull(nIndex, Types.NUMERIC);
            else
                aSql.setByte(nIndex, ((Byte)aValue).byteValue());
        }
        else
        if (aType.equals(char.class))
        {
            if (aValue == null)
                aSql.setNull(nIndex, Types.VARCHAR);
            else
                aSql.setString(nIndex, String.valueOf(aValue));
        }
        else
        if (
            (aType.equals(short.class)) ||
            (aType.equals(Short.class))
           )
        {
            if (aValue == null)
                aSql.setNull(nIndex, Types.NUMERIC);
            else
                aSql.setShort(nIndex, ((Short)aValue).shortValue());
        }
        else
        if (
            (aType.equals(int.class    )) ||
            (aType.equals(Integer.class))
           )
        {
            if (aValue == null)
                aSql.setNull(nIndex, Types.NUMERIC);
            else
                aSql.setInt(nIndex, ((Integer)aValue).intValue());
        }
        else
        if (
            (aType.equals(long.class)) ||
            (aType.equals(Long.class))
           )
        {
            if (aValue == null)
                aSql.setNull(nIndex, Types.NUMERIC);
            else
                aSql.setLong(nIndex, ((Long)aValue).longValue());
        }
        else
        if (
            (aType.equals(float.class)) ||
            (aType.equals(Float.class))
           )
        {
            if (aValue == null)
                aSql.setNull(nIndex, Types.FLOAT);
            else
                aSql.setFloat(nIndex, ((Float)aValue).floatValue());
        }
        else
        if (
            (aType.equals(double.class)) ||
            (aType.equals(Double.class))
           )
        {
            if (aValue == null)
                aSql.setNull(nIndex, Types.DOUBLE);
            else
                aSql.setDouble(nIndex, ((Double)aValue).doubleValue());
        }
        else
        if (aType.equals(java.util.Date.class))
        {
            if (aValue == null)
                aSql.setNull(nIndex, Types.NUMERIC);
            else
                aSql.setLong(nIndex, mapDate((java.util.Date)aValue));
        }
        else
            throw new UnsupportedOperationException ("AnsiSqlGenerator.setValueOnPreparedStatement () ... not implemented for type '"+aType.getName()+"' !");
    }
    
    //--------------------------------------------------------------------------
    private static boolean impl_hasResultSetColumn(final ResultSet aResult,
    											   final String    sColumn)
    	throws Exception
    {
        final ResultSetMetaData aMeta    = aResult.getMetaData ();
        final int               nColumns = aMeta.getColumnCount();

        for (int nColumn=1; nColumn<=nColumns; nColumn++)
        {
        	final String sColumnCheck = aMeta.getColumnName(nColumn);
        	if (StringUtils.equals(sColumn, sColumnCheck))
                return true;
        }
        return false;
    }
    
    //--------------------------------------------------------------------------
    @Override
    public Object getValueFromResultSet (ResultSet  aResult,
                                         String     sColumn,
                                         Class< ? > aType  )
        throws Exception
    {
        // tricky ?! .-)
        // Internal we use prefix to name our columns, tables etcpp.
        // But outside code (using meta info) uses 'pure column' names instead.
        // So we have to adapt given meta info to our own needs .-)
        String sRealColumn = EntityMetaInfo.PREFIX_COLUMNS + sColumn;

        // If DB schema was changed at runtime (e.g. on updating code within cluster)
        // we must handle missing columns "gracefully".
        final boolean bColumnExists = impl_hasResultSetColumn (aResult, sRealColumn);
        if ( ! bColumnExists)
        	return null;
        
        // handle NULL values generic
        // so the following lines of code must not handle that again and again ...
        if (aResult.getObject(sRealColumn) == null)
            return null;
        
        if (aType.equals(String.class))
            return aResult.getString(sRealColumn);
        
        else
        if (
            (aType.equals(boolean.class)) ||
            (aType.equals(Boolean.class))
           )
        {
            int nValue = aResult.getInt(sRealColumn);
            if (nValue == AnsiSqlGenerator.BOOLEAN_TRUE)
                return Boolean.TRUE;
            else
                return Boolean.FALSE;
        }
        
        else
        if (
            (aType.equals(byte.class)) ||
            (aType.equals(Byte.class))
           )
            return aResult.getByte(sRealColumn);
        
        else
        if (
            (aType.equals(char.class))
           )
        {
            return aResult.getString(sRealColumn).charAt(0);
        }
        
        else
        if (
            (aType.equals(short.class)) || 
            (aType.equals(Short.class))
           )
            return aResult.getShort(sRealColumn);
        
        else
        if (
            (aType.equals(int.class    )) ||
            (aType.equals(Integer.class))
           )
            return aResult.getInt(sRealColumn);
        
        else
        if (
            (aType.equals(long.class)) ||
            (aType.equals(Long.class))
           )
            return aResult.getLong(sRealColumn);
        
        else
        if (
            (aType.equals(float.class)) ||
            (aType.equals(Float.class))
           )
            return aResult.getFloat(sRealColumn);
        
        else
        if (
            (aType.equals(double.class)) ||
            (aType.equals(Double.class))
           )
            return aResult.getDouble(sRealColumn);
        
        else
        if (aType.equals(java.util.Date.class))
            return mapDate(aResult.getLong(sRealColumn));
        
        else
            throw new UnsupportedOperationException ("AnsiSqlGenerator.setValueOnPreparedStatement () ... not implemented for type '"+aType.getName()+"' !");
    }
    
    //--------------------------------------------------------------------------
    /** maps the given date/time-stamp (as long ms from the beginning of the world)
     *  to the corresponding Date object.
     *  
     *  @param  nDate [IN]
     *          date/time stamp in ms from ...
     *          
     *  @return the corresponding Date object or null if nDate is null.
     */
    public java.util.Date mapDate (Long nDate)
        throws Exception
    {
        if (nDate == null)
            return null;
        return new java.util.Date (nDate);
    }
    
    //--------------------------------------------------------------------------
    public Long mapDate (java.util.Date aDate)
        throws Exception
    {
        if (aDate == null)
            return null;
        return aDate.getTime();
    }
    
    //--------------------------------------------------------------------------
    protected void impl_createSqlCreateUser (final Map< String, Object > lArgs,
    										 final List< String >        lSqls)
        throws Exception
    {
    	throw new UnsupportedOperationException ("Has to be implemented in derived class !");
    }
    
    //--------------------------------------------------------------------------
    protected void impl_createSqlQueryUser (final Map< String, Object > lArgs,
    										final List< String >        lSqls)
        throws Exception
    {
    	throw new UnsupportedOperationException ("Has to be implemented in derived class !");
    }

    //--------------------------------------------------------------------------
    /** create a suitable sql statement which can be used to create
     *  a new DB schema for the specified entity.
     *
     *  e.g. "create schema schema_name ..."
     *
     *  @param  aMeta
     *          contains meta information about an entity class as e.g.
     *          bound DB schema.
     *
     *  @param  lSqls [OUT]
	 *          the new generated sql statement.
     */
    protected void impl_createSqlCreateSchema4Entity (final Row            aMeta,
			 										  final List< String > lSqls)
        throws Exception
    {
        StringBuffer sSql = new StringBuffer (256);

        sSql.append ("create schema "      );
        sSql.append (impl_nameSchema(aMeta));

        lSqls.add(sSql.toString ());
    }
    
    //--------------------------------------------------------------------------
    /** create a suitable sql statement which can be used to create
     *  a new table for the specified entity.
     *
     *  e.g. "create table table_name (column_a, column_b) constraint ..."
     *
     *  @param  aMeta
     *          contains meta information about an entity class as e.g.
     *          configured column and table names.
     *
     *  @param  lSqls [OUT]
	 *          the new generated sql statement.
     */
    protected void impl_createSqlCreateTable4Entity (final Row            aMeta,
			  										 final List< String > lSqls)
        throws Exception
    {
        StringBuffer sSql = new StringBuffer (256);

        sSql.append ("create table "      );
        sSql.append (impl_nameTable(aMeta));
        sSql.append (" ("                 );

        Iterator< String >    pColumns      = aMeta.listColumns();
        int                   nColumns      = 0;
        boolean               bAddSeparator = false;

        while (pColumns.hasNext())
        {
            if (bAddSeparator)
                sSql.append (", ");
            else
                bAddSeparator = true;

            String sColumn = pColumns.next();
            Column aColumn = aMeta.getColumn(sColumn);

            sSql.append (impl_nameColumn(sColumn)     );
            sSql.append (" "                          );
            sSql.append (mapJavaTypeToSqlType(aColumn));
            
            if ( ! aColumn.CanBeNull)
            	sSql.append(" not null");

            ++nColumns;
        }

        sSql.append (", constraint "                      );
        sSql.append (impl_namePrimaryKeyConstraint (aMeta));
        sSql.append (" primary key ("                     );
        sSql.append (impl_nameColumn(aMeta.getIdColumn()) );
        sSql.append ("))"                                 );
        
        lSqls.add(sSql.toString ());
    }

    //--------------------------------------------------------------------------
    protected void impl_createSqlRemoveTable4Entity (final Row            aMeta,
			 										 final List< String > lSqls)
        throws Exception
    {
        StringBuffer sSql = new StringBuffer (256);

        sSql.append ("drop table "        );
        sSql.append (impl_nameTable(aMeta));
        
        lSqls.add(sSql.toString ());
    }
    
    //--------------------------------------------------------------------------
    /** create a suitable sql statement which can be used to insert new
     *  entities of the specified type into the db.
     *
     *  e.g. "insert into table_nam (column_a, column_b) values (?, ?)"
     *
     *  @param  aMeta
     *          contains meta information about an entity class as e.g.
     *          configured column and table names.
     *
     *  @param  lSqls [OUT]
	 *          the new generated sql-insert statement.
     */
    protected void impl_createSqlInsert4Entity (final Row            aMeta,
			 									final List< String > lSqls)
        throws Exception
    {
        StringBuffer sSql = new StringBuffer (256);
        sSql.append ("insert into "       );
        sSql.append (impl_nameTable(aMeta));
        sSql.append (" ("                 );

        Iterator< String >    pColumns      = aMeta.listColumns();
        int                   nColumns      = 0;
        boolean               bAddSeparator = false;

        while (pColumns.hasNext())
        {
            if (bAddSeparator)
                sSql.append (", ");
            else
                bAddSeparator = true;

            String sColumn = pColumns.next();
            sSql.append (impl_nameColumn(sColumn));

            ++nColumns;
        }

        sSql.append (") values (");

        int i             = 0;
        int c             = nColumns;
            bAddSeparator = false;
        for (i=0; i<c; ++i)
        {
            if (bAddSeparator)
                sSql.append (", ");
            else
                bAddSeparator = true;
            sSql.append ("?");
        }

        sSql.append (")");
        lSqls.add(sSql.toString ());
    }

    //--------------------------------------------------------------------------
    /** create a suitable sql statement which can be used to update existing
     *  entities of the specified type inside the db.
     *
     *  e.g. "update table_name column_a=?, column_b=? where id_column=?"
     *
     *  @param  aMeta
     *          contains meta information about an entity class as e.g.
     *          configured column and table names.
     *
     *  @param  lSqls [OUT]
	 *          the new generated sql-update statement.
     */
    protected void impl_createSqlUpdate4Entity (final Row            aMeta,
			 									final List< String > lSqls)
        throws Exception
    {
        StringBuffer sSql = new StringBuffer (256);
        sSql.append ("update "            );
        sSql.append (impl_nameTable(aMeta));
        sSql.append (" set "              );

        Iterator< String >    pColumns      = aMeta.listColumns();
        int                   nColumns      = 0;
        boolean               bAddSeparator = false;
        String                sIdColumn     = aMeta.getIdColumn();

        while (pColumns.hasNext())
        {
            String sColumn = pColumns.next();
        	if (StringUtils.equals(sIdColumn, sColumn))
    			continue;

        	// TODO ignore all further columns where meta say they are not allowed within update !

            if (bAddSeparator)
                sSql.append (", ");
            else
                bAddSeparator = true;

            sSql.append (impl_nameColumn(sColumn));
            sSql.append (" = ?"                  );

            ++nColumns;
        }

        sSql.append (" where "                 );
        sSql.append (impl_nameColumn(sIdColumn));
        sSql.append (" = ?"                    );

        lSqls.add(sSql.toString ());
    }

    //--------------------------------------------------------------------------
    /** create a suitable sql statement which can be used to delete an
     *  entity from the db (by using it's primary key!).
     *
     *  e.g. "delete from table_name where primary_key_column = ?"
     *
     *  @param  aMeta
     *          contains meta information about an entity class as e.g.
     *          configured column and table names.
     *
     *  @param  lSqls [OUT]
	 *          the new generated sql-delete statement.
     */
    protected void impl_createSqlDelete4Entity (final Row            aMeta,
			 									final List< String > lSqls)
        throws Exception
    {
        StringBuffer sSql = new StringBuffer (256);
        sSql.append ("delete from "                      );
        sSql.append (impl_nameTable(aMeta)               );
        sSql.append (" where "                           );
        sSql.append (impl_nameColumn(aMeta.getIdColumn()));
        sSql.append (" = ?"                              );

        lSqls.add(sSql.toString ());
    }

    //--------------------------------------------------------------------------
    protected void impl_createSqlDeleteAll4Entity (final Row            aMeta,
			 									   final List< String > lSqls)
        throws Exception
    {
        StringBuffer sSql = new StringBuffer (256);
        sSql.append ("delete from "       );
        sSql.append (impl_nameTable(aMeta));

        lSqls.add(sSql.toString ());
    }

    //--------------------------------------------------------------------------
    protected void impl_createSqlGetAll4Entity (final Row            aMeta,
			 									final List< String > lSqls)
        throws Exception
    {
        StringBuffer sSql = new StringBuffer (256);
        sSql.append ("select * from "     );
        sSql.append (impl_nameTable(aMeta));

        lSqls.add(sSql.toString ());
    }
    
    //--------------------------------------------------------------------------
    protected void impl_createSqlQueryById4Entity (final Row            aMeta,
			 									   final List< String > lSqls)
        throws Exception
    {
        StringBuffer sSql = new StringBuffer (256);

        sSql.append ("select * from "                    );
        sSql.append (impl_nameTable(aMeta)               );
        sSql.append (" where "                           );
        sSql.append (impl_nameColumn(aMeta.getIdColumn()));
        sSql.append (" = ?"                              );

        lSqls.add(sSql.toString ());
    }

    //--------------------------------------------------------------------------
    protected void impl_createSqlPropQueryAll4Entity (final Row             aMeta ,
                                                      final IDBBackendQuery iQuery,
                                                      final List< String >  lSqls )
        throws Exception
    {
        StringBuffer sSql = new StringBuffer (256);

        sSql.append ("select * from "     );
        sSql.append (impl_nameTable(aMeta));
        sSql.append (" where "            );

        boolean          bFirst = true;
        QueryPartValue[] lParts = iQuery.getQueryParts();
        int              i      = 0;
        int              c      = lParts.length;
        
        for (i=0; i<c; ++i)
        {
            QueryPartValue      aValuePart = lParts[i];
        	QueryPart           aPart      = aValuePart.getPart();
        	EQueryPartBinding   eBinding   = aPart.getLogicBinding();
        	EQueryPartOperation eOperation = aPart.getOperation();
        	String              sColumn    = aPart.getColumn();

        	if (bFirst)
        		bFirst = false;
        	else
        	{
        		if (eBinding == EQueryPartBinding.E_AND)
        			sSql.append (" and");
        		else
        		if (eBinding == EQueryPartBinding.E_OR)
        			sSql.append (" or");
        		else
        			throw new IllegalArgumentException ("Unknown logical binding. Did you changed enum IDBQuery.ELogicBinding and forgot to change this line of code here ?");
        	}

    		sSql.append (" "                     );
    		sSql.append (impl_nameColumn(sColumn));

        	if (eOperation == EQueryPartOperation.E_MATCH)
        		sSql.append (" = ?");
        	else
        	if (eOperation == EQueryPartOperation.E_LIKE)
        		sSql.append (" like ?");
        	else
        	if (eOperation == EQueryPartOperation.E_BETWEEN)
        		sSql.append (" between ? and ?");
        	else
        	if (eOperation == EQueryPartOperation.E_LESS_THAN)
        		sSql.append (" < ?");
        	else
        	if (eOperation == EQueryPartOperation.E_GREATER_THAN)
        		sSql.append (" > ?");
    		else
    			throw new IllegalArgumentException ("Unknown operation. Did you changed enum IDBQuery.EOperation and forgot to change this line of code here ?");
        }

        lSqls.add(sSql.toString ());
    }
    
    //--------------------------------------------------------------------------
    /** support quoting of identifier ...
     * 
     *  All places where quoting must be used uses this method.
     *  But this method decide how quoting has to be done ...
     *  or if quoting is required in general.
     *  
     *  So we have on piece of code where we can decide how and if
     *  quoting must be implemented .-)
     *  
     *  @param  sIdentifier [IN]
     *          the identifier to be quoted here.
     *          MUST NOT BE NULL !
     *          
     *  @return the quoted identifier.
     */
    private String impl_quote (String sIdentifier)
        throws Exception
    {
    	final StringBuffer sQuoted = new StringBuffer (256);
    	sQuoted.append(m_sIdentifierQuote);
    	sQuoted.append(sIdentifier       );
    	sQuoted.append(m_sIdentifierQuote);
        return sQuoted.toString ();
    }
    
    //--------------------------------------------------------------------------
    private String impl_nameSchema (Row aMeta)
        throws Exception
    {
    	final String sSchema = aMeta.getSchema();
        return impl_quote (sSchema);
    }

    //--------------------------------------------------------------------------
    /** @return the table name for the given entity meta information.
     * 
     *  With Ansi SQL naming a table can be done in two ways:
     *  - full qualified using schema.table notation
     *  - relative using table name only (without schema)
     *  
     *  Here we check the meta information if a schema is available.
     *  Then we do the right things to get a table name to be used within
     *  SQL statements.
     *  
     *  Note   returning name will contain quotes. (if enabled)
     *          So you don't have to add quotes outside.
     *          
     *  @param  aMeta [IN]
     *          the set of meta information.
     */
    private String impl_nameTable (Row aMeta)
        throws Exception
    {
        String       sSchema = aMeta.getEntityMetaInfo().getSchema();
        String       sTable  = EntityMetaInfo.PREFIX_TABLES+aMeta.getTable();
        StringBuffer sName   = new StringBuffer (256);
        
        // schema is optional
        if ( ! StringUtils.isEmpty(sSchema))
        {
            sName.append (impl_quote (sSchema));
            sName.append ("."                 );
        }
        
        // table is not optional :-)
        sName.append (impl_quote (sTable));
        
        return sName.toString ();
    }
    
    //--------------------------------------------------------------------------
    private String impl_nameColumn (String sColumn)
        throws Exception
    {
        String sName = EntityMetaInfo.PREFIX_COLUMNS+sColumn;
        return impl_quote (sName);
    }
    
    //--------------------------------------------------------------------------
    /** @return the name for the primary key constraint.
     * 
     *  Because such name must be unique within the whole DB schema
     *  where this entity is stored we must find a suitable naming schema
     *  generating 'unique names'.
     *  
     *  The idea: use a prefix (e.g. 'pk') in combination with the table name
     *  of that entity. Because tables are unique too ... and the prefix make
     *  it possible to generate several depending resources based on that.
     *  
     *  Note   returning name will contain quotes (if enabled).
     *          So you don't have to add quotes outside.
     *          
     *  @param  aMeta [IN]
     *          the set of meta information.
     */
    private String impl_namePrimaryKeyConstraint (Row aMeta)
        throws Exception
    {
        String sName = EntityMetaInfo.PREFIX_CONSTRAINT_PRIMARY_KEY+aMeta.getTable();
        return impl_quote (sName);
    }

    //--------------------------------------------------------------------------
    private void impl_dumpSqlStatementsIfConfigured (final List< String > lSqls)
        throws Exception
    {
    	if ( ! m_bDumpSqlStatements)
    		return;
    
    	final StringBuffer sDump = new StringBuffer (256);

		for (final String sSql : lSqls)
		{
			sDump.append(sSql);
			sDump.append("\n");
		}

    	System.out.println(sDump.toString ());
    }
    
    //--------------------------------------------------------------------------
    protected String m_sIdentifierQuote = "\"";

    //--------------------------------------------------------------------------
    protected String m_sStringQuote = "\'";

    //--------------------------------------------------------------------------
    protected boolean m_bDumpSqlStatements = false;
}
