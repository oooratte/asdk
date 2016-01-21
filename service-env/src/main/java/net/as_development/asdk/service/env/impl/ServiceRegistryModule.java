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
package net.as_development.asdk.service.env.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import net.as_development.asdk.api.service.env.IServiceRegistryModule;
import net.as_development.asdk.api.service.env.ServiceDescriptor;

//=============================================================================
/** Helper class implementing the IServiceRegistryModule interface.
*  It provides several basic functions regarding those registry.
*  A derived class has to ...
*  - define meta data for service interfaces
*  - and implement the mapServiceToInstance (String) method.
*
*  @see IServiceRegistryModule
*/
public abstract class ServiceRegistryModule implements IServiceRegistryModule
{
  //--------------------------------------------------------------------------
  public ServiceRegistryModule ()
  {}

  //--------------------------------------------------------------------------
  @Override
  public List< String > listMetaServices ()
      throws Exception
  {
      List< String > lList = new Vector< String >(mem_Metas ().keySet());
      return lList;
  }
  
  //--------------------------------------------------------------------------
  @Override
  public abstract List< String > listServices ()
      throws Exception;
  
  //--------------------------------------------------------------------------
  @Override
  public ServiceDescriptor getServiceMeta (Class< ? > aService)
      throws Exception
  {
      String sService = aService.getName ();
      return getServiceMeta (sService);
  }

  //--------------------------------------------------------------------------
  @Override
  public ServiceDescriptor getServiceMeta(String sService)
      throws Exception
  {
      Map< String, ServiceDescriptor > lMetas = mem_Metas ();

      if (lMetas.containsKey(sService))
          return lMetas.get(sService);

      return null;
  }

  //--------------------------------------------------------------------------
  @SuppressWarnings("unchecked")
  @Override
  public < T > T mapServiceToImplementation(Class< ? > aService)
      throws Exception
  {
      String sService = aService.getName ();
      return (T) mapServiceToImplementation (sService);
  }

  //--------------------------------------------------------------------------
  @Override
  public abstract < T > T mapServiceToImplementation(String sService)
      throws Exception;
  
  //--------------------------------------------------------------------------
  /** mark the given service as singleton.
   *
   *  @param	aService [IN]
   *  		the service which should be handled as singleton.
   */
  protected void markServiceSingleton (Class< ? > aService)
      throws Exception
  {
      ServiceDescriptor aMeta = impl_getOrCreateServiceMeta (aService);
      aMeta.IsSingleton = true;
  }

  //--------------------------------------------------------------------------
  /** mark the given service as pooled instance.
   *
   *  @param	aService [IN]
   *  		the service which should be pooled.
   *
   *  @param	nPoolSize [IN]
   *  		the size of the pool.
   */
  protected void markServicePooled (Class< ? > aService ,
                                    int        nPoolSize)
      throws Exception
  {
      ServiceDescriptor aMeta = impl_getOrCreateServiceMeta (aService);
      aMeta.PoolSize = nPoolSize;
  }

  //--------------------------------------------------------------------------
  private ServiceDescriptor impl_getOrCreateServiceMeta (Class< ? > aService)
      throws Exception
  {
      String                     sService = aService.getName ();
      Map< String, ServiceDescriptor > lMetas   = mem_Metas ();
      ServiceDescriptor                aMeta    = null;

      if (lMetas.containsKey(sService))
          aMeta = lMetas.get(sService);
      else
      {
          aMeta = new ServiceDescriptor ();
          lMetas.put(sService, aMeta);
      }

      return aMeta;
  }

  //--------------------------------------------------------------------------
  private Map< String, ServiceDescriptor > mem_Metas ()
      throws Exception
  {
      if (m_lMetas == null)
          m_lMetas = new HashMap< String, ServiceDescriptor > (10);
      return m_lMetas;
  }

  //--------------------------------------------------------------------------
  private Map< String, ServiceDescriptor > m_lMetas = null;
}
