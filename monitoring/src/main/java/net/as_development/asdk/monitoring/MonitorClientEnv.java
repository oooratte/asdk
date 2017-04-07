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
package net.as_development.asdk.monitoring;

import net.as_development.asdk.api.monitoring.IMonitor;
import net.as_development.asdk.distributed_cache.DistributedCacheConfig;
import net.as_development.asdk.distributed_cache.impl.ERunMode;
import net.as_development.asdk.monitoring.app.config.MonitorAppConfig;
import net.as_development.asdk.monitoring.core.processor.DistributedCacheMonitorRecordProcessor;

//=============================================================================
public class MonitorClientEnv
{
    //-------------------------------------------------------------------------
    private MonitorClientEnv ()
        throws Exception
    {}

    //-------------------------------------------------------------------------
    public static synchronized MonitorClientEnv get ()
        throws Exception
    {
        if (m_gSingleton == null)
            m_gSingleton = new MonitorClientEnv ();
        return m_gSingleton;
    }
    
    //-------------------------------------------------------------------------
    public static synchronized IMonitor newMonitor (final String sScope)
        throws Exception
    {
        final MonitorClientEnv aEnv = get ();
        
        if (aEnv.m_aCacheProcessor == null)
            throw new RuntimeException ("Environment not initialized ! Cant create new monitor.");

        final IMonitor iMonitor = Monitoring.newMonitor(sScope);
        return iMonitor;
    }
    
    //-------------------------------------------------------------------------
    public synchronized MonitorAppConfig configure ()
        throws Exception
    {
        return mem_Config ();
    }
    
    //-------------------------------------------------------------------------
    public synchronized void start ()
        throws Exception
    {
        if (m_aCacheProcessor != null)
            return;
        
        m_aCacheProcessor = new DistributedCacheMonitorRecordProcessor ();

        final MonitorAppConfig       aMonitorConfig = mem_Config ();
        final DistributedCacheConfig aCacheConfig   = m_aCacheProcessor.configure ();

        System.err.println ("configure client : host = "+aMonitorConfig.getServerHost());
        System.err.println ("configure client : port = "+aMonitorConfig.getServerPort());
        
        aCacheConfig.setRunMode     (ERunMode.E_CLIENT             );
        aCacheConfig.enableMulticast(false                         );
        aCacheConfig.setAddress     (aMonitorConfig.getServerHost());
        aCacheConfig.setPort        (aMonitorConfig.getServerPort());

        m_aCacheProcessor.connect();
        
        Monitoring.setProcessor(m_aCacheProcessor);
    }

    //-------------------------------------------------------------------------
    public synchronized void stop ()
        throws Exception
    {
        if (m_aCacheProcessor == null)
            return;
        
        final DistributedCacheMonitorRecordProcessor aCacheProcessor = m_aCacheProcessor;
                                                   m_aCacheProcessor = null;
        Monitoring.setProcessor(null);

        if (aCacheProcessor != null)
            aCacheProcessor.disconnect();
    }

    //-------------------------------------------------------------------------
    private MonitorAppConfig mem_Config ()
        throws Exception
    {
        if (m_aConfig == null)
            m_aConfig = new MonitorAppConfig ();
        return m_aConfig;
    }

    //-------------------------------------------------------------------------
    private static MonitorClientEnv m_gSingleton = null;
    
    //-------------------------------------------------------------------------
    private MonitorAppConfig m_aConfig = null;
    
    //-------------------------------------------------------------------------
    private DistributedCacheMonitorRecordProcessor m_aCacheProcessor = null;
}
