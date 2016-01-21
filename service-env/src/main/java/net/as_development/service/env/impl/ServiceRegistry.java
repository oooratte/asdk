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
package net.as_development.service.env.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import net.as_development.api.service.env.IServiceRegistry;
import net.as_development.api.service.env.IServiceRegistryModule;
import net.as_development.api.service.env.ServiceDescriptor;

//=============================================================================
/** Helper class implementing the IServiceRegistry interface.
 *  It provides several basic functions regarding those registry.
 *  A derived class has to ...
 *  - define meta data for service interfaces
 *  - and implement the mapServiceToInstance () method.
 *
 *  @see IServiceRegistry
 */
public class ServiceRegistry implements IServiceRegistry
{
	//-------------------------------------------------------------------------
	public ServiceRegistry ()
	    throws Exception
	{}

    //--------------------------------------------------------------------------
    @Override
    public void registerModule(final IServiceRegistryModule iModule)
        throws Exception
    {
        impl_registerModule4Metas    (iModule);
        impl_registerModule4Services (iModule);
    }
    
    //--------------------------------------------------------------------------
    @Override
    public List< String > listMetaServices()
        throws Exception
    {
        List< String > lMetaServices = new Vector< String >(mem_Metas().keySet());
        return lMetaServices;
    }

    //--------------------------------------------------------------------------
    @Override
    public List< String > listServices()
        throws Exception
    {
        List< String > lServices = new Vector< String >(mem_Services().keySet());
        return lServices;
    }
    
    //--------------------------------------------------------------------------
    @Override
    public ServiceDescriptor getServiceMeta(Class< ? > aService)
        throws Exception
    {
        String sService = aService.getName ();
        return getServiceMeta (sService);
    }
    
    //--------------------------------------------------------------------------
    @Override
    public ServiceDescriptor getServiceMeta(final String sService)
        throws Exception
    {
        Map< String, IServiceRegistryModule > lMetas  = mem_Metas ();
        IServiceRegistryModule                iModule = lMetas.get(sService);
        ServiceDescriptor                           aMeta   = null;
        
        if (iModule != null)
            aMeta = iModule.getServiceMeta(sService);
        
        return aMeta;
    }

    //--------------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    @Override
    public < T > T mapServiceToImplementation(final Class< ? > aService)
        throws Exception
    {
        String sService = aService.getName ();
        return (T) mapServiceToImplementation (sService);
    }

    //--------------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    @Override
    public < T > T mapServiceToImplementation(final String sService)
        throws Exception
    {
        Map< String, IServiceRegistryModule > lServices = mem_Services ();
        IServiceRegistryModule                iModule   = lServices.get(sService);
        Object                                aService  = null;
        
        if (iModule != null)
            aService = iModule.mapServiceToImplementation(sService);
        else
        {
            Iterator< IServiceRegistryModule > pModules = lServices.values().iterator();
            while (pModules.hasNext())
            {
                iModule  = pModules.next();
                aService = iModule.mapServiceToImplementation(sService);
                if (aService != null)
                    break;
            }
        }

//		  TODO think about me
//        // The tricky part here ...
//        // On Client side we can't use Class.forName() to generate objects dynamically
//        // if no registration matched before. But on server side we can do that ...
//        // But ... GWT don't compile if it detects Class.forName () at client side code ...
//        // so the trick is to set/use a special hook instead .-)
//        // This will work - and is used - on server side only !
//        if (
//        	(aService                           == null) &&
//        	(ServiceEnv.m_gClassForNameHook != null)
//           )
//        	aService = ServiceEnv.m_gClassForNameHook.mapServiceToImplementation(sService);
        
        return (T) aService;
    }

    //--------------------------------------------------------------------------
    private void impl_registerModule4Metas (final IServiceRegistryModule iModule)
        throws Exception
    {
        Map< String, IServiceRegistryModule > lMetas = mem_Metas ();
        for (String sService : iModule.listMetaServices())
        {
            if (lMetas.containsKey(sService))
                throw new RuntimeException ("Think about double registration of service meta data within registry.");
            
            lMetas.put(sService, iModule);
        }
    }
    
    //--------------------------------------------------------------------------
    private void impl_registerModule4Services (final IServiceRegistryModule iModule)
        throws Exception
    {
        Map< String, IServiceRegistryModule > lServices = mem_Services ();
        for (String sService : iModule.listServices())
        {
            if (lServices.containsKey(sService))
                throw new RuntimeException ("Think about double registration of services within registry.");
            
            lServices.put(sService, iModule);
        }
    }
    
    //--------------------------------------------------------------------------
    private Map< String, IServiceRegistryModule > mem_Metas ()
        throws Exception
    {
        if (m_lMetas == null)
            m_lMetas = new HashMap< String, IServiceRegistryModule > (10);
        return m_lMetas;
    }

    //--------------------------------------------------------------------------
    private Map< String, IServiceRegistryModule > mem_Services ()
        throws Exception
    {
        if (m_lServices == null)
            m_lServices = new HashMap< String, IServiceRegistryModule > (10);
        return m_lServices;
    }
    
    //--------------------------------------------------------------------------
    private Map< String, IServiceRegistryModule > m_lMetas = null;

    //--------------------------------------------------------------------------
    private Map< String, IServiceRegistryModule > m_lServices = null;
}
