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
package net.as_development.asdk.api.sql;


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
