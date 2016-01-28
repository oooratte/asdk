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
