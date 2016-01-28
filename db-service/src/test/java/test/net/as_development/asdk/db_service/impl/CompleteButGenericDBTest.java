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
package test.net.as_development.asdk.db_service.impl;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import net.as_development.asdk.api.service.env.IServiceEnv;
import net.as_development.asdk.api.service.env.IServiceRegistry;
import net.as_development.asdk.api.service.env.IServiceRegistryModule;
import net.as_development.asdk.db_service.DBServiceRegistryModule;
import net.as_development.asdk.service.env.ServiceEnv;
import test.net.as_development.asdk.db_service.test.helper.DbEnvProvider;
import test.net.as_development.asdk.db_service.test.helper.GenericDbTest;

//==============================================================================
@Ignore
public class CompleteButGenericDBTest
{
    //--------------------------------------------------------------------------
    public CompleteButGenericDBTest()
    {
    }

    //--------------------------------------------------------------------------
    @BeforeClass
    public static void setUpClass()
        throws Exception
    {
        IServiceEnv            iServiceManager = ServiceEnv.get();
        IServiceRegistry       iRegistry       = iServiceManager.getServiceRegistry();
        IServiceRegistryModule iModule         = new DBServiceRegistryModule ();
        iRegistry.registerModule(iModule);
    }

    //--------------------------------------------------------------------------
    @AfterClass
    public static void tearDownClass()
        throws Exception
    {
    }

    //--------------------------------------------------------------------------
    @Before
    public void setUp()
        throws Exception
    {
      m_nEnv = DbEnvProvider.ENV_EMBEDDED_SQL;
      //m_nEnv = DbEnvProvider.ENV_AMAZON_SIMPLEDB;
      //m_nEnv = DbEnvProvider.ENV_REMOTE_SQL;
      //m_nEnv = DbEnvProvider.ENV_MONGODB;
    }

    //--------------------------------------------------------------------------
    @After
    public void tearDown()
        throws Exception
    {
    }

    //--------------------------------------------------------------------------
    /**
     */
    @Test
    public void testIdGeneration ()
        throws Exception
    {
        GenericDbTest aTest = new GenericDbTest ();
        aTest.defineDbEnv(m_nEnv);
        aTest.testIdGeneration();
    }

    //--------------------------------------------------------------------------
    /**
     */
    @Test
    public void testIdReUsing ()
        throws Exception
    {
        GenericDbTest aTest = new GenericDbTest ();
        aTest.defineDbEnv(m_nEnv);
        aTest.testIdReUsing();
    }
    
    //--------------------------------------------------------------------------
    /**
     */
    @Test
    public void testSchemaCreation ()
        throws Exception
    {
        GenericDbTest aTest = new GenericDbTest ();
        aTest.defineDbEnv(m_nEnv);
        aTest.testSchemaCreation();
    }

    //--------------------------------------------------------------------------
    /**
     */
    @Test
    public void testSchemaRemove ()
        throws Exception
    {
        GenericDbTest aTest = new GenericDbTest ();
        aTest.defineDbEnv(m_nEnv);
        aTest.testSchemaRemove();
    }
    
    //--------------------------------------------------------------------------
    @Test
    public void testStoringEntities ()
        throws Exception
    {
        GenericDbTest aTest = new GenericDbTest ();
        aTest.defineDbEnv(m_nEnv);
        aTest.testStoringEntities ();
    }

    //--------------------------------------------------------------------------
    @Test
    public void testUpdateEntities ()
        throws Exception
    {
        GenericDbTest aTest = new GenericDbTest ();
        aTest.defineDbEnv(m_nEnv);
        aTest.testUpdateEntities ();
    }

    //--------------------------------------------------------------------------
    @Test
    public void testRemovingEntities ()
        throws Exception
    {
        GenericDbTest aTest = new GenericDbTest ();
        aTest.defineDbEnv(m_nEnv);
        aTest.testRemovingEntities ();
    }

    //--------------------------------------------------------------------------
    @Test
    public void testQueringEntities ()
        throws Exception
    {
        GenericDbTest aTest = new GenericDbTest ();
        aTest.defineDbEnv(m_nEnv);
        aTest.testQueringEntities();
    }
    
    //--------------------------------------------------------------------------
    @Test
    public void testTypeMapping ()
        throws Exception
    {
        GenericDbTest aTest = new GenericDbTest ();
        aTest.defineDbEnv(m_nEnv);
        aTest.testTypeMapping();
    }
    
    //--------------------------------------------------------------------------
    @Test
    public void testPerformance ()
        throws Exception
    {
        GenericDbTest aTest = new GenericDbTest ();
        aTest.defineDbEnv(m_nEnv);
        aTest.testPerformance();
    }

    //--------------------------------------------------------------------------
    @Test
    public void testModifyStamp ()
        throws Exception
    {
        GenericDbTest aTest = new GenericDbTest ();
        aTest.defineDbEnv(m_nEnv);
        aTest.testModifyStamp ();
    }
    
    //--------------------------------------------------------------------------
    private int m_nEnv = -1;
}