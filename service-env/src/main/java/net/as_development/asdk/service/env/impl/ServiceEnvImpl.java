/**
 * This is free and unencumbered software released into the public domain.
 *
 * Anyone is free to copy, modify, publish, use, compile, sell, or
 * distribute this software, either in source code form or as a compiled
 * binary, for any purpose, commercial or non-commercial, and by any
 * means.
 *
 * In jurisdictions that recognize copyright laws, the author or authors
 * of this software dedicate any and all copyright interest in the
 * software to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and
 * successors. We intend this dedication to be an overt act of
 * relinquishment in perpetuity of all present and future rights to this
 * software under copyright law.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 * For more information, please refer to <http://unlicense.org/>
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
/** see IServiceEnv
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
   *  TODO define algorithm for retrieving pooled instances (round robin?!)
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
