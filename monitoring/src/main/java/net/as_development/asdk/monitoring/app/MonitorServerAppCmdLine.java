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

import net.as_development.asdk.tools.commandline.CommandLineBase;

//=============================================================================
public class MonitorServerAppCmdLine extends CommandLineBase
{
    //-------------------------------------------------------------------------
    public static final String OPT_SHORT_CONFIG_PATH = "cp";
    public static final String OPT_SHORT_REPORT_PATH = "rp";

    public static final String OPT_LONG_CONFIG_PATH  = "config-path";
    public static final String OPT_LONG_REPORT_PATH  = "report-path";

    //-------------------------------------------------------------------------
    public MonitorServerAppCmdLine()
    	throws Exception
    {
    	super("asdk-monitor-server");

    	addOption (OPT_SHORT_CONFIG_PATH                ,
    			   OPT_LONG_CONFIG_PATH                 ,
    			   CommandLineBase.HAS_VALUE            ,
    			   CommandLineBase.REQUIRED             ,
    			   "the path where config files exists");

        addOption (OPT_SHORT_REPORT_PATH                ,
                   OPT_LONG_REPORT_PATH                 ,
                   CommandLineBase.HAS_VALUE            ,
                   CommandLineBase.NOT_REQUIRED         ,
                   "the path where report files will be generated");
    }

    //-------------------------------------------------------------------------
    public String getConfigPath ()
        throws Exception
    {
    	return getOptionValue(OPT_SHORT_CONFIG_PATH);
    }

    //-------------------------------------------------------------------------
    public String getReportPath ()
        throws Exception
    {
        return getOptionValue(OPT_SHORT_REPORT_PATH);
    }
}
