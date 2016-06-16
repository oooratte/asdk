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

import java.util.List;

import net.as_development.asdk.api.db.IPersistenceUnit;
import net.as_development.asdk.db_service.impl.EntityMetaInfoProvider;
import net.as_development.asdk.db_service.impl.Row;

//==============================================================================
/** Those interface will be expected by a backend implementation which
 *  maps a new data base to our framework.
 */
public interface IDBBackend
{
    //--------------------------------------------------------------------------
	/** Set the meta information provider to this implementation.
	 * 
	 *  Meta informations knows nearly anything about entities to be
	 *  load/saved by this DB backend as e.g. attribute and column names,
	 *  tables, types.
	 *  
	 *  @param	aProvider [IN]
	 *  		the meta information provider.
	 */
    public void setEntityMetaInfoProvider (EntityMetaInfoProvider aProvider)
        throws Exception;

    //--------------------------------------------------------------------------
    /** back end should create a new DB.
     * 
     *  @param	aMeta [IN]
     *  		describe the structure of the DB more in detail.
     */
    public void createDB (Row aMeta)
        throws Exception;

    //--------------------------------------------------------------------------
    /** back end should create a new table.
     * 
     *  @param	aMeta [IN]
     *  		describe the structure of the table more in detail.
     */
    public void createTable (Row aMeta)
        throws Exception;

    //--------------------------------------------------------------------------
    /** back end should remove an existing table.
     * 
     *  Note   Do nothing in case table do not exists.
     *          Don't throw an exception in that special case.
     * 
     *  @param  aMeta [IN]
     *          describe the structure of the table more in detail.
     */
    public void removeTable (Row aMeta)
        throws Exception;
    
    //--------------------------------------------------------------------------
    /** must be overwritten by derived class to support insert operation.
     *
     *  @param  lRows [IN]
     *          contains all information about the new rows.
     *          (meta info and values)
     *
     *  @throws Exception in case operation failed (by any reason).
     */
    public void insertRows (List< Row > lRows)
        throws Exception;

    //--------------------------------------------------------------------------
    /** must be overwritten by derived class to support update operation.
     *
     *  @param  lRows [IN]
     *          contains all information about all rows for updating.
     *          (meta info and values)
     *
     *  @throws Exception in case operation failed (by any reason).
     */
    public void updateRows (List< Row > lRows)
        throws Exception;

    //--------------------------------------------------------------------------
    /** must be overwritten by derived class to support delete operation.
     *
     *  @param  lRows [IN]
     *          contains all information about all rows to be deleted.
     *          (meta info and values)
     *
     *  @throws Exception in case operation failed (by any reason).
     */
    public void deleteRows (List< Row > lRows)
        throws Exception;

    //--------------------------------------------------------------------------
    /** must be overwritten by derived class to support deletion of
     *  all entities (Means whole table of one entity type!)
     *
     *  @param  aMeta [IN]
     *  		describe the table (one row of it) more in detail.
     *
     *  @throws Exception in case operation failed (by any reason).
     */
    public void deleteAllRows (Row aMeta)
        throws Exception;

    //--------------------------------------------------------------------------
    /** must be overwritten by derived class to support get-by-id operation.
     *
     *  @param  aRow [IN/OUT]
     *          contains all information about the queried entity.
     *          (meta info and id)
     *          return row filled with all values retrieved from DB.
     *
     *  @throws Exception in case operation failed (by any reason).
     */
    public void getRowById (Row aRow)
        throws Exception;

    //--------------------------------------------------------------------------
    /** must be overwritten by derived class to support get-all operation.
     *
     *  @param  aMeta [IN]
     *          describe the table (one row of it) more in detail.
     *  
     *  @param  sNextToken [IN]
     *          points to the last iterator of last query where this query
     *          should start.
     *          It depends from the real back end implementation how such token
     *          is generated and used.
     *  
     *  @param  lResults [OUT]
     *          here we have to fill in our results.
     *          Was cleared already outside. We have to add something here only .-)
     *
     *  @return return the next possible token
     *          to be used for a sub sequential query.
     *          An empty or null token means: no further query results available.
     *
     *  @throws Exception in case operation failed (by any reason).
     */
    public String getAllRows (Row         aMeta     ,
                              String      sNextToken,
                              List< Row > lResults  )
        throws Exception;
    
    //--------------------------------------------------------------------------
    /** must be overwritten by derived class to support generic queries.
     * 
     *  @param  aMeta [IN]
     *  		describe the table (one row of it) more in detail.
     *  
     *  @param	sNextToken [IN]
     *  		points to the last iterator of last query where this query
     *  		should start.
     *          It depends from the real back end implementation how such token
     *          is generated and used.
     *  
     *  @param	lResults [OUT]
     *  		here we have to fill in our results.
     *  		Was cleared already outside. We have to add something here only .-)
     *
     *  @param	iQuery [IN]
     *  		the query itself.
     *  
     *  @return return the next possible token
     *          to be used for a sub sequential query.
     *          An empty or null token means: no further query results available.
     *
     *  @throws Exception in case operation failed (by any reason).
     */
    public String queryRows (Row             aMeta     ,
                             String          sNextToken,
                             List< Row >     lResults  ,
                             IDBBackendQuery iQuery    )
        throws Exception;
}
