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

//==============================================================================
/** Define the operation bound to an IDBQueryPart.
 */
public enum EQueryPartOperation
{
    //--------------------------------------------------------------------------
	/** define a 'match' operation (equals, ==)
	 *  Can be used for all types (int, bool, string, date, time etcpp).
	 */
	E_MATCH,
	
    //--------------------------------------------------------------------------
	/** define a 'like' operation which supports wildcards like * or ?
	 *  Can be used for string types only.
	 *  Supported wildcards are the following ones:
	 *  '*' if you wish to match to [0..n] chars of any kind at that place 
	 *  '?' if you wish to match to [1] char of any kind at that place 
	 */
	E_LIKE,
	
    //--------------------------------------------------------------------------
	/** define a 'between' operation where we search a value inside the specified range.
	 *  Value of the corresponding IDBQueryPart instance must be from type QueryRange.
	 */
	E_BETWEEN,
	
    //--------------------------------------------------------------------------
	/** can be used to find all entities less than a specified reference value.
	 *  @note means NOT lees-or-equals ! 
	 */
	E_LESS_THAN,
	
    //--------------------------------------------------------------------------
	/** can be used to find all entities greater than a specified reference value.
	 *  @note means NOT greater-or-equals ! 
	 */
	E_GREATER_THAN;
}