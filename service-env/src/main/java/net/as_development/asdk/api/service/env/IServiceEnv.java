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

import net.as_development.asdk.api.service.env.IServiceRegistry;

//==============================================================================
/** Implements central factory where you can get instances for interfaces.
*  Use such service environment as global instance.
*
*  <code>
*  // define service mappings before you call createInstance () first time ...
*  IServiceEnv iSmgr = ServiceEnv.get ();
*  iSmgr.setServiceRegistry (new YourServiceRegistryImpl ());
*
*  // create a service
*  IFoo iFoo = iSmgr.createInstance (IFoo.class);
*  </code>
*
*  Internal those service manager provides a full edge DI (dependency injection)
*  framework ... so the returned instance will be already full initialized with
*  all depending services/interfaces/objects it needs to work.
*/
public interface IServiceEnv
{
  //--------------------------------------------------------------------------
  /** Provides access to the underlying service registry where all
   *  service implementations are	bound to it's interface counter part.
   *
   *  Has to be called and initialized BEFORE anyone calls createInstance ()
   *  on these IServiceEnv instance.
   *
   *  It's a kind of bootstrap a dependency injection framework.
   *  Those service manager needs a service registry to work.
   *  So bootstrap code must be the following one:
   *  
   *  <code>
   *  IServiceEnv      iManager  = ServiceEnv.get ();
   *  IServiceRegistry iRegistry = iManager.getServiceRegistry ();
   *  
   *  iRegistry.registerModule (new CorModule ());
   *  iRegistry.registerModule (new UiModule  ());
   *  ...
   *  </code>
   *
   *  @return	the registry where service bindings are available.
   */
  public IServiceRegistry getServiceRegistry ()
      throws Exception;

  //--------------------------------------------------------------------------
  /** Creates an instance implementing the specified interface.
   *
   *  An internal lookup into the @link{ServiceRegistry} will be used
   *  to retrieve a real implementation class for the given interface.
   *  If lookup will be successfully such impl. class will be created
   *  and returned.
   *
   *  The returned object will be full initialized already. DI (dependency injection)
   *  was used doing such stuff .-)
   *
   *  @param  <T>
   *          defines the return type of this method so an explicit cast isn't necessary.
   *
   *  @param  aService
   *          the service where an implementation is searched for.
   *
   *  @throws an exception if service registry lookup or service creation failed.
   */
  public < T > T getService (final Class< ? > aService)
      throws Exception;
  
  //--------------------------------------------------------------------------
  /** same then getService(Class) ... but using a string parameter instead.
   * 
   *  Makes it possible to create service on demand where those names are e.g.
   *  read from a configuration. In such cases no class object will be available.
   *  
   *  @param  <T>
   *          defines the return type of this method so an explicit cast isn't necessary.
   *
   *  @param  sService
   *          the service where an implementation is searched for.
   *
   *  @throws an exception if service registry lookup or service creation failed.
   */
  public < T > T getService (final String sService)
      throws Exception;
}
