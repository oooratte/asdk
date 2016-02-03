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