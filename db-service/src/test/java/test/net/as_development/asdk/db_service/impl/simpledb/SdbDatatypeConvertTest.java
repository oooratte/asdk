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
public class SdbDatatypeConvertTest
{
    //--------------------------------------------------------------------------
	@Test
	public void testToSdbValueByte ()
		throws Exception
	{
	}		
	/*
    //--------------------------------------------------------------------------
	@Test
	public void testToSdbValueByte ()
		throws Exception
	{
		String sMin = String.valueOf(Byte.MIN_VALUE);
		String sMax = String.valueOf(Byte.MAX_VALUE);
		String sNull = SdbDatatypeConvert.NULL;
		
		Assert.assertEquals("testToSdbValueByte [01]", "-10", SdbDatatypeConvert.toSdbValue(byte.class, -10           , false));
		Assert.assertEquals("testToSdbValueByte [02]", "28" , SdbDatatypeConvert.toSdbValue(byte.class, 28            , false));
		Assert.assertEquals("testToSdbValueByte [03]", sMin , SdbDatatypeConvert.toSdbValue(byte.class, Byte.MIN_VALUE, false));
		Assert.assertEquals("testToSdbValueByte [04]", sMax , SdbDatatypeConvert.toSdbValue(byte.class, Byte.MAX_VALUE, false));

		Assert.assertEquals("testToSdbValueByte [05]", sNull, SdbDatatypeConvert.toSdbValue(Byte.class, null          , false));

		Assert.assertEquals("testToSdbValueByte [06]", "-57", SdbDatatypeConvert.toSdbValue(Byte.class, -57           , false));
		Assert.assertEquals("testToSdbValueByte [07]", "38" , SdbDatatypeConvert.toSdbValue(Byte.class, 38            , false));
		Assert.assertEquals("testToSdbValueByte [08]", sMin , SdbDatatypeConvert.toSdbValue(Byte.class, Byte.MIN_VALUE, false));
		Assert.assertEquals("testToSdbValueByte [09]", sMax , SdbDatatypeConvert.toSdbValue(Byte.class, Byte.MAX_VALUE, false));
	}

    //--------------------------------------------------------------------------
	@Test
	public void testToSdbValueShort ()
		throws Exception
	{
		String sMin = String.valueOf(Short.MIN_VALUE);
		String sMax = String.valueOf(Short.MAX_VALUE);
		String sNull = SdbDatatypeConvert.NULL;
		
		Assert.assertEquals("testToSdbValueShort [01]", "-389"  , SdbDatatypeConvert.toSdbValue(short.class, -389           , false));
		Assert.assertEquals("testToSdbValueShort [02]", "1687"  , SdbDatatypeConvert.toSdbValue(short.class, 1687           , false));
		Assert.assertEquals("testToSdbValueShort [03]", sMin    , SdbDatatypeConvert.toSdbValue(short.class, Short.MIN_VALUE, false));
		Assert.assertEquals("testToSdbValueShort [04]", sMax    , SdbDatatypeConvert.toSdbValue(short.class, Short.MAX_VALUE, false));

		Assert.assertEquals("testToSdbValueShort [05]", sNull   , SdbDatatypeConvert.toSdbValue(Short.class, null           , false));

		Assert.assertEquals("testToSdbValueShort [06]", "-19356", SdbDatatypeConvert.toSdbValue(Short.class, -19356         , false));
		Assert.assertEquals("testToSdbValueShort [07]", "9078"  , SdbDatatypeConvert.toSdbValue(Short.class, 9078           , false));
		Assert.assertEquals("testToSdbValueShort [08]", sMin    , SdbDatatypeConvert.toSdbValue(Short.class, Short.MIN_VALUE, false));
		Assert.assertEquals("testToSdbValueShort [09]", sMax    , SdbDatatypeConvert.toSdbValue(Short.class, Short.MAX_VALUE, false));
	}

	//--------------------------------------------------------------------------
	@Test
	public void testToSdbValueInt ()
		throws Exception
	{
		String sMin = String.valueOf(Integer.MIN_VALUE);
		String sMax = String.valueOf(Integer.MAX_VALUE);
		String sNull = SdbDatatypeConvert.NULL;
		
		Assert.assertEquals("testToSdbValueInteger [01]", "-192837"  , SdbDatatypeConvert.toSdbValue(int.class, -192837          , false));
		Assert.assertEquals("testToSdbValueInteger [02]", "12123434" , SdbDatatypeConvert.toSdbValue(int.class, 12123434         , false));
		Assert.assertEquals("testToSdbValueInteger [03]", sMin       , SdbDatatypeConvert.toSdbValue(int.class, Integer.MIN_VALUE, false));
		Assert.assertEquals("testToSdbValueInteger [04]", sMax       , SdbDatatypeConvert.toSdbValue(int.class, Integer.MAX_VALUE, false));

		Assert.assertEquals("testToSdbValueInteger [05]", sNull      , SdbDatatypeConvert.toSdbValue(Integer.class, null             , false));

		Assert.assertEquals("testToSdbValueInteger [06]", "-11223344", SdbDatatypeConvert.toSdbValue(Integer.class, -11223344        , false));
		Assert.assertEquals("testToSdbValueInteger [07]", "9078"     , SdbDatatypeConvert.toSdbValue(Integer.class, 9078             , false));
		Assert.assertEquals("testToSdbValueInteger [08]", sMin       , SdbDatatypeConvert.toSdbValue(Integer.class, Integer.MIN_VALUE, false));
		Assert.assertEquals("testToSdbValueInteger [09]", sMax       , SdbDatatypeConvert.toSdbValue(Integer.class, Integer.MAX_VALUE, false));
	}

	//--------------------------------------------------------------------------
	@Test
	public void testToSdbValueLong ()
		throws Exception
	{
		String sMin = String.valueOf(Long.MIN_VALUE);
		String sMax = String.valueOf(Long.MAX_VALUE);
		String sNull = SdbDatatypeConvert.NULL;
		
		Assert.assertEquals("testToSdbValueLong [01]", "-192837"  , SdbDatatypeConvert.toSdbValue(long.class, -192837          , false));
		Assert.assertEquals("testToSdbValueLong [02]", "12123434" , SdbDatatypeConvert.toSdbValue(long.class, 12123434         , false));
		Assert.assertEquals("testToSdbValueLong [03]", sMin       , SdbDatatypeConvert.toSdbValue(long.class, Long.MIN_VALUE, false));
		Assert.assertEquals("testToSdbValueLong [04]", sMax       , SdbDatatypeConvert.toSdbValue(long.class, Long.MAX_VALUE, false));

		Assert.assertEquals("testToSdbValueLong [05]", sNull      , SdbDatatypeConvert.toSdbValue(Long.class, null             , false));

		Assert.assertEquals("testToSdbValueLong [06]", "-11223344", SdbDatatypeConvert.toSdbValue(Long.class, -11223344        , false));
		Assert.assertEquals("testToSdbValueLong [07]", "9078"     , SdbDatatypeConvert.toSdbValue(Long.class, 9078             , false));
		Assert.assertEquals("testToSdbValueLong [08]", sMin       , SdbDatatypeConvert.toSdbValue(Long.class, Long.MIN_VALUE, false));
		Assert.assertEquals("testToSdbValueLong [09]", sMax       , SdbDatatypeConvert.toSdbValue(Long.class, Long.MAX_VALUE, false));
	}

	//--------------------------------------------------------------------------
	@Test
	public void testToSdbValueDouble ()
		throws Exception
	{
		String sMin = String.valueOf(Double.MIN_VALUE);
		String sMax = String.valueOf(Double.MAX_VALUE);
		String sNull = SdbDatatypeConvert.NULL;
		
		Assert.assertEquals("testToSdbValueDouble [01]", "-157.289"  , SdbDatatypeConvert.toSdbValue(double.class, -157.289        , false));
		Assert.assertEquals("testToSdbValueDouble [02]", "4477.8892" , SdbDatatypeConvert.toSdbValue(double.class, 4477.8892       , false));
		Assert.assertEquals("testToSdbValueDouble [03]", sMin        , SdbDatatypeConvert.toSdbValue(double.class, Double.MIN_VALUE, false));
		Assert.assertEquals("testToSdbValueDouble [04]", sMax        , SdbDatatypeConvert.toSdbValue(double.class, Double.MAX_VALUE, false));

		Assert.assertEquals("testToSdbValueDouble [05]", sNull       , SdbDatatypeConvert.toSdbValue(Double.class, null            , false));

		Assert.assertEquals("testToSdbValueDouble [06]", "-11223344" , SdbDatatypeConvert.toSdbValue(Double.class, -11223344       , false));
		Assert.assertEquals("testToSdbValueDouble [07]", "9078"      , SdbDatatypeConvert.toSdbValue(Double.class, 9078            , false));
		Assert.assertEquals("testToSdbValueDouble [08]", sMin        , SdbDatatypeConvert.toSdbValue(Double.class, Double.MIN_VALUE, false));
		Assert.assertEquals("testToSdbValueDouble [09]", sMax        , SdbDatatypeConvert.toSdbValue(Double.class, Double.MAX_VALUE, false));

		// check trailing 0 special .-)
		Assert.assertEquals("testToSdbValueDouble [02]", "1.101"     , SdbDatatypeConvert.toSdbValue(double.class, 1.101           , false));
		Assert.assertEquals("testToSdbValueDouble [02]", "1.101"     , SdbDatatypeConvert.toSdbValue(double.class, 1.1010          , false));
		Assert.assertEquals("testToSdbValueDouble [02]", "1.101"     , SdbDatatypeConvert.toSdbValue(double.class, 1.10100         , false));

		Assert.assertEquals("testToSdbValueDouble [02]", "34.101"    , SdbDatatypeConvert.toSdbValue(Double.class, 34.101          , false));
		Assert.assertEquals("testToSdbValueDouble [02]", "34.101"    , SdbDatatypeConvert.toSdbValue(Double.class, 34.1010         , false));
		Assert.assertEquals("testToSdbValueDouble [02]", "34.101"    , SdbDatatypeConvert.toSdbValue(Double.class, 34.10100        , false));
	}

	//--------------------------------------------------------------------------
	@Test
	public void testToSdbValueFloat ()
		throws Exception
	{
		String sMin = String.valueOf(Float.MIN_VALUE);
		String sMax = String.valueOf(Float.MAX_VALUE);
		String sNull = SdbDatatypeConvert.NULL;
		
		Assert.assertEquals("testToSdbValueFloat [01]", "-157.289"  , SdbDatatypeConvert.toSdbValue(float.class, -157.289        , false));
		Assert.assertEquals("testToSdbValueFloat [02]", "4477.8892" , SdbDatatypeConvert.toSdbValue(float.class, 4477.8892       , false));
		Assert.assertEquals("testToSdbValueFloat [03]", sMin        , SdbDatatypeConvert.toSdbValue(float.class, Float.MIN_VALUE, false));
		Assert.assertEquals("testToSdbValueFloat [04]", sMax        , SdbDatatypeConvert.toSdbValue(float.class, Float.MAX_VALUE, false));

		Assert.assertEquals("testToSdbValueFloat [05]", sNull       , SdbDatatypeConvert.toSdbValue(Float.class, null            , false));

		Assert.assertEquals("testToSdbValueFloat [06]", "-11223344" , SdbDatatypeConvert.toSdbValue(Float.class, -11223344       , false));
		Assert.assertEquals("testToSdbValueFloat [07]", "9078"      , SdbDatatypeConvert.toSdbValue(Float.class, 9078            , false));
		Assert.assertEquals("testToSdbValueFloat [08]", sMin        , SdbDatatypeConvert.toSdbValue(Float.class, Float.MIN_VALUE, false));
		Assert.assertEquals("testToSdbValueFloat [09]", sMax        , SdbDatatypeConvert.toSdbValue(Float.class, Float.MAX_VALUE, false));

		// check trailing 0 special .-)
		Assert.assertEquals("testToSdbValueFloat [10]", "1.101"     , SdbDatatypeConvert.toSdbValue(float.class, 1.101           , false));
		Assert.assertEquals("testToSdbValueFloat [11]", "1.101"     , SdbDatatypeConvert.toSdbValue(float.class, 1.1010          , false));
		Assert.assertEquals("testToSdbValueFloat [12]", "1.101"     , SdbDatatypeConvert.toSdbValue(float.class, 1.10100         , false));

		Assert.assertEquals("testToSdbValueFloat [13]", "34.101"    , SdbDatatypeConvert.toSdbValue(Float.class, 34.101          , false));
		Assert.assertEquals("testToSdbValueFloat [14]", "34.101"    , SdbDatatypeConvert.toSdbValue(Float.class, 34.1010         , false));
		Assert.assertEquals("testToSdbValueFloat [15]", "34.101"    , SdbDatatypeConvert.toSdbValue(Float.class, 34.10100        , false));
	}

	//--------------------------------------------------------------------------
	@Test
	public void testToSdbValueString ()
		throws Exception
	{
		String sNull = SdbDatatypeConvert.NULL;

		Assert.assertEquals("testToSdbValueString [01]", sNull                 , SdbDatatypeConvert.toSdbValue(String.class, null                  , false));
		Assert.assertEquals("testToSdbValueString [01]", ""                    , SdbDatatypeConvert.toSdbValue(String.class, ""                    , false));
		Assert.assertEquals("testToSdbValueString [01]", "any string"          , SdbDatatypeConvert.toSdbValue(String.class, "any string"          , false));
		Assert.assertEquals("testToSdbValueString [01]", "öäüß#+*-.,;:§$%&/()=", SdbDatatypeConvert.toSdbValue(String.class, "öäüß#+*-.,;:§$%&/()=", false));
		Assert.assertEquals("testToSdbValueString [01]", "\\\"\n\r\t"          , SdbDatatypeConvert.toSdbValue(String.class, "\\\"\n\r\t"          , false));
	}

	//--------------------------------------------------------------------------
	@Test
	public void testToSdbValueDate ()
		throws Exception
	{
		Date   aDate = new Date ();
		String sDate = Long.toString(aDate.getTime());
		String sNull = SdbDatatypeConvert.NULL;

		Assert.assertEquals("testToSdbValueDate [01]", sNull, SdbDatatypeConvert.toSdbValue(Date.class, null , false));
		Assert.assertEquals("testToSdbValueDate [01]", sDate, SdbDatatypeConvert.toSdbValue(Date.class, aDate, false));
	}

	//--------------------------------------------------------------------------
	@Test
	public void testToSdbValueObject ()
		throws Exception
	{
		// support that first ... test it then .-)
	}
	*/
}
