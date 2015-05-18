/**
 * Copyright 2012 Andreas Schlüns - as-development.net
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package test.net.as_development.sql.server.impl;

import junit.framework.Assert;
import net.as_development.sql.server.impl.StandaloneHSQLDBServer;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

//==============================================================================
/**
 */
public class HsqlDBServerTest
{
    //--------------------------------------------------------------------------
    public HsqlDBServerTest() {
    }

    //--------------------------------------------------------------------------
    @BeforeClass
    public static void setUpClass()
        throws Exception
    {
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
    {
    }

    //--------------------------------------------------------------------------
    @After
    public void tearDown()
    {
    }

    //--------------------------------------------------------------------------
    /** test if server uses default values.
     */
    @Test
    public synchronized void testForDefaults ()
        throws Exception
    {
    	StandaloneHSQLDBServer aServer = new StandaloneHSQLDBServer ();

        Assert.assertNotNull("No default for admin user."    , aServer.getUser         ());
        Assert.assertNotNull("No default for admin password.", aServer.getPassword     ());
        Assert.assertNotNull("No default for driver class."  , aServer.getDriverClass  ());
        Assert.assertNotNull("No default for connection url.", aServer.getConnectionUrl());
    }

    //--------------------------------------------------------------------------
    /** test if admin user can be set.
     */
    @Test
    public synchronized void testUserSettings ()
        throws Exception
    {
    	StandaloneHSQLDBServer aServer = new StandaloneHSQLDBServer ();

        // a) setting a new user  before(!) server was started should be possible always
        String sOrgUser = aServer.getUser();
        String sNewUser = sOrgUser+"_new";
        aServer.setUser (sNewUser);
        String sCheckUser = aServer.getUser();

        Assert.assertEquals("Setting a new user on server failed.", sNewUser, sCheckUser);

        // b) setting a new password before(!) server was started should be possible always
        String sOrgPassword = aServer.getPassword();
        String sNewPassword = sOrgPassword+"_new";
        aServer.setPassword (sNewPassword);
        String sCheckPassword = aServer.getPassword();

        Assert.assertEquals("Setting a new password on server failed.", sNewPassword, sCheckPassword);

        // c) after starting the server ... it shouldnt be possible to set new user or password
        aServer.start ();
        try
        {
            aServer.setUser(sOrgUser);
            Assert.fail ("No exception for setUser () after server was started.");
        }
        catch (Throwable ex)
        {}

        try
        {
            aServer.setPassword(sOrgPassword);
            Assert.fail ("No exception for setPassword () after server was started.");
        }
        catch (Throwable ex)
        {}
        aServer.stop ();
    }

    //--------------------------------------------------------------------------
    /** test if admin user can be set.
     */
    @Test
    public synchronized void testStartStop ()
        throws Exception
    {
        // a) not automatic start allowed
    	StandaloneHSQLDBServer aServer = new StandaloneHSQLDBServer ();
        Assert.assertFalse("Server shouldnt be running directly after creation.", aServer.isRunning());

        // b) but running state should be the right one after start
        aServer.start ();
        Assert.assertTrue("Server was not started successfull ... was it started already ?", aServer.isRunning());

        // c) double call to start should do nothing .-)
        aServer.start ();
        Assert.assertTrue("Server stopped after second start () call ?!", aServer.isRunning());

        // d) stop should change (of course) running state
        aServer.stop ();
        Assert.assertFalse("Server was not stopped successfull.", aServer.isRunning());

        // e) second call of stop should do nothing
        aServer.stop ();
        Assert.assertFalse("Server starts on second stop call ?!", aServer.isRunning());
    }
}