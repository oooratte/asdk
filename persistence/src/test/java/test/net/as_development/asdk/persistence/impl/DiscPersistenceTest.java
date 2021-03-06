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

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import net.as_development.asdk.persistence.SimplePersistenceConfig;
import net.as_development.asdk.persistence.impl.DiscPersistence;

//=============================================================================
public class DiscPersistenceTest
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
	public void testInitialEmpty()
		throws Exception
	{
		final DiscPersistence aDisc = new DiscPersistence ();
		aDisc.configure(SimplePersistenceConfig.CFG_PERSISTENCE_SCOPE, "test-disc-persistence-initial-empty");

		final List< String >  lKeys = aDisc.listKeys();
		
		Assert.assertNotNull ("testInitialEmpty [01] has not to be null", lKeys          );
		Assert.assertTrue    ("testInitialEmpty [02] is not empty"      , lKeys.isEmpty());
	}

	//-------------------------------------------------------------------------
	@Test
	public void testSetGet()
		throws Exception
	{
		final DiscPersistence aDisc = new DiscPersistence ();
		aDisc.configure(SimplePersistenceConfig.CFG_PERSISTENCE_SCOPE, "test-disc-persistence-set-get");

		final Map< String, Object > lChanges = new HashMap< String, Object > ();
		lChanges.put ("1", "value-01");
		lChanges.put ("2", "value-02");
		
		aDisc.set(lChanges);
		
		Assert.assertEquals ("testSetGetCommit [01] wrong value", "value-01", aDisc.get("1"));
		Assert.assertEquals ("testSetGetCommit [02] wrong value", "value-02", aDisc.get("2"));
	}
}
