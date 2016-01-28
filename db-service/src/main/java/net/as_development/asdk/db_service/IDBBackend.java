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
package net.as_development.asdk.db_service;

import java.util.List;

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
     *  @note   Do nothing in case table do not exists.
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
     *          (meta info & values)
     *
     *  @throws an exception in case operation failed (by any reason).
     */
    public void insertRows (List< Row > lRows)
        throws Exception;

    //--------------------------------------------------------------------------
    /** must be overwritten by derived class to support update operation.
     *
     *  @param  lRows [IN]
     *          contains all information about all rows for updating.
     *          (meta info & values)
     *
     *  @throws an exception in case operation failed (by any reason).
     */
    public void updateRows (List< Row > lRows)
        throws Exception;

    //--------------------------------------------------------------------------
    /** must be overwritten by derived class to support delete operation.
     *
     *  @param  lRows [IN]
     *          contains all information about all rows to be deleted.
     *          (meta info & values)
     *
     *  @throws an exception in case operation failed (by any reason).
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
     *  @throws an exception in case operation failed (by any reason).
     */
    public void deleteAllRows (Row aMeta)
        throws Exception;

    //--------------------------------------------------------------------------
    /** must be overwritten by derived class to support get-by-id operation.
     *
     *  @param  aRow [IN/OUT]
     *          contains all information about the queried entity.
     *          (meta info & id)
     *
     *  @return row filled with all values retrieved from DB.
     *
     *  @throws an exception in case operation failed (by any reason).
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
     *  @throws an exception in case operation failed (by any reason).
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
     *  @throws an exception in case operation failed (by any reason).
     */
    public String queryRows (Row             aMeta     ,
                             String          sNextToken,
                             List< Row >     lResults  ,
                             IDBBackendQuery iQuery    )
        throws Exception;
}
