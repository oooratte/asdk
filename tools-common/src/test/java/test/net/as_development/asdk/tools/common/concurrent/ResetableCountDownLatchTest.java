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
package test.net.as_development.asdk.tools.common.concurrent;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Test;

import net.as_development.asdk.tools.common.concurrent.ResetableCountDownLatch;

//=============================================================================
public class ResetableCountDownLatchTest
{
	//-------------------------------------------------------------------------
	@Test
	public void testReusingOfLatch ()
		throws Exception
	{
		final ResetableCountDownLatch aLatch = new ResetableCountDownLatch (1);

		aLatch.reset();
		impl_useAndCheckLatch (aLatch);

		aLatch.reset();
		impl_useAndCheckLatch (aLatch);
	}

	//-------------------------------------------------------------------------
	@Test
	public void testResetAwaitSync ()
		throws Exception
	{
		final ResetableCountDownLatch aLatch = new ResetableCountDownLatch (1);
		Executors.newSingleThreadExecutor().execute(new Runnable ()
		{
			@Override
			public void run ()
			{
				try
				{
					System.err.println("... await");
					aLatch.await(2000, TimeUnit.MILLISECONDS);
				}
				catch (Throwable ex)
				{
					// ignored
				}
			}
		});
		
		try
		{
			Thread.sleep(500);
			System.err.println("... reset");
			aLatch.reset();
			Assert.fail("testResetAwaitSync [01] miss IllegalStateException for latch with awaiting clients where reset is called");
		}
		catch (IllegalStateException exIllegalState)
		{
			System.err.println("... got expected exception");
		}
		System.err.println("OK");
	}

	//-------------------------------------------------------------------------
	private void impl_useAndCheckLatch (final ResetableCountDownLatch aLatch)
		throws Exception
    {
		Executors.newSingleThreadExecutor().execute(new Runnable ()
		{
			@Override
			public void run ()
			{
				try
				{
					System.err.println("... sleep");
					Thread.sleep(250);
					System.err.println("... count down");
					aLatch.countDown();
				}
				catch (Throwable ex)
				{
					// ???
				}
			}
		});
		
		System.err.println("... wait");
		final boolean bOK = aLatch.await(2000, TimeUnit.MILLISECONDS);
		if ( ! bOK)
			throw new Exception ("await timed out");

		System.err.println("OK");
    }
}
