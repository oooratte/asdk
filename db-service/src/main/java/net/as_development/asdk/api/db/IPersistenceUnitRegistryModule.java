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
/** Used for registration of persistence unit modules in a very ease way.
 */
public interface IPersistenceUnitRegistryModule
{
	//--------------------------------------------------------------------------
	/** @return the list of persistence units provided  by this module.
	 *	        Must not be null - nor empty !
	 *
	 *  Its not required persistence unit returned here has to be complete.
	 *  Global parts as e.g. the DB connection parameter will be added outside !
	 *  YOU have to define YOUR parameter set here only (e.g. entities, unit names etcpp)
	 */
	public List< IPersistenceUnit > listPersistenceUnits ()
		throws Exception;
}
