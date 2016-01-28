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

import net.as_development.asdk.db_service.impl.QueryPartValue;

//=============================================================================
/** wraps the IDBQuery to the back end implementation ...
 *  Here we can provide some methods which are usefully for the back end only
 *  without the need to publish more details on the 'normal API'. 
 */
public interface IDBBackendQuery
{
    //-------------------------------------------------------------------------
	/** @return the internal id of this query.
	 * 
	 *  Those id will be guaranteed as unique. The back end implementation
	 *  can use it to e.g. implement caching of 'compiled' queries .-)
	 */
	public String getId ()
		throws Exception;
	
    //-------------------------------------------------------------------------
	/** provides read access to the different parts of this query.
	 * 
	 *  Order within that list is important. On creating the query the 'user'
	 *  decided in which order those parts has to be added ... and now we have
	 *  respect that too. Otherwise e.g. logical operator like 'and'/'or'
	 *  wont work as expected.
	 *  
	 *  So please start with the first part at position 0 .-)
	 */
	public QueryPartValue[] getQueryParts ()
		throws Exception;
}
