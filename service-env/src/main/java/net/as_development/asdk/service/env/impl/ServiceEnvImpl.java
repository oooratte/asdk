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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.as_development.asdk.service.env.impl;

import java.util.HashMap;
import java.util.Map;

import net.as_development.asdk.api.service.env.IDependencyInjection;
import net.as_development.asdk.api.service.env.IServiceEnv;
import net.as_development.asdk.api.service.env.IServiceRegistry;
import net.as_development.asdk.api.service.env.ServiceDescriptor;

//=============================================================================
/** @see IServiceEnv
 */
public class ServiceEnvImpl implements IServiceEnv
{
  //--------------------------------------------------------------------------
  @SuppressWarnings("unchecked")
  @Override
  public < T > T getService(Class< ? > aService)
      throws Exception
  {
      String sService = aService.getName ();
      return (T) getService (sService);
  }

  //--------------------------------------------------------------------------
  @SuppressWarnings("unchecked")
  @Override
  public < T > T getService(String sType)
      throws Exception
  {
      ServiceDescriptor aMeta = m_iRegistry.getServiceMeta(sType);
      Object      aImpl = null;

      if (aMeta != null)
      {
          if (aMeta.IsSingleton)
              aImpl = impl_getOrCreateSingleton (sType);
          else
          if (aMeta.PoolSize > 0)
              aImpl = impl_getOrCreatePooledInstance (sType, aMeta.PoolSize);
      }

      if (aImpl == null)
          aImpl = impl_createAndPrepareNewInstance (sType);

      if (aImpl == null)
      	throw new Exception ("Could not create an instancfe of type '"+sType+"'. Did you configured service registry right ?");
      
      return (T) aImpl;
  }
  
  //--------------------------------------------------------------------------
  @Override
  public IServiceRegistry getServiceRegistry()
      throws Exception
  {
      return mem_Registry ();
  }

  //--------------------------------------------------------------------------
  /** @return the singleton instance registered for the given service interface.
   *
   *  If those instance does not exists - it will be created.
   *  If those instance was still created before - it will be reused.
   *
   *  @param	sService [IN]
   *  		the asked service interface.
   */
  private Object impl_getOrCreateSingleton (String sService)
      throws Exception
  {
      Map< String, Object > lSingletons = mem_Singletons ();
      Object                aSingleton  = null;

      if (lSingletons.containsKey(sService))
          aSingleton = lSingletons.get(sService);
      else
      {
          aSingleton = impl_createAndPrepareNewInstance (sService);
          lSingletons.put(sService, aSingleton);
      }

      return aSingleton;
  }

  //--------------------------------------------------------------------------
  /** @return one instance from the pool suitable for those kind of service.
   *
   *  If those pool does not exists - it will be created.
   *  If no instance exists within the pool - a new instance will be created and pooled.
   *  If the pool isn't full - a new instance will be created and pooled.
   *  If the pool is filled complete - an existing instance will be returned ...
   *
   *  @todo define algorithm for retrieving pooled instances (round robin?!)
   *
   *  @param	sService [IN]
   *  		the asked service interface.
   *
   *  @param	nPoolSize [IN]
   *  		the pool size for creating a new one.
   */
  private Object impl_getOrCreatePooledInstance (String sService ,
                                                 int    nPoolSize)
      throws Exception
  {
      throw new UnsupportedOperationException ("Not implemented yet.");
  }

  //--------------------------------------------------------------------------
  /** create and prepare a complete new instance of the requested service.
   *
   *  'Preparing' means: DI - dependency injection .-)
   *
   *  @param	aService [IN]
   *  		the asked service interface.
   *
   *  @return the service instance.
   */
  private Object impl_createAndPrepareNewInstance (String sService)
      throws Exception
  {
      Object aImpl = m_iRegistry.mapServiceToImplementation(sService);

      if (aImpl == null)
      	aImpl = impl_mapDynamicAndTricky (sService);
      
      if (aImpl instanceof IDependencyInjection)
          impl_inject ((IDependencyInjection)aImpl);

      return aImpl;
  }

  //--------------------------------------------------------------------------
  protected Object impl_mapDynamicAndTricky (String sService)
      throws Exception
  {
	  try
	  {
		  final Class< ? > aServiceClass = Class.forName(sService);
		  final Object     aServiceInst  = aServiceClass.newInstance();
		  
		  return aServiceInst;
	  }
	  catch (Throwable ex)
	  {
		  // TODO ???
	  }

  	  return null;
  }
  
  //--------------------------------------------------------------------------
  /** implements the DI - dependency injection - for new created services.
   *
   *  @param	iInjectable [IN/OUT]
   *  		the service which wish to be injected and initialized.
   */
  private void impl_inject (IDependencyInjection iInjectable)
      throws Exception
  {
      Class< ? >[] lInjections = iInjectable.getRequiredInjections();
      for (Class<?> aInjectionType : lInjections)
      {
          Object aInjection = getService (aInjectionType);
          iInjectable.inject(aInjectionType, aInjection);
      }
  }

  //--------------------------------------------------------------------------
  private IServiceRegistry mem_Registry ()
      throws Exception
  {
      if (m_iRegistry == null)
          m_iRegistry = new ServiceRegistry ();
      return m_iRegistry;
  }
  
  //--------------------------------------------------------------------------
  private Map< String, Object > mem_Singletons ()
      throws Exception
  {
      if (m_lSingletons == null)
          m_lSingletons = new HashMap< String, Object > (10);
      return m_lSingletons;
  }

  //--------------------------------------------------------------------------
  private IServiceRegistry m_iRegistry = null;

  //--------------------------------------------------------------------------
  private Map< String, Object > m_lSingletons = null;
}
