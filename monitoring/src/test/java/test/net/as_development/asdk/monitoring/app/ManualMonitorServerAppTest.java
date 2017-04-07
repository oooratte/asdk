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
package test.net.as_development.asdk.monitoring.app;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import net.as_development.asdk.api.monitoring.IMonitor;
import net.as_development.asdk.monitoring.MonitorClientEnv;
import net.as_development.asdk.monitoring.app.MonitorServer;
import net.as_development.asdk.monitoring.app.config.GlobalConfig;
import net.as_development.asdk.monitoring.app.config.MonitorAppConfig;


public class ManualMonitorServerAppTest
{
    public static void main(final String[] lMyArgs)
        throws Exception
    {
        final CountDownLatch aServerStartSync = new CountDownLatch (1);
        final CountDownLatch aClientStopSync  = new CountDownLatch (1);
        
        System.err.println ("start server ...");
        final MonitorServer aServer = impl_startServer      (aServerStartSync);
        System.err.println ("start client ...");
        System.err.println ("generate test data ...");
        impl_generateTestData (aServerStartSync, aClientStopSync);

        System.err.println ("wait for client ...");
        aClientStopSync.await(30000, TimeUnit.MILLISECONDS);
        
        System.err.println ("stop server ...");
        aServer.stop();
        System.err.println ("FINI");
        
        System.exit(0);
    }

    private static MonitorServer impl_startServer (final CountDownLatch aServerStartSync)
        throws Exception
    {
        final MonitorServer aServer = new MonitorServer ();
        new Thread ()
        {
            @Override
            public void run ()
            {
                try
                {
                    final GlobalConfig     aConfig       = GlobalConfig.get();
                    final MonitorAppConfig aServerConfig = aServer.configure ();
                    
                    aServerConfig.setServerHost(aConfig.getServerHost());
                    aServerConfig.setServerPort(aConfig.getServerPort());
                    
                    aServer.start();
                    aServerStartSync.countDown();
                    aServer.join ();
                }
                catch (Throwable ex)
                {
                    System.err.println (ex.getMessage ());
                    ex.printStackTrace (System.err      );
                }
            }
        }.start();
        
        return aServer;
    }
    
    private static void impl_generateTestData (final CountDownLatch aServerStartSync,
                                               final CountDownLatch aClientStopSync )
        throws Exception
    {
        new Thread ()
        {
            @Override
            public void run ()
            {
                try
                {
                    System.err.println("wait for server ...");
                    aServerStartSync.await();
                    
                    final MonitorClientEnv aEnv = MonitorClientEnv.get();

                    System.err.println("configure client env ...");
                    aEnv.configure()
                        .setServerHost("localhost")
                        .setServerPort(GlobalConfig.DEFAULT_SERVER_PORT);

                    System.err.println("start client env ...");
                    aEnv.start();

                    System.err.println("generate test data ...");
                    final IMonitor aMonitor = MonitorClientEnv.newMonitor("test");
                    for (int i=0; i<10; ++i)
                    {
                        System.err.println("... send new record : "+i);
                        aMonitor.record("record-"+i, "message-"+i, null);
                    }
                    
                    Thread.sleep (5000);
                    
                    System.err.println("stop client env ...");
                    aEnv.stop();
                }
                catch (Throwable ex)
                {
                    System.err.println (ex.getMessage ());
                    ex.printStackTrace (System.err      );
                }

                aClientStopSync.countDown();
            }
        }.start();
    }
}
