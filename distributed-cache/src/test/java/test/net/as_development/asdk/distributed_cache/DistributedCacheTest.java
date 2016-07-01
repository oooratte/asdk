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
package test.net.as_development.asdk.distributed_cache;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Ignore;
import org.junit.Test;

import net.as_development.asdk.distributed_cache.DistributedCache;
import net.as_development.asdk.distributed_cache.impl.ERunMode;

//=============================================================================
@Ignore
public class DistributedCacheTest
{
	//-------------------------------------------------------------------------
	@Test
	public void test()
		throws Exception
	{
		final DistributedCache aServer       = impl_newCache (ERunMode.E_SERVER); 
		final CountDownLatch   aShutdownSync = new CountDownLatch (3);
		final ExecutorService  aBE01         = newBE (1, aShutdownSync);
		final ExecutorService  aBE02         = newBE (2, aShutdownSync);
		final ExecutorService  aBE03         = newBE (3, aShutdownSync);
		aShutdownSync.await();
	}

	//-------------------------------------------------------------------------
	@Test
	public void testListen ()
		throws Exception
	{
		final DistributedCache aCache = new DistributedCache ();
		aCache.connect();

		Thread.sleep(100000);
	}
	
	//-------------------------------------------------------------------------
	@Test
	public void testSend ()
		throws Exception
	{
		final DistributedCache aCache = new DistributedCache ();
		aCache.connect();
		aCache.set("foo", "bar");
		aCache.disconnect();
	}

	//-------------------------------------------------------------------------
	private DistributedCache impl_newCache (final ERunMode eRunMode)
		throws Exception
	{
		final DistributedCache aCache = new DistributedCache ();
		aCache.configure().setRunMode(eRunMode);
		aCache.connect  ();
		return aCache;
	}

	//-------------------------------------------------------------------------
	private ExecutorService newBE (final int            nBE          ,
								   final CountDownLatch aShutdownSync)
	    throws Exception
	{
		final ExecutorService aBE = Executors.newSingleThreadExecutor();
		aBE.execute(new Runnable ()
		{
			private DistributedCache aBECache = impl_newCache (ERunMode.E_CLIENT); 
			
			@Override
			public void run()
			{
				try
				{
					System.err.println("["+nBE+"] : connect ....");
					aBECache.connect();
					for (int i=0; i<10; ++i)
					{
						Thread.sleep(100);

						final String sMsgId = UUID.randomUUID().toString();
						aBECache.set ("msg."+sMsgId+".in" , "true");
						aBECache.set ("msg."+sMsgId+".out", "true");
					}
				}
				catch (Throwable ex)
				{
					System.err.println(ex.getMessage());
				}
				finally
				{
					System.err.println("["+nBE+"] : disconnect ....");
					aBECache.disconnectQuietly();
				}
				
				System.err.println("["+nBE+"] : shutdown ....");
				if (aShutdownSync != null)
					aShutdownSync.countDown();
			}
		});
		return aBE;
	}
}
