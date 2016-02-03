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
