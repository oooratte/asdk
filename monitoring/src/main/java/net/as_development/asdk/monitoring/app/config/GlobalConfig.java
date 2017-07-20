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
package net.as_development.asdk.monitoring.app.config;

import java.util.Map;
import net.as_development.tools.configuration.IReadOnlyConfiguration;

//=============================================================================
public class GlobalConfig
{
    //-------------------------------------------------------------------------
    public static final String CONFIG_PACKAGE = "/global";
    
    //-------------------------------------------------------------------------
    public static final String  DEFAULT_SERVER_HOST = "localhost";
    public static final Integer DEFAULT_SERVER_PORT = 9876;

    //-------------------------------------------------------------------------
    private GlobalConfig ()
        throws Exception
    {}

    //-------------------------------------------------------------------------
    public static synchronized GlobalConfig get ()
        throws Exception
    {
    	if (m_gSingleton == null)
    		m_gSingleton = new GlobalConfig ();
    	return m_gSingleton;
    }
    
    //-------------------------------------------------------------------------
    public String getServerHost ()
        throws Exception
    {
        final IReadOnlyConfiguration iCfg   = mem_Config ();
        final String                sValue = iCfg.get("monitor.server.host", String.class, DEFAULT_SERVER_HOST);
        return sValue;
    }

    //-------------------------------------------------------------------------
    public Integer getServerPort ()
    	throws Exception
    {
    	final IReadOnlyConfiguration iCfg   = mem_Config ();
    	final Integer               nValue = ConfigUtils.readInt (iCfg.get("monitor.server.port", String.class), DEFAULT_SERVER_PORT);
    	return nValue;
    }

    //-------------------------------------------------------------------------
    public String getDataPath ()
        throws Exception
    {
        final IReadOnlyConfiguration iCfg   = mem_Config ();
        final String                sValue = iCfg.get("monitor.data.path", String.class);
        return sValue;
    }

    //-------------------------------------------------------------------------
    public <T> T getDirectValue (final String     sKey    ,
    							 final Class< T > aType   ,
    							 final T          aDefault)
    	throws Exception
    {
    	final IReadOnlyConfiguration iCfg   = mem_Config ();
    	final T                     aValue = (T) iCfg.get(sKey, aType, aDefault);
    	return aValue;
    }

    //-------------------------------------------------------------------------
    private final IReadOnlyConfiguration mem_Config ()
        throws Exception
    {
    	if (m_iConfig == null)
    		m_iConfig = ConfigAccess.accessConfig(CONFIG_PACKAGE);
    	return m_iConfig;
    }

    //-------------------------------------------------------------------------
    private static GlobalConfig m_gSingleton = null;
    
    //-------------------------------------------------------------------------
    private IReadOnlyConfiguration m_iConfig = null;
}
