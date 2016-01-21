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
package test.net.as_development.service_manager;

import net.as_development.api.service.env.IServiceEnv;
import net.as_development.api.service.env.IServiceRegistry;
import net.as_development.service.env.ServiceEnv;
import net.as_development.service.env.impl.ServiceEnvImpl;

import org.junit.Assert;
import org.junit.Test;

//=============================================================================
public class ServiceEnvTest
{
    //-------------------------------------------------------------------------
    public ServiceEnvTest()
    {}

    //-------------------------------------------------------------------------
    @Test
    public void testServiceEnvItselfIsSingleton ()
        throws Exception
    {
        IServiceEnv iSmgr1 = ServiceEnv.get();
        IServiceEnv iSmgr2 = ServiceEnv.get();

        Assert.assertNotNull("testServiceEnvItselfIsSingleton [01] global service manager instance has not to be null.", iSmgr1);
        Assert.assertSame   ("testServiceEnvItselfIsSingleton [02] expect to get same instance second time too."       , iSmgr1, iSmgr2);
    }

    //-------------------------------------------------------------------------
    @Test
    public void testSingletons ()
        throws Exception
    {
        IServiceEnv  iSmgr     = new ServiceEnvImpl  ();
        IServiceRegistry iRegistry = iSmgr.getServiceRegistry();
        iRegistry.registerModule(new ExampleServiceRegistry ());
        
        IExampleServiceB aB1 = iSmgr.getService(IExampleServiceB.class);
        IExampleServiceB aB2 = iSmgr.getService(IExampleServiceB.class);

        Assert.assertSame ("testSingletons [01] expect to get same instance second time too.", aB1, aB2);
    }

    //-------------------------------------------------------------------------
    @Test
    public void testInjection ()
        throws Exception
    {
        IServiceEnv  iSmgr     = new ServiceEnvImpl  ();
        IServiceRegistry iRegistry = iSmgr.getServiceRegistry();
        iRegistry.registerModule(new ExampleServiceRegistry ());

        IExampleServiceA iServiceA = iSmgr.getService(IExampleServiceA.class);
        Assert.assertNotNull("testInjection [01] check injected member 'serviceB' of 'serviceA'", iServiceA.getB());
    }
}
