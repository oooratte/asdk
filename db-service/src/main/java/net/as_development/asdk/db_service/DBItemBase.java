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

import net.as_development.asdk.api.db.IDB;
import net.as_development.asdk.api.db.IDBPool;
import net.as_development.asdk.service.env.ServiceEnv;

//=============================================================================
/** A base class for all DB related implementations.
 *  It handle creating the right DB instance and provide them to the derived
 *  class. Further it supports a set of helper functions as e.g. naming queries
 *  unique.
 */
public abstract class DBItemBase
{
	//--------------------------------------------------------------------------
	public DBItemBase ()
	{}

	//--------------------------------------------------------------------------
	/** @return a persistence unit ID suitable for the given class.
	 * 
	 *  @param	aImplClass [IN]
	 *  		the class using this DB base class.
	 */
	public static String namePersistenceUnit (Class< ? > aImplClass)
	{
		return aImplClass.getName ();
	}
	
	//--------------------------------------------------------------------------
	/** @return an unique (but stable/fix) query name.
	 * 
	 *  Because such name is used for caching also it's important to have
	 *  and unique name first but a stable name too.
	 *  
	 *  We use a combination of class and 'internal query name' you provide.
	 *  Of course you must make sure your internal query name is unique within
	 *  the area of your implementation class .-)
	 *  
	 *  @param	aImplClass [IN]
	 *  		the class using DB queries.
	 *  
	 *  @param	sQuery [IN]
	 *  		your internal query name.
	 */
	public static String nameQuery (Class< ? > aImplClass,
									String     sQuery    )
	{
		String       sClass   = aImplClass.getName();
		StringBuffer sQueryId = new StringBuffer (256);
		sQueryId.append (sClass);
		sQueryId.append (":"   );
		sQueryId.append (sQuery);
		return sQueryId.toString ();
	}
	
	//--------------------------------------------------------------------------
	/** @return the name of the persistence unit bound to that DB instance.
	 *  Must be overwritten by the derived class and is called from this
	 *  base class on creating the internal DB instance. 
	 */
	protected abstract String getPersistenceUnitName ()
		throws Exception;
	
	//--------------------------------------------------------------------------
	/** @return the internal DB instance to be used here.
	 */
	protected IDB mem_DB ()
		throws Exception
	{
		return mem_DBPool ().getDbForPersistenceUnit(getPersistenceUnitName ());
	}
	
	//--------------------------------------------------------------------------
	private IDBPool mem_DBPool ()
		throws Exception
	{
		if (m_iDBPool == null)
			m_iDBPool = ServiceEnv.get ().getService(IDBPool.class);
		return m_iDBPool;
	}
	
	//--------------------------------------------------------------------------
	private IDBPool m_iDBPool = null; 
}
