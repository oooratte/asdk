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
package test.net.as_development.asdk.monitoring;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import junit.framework.Assert;
import net.as_development.asdk.api.monitoring.IMonitor;
import net.as_development.asdk.api.monitoring.IMonitorRecordProcessor;
import net.as_development.asdk.monitoring.Monitoring;
import net.as_development.asdk.monitoring.core.MonitorRecord;

//==============================================================================
public class MonitoringTest
{
	//--------------------------------------------------------------------------
	@Test
	public void test()
		throws Exception
	{
		final IMonitor        iMonitorA         = Monitoring.newMonitor("test-A");
		final IMonitor        iMonitorB         = Monitoring.newMonitor("test-B");
		final ExecutorService aThreadPool       = Executors.newFixedThreadPool(2);
		final int             nRecordsPerThread = 10;
		final CountDownLatch  aProcessingSync   = new CountDownLatch (2 * nRecordsPerThread);

		Monitoring.setProcessor(new IMonitorRecordProcessor ()
		{
			@Override
			public void processRecord(final MonitorRecord aRecord)
				throws Exception
			{
				System.out.println(aRecord);
				aProcessingSync.countDown();
			}
		});
		
		final Future< ? > aThreadA = aThreadPool.submit(new Runnable ()
		{
			@Override
			public void run()
			{
				try
				{
					System.err.println("START A ...");
					for (int i=0; i<10; ++i)
					{
						iMonitorA.record("i"+i, "test message A-("+i+")", "i"+i, i);
						Thread.sleep(ThreadLocalRandom.current().nextLong(0, 10));
					}
					System.err.println("... END A");
				}
				catch (Throwable exIgnore)
				{}
			}
		});
		
		final Future< ? > aThreadB = aThreadPool.submit(new Runnable ()
		{
			@Override
			public void run()
			{
				try
				{
					System.err.println("START B ...");
					for (int i=0; i<10; ++i)
					{
						iMonitorB.record("i"+i, "test message B-("+i+")", "i"+i, i);
						Thread.sleep(ThreadLocalRandom.current().nextLong(0, 10));
					}
					System.err.println("... END B");
				}
				catch (Throwable exIgnore)
				{}
			}
		});

		// join with threads (throws exception for timeout)
		aThreadA.get(10000, TimeUnit.MILLISECONDS);
		aThreadB.get(10000, TimeUnit.MILLISECONDS);

		// join with async processing
		Assert.assertTrue("test [] some records was not processed in time", aProcessingSync.await(10000, TimeUnit.MILLISECONDS));
	}
}
