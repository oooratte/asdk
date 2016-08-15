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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

import net.as_development.asdk.persistence.ISimplePersistence;
import net.as_development.asdk.persistence.ISimplePersistenceTransacted;
import net.as_development.asdk.persistence.SimplePersistenceFactory;
import net.as_development.asdk.persistence.impl.HZClient;
import test.net.as_development.asdk.persistence.SimplePersistenceBeanBaseTest;
import test.net.as_development.asdk.persistence.mocks.HazelcastMock;

//=============================================================================
@PrepareForTest
(
	{
		HZClient.class,
		HazelcastInstance   .class,
		HazelcastClient     .class,
		Hazelcast           .class
	}
)
@RunWith
(
	PowerMockRunner.class
)
public class HazelcastPersistenceTest
{
	//-------------------------------------------------------------------------
	private static final Logger LOG = LoggerFactory.getLogger(HazelcastPersistenceTest.class);
	
	//-------------------------------------------------------------------------
//  PowerMockRule isnt able to mock final class Hazelcast ;-(
//  Where PowerMockRunner is able doing that ... ?
	
//	@Rule
//	public PowerMockRule m_aMockEnv = PowerMockRule.create(this);

	//-------------------------------------------------------------------------
	@Before
	public void setUp ()
		throws Exception
	{
		m_aDataSink      = new HashMap< Object, Object >();
		m_aHazelcastMock = HazelcastMock.createFullFunctionalMock(m_aDataSink);
	}
	
	//-------------------------------------------------------------------------
	@Test
	public void testInitialValueIsNull ()
		throws Exception
	{
		final String TEST_KEY = "test-key-initial";
		
		final ISimplePersistence iPersist = impl_newPersistenceInstance ();

		Assert.assertNull("testInitialValueIsNull [01] initial value has to be 'null'.", iPersist.get (TEST_KEY));
	}

	//-------------------------------------------------------------------------
	@Test
	public void testSetGet ()
		throws Exception
	{
		final String TEST_KEY   = "test-key-set-get";
		final String TEST_VALUE = "test-value-set-get";
		
		final ISimplePersistence iPersist = impl_newPersistenceInstance ();

		iPersist.set(TEST_KEY, TEST_VALUE);
		Assert.assertEquals("test [02] unexpected value after set/get", TEST_VALUE, iPersist.get (TEST_KEY));
	}

	//-------------------------------------------------------------------------
	@Test
	public void testListKeys ()
		throws Exception
	{
		final ISimplePersistence iPersist = impl_newPersistenceInstance ();
		iPersist.set ("a", "01");
		iPersist.set ("b", "02");
		iPersist.set ("c", "03");
		iPersist.set ("d", "04");
		
		final List< String > lKeys = iPersist.listKeys ();
		Assert.assertNotNull ("testListKeys [01] null is unexpected"        , lKeys              );
		Assert.assertFalse   ("testListKeys [02] empty keyset is unexpected", lKeys.isEmpty()    );
		Assert.assertEquals  ("testListKeys [03] unexpected key count"      , 4, lKeys.size()    );
		Assert.assertTrue    ("testListKeys [04] miss key 'a'"              , lKeys.contains("a"));
		Assert.assertTrue    ("testListKeys [05] miss key 'b'"              , lKeys.contains("b"));
		Assert.assertTrue    ("testListKeys [06] miss key 'c'"              , lKeys.contains("c"));
		Assert.assertTrue    ("testListKeys [07] miss key 'd'"              , lKeys.contains("d"));
	}

	//-------------------------------------------------------------------------
	private ISimplePersistenceTransacted impl_newPersistenceInstance ()
		throws Exception
	{
		final ISimplePersistenceTransacted iInst = SimplePersistenceFactory.get(HZClient.class.getName(),
				   ISimplePersistence.CFG_PERSISTENCE_SCOPE, HazelcastPersistenceTest.class.getName(),
				   HZClient.CFG_SERVER_HOST                , "127.0.0.1",
				   HZClient.CFG_SERVER_PORT                , "4711"     ,
				   HZClient.CFG_SERVER_ID                  , "test"     ,
				   HZClient.CFG_SERVER_PASSWORD            , "test"     );
		
		return iInst;
	}
	
	//-------------------------------------------------------------------------
	private HazelcastInstance m_aHazelcastMock = null;
	
	//-------------------------------------------------------------------------
	private Map< Object, Object > m_aDataSink = null;
}
