package test.net.as_development.asdk.distributed_cache;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;

import net.as_development.asdk.distributed_cache.DistributedCache;

//=============================================================================
public class DistributedCacheTest
{
	//-------------------------------------------------------------------------
	@Test
	public void test()
		throws Exception
	{
		final CountDownLatch  aShutdownSync = new CountDownLatch (3);
		final ExecutorService aBE01         = newBE (1, aShutdownSync);
		final ExecutorService aBE02         = newBE (2, aShutdownSync);
		final ExecutorService aBE03         = newBE (3, aShutdownSync);
		aShutdownSync.await();
	}

	//-------------------------------------------------------------------------
	private DistributedCache impl_newCache ()
		throws Exception
	{
		final DistributedCache aCache = new DistributedCache ();
		aCache.connect();
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
			private DistributedCache aBECache = impl_newCache (); 
			
			@Override
			public void run()
			{
				try
				{
					System.err.println("["+nBE+"] : connect ....");
					aBECache.connect();
					for (int i=0; i<100; ++i)
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
