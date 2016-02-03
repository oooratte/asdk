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

import org.junit.Assert;
import org.junit.Test;

import net.as_development.asdk.db_service.impl.simpledb.NumberStringUtils;

//==============================================================================
public class NumberStringUtilsTest
{
	//--------------------------------------------------------------------------
	private static final int RANGE = 10;
	
	//--------------------------------------------------------------------------
	@Test
	public void testBytes()
		throws Exception
	{
		impl_testBytes (Byte.MIN_VALUE              );
		impl_testBytes ((byte)(0-(RANGE/2))         );
		impl_testBytes ((byte)(Byte.MAX_VALUE-RANGE));
	}

	//--------------------------------------------------------------------------
	@Test
	public void testShorts()
		throws Exception
	{
		impl_testShorts (Short.MIN_VALUE               );
		impl_testShorts ((short)(0-(RANGE/2))          );
		impl_testShorts ((short)(Short.MAX_VALUE-RANGE));
	}
	
	//--------------------------------------------------------------------------
	@Test
	public void testInts()
		throws Exception
	{
		impl_testInts (Integer.MIN_VALUE             );
		impl_testInts ((int)(0-(RANGE/2))            );
		impl_testInts ((int)(Integer.MAX_VALUE-RANGE));
	}
	
	//--------------------------------------------------------------------------
	@Test
	public void testLongs()
		throws Exception
	{
		impl_testLongs (Long.MIN_VALUE              );
		impl_testLongs ((long)(0-(RANGE/2))         );
		impl_testLongs ((long)(Long.MAX_VALUE-RANGE));
	}
	
	//--------------------------------------------------------------------------
	private void impl_testBytes(byte nStart)
		throws Exception
	{
		String sLast  = null;
		String sNow   = null;
		byte   nValue = nStart;
		
		for (int i=0; i<RANGE; ++i)
		{
			sNow = NumberStringUtils.mapToString(nValue);
			impl_log ("["+nValue+"] -> '"+sNow+"'");

			if (sLast != null)
				Assert.assertTrue ("["+nValue+"] : '"+sLast+"' < '"+sNow+"'", sLast.compareTo(sNow) < 0);
			sLast = sNow;
			
			byte nCheck = NumberStringUtils.mapToNumber(sNow, Byte.class);
			impl_log  ("["+nCheck+"] <- '"+sNow+"'");
			Assert.assertEquals (nCheck+" vs "+nValue, nCheck, nValue);
			
			nValue++;
		}
	}

	//--------------------------------------------------------------------------
	private void impl_testShorts(short nStart)
		throws Exception
	{
		String sLast  = null;
		String sNow   = null;
		short  nValue = nStart;
		
		for (int i=0; i<RANGE; ++i)
		{
			sNow = NumberStringUtils.mapToString(nValue);
			impl_log ("["+nValue+"] -> '"+sNow+"'");
			
			if (sLast != null)
				Assert.assertTrue ("["+nValue+"] : '"+sLast+"' < '"+sNow+"'", sLast.compareTo(sNow) < 0);
			sLast = sNow;
			
			short nCheck = NumberStringUtils.mapToNumber(sNow, Short.class);
			impl_log  ("["+nCheck+"] <- '"+sNow+"'");
			Assert.assertEquals (nCheck+" vs "+nValue, nCheck, nValue);
			
			nValue++;
		}
	}
	
	//--------------------------------------------------------------------------
	private void impl_testInts(int nStart)
		throws Exception
	{
		String sLast  = null;
		String sNow   = null;
		int    nValue = nStart;
		
		for (int i=0; i<RANGE; ++i)
		{
			sNow = NumberStringUtils.mapToString(nValue);
			impl_log ("["+nValue+"] -> '"+sNow+"'");
			
			if (sLast != null)
				Assert.assertTrue ("["+nValue+"] : '"+sLast+"' < '"+sNow+"'", sLast.compareTo(sNow) < 0);
			sLast = sNow;
			
			int nCheck = NumberStringUtils.mapToNumber(sNow, Integer.class);
			impl_log  ("["+nCheck+"] <- '"+sNow+"'");
			Assert.assertEquals (nCheck+" vs "+nValue, nCheck, nValue);
			
			nValue++;
		}
	}

	//--------------------------------------------------------------------------
	private void impl_testLongs(long nStart)
		throws Exception
	{
		String sLast  = null;
		String sNow   = null;
		long   nValue = nStart;
		
		for (int i=0; i<RANGE; ++i)
		{
			sNow = NumberStringUtils.mapToString(nValue);
			impl_log ("["+nValue+"] -> '"+sNow+"'");
			
			if (sLast != null)
				Assert.assertTrue ("["+nValue+"] : '"+sLast+"' < '"+sNow+"'", sLast.compareTo(sNow) < 0);
			sLast = sNow;
			
			long nCheck = NumberStringUtils.mapToNumber(sNow, Long.class);
			impl_log  ("["+nCheck+"] <- '"+sNow+"'");
			Assert.assertEquals (nCheck+" vs "+nValue, nCheck, nValue);
			
			nValue++;
		}
	}
	
	//--------------------------------------------------------------------------
	private void impl_log (String sMsg)
		throws Exception
	{
		System.out.println (sMsg);
	}
}
