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
package test.net.as_development.asdk.service.env;

import java.util.List;
import java.util.Vector;

import org.junit.Ignore;

import net.as_development.asdk.service.env.impl.ServiceRegistryModule;

//=============================================================================
/**
 */
@Ignore
public class ExampleServiceRegistry extends ServiceRegistryModule
{
    //--------------------------------------------------------------------------
    public ExampleServiceRegistry ()
        throws Exception
    {
        markServiceSingleton (IExampleServiceB.class);
    }

    //--------------------------------------------------------------------------
    @Override
    public List< String > listServices() throws Exception
    {
        if (m_lServices == null)
        {
            List< String > lServices = new Vector< String > (10);
            lServices.add (IExampleServiceA.class.getName ());
            lServices.add (IExampleServiceB.class.getName ());
            m_lServices = lServices;
        }
        return m_lServices;
    }

    //--------------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    @Override
    public < T > T mapServiceToImplementation(String sService)
        throws Exception
    {
        if (sService.equals (IExampleServiceA.class.getName ()))
            return (T) new ExampleServiceA ();

        if (sService.equals (IExampleServiceB.class.getName ()))
            return (T) new ExampleServiceB ();

        return null;
    }
    
    //--------------------------------------------------------------------------
    private List< String > m_lServices = null;
}

