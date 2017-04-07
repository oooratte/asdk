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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Test;

import junit.framework.Assert;
import net.as_development.asdk.distributed_cache.DistributedCache;
import net.as_development.asdk.distributed_cache.DistributedCacheItem;
import net.as_development.asdk.distributed_cache.DistributedCacheSink;
import net.as_development.asdk.distributed_cache.impl.ERunMode;
import net.as_development.asdk.tools.common.CollectionUtils;
import net.as_development.asdk.tools.common.pattern.observation.Observer;
import net.as_development.asdk.tools.logging.ELogLevel;
import net.as_development.asdk.tools.logging.LoggerFactory;
import net.as_development.asdk.tools.logging.impl.Logger;

//=============================================================================

public class DistributedCacheTest
{
	//-------------------------------------------------------------------------
	private static Logger LOG = LoggerFactory.newLogger(DistributedCacheTest.class);

	//-------------------------------------------------------------------------
	@Test
	public void test()
		throws Exception
	{
		final int              nMessagesPerBE    = 1;
		final DistributedCache aServer           = newCache ("server", ERunMode.E_SERVER); 
		final CountDownLatch   aShutdownSync     = new CountDownLatch (3);
		final ExecutorService  aBE01             = newBE (1, nMessagesPerBE, aShutdownSync);
		final ExecutorService  aBE02             = newBE (2, nMessagesPerBE, aShutdownSync);
		final ExecutorService  aBE03             = newBE (3, nMessagesPerBE, aShutdownSync);
		final int              nExpectedMessages = 3 * nMessagesPerBE;

		aShutdownSync.await();
		Thread.sleep(1000); // not fine ... but (for the moment) functional ;-)

		final DistributedCacheSink aCachSink = aServer.getCacheSink();
		final List< String >       lAll      = aCachSink.listAll();
		Assert.assertNotNull("test [01] never return NULL here !", lAll          );
		Assert.assertFalse  ("test [02] miss all messages"       , lAll.isEmpty());

		System.err.println (CollectionUtils.toString(lAll, '\n'));
		
		Assert.assertEquals("test [03] miss some messages", nExpectedMessages, lAll.size());
	}

	//-------------------------------------------------------------------------
	@Test
	public void testCacheSink()
		throws Exception
	{
		final DistributedCache                 aServer        = newCache ("server", ERunMode.E_SERVER); 
		final DistributedCacheSink             aCacheSink     = aServer.getCacheSink();
		final Map< String, String >            aObserverCache = new HashMap< String, String > ();
		final Observer< DistributedCacheItem > aObserver      = new Observer< DistributedCacheItem > ()
		{
			@Override
			public void notify(final DistributedCacheItem aCacheItem)
				throws Exception
			{
				aObserverCache.put(aCacheItem.sKey, aCacheItem.sValue);
			}
		};
		
		aCacheSink.addObserver(aObserver);

		final String TEST_KEY_01   = "key.01"  ;
		final String TEST_VALUE_01 = "value.01";
		final String TEST_KEY_02   = "key.02"  ;
		final String TEST_VALUE_02 = "value.02";
		
		// a) new key should reach internal cache sink ... and should be notified to registered observer 
		
		aServer.set(TEST_KEY_01, TEST_VALUE_01);
		
		Assert.assertEquals("testCacheSink [01] new set value didnt reached the cache sink", TEST_VALUE_01, aCacheSink    .get(TEST_KEY_01));
		Assert.assertEquals("testCacheSink [02] new set value didnt reached the observer"  , TEST_VALUE_01, aObserverCache.get(TEST_KEY_01));

		// b) disable internal cache sink ... now new keys will be notified to registered observer only
		
		aCacheSink.setCachingEnabled(false);

		aServer.set(TEST_KEY_02, TEST_VALUE_02);

		Assert.assertEquals("testCacheSink [01] new set value didnt reached the cache sink", null         , aCacheSink    .get(TEST_KEY_02));
		Assert.assertEquals("testCacheSink [02] new set value didnt reached the observer"  , TEST_VALUE_02, aObserverCache.get(TEST_KEY_02));
	}

	//-------------------------------------------------------------------------
	private DistributedCache newCache (final String   sID     ,
									   final ERunMode eRunMode)
		throws Exception
	{
		final DistributedCache aCache = new DistributedCache ();

		aCache	.configure()
				.setRunMode     (eRunMode   )
				.enableMulticast(false      )
				.setAddress     ("localhost")
				.setPort		(9876       );
		
		impl_oberveAndLog (sID, aCache);
		
		aCache.connect  ();
		return aCache;
	}

	//-------------------------------------------------------------------------
	private ExecutorService newBE (final int            nBE          ,
			   					   final int            nMessageCount,
								   final CountDownLatch aShutdownSync)
	    throws Exception
	{
		final ExecutorService aBE = Executors.newSingleThreadExecutor();
		aBE.execute(new Runnable ()
		{
			private final String           sID      = "client-"+nBE;
			private final DistributedCache aBECache = newCache (sID, ERunMode.E_CLIENT); 
			
			@Override
			public void run()
			{
				try
				{
					LOG	.forLevel	(ELogLevel.E_DEBUG)
						.withMessage("("+sID+") connect ...")
						.log 		();
					aBECache.connect();

					for (int i=0; i<nMessageCount; ++i)
					{
						Thread.sleep(ThreadLocalRandom.current().nextLong(0, 100));

						final String sMsgId = sID + "_" + i;

						LOG	.forLevel	(ELogLevel.E_DEBUG)
							.withMessage("("+sID+") : .... send : msg."+sMsgId+"=true")
							.log 		();
						aBECache.set ("msg."+sMsgId+".in" , "true");
					}
				}
				catch (Throwable ex)
				{
					LOG	.forLevel	(ELogLevel.E_ERROR)
						.withError	(ex)
						.withMessage("("+sID+")")
						.log 		();
				}
				finally
				{
					LOG	.forLevel	(ELogLevel.E_DEBUG)
						.withMessage("("+sID+") disconnect ...")
						.log 		();
					aBECache.disconnectQuietly();
				}
				
				LOG	.forLevel	(ELogLevel.E_DEBUG)
					.withMessage("("+sID+") shutdown ...")
					.log 		();
				if (aShutdownSync != null)
					aShutdownSync.countDown();
			}
		});
		return aBE;
	}

	//-------------------------------------------------------------------------
	private void impl_oberveAndLog (final String           sID   ,
									final DistributedCache aCache)
		throws Exception
    {
		final Observer< DistributedCacheItem > aObserver = new Observer< DistributedCacheItem > ()
		{
			@Override
			public void notify(final DistributedCacheItem aCacheItem)
				throws Exception
			{
				LOG	.forLevel	(ELogLevel.E_DEBUG)
					.withMessage("("+sID+") observer got item : "+aCacheItem)
					.log 		();
			}
		};
		aCache.getCacheSink().addObserver(aObserver);
    }
}
