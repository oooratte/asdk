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

import org.apache.commons.lang3.StringUtils;

import net.as_development.tools.configuration.ConfigurationFactory;
import net.as_development.tools.configuration.IReadOnlyConfiguration;
import sun.net.www.protocol.classpath.ClasspathConnection;

//=============================================================================
public class ConfigAccess
{
    //-------------------------------------------------------------------------
    private ConfigAccess ()
    	throws Exception
    {}

    //-------------------------------------------------------------------------
    public static void defineOutsideConfigPath (final String sConfigPath)
    	throws Exception
    {
    	System.setProperty("CONFIG_PATH", sConfigPath);
    }
    
    //-------------------------------------------------------------------------
    public static IReadOnlyConfiguration accessConfig (final String sConfig)
        throws Exception
    {
    	final ConfigAccess          aAccess     = mem_Singleton ();
    	final String                sConfigRoot = aAccess.mem_ConfigRoot ();
    	final IReadOnlyConfiguration iConfig     = ConfigurationFactory.getComplexConfiguration(sConfigRoot, sConfig);
    	return iConfig;
    }

    //-------------------------------------------------------------------------
    private static ConfigAccess mem_Singleton ()
        throws Exception
    {
    	if (m_gSingleton == null)
    		m_gSingleton = new ConfigAccess ();
    	return m_gSingleton;
    }

    //-------------------------------------------------------------------------
    private String mem_ConfigRoot ()
        throws Exception
    {
    	if (m_sConfigRoot == null)
    	{
    		String sRoot  = ConfigAccess.class.getName ();
    		       sRoot  = StringUtils.substringBeforeLast(sRoot, ".");
    		       sRoot  = StringUtils.replace(sRoot, ".", "/");
    		       sRoot  = StringUtils.removeEnd(sRoot, "/");
    		       sRoot  = ClasspathConnection.PROTOCOL + ":/" + sRoot;
    		       System.err.println ("config path : "+sRoot);
    		m_sConfigRoot = sRoot;
    	}
    	return m_sConfigRoot;
    }

    //-------------------------------------------------------------------------
    private static ConfigAccess m_gSingleton = null;

    //-------------------------------------------------------------------------
    private String m_sConfigRoot = null;
}
