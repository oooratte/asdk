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

import org.junit.Test;

import net.as_development.asdk.persistence.ISimplePersistence;
import net.as_development.asdk.persistence.SimplePersistenceConfig;
import net.as_development.asdk.persistence.impl.MemoryPersistence;
import net.as_development.asdk.persistence.impl.SimplePersistenceImpl;
import net.as_development.asdk.tools.common.NumberUtils;

//=============================================================================
public class SimplePersistenceImplTest
{
	//-------------------------------------------------------------------------
	@Test
	public void test()
		throws Exception
	{
		final SimplePersistenceImpl aImpl = new SimplePersistenceImpl ();
		aImpl.configure(SimplePersistenceConfig.CFG_PERSISTENCE_IMPL       , MemoryPersistence.class.getName(),
						SimplePersistenceConfig.CFG_PERSISTENCE_AUTO_COMMIT, "true"                          );

		aImpl.set("scope-01.key-1a", "value-01-a");
		aImpl.set("scope-02.key-2a", "value-02-a");

		System.err.println(aImpl.listKeys());
		System.err.println(aImpl.get("scope-01.key-1a"));
		System.err.println(aImpl.get("scope-02.key-2a"));
		System.err.println(aImpl.get("key-1a"));
		System.err.println(aImpl.get("key-2a"));

		final ISimplePersistence aScope01 = aImpl.getSubset("scope-01");
		System.err.println(aScope01.listKeys()   );
		System.err.println(aScope01.get("key-1a"));
		System.err.println(aScope01.get("key-2a"));

		final ISimplePersistence aScope02 = aImpl.getSubset("scope-02");
		System.err.println(aScope02.listKeys()   );
		System.err.println(aScope02.get("key-1a"));
		System.err.println(aScope02.get("key-2a"));
	}

	//-------------------------------------------------------------------------
	@Test
	public void testSimpleDefaults()
		throws Exception
	{
		final SimplePersistenceImpl aImpl = new SimplePersistenceImpl ();
		aImpl.configure(SimplePersistenceConfig.CFG_PERSISTENCE_IMPL       , MemoryPersistence.class.getName(),
						SimplePersistenceConfig.CFG_PERSISTENCE_AUTO_COMMIT, "true"                          );

		aImpl.set("foo", "2");
		System.err.println(aImpl.setIf("foo", "2", "1"));
	}
}
