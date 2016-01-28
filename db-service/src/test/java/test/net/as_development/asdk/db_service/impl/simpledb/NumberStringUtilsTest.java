/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
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
