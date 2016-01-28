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


//=============================================================================
/** Can be used for setup/tear down of the DB server.
 */
public interface IDBServer
{
    //-------------------------------------------------------------------------
	public void setServerConnection (IPersistenceUnit iData)
		throws Exception;
	
    //-------------------------------------------------------------------------
	/** register the given set of persistence units within this server instance.
	 *  Must be called BEFORE createSchema () will be called !
	 * 
	 *	@param	lPUs [IN]
	 *			the list of persistence units for registration.
	 */
	public void registerPersistenceUnit (IPersistenceUnit... lPUs)
		throws Exception;
	
    //-------------------------------------------------------------------------
	public void registerPersistenceUnit (List< IPersistenceUnit > lPUs)
		throws Exception;
	
    //-------------------------------------------------------------------------
	/** register the given module within this server instance.
	 *  Must be called BEFORE createSchema () will be called !
	 * 
	 *  @param	lModules [IN]
	 *  		the set of modules to be registered.
	 */
	public void registerPersistenceRegistryModule (IPersistenceUnitRegistryModule... lModules)
		throws Exception;
	
    //-------------------------------------------------------------------------
	public void registerPersistenceRegistryModule (List< IPersistenceUnitRegistryModule > lModules)
		throws Exception;
	
    //-------------------------------------------------------------------------
	/** initialize the runtime.
	 *  E.g. the global DBPool will be filled with valid data so
	 *  YOU can use it .-)
	 * 
	 *  @throws an exception if initialization failed by any reason.
	 */
	public void initRuntime ()
		throws Exception;
	
    //-------------------------------------------------------------------------
	/** create all schema within this server.
	 *  Schema will be defined by all persistence units registered before ...
	 *  
	 *  @note	this will remove ALL schema/data already existing within this server instance !
	 *  
	 *  @throws an exception if (at least one) schema could not be created successfully.
	 */
	public void createSchema ()
		throws Exception;
}
