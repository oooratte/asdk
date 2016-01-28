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
package net.as_development.asdk.db_service.impl;

import java.util.UUID;

import net.as_development.asdk.api.db.PersistentId.EStrategy;

//==============================================================================
/** Provide several functions around DB identities.
 */
public class IdStrategy
{
    //--------------------------------------------------------------------------
	/// max length of UUID based ID strings
	public static final int LENGTH_UUID = 36;
	
    //--------------------------------------------------------------------------
	/** @return the maximum length of string values for the specified
	 * 			id generation strategy.
	 * 
	 *  Such information will be usefully e.g. for the DB back end
	 *  to know which length must be reserved for the ID column.
	 *  
	 *  @param	eStrategy [IN]
	 *  		specify the ID generation strategy.
	 */
	public static int getIdLengthForStrategy (EStrategy eStrategy)
		throws Exception
	{
		if (eStrategy == EStrategy.E_UUID)
			return IdStrategy.LENGTH_UUID;
		
		throw new IllegalArgumentException ("Did you changed enum PersistendId.EStrategy ... but forgot to adapt this method here ?");
	}
	
    //--------------------------------------------------------------------------
	/** @return a new generated ID suitable for the specified strategy.
	 * 
	 *  @param	eStrategy [IN]
	 *  		specify the ID generation strategy.
	 */
	public static String newId (EStrategy eStrategy)
		throws Exception
	{
		if (eStrategy == EStrategy.E_UUID)
			return UUID.randomUUID().toString ();
		
		throw new IllegalArgumentException ("Did you changed enum PersistendId.EStrategy ... but forgot to adapt this method here ?");
	}
}
