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
package test.net.as_development.asdk.sql.server.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import junit.framework.Assert;
import net.as_development.asdk.sql.server.impl.StandaloneH2Server;
import net.as_development.asdk.tools.test.TestUtils;

//==============================================================================
/**
 */
@Ignore
public class StandaloneH2DBServerTest
{
    //--------------------------------------------------------------------------
    public StandaloneH2DBServerTest()
    {}

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
    	TestUtils.ignoreTestAtTravisCI();
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
    	final StandaloneH2Server aServer = new StandaloneH2Server ();

        Assert.assertNotNull("No default for admin user."    , aServer.getUser         ());
        Assert.assertNotNull("No default for admin password.", aServer.getPassword     ());
        Assert.assertNotNull("No default for driver class."  , aServer.getDriverClass  ());
        Assert.assertNotNull("No default for connection url.", aServer.getConnectionUrl());
    }

    //--------------------------------------------------------------------------
    /** test if any user can be set.
     */
    @Test
    public synchronized void testUserSettings ()
        throws Exception
    {
    	final StandaloneH2Server aServer = new StandaloneH2Server ();

        // a) setting a new user  before(!) server was started should be possible always
    	final String sOrgUser = aServer.getUser();
    	final String sNewUser = sOrgUser+"_new";
        aServer.setUser (sNewUser);
        final String sCheckUser = aServer.getUser();

        Assert.assertEquals("Setting a new user on server failed.", sNewUser, sCheckUser);

        // b) setting a new password before(!) server was started should be possible always
        final String sOrgPassword = aServer.getPassword();
        final String sNewPassword = sOrgPassword+"_new";
        aServer.setPassword (sNewPassword);
        final String sCheckPassword = aServer.getPassword();

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
    /** test start/stop in pair
     */
    @Test
    public synchronized void testStartStop ()
        throws Exception
    {
        // a) not automatic start allowed
    	final StandaloneH2Server aServer = new StandaloneH2Server ();
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

    //--------------------------------------------------------------------------
    /** test connection
     */
    @Test
    public synchronized void testConnection ()
        throws Exception
    {
    	final String USER   = "fuckel"  ;
    	final String PASSWD = "lsmf4711";
    	
    	final StandaloneH2Server aServer = new StandaloneH2Server ();
    	aServer.setDBName           ("testdb");
    	aServer.setUser             (USER    );
    	aServer.setPassword         (PASSWD  );
    	aServer.setPort             (3308    );
    	aServer.enablePersistentData(false   );

    	aServer.start ();

        final String            sConnectionURL = aServer.getConnectionUrl();
        final Connection        aConnection    = DriverManager.getConnection (sConnectionURL, USER, PASSWD);
        final PreparedStatement aSQL           = aConnection.prepareStatement("SHOW TABLES");
        final ResultSet         aResult        = aSQL.executeQuery();

        aResult.close();
        aSQL   .close();
        
        aServer.stop ();
    }
}