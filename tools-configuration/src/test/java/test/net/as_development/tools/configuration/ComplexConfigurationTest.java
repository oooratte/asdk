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
package test.net.as_development.tools.configuration;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import net.as_development.tools.configuration.ConfigurationFactory;
import net.as_development.tools.configuration.IComplexConfiguration;

//=============================================================================
public class ComplexConfigurationTest
{
	//-------------------------------------------------------------------------
	@Test
	public void testSimpleGet ()
		throws Exception
	{
		final String TEST_KEY   = "key.value";
		final String TEST_VALUE = "value";
		
		final IComplexConfiguration iConfig = ConfigurationFactory.getComplexConfiguration("classpath:/test/net/as_development/tools/configuration/res", "complex_test");
		Assert.assertEquals("testSimpleGet [01]", TEST_VALUE, iConfig.get(TEST_KEY, String.class));
	}

	//-------------------------------------------------------------------------
	@Test
	public void testGetChildKeysOfType ()
		throws Exception
	{
		final String TEST_ROOTKEY      = "list";
		final String TEST_KEYTYPE      = "item";
		final int    EXPECTED_LISTSIZE = 5;
		
		final IComplexConfiguration iConfig = ConfigurationFactory.getComplexConfiguration("classpath:/test/net/as_development/tools/configuration/res", "complex_test");
		final Set< Map< String, String > > lItems  = iConfig.gets(TEST_ROOTKEY, TEST_KEYTYPE);
		Assert.assertEquals ("testGetChildKeysOfType [01]", EXPECTED_LISTSIZE, lItems.size());
		
		final Iterator< Map< String, String > > rItems = lItems.iterator();
		while (rItems.hasNext())
		{
			final Map< String, String > aItem = rItems.next();
			
			final String sItemName  = aItem.get("name" );
			final String sItemValue = aItem.get("value");
			
			Assert.assertTrue("testGetChildKeysOfType [02]", StringUtils.startsWith(sItemName , "Item-0" ));
			Assert.assertTrue("testGetChildKeysOfType [03]", StringUtils.startsWith(sItemValue, "Value-0"));
		}
	}
	//-------------------------------------------------------------------------
	@Test
	public void testVariableSubstitution ()
		throws Exception
	{
		final String TEST_KEY_4_TEMP = "var.using.temp";
		final String TEMP_DIR        = FileUtils.getTempDirectoryPath();

		final IComplexConfiguration iConfig = ConfigurationFactory.getComplexConfiguration("classpath:/test/net/as_development/tools/configuration/res", "complex_test");
		final String sValue = iConfig.get(TEST_KEY_4_TEMP, String.class);

		Assert.assertFalse ("testVariableSubstitution [01] found non substituted variables", StringUtils.contains  (sValue, "${"    ));
		Assert.assertTrue  ("testVariableSubstitution [02] temp variable not substituted"  , StringUtils.startsWith(sValue, TEMP_DIR));
	}
}
