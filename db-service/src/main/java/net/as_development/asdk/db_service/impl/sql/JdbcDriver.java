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
package net.as_development.asdk.db_service.impl.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import org.apache.commons.lang3.StringUtils;

//==============================================================================
/**
 */
public class JdbcDriver
{
    //--------------------------------------------------------------------------
    public static synchronized void load (String sDriver)
        throws Exception
    {
        // @todo detect calling these method with different drivers at runtime
        //       and throw an exception in such case.
        //       Switching the driver at runtime seems to be dangerous.

        if (
            (JdbcDriver.g_sJdbcDriver != null                     ) &&
            (! StringUtils.equals(JdbcDriver.g_sJdbcDriver, sDriver))
           )
            throw new RuntimeException ("Switching Jdbc driver at runtime can be dangerous and wont be supported.");

        if (JdbcDriver.g_aJdbcDriver == null)
        {
            JdbcDriver.g_aJdbcDriver = Class.forName(sDriver);
            JdbcDriver.g_sJdbcDriver = sDriver;
        }
    }

    //--------------------------------------------------------------------------
    public static synchronized Connection getConnection (String sUrl     ,
                                                         String sUser    ,
                                                         String sPassword)
        throws Exception
    {
        return DriverManager.getConnection(sUrl, sUser, sPassword);
    }

    //--------------------------------------------------------------------------
    private static String g_sJdbcDriver = null;

    //--------------------------------------------------------------------------
	private static Class< ? > g_aJdbcDriver = null;
}
