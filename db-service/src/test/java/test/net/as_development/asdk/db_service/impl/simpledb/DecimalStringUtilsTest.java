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
package test.net.as_development.asdk.db_service.impl.simpledb;

import org.junit.Test;

//==============================================================================
public class DecimalStringUtilsTest
{
	//--------------------------------------------------------------------------
	private static final int RANGE = 10;
	
	//--------------------------------------------------------------------------
	@Test
	public void testDummy ()
		throws Exception
	{
		// does nothing ...
		// but test run itself wont fail by 'missing test method' .-)
	}
/*
	//--------------------------------------------------------------------------
	@Test
	public void testFloats()
		throws Exception
	{
		impl_testFloats (Float.MIN_VALUE               );
		impl_testFloats ((float)(0-(RANGE/2))          );
		impl_testFloats ((float)(Float.MAX_VALUE-RANGE));
	}
	
	//--------------------------------------------------------------------------
	private void impl_testFloats(float nStart)
		throws Exception
	{
		String sLast  = null;
		String sNow   = null;
		float  nValue = nStart;
		
		for (int i=0; i<RANGE; ++i)
		{
			sNow = DecimalStringUtils.mapToString(nValue);
			impl_log ("["+nValue+"] -> '"+sNow+"'");

			if (sLast != null)
				Assert.assertTrue ("["+nValue+"] : '"+sLast+"' < '"+sNow+"'", sLast.compareTo(sNow) < 0);
			sLast = sNow;
			
			float nCheck = DecimalStringUtils.mapToNumber(sNow, Float.class);
			impl_log  ("["+nCheck+"] <- '"+sNow+"'");
			Assert.assertEquals (nCheck+" vs "+nValue, nCheck, nValue, 1);
			
			nValue+=1.00F;
		}
	}

	//--------------------------------------------------------------------------
	private void impl_testDoubles(double nStart)
		throws Exception
	{
		String sLast  = null;
		String sNow   = null;
		double nValue = nStart;
		
		for (int i=0; i<RANGE; ++i)
		{
			sNow = DecimalStringUtils.mapToString(nValue);
			impl_log ("["+nValue+"] -> '"+sNow+"'");
			
			if (sLast != null)
				Assert.assertTrue ("["+nValue+"] : '"+sLast+"' < '"+sNow+"'", sLast.compareTo(sNow) < 0);
			sLast = sNow;
			
			double nCheck = DecimalStringUtils.mapToNumber(sNow, Double.class);
			impl_log  ("["+nCheck+"] <- '"+sNow+"'");
			Assert.assertEquals (nCheck+" vs "+nValue, nCheck, nValue, 1);
			
			nValue++;
		}
	}
	
	//--------------------------------------------------------------------------
	private void impl_log (String sMsg)
		throws Exception
	{
		System.out.println (sMsg);
	}
*/
}
