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
package net.as_development.asdk.api.db;


import java.util.List;

//==============================================================================
/** provides all functionality of an 'object oriented' data base abstraction layer.
 *
 *  Those layer wont support relation ships .. it tries to support a more 'non sql'
 *  related approach where entities (objects) can be stored and retrieved.
 *
 *  Concepts:
 *  
 *  -------- Handling of multiple entity types at the same time --------
 *  
 *  Sometimes you have to handle more then one entity at the same time.
 *  And most time those entities are from different type. Normal you have to
 *  issue several different requests to different DB objects (one for every entity type)
 *  to store, update or remove it.
 *  
 *  Thats different here - at least for store and remove .-)
 *  E.g. for storing new or updating existing entities ...
 *  You can create and prepare all entity objects outside and call IDB.storeEntities()
 *  with an array of those objects.
 *  
 *  An example:
 *  You wish to create a new user and further he should get a new created
 *  home directory.
 *  
 *  <code>
 *  	UserEntity    aUser         = new UserEntity ();
 *  	              aUser.Name    = ...;
 *  
 *  	HomeDirEntity aHomeDir      = new HomeDirEntity ();
 *  	              aHomeDir.Path = ...;
 *  
 *  	iDB.storeEntities (aUser, aHomeDir);
 *  </code>
 *  
 *  -------- Queries --------
 *  
 *  Any query to be used here has to be created first by calling IDB.prepareQuery ().
 *  The parameter you have to provide with that call is an unique ID for that new query.
 *  Internal it's used to e.g. compile queries at the first time they are used and cache
 *  them for later using.
 *  
 *  -------- Paging --------
 *  
 *  Sometimes your queries match to so many result entities that providing all
 *  of them at the same time can make trouble (e.g. high memory consumption).
 *  
 *  On the other side the 'distributed character' of the underlying DB implementation
 *  may result in to long request times ... because many sub layer has to be asked
 *  (hopefully in parallel) ... but they return her own result sets at different times.
 *  
 *  So paging will be the answer. Every time you expect to get a bunch of entities
 *  back ... you wont get all at the same time (if the size of the result set does not
 *  fit into a minimum range). You get 'pages of entities' instead. 
 *  
 *  Additional to the result page an iterator/cursor is provided to you - called 'NextToken'.
 *  
 * 	On calling e.g. the query method the first time you can call it with NextToken=NULL
 *  (or even with an empty string). You will get the results filled into the result list
 *  (means first page of data available at that time) ... and the return value of the method
 *  itself will be a valid next-token (if more data exists or can be expected).
 *  
 *  If no further results exists the given result list will be cleared and not filled.
 *  Furthermore the method return NULL as next token so you know - it's over ... no more data
 *  available.
 *  
 *  <code>
 *  	IQuery iQuery     = iDB.prepareQuery ("my_query");
 *  	// ... set query values on iQuery
 *  	String sNextToken = null;
 *  	do
 *  	{
 *  		List< MyEntity > lResults   = new ArrayList< MyEntity >(10);
 *  						 sNextToken = iDB.query (MyEntity.class,
 *  												 sNextToken    ,
 *  												 lResults      ,
 *  												 iQuery        );
 *  
 *  		// process result list ...
 *  		// There is no need to check next token here again ...
 *  		// result list will be empty if no results exists ...
 *  		// so the following loop will work even if next token will be null .-)
 *  
 *  		Iterator< MyEntity > pIt = lResults.iterator ();
 *  		while (pIt.hasMoreElements ())
 *  			...  
 *  	}
 *  	while (sNextToken != null);
 *  </code>
 *  
 *  It's not required to use such NextToken inside a loop. You can cache such token
 *  or even provide it to any other piece of code ... so if the token comes back to
 *  to DB-layer you can retrieve the next page of results.
 *  
 *  Problem doing so: ... those token might time out .-)
 *  @todo can it be solved ?
 */
public interface IDB
{
    //-------------------------------------------------------------------------
	/** bind these DB instance to a persistence unit configuration.
	 *
	 *  @note	Must be called as first method after creating a new
	 *  		instance of type IDB to initialize those instance
	 *  		right and prepare it for it's further work.
     *
     *  @param	sUnit [IN]
     *          name of the persistence unit.
     *
     *  @throws an IllegalArgumentException in case given persistence unit does not exists.
     *  
     *  @throws an exception in case an internal (runtime) error occur.
     */
    public void setPersistenceUnit (String sUnit)
        throws Exception;

    //-------------------------------------------------------------------------
    /** same as setPersistenceUnit(java.lang.String) ...
     * 
     *  @param	aUnit [IN]
     *          the persistence unit to be used here.
     *          
     *  @throws an IllegalArgumentException in case given persistence unit does not exists.
     *  
     *  @throws an exception in case an internal (runtime) error occur.
     */
    public void setPersistenceUnit (IPersistenceUnit aUnit)
        throws Exception;

    //-------------------------------------------------------------------------
	/** make all given entities persistent inside DB.
	 * 
	 *  You can store different kind of entities here at the same time.
	 *  E.g. you can create a collection of different entities which
	 *  'works' together (e.g. user->file->storage) and put them
	 *  to the DB together also.
     *
     *  @param  lEntities [IN]
     *          the list of entities to be made persistent.
     *          If list will be empty or null - nothing will happen.
     *
     *  @throws an exception in case an internal (runtime) error occured.
     */
    public < TEntity extends IEntity > void storeEntities (TEntity... lEntities)
        throws Exception;

    //-------------------------------------------------------------------------
	/** remove the given set of entities from the DB.
	 *
	 *  You can remove different kind of entities here at the same time.
	 *  E.g. you can create a collection of different entities which
	 *  'works' together (e.g. user->file->storage) and remove them
	 *  from the DB together also. Because there is no relation ship between
	 *  those entities order of remove request can be ignored. 
	 *  
	 *  @param	lEntities [IN]
	 *  		the list of entities to be removed.
     *          If list will be empty or null - nothing will happen.
     */
	public < TEntity extends IEntity > void removeEntities (TEntity... lEntities)
		throws Exception;

    //-------------------------------------------------------------------------
	/** remove all entities of specified type from the DB where it's
	 *  Id's match to given Id set.
	 *
	 *  @param	aType [IN]
     *          the type of those entities to be removed here.
     *          
	 *  @param	lIds [IN]
     *          the list of Id's where the matching entities must be removed.
     *          If list will be empty or null - nothing will happen.
     */
	public < TEntity extends IEntity > void removeEntitiesById (Class< TEntity > aType,
																   String... 		lIds )
		throws Exception;
	
    //-------------------------------------------------------------------------
	/** remove all entities of given type from these DB.
     *
     *  @param	aType [IN]
     *  		describe the type where all corresponding entities
     *  		has to be removed.
     *
     *  @throws an exception in case an internal (runtime) error occur.
     */
	public < TEntity extends IEntity > void removeAllEntitiesOfType (Class< TEntity > aType)
		throws Exception;

    //-------------------------------------------------------------------------
	/** direct access to ONE entity which specified ID.
	 * 
	 *  @param	aType [IN]
     *          the type of entity asked here.
     *          
	 *  @param	aId [IN]
     *          the unique ID (primary key) of those entity.
     *          
     *  @return	the queried entity if it exists; null otherwise.
     *
     *  @throws an exception in case an internal (runtime) error occur.
	 */
    public < TEntity extends IEntity > TEntity getEntityById(Class< TEntity > aType,
            							   					    String           sId  )
    	throws Exception;
    
    //-------------------------------------------------------------------------
    /** return a list of entities matching to the given list of primary keys.
     *
     *  @param  aType [IN]
     *          the type of entities where we must search for.
     *
     *	@param	sNextToken [IN]
     *			the next token where further results might be available for.
     *			Can be null or an empty string if this the 'first query'.
     *
     *	@param	lResults [OUT]
     *			the list of matching entities for this request.
     *			Will be empty if no (further) results exists.
     *
     *  @param  lIds [IN]
     *          the list of Id's.
     *          If list will be empty (or even null) - nothing will happen.
     *
     * 	@return	the new next token (if there is one).
     * 			Return null if no further results exists.	
     *
     *  @throws an exception in case an internal (runtime) error occur.
     */
	public < TEntity extends IEntity > String getEntitiesById (Class< TEntity > aType     ,
																  String           sNextToken,
																  List< TEntity >  lResults  ,
																  String...        lIds      )
        throws Exception;

    //-------------------------------------------------------------------------
    /** return all entities matching to the given type.
     *
     *  Please use it carefully because it can return more data as you need real .-)
     *
     *  @param  aType [IN]
     *          the type of entities where we must search for.
     *
     *  @param  sNextToken [IN]
     *          the next token where further results might be available for.
     *          Can be null or an empty string if this the 'first query'.
     *
     *  @param  lResults [OUT]
     *          the list of matching entities for this request.
     *          Will be empty if no (further) results exists.
     *
     *  @return the new next token (if there is one).
     *          Return null if no further results exists.   
     *
     *  @throws an exception in case an internal (runtime) error occur.
     */
	public < TEntity extends IEntity > String getAllEntitiesOfType (Class< TEntity > aType     ,
	                                                                   String           sNextToken,
	                                                                   List< TEntity >  lResults  )
	    throws Exception;
	
    //-------------------------------------------------------------------------
    /** provide a query object which we can use later at the interface method
     *  IDB.query ().
     *  
     *  @param  aType [IN]
     *          the type of entities where the query must be suitable for.
     *
     *  @param	sQueryId [ID]
     *			an unique ID for this query.
     *			Will be might used for internal caching of pre-compiled queries.
     *
     *  @return	the query object to be used now.
     */
    public < TEntity extends IEntity > IDBQuery< TEntity > prepareQuery (Class< TEntity > aType   ,
    															 		    String           sQueryId)
    	throws Exception;
    
    //-------------------------------------------------------------------------
	/** query for entities where it's attributes match the given set of
	 *  query parameters/values.
	 *  
	 *  An instance of IDBQuery can be obtained by calling IDB.prepareQuery ().
	 *  
	 *  @see	IDBQuery
	 *  @see	prepareQuery(java.lang.String)
	 *  
     *	@param	aType [IN]
     *			the entity type where this query is for.
     *
     *	@param	sNextToken [IN]
     *			the next token where further results might be available for.
     *			Can be null or an empty string if this the 'first query'.
     *
     *	@param	lResults [OUT]
     *			the list of matching entities for this query.
     *			Will be empty if no (further) results exists.
     *
     * 	@param	iQuery [IN]
     * 			the query itself.
     * 
     * 	@return	the new next token (if there is one).
     * 			Return null if no further results exists.	
	 */
    public < TEntity extends IEntity > String query (Class< TEntity >    aType     ,
													    String              sNextToken,
														List< TEntity >     lResults  ,
                                                        IDBQuery< TEntity > iQuery    )
        throws Exception;
    
    //-------------------------------------------------------------------------
    /** Does the same then query () ... but instead of be usable for big sets of
     *  results these method can be used if one result will be expected.
     *
     *  Of course YOU have to make sure exact one result will be possible for that query.
     *  E.g. you should issue those query on columns where the UNIQUE property was set .-)
     *
     *	@param	aType [IN]
     *          the entity type where this query is for.
     *
     * 	@param	iQuery [IN]
     * 		the query itself.
     *
     *  @return the (hopefully) one and only result.
     *          Can be null.
     */
    public < TEntity extends IEntity > TEntity queryOne (Class< TEntity >    aType ,
                                                            IDBQuery< TEntity > iQuery)
        throws Exception;
}
