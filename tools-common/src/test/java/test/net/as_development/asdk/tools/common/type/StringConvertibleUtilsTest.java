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
package test.net.as_development.asdk.tools.common.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import junit.framework.Assert;
import net.as_development.asdk.tools.common.CollectionUtils;
import net.as_development.asdk.tools.common.type.StringConvertibleUtils;

//=============================================================================
public class StringConvertibleUtilsTest
{
	//-------------------------------------------------------------------------
	@Test
	public void test()
		throws Exception
	{
		final String                STRING_VALUE    = "string-a";
		final long                  LONG_VALUE      = 1L;
		final double                DOUBLE_VALUE    = 36.5d;
		final Object                NULL_VALUE      = null;
		final List< Long >          LONG_LIST_VALUE = new ArrayList< Long > ();
		final Map< String, Object > MAP_VALUE       = new HashMap< String, Object > ();

		LONG_LIST_VALUE.add(10L );
		LONG_LIST_VALUE.add(null);
		LONG_LIST_VALUE.add(25L );
		
		MAP_VALUE.put("n-46-value"  , 46  );
		MAP_VALUE.put("a-null-value", null);
		
		final String sEncoded = StringConvertibleUtils.convertToString(STRING_VALUE, LONG_VALUE, DOUBLE_VALUE, NULL_VALUE, LONG_LIST_VALUE, MAP_VALUE);
		Assert.assertFalse("test [01] encoding failed : no encoded string returned", StringUtils.isEmpty(sEncoded));

		final List< Object > lDecoded = StringConvertibleUtils.convertFromString(sEncoded);
		Assert.assertNotNull("test [02] has never return NULL as result !" ,    lDecoded       );
		Assert.assertEquals ("test [03] unexpected count of decoded values", 6, lDecoded.size());

		Assert.assertEquals ("test [04a] unexpected STRING_VALUE (or position in list!)"   , STRING_VALUE   , lDecoded.get(0));
		Assert.assertEquals ("test [04b] unexpected LONG_VALUE (or position in list!)"     , LONG_VALUE     , lDecoded.get(1));
		Assert.assertEquals ("test [04c] unexpected DOUBLE_VALUE (or position in list!)"   , DOUBLE_VALUE   , lDecoded.get(2));
		Assert.assertEquals ("test [04d] unexpected NULL_VALUE (or position in list!)"     , NULL_VALUE     , lDecoded.get(3));
		Assert.assertEquals ("test [04e] unexpected LONG_LIST_VALUE (or position in list!)", LONG_LIST_VALUE, lDecoded.get(4));
		Assert.assertEquals ("test [04f] unexpected MAP_VALUE (or position in list!)"      , MAP_VALUE      , lDecoded.get(5));
	}
}
