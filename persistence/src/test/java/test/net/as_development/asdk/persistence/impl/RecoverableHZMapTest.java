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
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.hazelcast.core.IMap;

import net.as_development.asdk.persistence.impl.SimpleRecoverableHZMap;
import test.net.as_development.asdk.persistence.mocks.IMapMock;

//=============================================================================
public class RecoverableHZMapTest
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
	public void test()
		throws Exception
	{
		final Map < String, String >              aDataSink = new HashMap< String, String > ();
		final IMap< String, String >               iMap      = IMapMock.createFullFunctionalMock(aDataSink, "test-map");
		
//		iMap.put("a", "01");
//		iMap.put("b", "02");

		System.err.println (aDataSink);
		
		final SimpleRecoverableHZMap< String, String > aMap = SimpleRecoverableHZMap.create(iMap);

//		iMap.put("c", "03");
//		iMap.put("b", "04");
//		iMap.put("a", null);

		System.err.println (aDataSink);
	}
}
