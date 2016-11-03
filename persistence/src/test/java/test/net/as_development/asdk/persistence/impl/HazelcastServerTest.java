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
package test.net.as_development.asdk.persistence.impl;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import net.as_development.asdk.persistence.ISimplePersistence;
import net.as_development.asdk.persistence.SimplePersistenceConfig;
import net.as_development.asdk.persistence.SimplePersistenceFactory;
import net.as_development.asdk.persistence.impl.EHZStoreType;
import net.as_development.asdk.persistence.impl.HZClient;
import net.as_development.asdk.persistence.impl.HZServer;

//=============================================================================
public class HazelcastServerTest
{
	//-------------------------------------------------------------------------
	@Before
	public void setUp()
		throws Exception
	{
	}

	//-------------------------------------------------------------------------
	@After
	public void tearDown()
		throws Exception
	{
	}

	//-------------------------------------------------------------------------
	@Test
	public void testCrashRecovery ()
		throws Exception
	{
		if ( ! (HZClient.DEFAULT_HZ_STORE_TYPE == EHZStoreType.E_MAP))
			return;

		final String SERVER_INTERFACE = "127.0.0.1";
		final String SERVER_HOST      = "127.0.0.1";
		final int    SERVER_PORT      = 7890;
		final String SERVER_ID        = "hz-server-test";
		final String SERVER_PASSWORD  = "test";
	
		final String PERSIST_SCOPE    = "test-crash-recovery";
		
		final String TEST_KEY         = "key-a";
		final String TEST_VALUE       = "value-01";
		
		HZServer aServer = new HZServer ();
		aServer.cleanPersistenceLayer();
		
		aServer.setInterface      (SERVER_INTERFACE);
		aServer.setPort           (SERVER_PORT     );
		aServer.setId             (SERVER_ID       );
		aServer.setPassword       (SERVER_PASSWORD );
		aServer.enablePersistence (FileUtils.getTempDirectoryPath()+"/test-crash-recovery");

		aServer.start();

		final ISimplePersistence iClient01 = SimplePersistenceFactory.get(HZClient.class.getName (),
													SimplePersistenceConfig.CFG_PERSISTENCE_SCOPE, PERSIST_SCOPE                    ,
													HZClient.CFG_SERVER_HOST                ,                  SERVER_HOST     ,
													HZClient.CFG_SERVER_PORT                , Integer.toString(SERVER_PORT    ),
													HZClient.CFG_SERVER_ID                  ,                  SERVER_ID       ,
													HZClient.CFG_SERVER_PASSWORD            ,                  SERVER_PASSWORD);

		Assert.assertTrue ("testCrashRecovery [01] initial list of keys has to be empty", iClient01.listKeys().isEmpty());
		iClient01.set(TEST_KEY, TEST_VALUE);
		Assert.assertEquals("testCrashRecovery [02] could not fill map with test values", TEST_VALUE, iClient01.get(TEST_KEY));

		// simulate crash : disable clean up of persistent data !
		aServer.setCleanOnShutdown(false);
		aServer.stop();

		// restart server - recovery is done automatically
		aServer.start();
		
		final ISimplePersistence iClient02 = SimplePersistenceFactory.get(HZClient.class.getName (),
													SimplePersistenceConfig.CFG_PERSISTENCE_SCOPE, PERSIST_SCOPE                    ,
													HZClient.CFG_SERVER_HOST                ,                  SERVER_HOST     ,
													HZClient.CFG_SERVER_PORT                , Integer.toString(SERVER_PORT    ),
													HZClient.CFG_SERVER_ID                  ,                  SERVER_ID       ,
													HZClient.CFG_SERVER_PASSWORD            ,                  SERVER_PASSWORD);
		Assert.assertEquals ("testCrashRecovery [03] recovered list has wrong size", 1         , iClient02.listKeys().size());
		Assert.assertEquals ("testCrashRecovery [04] recovery shows wrong values"  , TEST_VALUE, iClient02.get(TEST_KEY)    );

		// do real shutdown with clean up of persistent data
		aServer.setCleanOnShutdown(true);
		aServer.stop();

		// restart server
		aServer.start();

		final ISimplePersistence iClient03 = SimplePersistenceFactory.get(HZClient.class.getName (),
													SimplePersistenceConfig.CFG_PERSISTENCE_SCOPE, PERSIST_SCOPE                    ,
													HZClient.CFG_SERVER_HOST                ,                  SERVER_HOST     ,
													HZClient.CFG_SERVER_PORT                , Integer.toString(SERVER_PORT    ),
													HZClient.CFG_SERVER_ID                  ,                  SERVER_ID       ,
													HZClient.CFG_SERVER_PASSWORD            ,                  SERVER_PASSWORD);
		Assert.assertTrue ("testCrashRecovery [05] persistent data was not cleaned", iClient03.listKeys().isEmpty());
		
		// final shutdown ;-)
		aServer.stop();
	}
}
