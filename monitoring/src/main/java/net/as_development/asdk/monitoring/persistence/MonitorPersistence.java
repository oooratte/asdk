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
package net.as_development.asdk.monitoring.persistence;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.FileUtils;

import net.as_development.asdk.distributed_cache.DistributedCacheItem;
import net.as_development.asdk.distributed_cache.DistributedCacheSink;
import net.as_development.asdk.monitoring.app.config.GlobalConfig;
import net.as_development.asdk.monitoring.core.MonitorRecord;
import net.as_development.asdk.tools.common.pattern.observation.Observer;

//=============================================================================
public class MonitorPersistence
{
    //-------------------------------------------------------------------------
    public static final long TIMESLOT_4_WRITE_IN_MS = 1000;
    
    //-------------------------------------------------------------------------
    public MonitorPersistence ()
        throws Exception
    {}

    //-------------------------------------------------------------------------
    public synchronized void configure (final GlobalConfig aConfig)
    	throws Exception
    {
    	m_aConfig = aConfig;
    }
    
    //-------------------------------------------------------------------------
    public synchronized void bindCacheSink (final DistributedCacheSink aSink)
        throws Exception
    {
        m_aCacheSink = aSink;
        m_aCacheSink.setCachingEnabled(false);
    }

    //-------------------------------------------------------------------------
    public synchronized void start ()
        throws Exception
    {
        if (m_aAsyncWrite != null)
            return;
        
        System.err.println("persistence : start ...");
        m_aAsyncWrite = Executors.newSingleThreadExecutor();
        m_aAsyncWrite.submit(new Runnable ()
        {
            @Override
            public void run()
            {
                try
                {
                    System.err.println("persistence run async write ...");
                    impl_runAsyncWrite ();
                }
                catch (Throwable exIgnore)
                {
                    exIgnore.printStackTrace(System.err);
                }
            }
        });
    }

    //-------------------------------------------------------------------------
    public synchronized void stop ()
        throws Exception
    {
        if (m_aAsyncWrite == null)
            return;
        
        System.err.println("persistence : stop ...");
        final ExecutorService aAsyncWrite = m_aAsyncWrite;
                            m_aAsyncWrite = null;

        if (aAsyncWrite != null)
        {
            System.err.println("persistence : trigger shutdown ...");
            aAsyncWrite.shutdownNow();
            System.err.println("persistence : wait for shutdown ...");
            final boolean bOK = aAsyncWrite.awaitTermination(30000, TimeUnit.MILLISECONDS);
            if ( ! bOK)
                throw new RuntimeException ("Terminating async write timed out !");
            System.err.println("persistence : OK");
        }
    }

    //-------------------------------------------------------------------------
    private /* no synchronized */ void impl_enqueueNewCacheItem (final DistributedCacheItem aItem)
        throws Exception
    {
        System.err.println("persistence enqueue new item ...");
        mem_CacheItemQueue ().offer(aItem);
    }

    //-------------------------------------------------------------------------
    private /* no synchronized */ void impl_runAsyncWrite ()
        throws Exception
    {
        final Observer< DistributedCacheItem > aObserver               = mem_Observer       ();
        final Queue   < DistributedCacheItem > aQueue                  = mem_CacheItemQueue ();
        final List    < DistributedCacheItem > lCacheItemsForNextWrite = new ArrayList< DistributedCacheItem > ();
              long                             nTimeOfLastWrite        = System.currentTimeMillis();

        System.err.println("... register observer");
        m_aCacheSink.addObserver(aObserver);
        
        while (true)
        {
            try
            {
                if (Thread.interrupted())
                {
                    System.err.println("persistence async write thread : is interrupted !");
                    break;
                }

                final long nNow  = System.currentTimeMillis();
                final long nTime = (nNow - nTimeOfLastWrite);
                if (nTime > TIMESLOT_4_WRITE_IN_MS)
                {
                    System.err.println("... time over : "+nTime+"");
                    
                    if ( ! lCacheItemsForNextWrite.isEmpty())
                    {
                        System.err.println("... write buffer");
                        impl_writeCacheItems (lCacheItemsForNextWrite);
                        System.err.println("... clear buffer");
                        lCacheItemsForNextWrite.clear ();
                        nTimeOfLastWrite = nNow;
                    }
                }
                
                System.err.println("persistence polls for new item ...");
                final DistributedCacheItem aItem = aQueue.poll();
                if (aItem != null)
                {
                    System.err.println("... add new item to buffer : "+aItem);
                    lCacheItemsForNextWrite.add (aItem);
                    continue;
                }
                
                System.err.println("... no new item : sleep");
                synchronized(aQueue)
                {
                    aQueue.wait(1000);
                }
                System.err.println("... no new item : wakeup");
            }
            catch (InterruptedException exInterrupt)
            {
                // expected ;-)
                break;
            }
            catch (Throwable ex)
            {
                System.err.println (ex.getMessage());
                ex.printStackTrace (System.err     );
                break;
            }
        }

        System.err.println("... deregister observer");
        m_aCacheSink.removeObserver(aObserver);
    }

    //-------------------------------------------------------------------------
    private /* no synchronized */ void impl_writeCacheItems (final List< DistributedCacheItem > lCacheItems)
        throws Exception
    {
        final StringBuffer sData = new StringBuffer (256);
        for (final DistributedCacheItem aCacheItem : lCacheItems)
        {
            sData.append (aCacheItem.sKey  );
            sData.append ("="              );
            sData.append (aCacheItem.sValue);
            sData.append ("\n"             );

            final MonitorRecord aRecord = MonitorRecord.create(aCacheItem.sValue);
            System.err.println ("RECORD : ["+aRecord+"]");
        }
        final File aCacheDir  = mem_DataPath ();
        final int  nCacheNr   = m_nCacheFileNr.incrementAndGet();
        final File aCacheFile = new File (aCacheDir, nCacheNr+".cache");

        if (nCacheNr < 2)
            aCacheDir.mkdirs();

        FileUtils.writeStringToFile(aCacheFile, sData.toString(), "utf-8");
    }
    
    //-------------------------------------------------------------------------
    private synchronized File mem_DataPath ()
        throws Exception
    {
    	if (m_aDataPath == null)
    	{
    		final String sDataPath = m_aConfig.getDataPath();
    		final File   aDataPath = new File (sDataPath);
    		m_aDataPath = aDataPath;
    	}
    	return m_aDataPath;
    }

    //-------------------------------------------------------------------------
    private synchronized Queue< DistributedCacheItem > mem_CacheItemQueue ()
        throws Exception
    {
        if (m_lCacheItemQueue == null)
            m_lCacheItemQueue = new ConcurrentLinkedQueue< DistributedCacheItem > ();
        return m_lCacheItemQueue;
    }

    //-------------------------------------------------------------------------
    private synchronized Observer< DistributedCacheItem > mem_Observer ()
        throws Exception
    {
        if (m_aObserver == null)
        {
            m_aObserver = new Observer< DistributedCacheItem >()
            {
                @Override
                public void notify(final DistributedCacheItem aItem)
                    throws Exception
                {
                    System.err.println ("observer got item : "+aItem);
                    impl_enqueueNewCacheItem (aItem);
                }
            };
        }
        return m_aObserver;
    }

    //-------------------------------------------------------------------------
    private synchronized CountDownLatch mem_ShutdownTrigger ()
        throws Exception
    {
        if (m_aShutdownTrigger == null)
            m_aShutdownTrigger = new CountDownLatch (1);
        return m_aShutdownTrigger;
    }

    //-------------------------------------------------------------------------
    private GlobalConfig m_aConfig = null;
    
    //-------------------------------------------------------------------------
    private File m_aDataPath = null;

    //-------------------------------------------------------------------------
    private DistributedCacheSink m_aCacheSink = null;

    //-------------------------------------------------------------------------
    private Queue< DistributedCacheItem > m_lCacheItemQueue = null;

    //-------------------------------------------------------------------------
    private Observer< DistributedCacheItem > m_aObserver = null;

    //-------------------------------------------------------------------------
    private ExecutorService m_aAsyncWrite = null;

    //-------------------------------------------------------------------------
    private CountDownLatch m_aShutdownTrigger = null;

    //-------------------------------------------------------------------------
    private AtomicInteger m_nCacheFileNr = new AtomicInteger (0);
}
