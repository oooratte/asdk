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
package net.as_development.asdk.db_service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.as_development.asdk.db_service.impl.Column;
import net.as_development.asdk.db_service.impl.Row;

//==============================================================================
/** Implementations of those interface must be used to generate any kind of SQL
 *  statements. But different implementations can generate different statements
 *  for different DB products. Thats because ANSI SQL isn't support 100% by an DB product ...
 */
public interface ISqlGenerator
{
    //--------------------------------------------------------------------------
    public enum ESqlError
    {
        E_UNKNOWN,
        E_RESOURCE_DO_NOT_EXISTS
    }
    
    //--------------------------------------------------------------------------
    public enum EStatementType
    {
        E_CREATE_SCHEMA,
        E_CREATE_TABLE,
        E_REMOVE_TABLE,
        E_INSERT,
        E_UPDATE,
        E_DELETE,
        E_DELETE_ALL,
        E_GET_ALL,
        E_QUERY_BY_ID,
        E_QUERY_BY_PROPS
    }
    
    //--------------------------------------------------------------------------
    /** map the given SQL exception to an suitable error code.
     * 
     *  Because such states depend from the real DB used here ...
     *  and ISqlGenerator implementation represent such 'real DB' ...
     *  it's up to this code to map those error states/codes.
     *  
     *  @param  aException [IN]
     *          the SQL exception to be 'translated' here.
     *          
     *  @return the corresponding internal error code.
     */
    public ESqlError mapSqlExceptionToError (SQLException aException)
        throws Exception;
    
    //--------------------------------------------------------------------------
    /** create a new SQL statement for the given set of parameters.
     * 
     *  Those statement must contain place holder instead of real values.
     *  Because it's used later in combination with a PreparedStatement instance.
     *  
     *  @param  eStatement [IN]
     *          describe the requested SQL statement.
     *          
     *  @param  aMeta [IN]
     *          all meta data describing the entity more in detail.
     *          
     *  @param  iQuery [IN, OPTIONAL]
     *          describe a SQL query more in detail.
     *          Can be null if no query is requested.
     *          
     *  @return the generated SQL statement containing place holder.
     */
    public String createSql (ISqlGenerator.EStatementType eStatement,
                             Row                          aMeta     ,
                             IDBBackendQuery              iQuery    )
        throws Exception;
    
    //--------------------------------------------------------------------------
    /** map the given Java type of an entity member to the corresponding SQL type
     *  to be used within SQL statements.
     *
     *  @param  aMeta [IN]
     *          describe the entity member type.
     *          
     *  @return the corresponding SQL type.
     */
    public String mapJavaTypeToSqlType (Column aMeta)
        throws Exception;
    
    //--------------------------------------------------------------------------
    /** set the given value on the also given PreparedStatement.
     * 
     *  @param  aSql [IN, OUT]
     *          the prepared statement where the value must be set.
     *
     *  @param  nIndex [IN]
     *          the position of the place holder which must be replaced
     *          within the prepared statement.
     *          
     *  @param  aType [IN]
     *          the java type of the given value.
     *          
     *  @param  aValue [IN]
     *          the value to be set on the prepared statement.
     */
    public void setValueOnPreparedStatement (PreparedStatement aSql  ,
                                             int               nIndex,
                                             Class< ? >        aType ,
                                             Object            aValue)
        throws Exception;
    
    //--------------------------------------------------------------------------
    /** extract an entity member value from the given result set.
     * 
     *  @param  aResult [IN]
     *          the result set where the value must be read from.
     *          
     *  @param  sColumn [IN]
     *          the column where the value must be read from.
     *          
     *  @param  aType [IN]
     *          the java type of this value so it's casted right.
     * 
     *  @return the value.
     */
    public Object getValueFromResultSet (ResultSet  aResult,
                                         String     sColumn,
                                         Class< ? > aType  )
        throws Exception;
}
