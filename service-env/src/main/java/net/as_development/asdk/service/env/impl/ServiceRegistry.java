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
package net.as_development.asdk.service.env.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import net.as_development.asdk.api.service.env.IServiceRegistry;
import net.as_development.asdk.api.service.env.IServiceRegistryModule;
import net.as_development.asdk.api.service.env.ServiceDescriptor;

//=============================================================================
/** Helper class implementing the IServiceRegistry interface.
 *  It provides several basic functions regarding those registry.
 *  A derived class has to ...
 *  - define meta data for service interfaces
 *  - and implement the mapServiceToInstance () method.
 *
 *  see IServiceRegistry
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
