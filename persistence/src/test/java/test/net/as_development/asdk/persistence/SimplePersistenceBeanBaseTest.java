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
package test.net.as_development.asdk.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import test.net.as_development.asdk.persistence.impl.HazelcastPersistenceTest;
import test.net.as_development.asdk.persistence.mocks.HazelcastMock;
import ch.qos.logback.classic.BasicConfigurator;
import net.as_development.asdk.persistence.ISimplePersistence;
import net.as_development.asdk.persistence.ISimplePersistenceTransacted;
import net.as_development.asdk.persistence.SimplePersistenceBeanBase;
import net.as_development.asdk.persistence.SimplePersistenceFactory;
import net.as_development.asdk.persistence.impl.HZClient;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

//=============================================================================
@PrepareForTest
(
	{
		HZClient         .class,
		HazelcastInstance.class,
		HazelcastClient  .class,
		Hazelcast        .class
	}
)
@RunWith
(
	PowerMockRunner.class
)
public class SimplePersistenceBeanBaseTest
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
		BasicConfigurator.configureDefaultContext();
		
		m_aDataSink      = new HashMap< Object, Object >();
		m_aHazelcastMock = HazelcastMock.createFullFunctionalMock(m_aDataSink);
	}

	//-------------------------------------------------------------------------
	@Test
	public void testSetGet ()
		throws Exception
	{
		final String KEY_01 = "key-01";
		final String KEY_02 = "key-02";
		final String VAL_01 = "val-01";
		final String VAL_02 = "val-02";
		
		final SimplePersistenceBeanBase    aBean    = new SimplePersistenceBeanBase ();
		final ISimplePersistenceTransacted iPersist = impl_newPersistenceInstance();
		aBean.bindToPeristenceLayer(iPersist);

		aBean.set(KEY_01, VAL_01);
		aBean.set(KEY_02, VAL_02);
		
		Assert.assertEquals ("testSetGet [01] unexpected value for key-01", VAL_01, aBean.get (KEY_01));
		Assert.assertEquals ("testSetGet [02] unexpected value for key-02", VAL_02, aBean.get (KEY_02));
	}
	
	//-------------------------------------------------------------------------
	/** - write changes to bean01
	 *  - DO NOT FLUSH CHANGES
	 *  - on bean02 such values must NOT be accessible
	 *  - flush bean01
	 *  - all values of bean01 has to become accessible on bean02 to
	 */
	@Test
	public void testFlush ()
		throws Exception
	{
		final SimplePersistenceBeanBase    aBean01    = new SimplePersistenceBeanBase ();
		final ISimplePersistenceTransacted iPersist01 = impl_newPersistenceInstance();
		aBean01.bindToPeristenceLayer(iPersist01);

		final SimplePersistenceBeanBase    aBean02    = new SimplePersistenceBeanBase ();
		final ISimplePersistenceTransacted iPersist02 = impl_newPersistenceInstance();
		aBean02.bindToPeristenceLayer(iPersist02);

		aBean01.set("a"  , "a-01"  );
		aBean01.set("a.b", "a.b-02");

		Assert.assertNull ("testFlush [01] value 'a' was not flushed ... but is accessible by any bean"  , aBean02.get("a"  ));
		Assert.assertNull ("testFlush [02] value 'a.b' was not flushed ... but is accessible by any bean", aBean02.get("a.b"));
		
		aBean01.flush();
		
		Assert.assertEquals ("testFlush [03] value 'a' was flushed ... but is not accessible by any bean"  , "a-01"  , aBean02.get("a"  ));
		Assert.assertEquals ("testFlush [04] value 'a.b' was flushed ... but is not accessible by any bean", "a.b-02", aBean02.get("a.b"));
	}


	//-------------------------------------------------------------------------
	@Test
	public void testSubset ()
		throws Exception
	{
		final SimplePersistenceBeanBase    aBean01    = new SimplePersistenceBeanBase ();
		final ISimplePersistenceTransacted iPersist01 = impl_newPersistenceInstance();
		aBean01.bindToPeristenceLayer(iPersist01);

		aBean01.set("a"  , "a-01"  );
		aBean01.set("a.b", "a.b-02");

		aBean01.set("b"  , "b-01"  );
		aBean01.set("b.a", "b.a-02");
		
		aBean01.flush ();

		final SimplePersistenceBeanBase aBean02 = aBean01.getSubset("a", SimplePersistenceBeanBase.class);
		final List< String > lKeys02 = aBean02.list();
		for (final String sKey : lKeys02)
			Assert.assertFalse("testSubset [01] found unexpected key '"+sKey+"' within subset", StringUtils.startsWith(sKey, "a"));
	}

	//-------------------------------------------------------------------------
	@Test
	public void testConcurrentAccess ()
		throws Exception
	{
		final SimplePersistenceBeanBase    aBean01    = new SimplePersistenceBeanBase ();
		final ISimplePersistenceTransacted iPersist01 = impl_newPersistenceInstance();
		aBean01.bindToPeristenceLayer(iPersist01);

		aBean01.set   ("a", "a-01");
		aBean01.set   ("b", "b-01");
		aBean01.flush ();
		
		final SimplePersistenceBeanBase    aBean02    = new SimplePersistenceBeanBase ();
		final ISimplePersistenceTransacted iPersist02 = impl_newPersistenceInstance();
		aBean02.bindToPeristenceLayer(iPersist02);

		Assert.assertEquals ("testConcurrentAccess [01] miss key-value pair 'a'", aBean01.get ("a"), aBean02.get("a"));
		Assert.assertEquals ("testConcurrentAccess [02] miss key-value pair 'b'", aBean01.get ("b"), aBean02.get("b"));
	}

	//-------------------------------------------------------------------------
	private ISimplePersistenceTransacted impl_newPersistenceInstance ()
		throws Exception
	{
		final ISimplePersistenceTransacted iInst = SimplePersistenceFactory.get(HZClient.class.getName(),
				   ISimplePersistence.CFG_PERSISTENCE_SCOPE, SimplePersistenceBeanBaseTest.class.getName(),
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
