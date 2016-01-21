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
package net.as_development.asdk.api.service.env;

import net.as_development.asdk.api.service.env.IServiceRegistryModule;

//=============================================================================
/** Knows the binding between interfaces and real classes.
 *
 *  An instance of this interface must be set on an service manager instance.
 *  Its used internal to map services/interfaces/classes to it's real implementation
 *  and instance. But additional to a simple IServiceRegistryModule these IServiceRegistry
 *  can be used to register different modules inside.
 *  
 *  What this good for ?
 *  
 *  Instead of writing one big registry module where all classes are registered
 *  you can write several of them. E.g. every gwt module can have it's own one.
 *  At the end you decide which modules will be registered within the global registry.
 *
 *  <code>
 *  ...
 *  IServiceRegistryModule iModule_01 = UIServiceModule.get   ();
 *  IServiceRegistryModule iModule_02 = CoreServiceModule.get ();
 *  ...
 *  IServiceRegistry iRegistry = new ServiceRegistry ();
 *  iRegistry.registerModule (iModule_01);
 *  iRegistry.registerModule (iModule_02);
 *  ...
 *  IServiceEnv iSmgr = ServiceEnv.get ();
 *  iSmgr.setServiceRegistry (iRegistry);
 *  ...  
 *  </code>
 */
public interface IServiceRegistry extends IServiceRegistryModule
{
    //--------------------------------------------------------------------------
    /** register a new module within this service registry.
     *
     *  @param	iModule [IN]
     *  		the new module for registration.
     */
    public void registerModule (final IServiceRegistryModule iModule)
        throws Exception;
}
