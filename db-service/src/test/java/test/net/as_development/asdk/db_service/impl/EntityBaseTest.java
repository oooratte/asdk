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
package test.net.as_development.asdk.db_service.impl;

import org.junit.Test;

import net.as_development.asdk.db_service.EntityBase;
import net.as_development.asdk.tools.test.AssertEx;

//==============================================================================
public class EntityBaseTest
{
    //--------------------------------------------------------------------------
	@Test
	public void testSetExpire ()
		throws Exception
	{
		EntityBase aEntity = new EntityBase ();

		AssertEx.assertNull("testSetExpire [01] default for 'expire' has to be 'null'.", aEntity.Expire);

		// a) Check if calculated expire time (given in seconds) match at least a range of values in the future.
		long nTestExpire          = 10;
		long nMaxDiff             = 50; // allow max difference between our and real calculated expire time of xx ms ... because those values are calculated in sequential order .-)
		long nExpectedExpire      = System.currentTimeMillis() + (nTestExpire * 1000);
		aEntity.setExpireInSeconds(nTestExpire);
		long nActualExpire        = aEntity.Expire;
		long nDiff                = nActualExpire - nExpectedExpire;

		AssertEx.assertTrue("testSetExpire [02] calculated expire time (in seconds) does not fit the range we expected here. Diff between expected and calculated time was "+nDiff+" ms.", nDiff <= nMaxDiff);

		// b) Check if calculated expire time (given in minutes) match at least a range of values in the future.
		nTestExpire          = 10;
		nMaxDiff             = 50; // allow max difference between our and real calculated expire time of xx ms ... because those values are calculated in sequential order .-)
		nExpectedExpire      = System.currentTimeMillis() + (nTestExpire * 1000 * 60);
		aEntity.setExpireInMinutes(nTestExpire);
		nActualExpire        = aEntity.Expire;
		nDiff                = nActualExpire - nExpectedExpire;

		AssertEx.assertTrue("testSetExpire [03] calculated expire time (in minutes) does not fit the range we expected here. Diff between expected and calculated time was "+nDiff+" ms.", nDiff <= nMaxDiff);

		// c) check reset of expire value if it's set to 0
		aEntity.setExpireInSeconds(1);
		AssertEx.assertNotNull("testSetExpire [04] expire set on calling setExpireInSeconds(1).", aEntity.Expire);
		aEntity.setExpireInSeconds(0);
		AssertEx.assertNull   ("testSetExpire [05] expire not rested on calling setExpireInSeconds(0).", aEntity.Expire);
		aEntity.setExpireInMinutes(1);
		AssertEx.assertNotNull("testSetExpire [06] expire set on calling setExpireInMinutes(1).", aEntity.Expire);
		aEntity.setExpireInMinutes(0);
		AssertEx.assertNull   ("testSetExpire [07] expire not rested on calling setExpireInMinutes(0).", aEntity.Expire);
	}

    //--------------------------------------------------------------------------
	@Test
	public void testIsExpired ()
		throws Exception
	{
		long nOffset            = 50;
		long nExpireInSeconds   = 1;
		long nWaitForNonExpired = (nExpireInSeconds * 1000) - nOffset;
		long nWaitForExpired    = (nExpireInSeconds * 1000) + nOffset;

		EntityBase aEntity = new EntityBase ();

		// a) check if isExpired() return FALSE if we call it to early.
		aEntity.setExpireInSeconds(nExpireInSeconds);
		synchronized(this)
		{
			wait(nWaitForNonExpired);
		}
		AssertEx.assertFalse("testIsExpired [01] expect to get FALSE if isExpired() is called to early.", aEntity.isExpired());

		// b) check if isExpired() return TRUE if we call it to late.
		aEntity.setExpireInSeconds(nExpireInSeconds);
		synchronized(this)
		{
			wait(nWaitForExpired);
		}
		AssertEx.assertTrue("testIsExpired [01] expect to get TRUE if isExpired() is called to late.", aEntity.isExpired());
	}
/*
    @todo those feature will be used first if we implement caching .-)
    
    //--------------------------------------------------------------------------
	@Test
	public void testSerialization ()
		throws Exception
	{
		EntityBase aEntity = new EntityBase ();

		ByteArrayOutputStream aByteArray = new ByteArrayOutputStream ();
		ObjectOutputStream    aSerialize = new ObjectOutputStream    (aByteArray);
		aSerialize.writeObject(aEntity);
		aSerialize.flush ();
		aSerialize.close ();

		String sSerialized = new String (aByteArray.toByteArray());
		System.out.println ("entity serialized = \n"+sSerialized);
	}
*/
}
