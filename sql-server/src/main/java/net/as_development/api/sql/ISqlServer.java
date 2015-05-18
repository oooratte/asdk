/**
 * Copyright 2012 Andreas Schlüns - as-development.net
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.as_development.api.sql;


//==============================================================================
/**
 */
public interface ISqlServer
{
    //--------------------------------------------------------------------------
    /** @return the class name of the JDBC driver class.
     */
    public String getDriverClass ()
        throws Exception;

    //--------------------------------------------------------------------------
    /** @return an Url to be used to connect to this DB instance using a JDBC driver.
     */
    public String getConnectionUrl ()
        throws Exception;

    //--------------------------------------------------------------------------
    public void setInterface(final String sInterface)
        throws Exception;

    //--------------------------------------------------------------------------
    public void setHost(final String sHost)
        throws Exception;

    //--------------------------------------------------------------------------
    public void setPort(final int nPort)
        throws Exception;

    //--------------------------------------------------------------------------
    public void setDBName(final String sDBName)
        throws Exception;

    //--------------------------------------------------------------------------
    public void setUser(String sUser)
        throws Exception;

    //--------------------------------------------------------------------------
    /** @return name of the administrator account.
     */
    public String getUser ()
        throws Exception;

    //--------------------------------------------------------------------------
    public void setPassword(String sPassword)
        throws Exception;

    //--------------------------------------------------------------------------
    /** @return password valid for the administrator account.
     */
    public String getPassword ()
        throws Exception;

    //--------------------------------------------------------------------------
    public boolean isRunning ()
        throws Exception;

    //--------------------------------------------------------------------------
    /** starts the sql server.
     *  Does nothing if server was already started.
     *
     *  @throws an exception if starting failed by any reason.
     */
    public void start ()
        throws Exception;

    //--------------------------------------------------------------------------
    /** stop the sql server.
     *  Does nothing in case server was not started before.
     *
     *  @throws an exception if stop failed by any reason.
     */
    public void stop ()
        throws Exception;
}
