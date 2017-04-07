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

import java.io.File;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import net.as_development.asdk.monitoring.app.config.ConfigAccess;
import net.as_development.asdk.monitoring.app.config.GlobalConfig;
import net.as_development.asdk.monitoring.app.config.MonitorAppConfig;

//=============================================================================
public class MonitorServerApp 
{
    //-------------------------------------------------------------------------
    public static final int RET_OK              = 0;
    public static final int RET_GENERAL_ERROR   = 1;
    public static final int RET_INVALID_CMDLINE = 2;
    
    //-------------------------------------------------------------------------
    public static void main(final String[] lArgs)
    	throws Exception
    {
    	try
    	{
        	final MonitorServerAppCmdLine aCmdLine = new MonitorServerAppCmdLine ();
        	aCmdLine.parse(lArgs);
        	
        	if (aCmdLine.needsHelp())
        	{
        		aCmdLine.printHelp();
        		return;
        	}

        	impl_prepareLoggingEnv                 (aCmdLine);
        	impl_provideConfigurationPathToRuntime (aCmdLine);
        	impl_provideReportPathToRuntime        (aCmdLine);
        	
    		impl_run ();
            
            System.out.println ("ok.");
    	}
    	catch (final Throwable ex)
    	{
    		System.err.println (ex.getMessage());
    		ex.printStackTrace (System.err     );
    		
        	System.exit(1);
    	}

    	System.exit(0);
    }

    //-------------------------------------------------------------------------
    private static void impl_prepareLoggingEnv (final MonitorServerAppCmdLine aCmdLine)
        throws Exception
    {
    	System.setProperty("APPLICATION_ID", "monitor-server-app"        );
    	System.setProperty("INSTANCE_ID"   , UUID.randomUUID().toString());
    }

    //-------------------------------------------------------------------------
    private static void impl_provideConfigurationPathToRuntime (final MonitorServerAppCmdLine aCmdLine)
        throws Exception
    {
    	final String sConfigPath = aCmdLine.getConfigPath();
    	if (StringUtils.isEmpty (sConfigPath))
    	    return;
    	
    	final File aConfigPath = new File (sConfigPath);
    	if ( ! aConfigPath.isDirectory())
    	{
    		System.err.println ("Config path '"+aConfigPath+"' is not a valid path.");
    		System.exit(1);
    	}

    	ConfigAccess.defineOutsideConfigPath(aConfigPath.getAbsolutePath());
    }

    //-------------------------------------------------------------------------
    private static void impl_provideReportPathToRuntime (final MonitorServerAppCmdLine aCmdLine)
        throws Exception
    {
        final String sReportPath = aCmdLine.getReportPath();
        if (StringUtils.isEmpty (sReportPath))
            return;
        
        final File aReportPath = new File (sReportPath);
        if ( ! aReportPath.isDirectory())
        {
            System.err.println ("Report path '"+aReportPath+"' is not a valid path.");
            System.exit(1);
        }

        //TestConfig.defineOutsideReportPath(aReportPath.getAbsolutePath());
    }

    //-------------------------------------------------------------------------
    private static void impl_run ()
    	throws Exception
    {
        final GlobalConfig     aConfig       = GlobalConfig.get();
        final MonitorServer    aServer       = new MonitorServer ();
        final MonitorAppConfig aServerConfig = aServer.configure ();
        
        aServerConfig.setServerHost(aConfig.getServerHost());
        aServerConfig.setServerPort(aConfig.getServerPort());
        
        aServer.start();
        aServer.join ();
    }
}
