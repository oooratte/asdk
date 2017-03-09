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
package test.net.as_development.asdk.tools.common;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import net.as_development.asdk.tools.common.CollectionUtils;

//=============================================================================
public class CollectionUtilsTest
{
	//-------------------------------------------------------------------------
	@Test
	public void testCopy()
		throws Exception
	{
		final int LIST_COUNT = 10;
		
		final List< Integer > lSource = new ArrayList< Integer > ();
		final List< Integer > lTarget = new ArrayList< Integer > ();
		
		for (int i=1; i<=LIST_COUNT; ++i)
			lSource.add(i);
		
		CollectionUtils.copy(lSource, lTarget);
		
		Assert.assertEquals("testCopy [01] not all items was copied", LIST_COUNT, lTarget.size());
		for (int i=1; i<=LIST_COUNT; ++i)
			Assert.assertEquals("testCopy [02] miss item '"+i+"'", (Integer)i, (Integer)lTarget.get(i-1));
		
		lSource.add(LIST_COUNT+1);
		Assert.assertEquals("testCopy [03] source and copied list are not independent", LIST_COUNT, lTarget.size());
	}
}
