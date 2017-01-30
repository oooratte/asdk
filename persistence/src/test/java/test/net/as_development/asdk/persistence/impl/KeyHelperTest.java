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

import org.junit.Assert;
import org.junit.Test;

import net.as_development.asdk.persistence.impl.KeyHelper;

//=============================================================================
public class KeyHelperTest
{
	//-------------------------------------------------------------------------
	@Test
	public void testNameKey()
		throws Exception
	{
		Assert.assertEquals("testNameKey [01]", ""                             , KeyHelper.nameKey(null, null));
		Assert.assertEquals("testNameKey [02]", ""                             , KeyHelper.nameKey(null, ""  ));
		Assert.assertEquals("testNameKey [03]", ""                             , KeyHelper.nameKey(""  , null));
		Assert.assertEquals("testNameKey [04]", ""                             , KeyHelper.nameKey(""  , ""  ));
		Assert.assertEquals("testNameKey [05]", "a"                            , KeyHelper.nameKey("a"       ));
		Assert.assertEquals("testNameKey [06]", "a"+KeyHelper.KEY_SEPARATOR+"b", KeyHelper.nameKey("a" , "b" ));
	}

	//-------------------------------------------------------------------------
	@Test
	public void testMakeKeyAbsolute()
		throws Exception
	{
		Assert.assertEquals("testMakeKeyAbsolute [01]", "scope"   +KeyHelper.KEY_SEPARATOR                                    +"a", KeyHelper.makeKeyAbsolute("scope"   , null       , "a"));
		Assert.assertEquals("testMakeKeyAbsolute [02]", "scope-01"+KeyHelper.KEY_SEPARATOR+"subset-01"+KeyHelper.KEY_SEPARATOR+"a", KeyHelper.makeKeyAbsolute("scope-01", "subset-01", "a"));
	}

	//-------------------------------------------------------------------------
	@Test
	public void testMakeKeyRelative()
		throws Exception
	{
		Assert.assertEquals("testMakeKeyRelative [01]", "a"                            , KeyHelper.makeKeyRelative("scope"   , null       , "scope"   +KeyHelper.KEY_SEPARATOR                                    +"a"));
		Assert.assertEquals("testMakeKeyRelative [02]", "a"                            , KeyHelper.makeKeyRelative("scope-01", "subset-01", "scope-01"+KeyHelper.KEY_SEPARATOR+"subset-01"+KeyHelper.KEY_SEPARATOR+"a"));
		Assert.assertEquals("testMakeKeyRelative [03]", "a"+KeyHelper.KEY_SEPARATOR+"b", KeyHelper.makeKeyRelative(null      , null       , "a"       +KeyHelper.KEY_SEPARATOR+"b"                                    ));

		try
		{
			KeyHelper.makeKeyRelative("scope-01", "subset-01", "scope-xx"+KeyHelper.KEY_SEPARATOR+"subset-yy"+KeyHelper.KEY_SEPARATOR+"a");
			Assert.fail ("testMakeKeyRelative [04] miss exception for key in different scope");
		}
		catch (Throwable ex)
		{}
	}

	//-------------------------------------------------------------------------
	@Test
	public void testIsAbsoluteKeyInScopeSubset()
		throws Exception
	{
		Assert.assertTrue ("testIsAbsoluteKeyInScopeSubset [01]", KeyHelper.isAbsoluteKeyInScopeSubset(null   , null    , "a"    +KeyHelper.KEY_SEPARATOR+"b"                                 ));
		Assert.assertFalse("testIsAbsoluteKeyInScopeSubset [02]", KeyHelper.isAbsoluteKeyInScopeSubset("scope", null    , "a"    +KeyHelper.KEY_SEPARATOR+"b"                                 ));
		Assert.assertTrue ("testIsAbsoluteKeyInScopeSubset [03]", KeyHelper.isAbsoluteKeyInScopeSubset("scope", "subset", "scope"+KeyHelper.KEY_SEPARATOR+"subset"+KeyHelper.KEY_SEPARATOR+"a"));
	}
}
