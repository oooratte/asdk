/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
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
