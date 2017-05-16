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
package net.as_development.asdk.monitoring.app;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import net.as_development.asdk.distributed_cache.DistributedCache;
import net.as_development.asdk.distributed_cache.DistributedCacheConfig;
import net.as_development.asdk.distributed_cache.DistributedCacheSink;
import net.as_development.asdk.distributed_cache.impl.ERunMode;
import net.as_development.asdk.monitoring.app.config.GlobalConfig;
import net.as_development.asdk.monitoring.app.config.MonitorAppConfig;
import net.as_development.asdk.monitoring.persistence.MonitorPersistence;

//=============================================================================
public class MonitorServer
{
    //-------------------------------------------------------------------------
    public MonitorServer ()
        throws Exception
    {}

    //-------------------------------------------------------------------------
    public synchronized MonitorAppConfig configure ()
        throws Exception
    {
        return mem_Config ();
    }
    
    //-------------------------------------------------------------------------
    public /* no synchronized */ void start ()
        throws Exception
    {
        ExecutorService aServerThread = null;
        synchronized(this)
        {
            if (m_aServerThread != null)
                return;
            m_aServerThread = Executors.newSingleThreadExecutor();
              aServerThread = m_aServerThread;
        }

        aServerThread.submit(new Runnable ()
        {
            @Override
            public void run()
            {
                try
                {
                    impl_runServer ();                    
                }
                catch (Throwable ex)
                {}
            }
        });

        impl_registerShutdownHook ();
    }

    //-------------------------------------------------------------------------
    public /* no synchronized */ void join ()
        throws Exception
    {
        mem_ShutdownTrigger ().await();
    }
    
    //-------------------------------------------------------------------------
    public /* no synchronized */ void stop ()
        throws Exception
    {
        ExecutorService aServerThread = null;
        synchronized(this)
        {
            if (m_aServerThread == null)
                return;
              aServerThread = m_aServerThread;
            m_aServerThread = null;
        }

        mem_ShutdownTrigger ().countDown();
        
        final boolean bOK = mem_ShutdownSync ().await(30000, TimeUnit.MILLISECONDS);
        if ( ! bOK)
            throw new Exception ("Server shutdown timed out ...");
        
        aServerThread.shutdown();
    }

    //-------------------------------------------------------------------------
    private /* no synchronized */ void impl_runServer ()
        throws Exception
    {
        final DistributedCache       aServer        = new DistributedCache   ();
        final MonitorAppConfig       aMonitorConfig = mem_Config             ();
        final DistributedCacheConfig aCacheConfig   = aServer.configure      ();
        final DistributedCacheSink   aCacheSink     = aServer.getCacheSink   ();
        final MonitorPersistence     aPersistence   = new MonitorPersistence ();

        System.err.println ("configure server : interface = 0.0.0.0");
        System.err.println ("configure server : port      = "+aMonitorConfig.getServerPort());

        aCacheConfig.setRunMode     (ERunMode.E_SERVER             );
        aCacheConfig.enableMulticast(false                         );
        aCacheConfig.setAddress     ("0.0.0.0"                     );
        aCacheConfig.setPort        (aMonitorConfig.getServerPort());
        
        aPersistence.configure      (GlobalConfig.get()            );
        aPersistence.bindCacheSink  (aCacheSink                    );
        aPersistence.start();
        
        System.err.println("server connect ...");
        aServer.connect();
        
        System.err.println("server wait for shutdown ...");
        mem_ShutdownTrigger ().await();

        System.err.println("server shutdown ...");
        aServer.disconnect();

        System.err.println("stop persistence ...");
        aPersistence.stop();
        
        System.err.println("server is down ...");
        mem_ShutdownSync ().countDown();
    }

    //-------------------------------------------------------------------------
    private synchronized void impl_registerShutdownHook ()
        throws Exception
    {
        if (m_aShutdownHook != null)
            return;
        
        final MonitorServer aServer = this;
        m_aShutdownHook = new Thread ()
        {
            @Override
            public void run ()
            {
                try
                {
                    System.err.println("shutdown hook triggered ...");
                    aServer.stop ();
                }
                catch(Throwable ex)
                {}
            }
        };
        System.err.println("register shutdown hook ...");
        Runtime.getRuntime().addShutdownHook(m_aShutdownHook);
    }

    //-------------------------------------------------------------------------
    private synchronized MonitorAppConfig mem_Config ()
        throws Exception
    {
        if (m_aConfig == null)
            m_aConfig = new MonitorAppConfig ();
        return m_aConfig;
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
    private synchronized CountDownLatch mem_ShutdownSync ()
        throws Exception
    {
        if (m_aShutdownSync == null)
            m_aShutdownSync = new CountDownLatch (1);
        return m_aShutdownSync;
    }

    //-------------------------------------------------------------------------
    private MonitorAppConfig m_aConfig = null;
    
    //-------------------------------------------------------------------------
    private ExecutorService m_aServerThread = null;

    //-------------------------------------------------------------------------
    private CountDownLatch m_aShutdownTrigger = null;

    //-------------------------------------------------------------------------
    private CountDownLatch m_aShutdownSync = null;

    //-------------------------------------------------------------------------
    private Thread m_aShutdownHook = null;
}
