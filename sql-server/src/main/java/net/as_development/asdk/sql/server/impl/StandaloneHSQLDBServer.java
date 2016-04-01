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
package net.as_development.asdk.sql.server.impl;


import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import net.as_development.asdk.api.sql.ISqlServer;

//==============================================================================
/**
 * TODO document me
 */
public class StandaloneHSQLDBServer implements ISqlServer
{
    //--------------------------------------------------------------------------
    private static final String HSQLDB_JDBC_PROTOCOL = "jdbc:hsqldb:";

    //--------------------------------------------------------------------------
    private static final String HSQL_INSECURE = "hsql";
    
    //--------------------------------------------------------------------------
    private static final String HSQL_SECURE = "hsqls";

    //--------------------------------------------------------------------------
    private static final String SYSPROP_TEMPDIR = "java.io.tmpdir";

    //--------------------------------------------------------------------------
    private static final String DIRNAME_LOCAL_CACHE_DATA = "hsqldb_standalone_sql_server_cache";

    //--------------------------------------------------------------------------
    private static final String DIRNAME_LOCAL_DB_DATA = "data";

    //--------------------------------------------------------------------------
    private static final String DEFAULT_USER = "SA";

    //--------------------------------------------------------------------------
    private static final String DEFAULT_PASSWORD = "";

    //--------------------------------------------------------------------------
    private static final String DEFAULT_INTERFACE = "0.0.0.0";

    //--------------------------------------------------------------------------
    private static final String DEFAULT_HOST = "127.0.0.1";

    //--------------------------------------------------------------------------
    private static final int    DEFAULT_PORT = 9100;

    //--------------------------------------------------------------------------
    private static final boolean DEFAULT_SECURE = false;
    
    //--------------------------------------------------------------------------
    private static final String DEFAULT_DB = "db";

    //--------------------------------------------------------------------------
    /** create new instance.
     */
    public StandaloneHSQLDBServer ()
    {}

    //--------------------------------------------------------------------------
    /** bind this server to a special place on disc.
     *  We will use that directory instead of using temp. one.
     *
     *  @param	sDir [IN]
     *  		the new working directory.
     */
    public synchronized void setWorkDir (String sDir)
    	throws Exception
    {
        if (m_bRunning)
            throw new Exception ("DB Server already started. Cant set new workdir then ...");
        
    	File aDir = new File (sDir);
    	
    	if ( ! aDir.exists())
    		aDir.mkdirs();
    	
    	if ( ! aDir.isDirectory())
    		throw new IllegalArgumentException ("Could not find nor create working directory '"+aDir.getAbsolutePath()+"'.");
    	
    	m_aWorkDir = aDir;
    }

    //--------------------------------------------------------------------------
    /** enable/disable persistence of data after DB shutdown.
     * 
     *  @param	bPersistent [IN]
     *  		the new state.
     */
    public synchronized void enablePersistentData (boolean bPersistent)
    	throws Exception
    {
    	m_bRemoveDataAfterShutdown = ! bPersistent;
    }

    //--------------------------------------------------------------------------
    @Override
    public synchronized String getDriverClass()
        throws Exception
    {
        return org.hsqldb.jdbc.JDBCDriver.class.getName();
    }

    //--------------------------------------------------------------------------
    @Override
    public synchronized String getConnectionUrl()
        throws Exception
    {
        return mem_ConnectionUrl ();
    }

    //--------------------------------------------------------------------------
    @Override
    public synchronized void setInterface (final String sInterface)
    	throws Exception
    {
        if (m_bRunning)
            throw new Exception ("DB Server already started. Cant set new interface then ...");
    	
        m_sInterface = sInterface;
    }
    
    //--------------------------------------------------------------------------
    @Override
    public synchronized void setHost (final String sHost)
    	throws Exception
    {
        if (m_bRunning)
            throw new Exception ("DB Server already started. Cant set new host then ...");
    	
        m_sHost = sHost;
    }

    //--------------------------------------------------------------------------
    @Override
    public synchronized void setPort (final int nPort)
    	throws Exception
    {
        if (m_bRunning)
            throw new Exception ("DB Server already started. Cant set new port then ...");
    	
        m_nPort = nPort;
    }

    //--------------------------------------------------------------------------
    @Override
    public synchronized void setDBName(final String sDBName)
        throws Exception
    {
        if (m_bRunning)
            throw new Exception ("DB Server already started. Cant set new DB name then ...");
    	
    	m_sDBName = sDBName;
    }

    //--------------------------------------------------------------------------
    @Override
    public synchronized void setUser(String sUser)
        throws Exception
    {
        if (m_bRunning)
            throw new Exception ("DB Server already started. Cant set new admin user then ...");

        m_sUser = sUser;
    }

    //--------------------------------------------------------------------------
    @Override
    public synchronized String getUser()
        throws Exception
    {
        return m_sUser;
    }

    //--------------------------------------------------------------------------
    @Override
    public synchronized void setPassword(String sPassword)
        throws Exception
    {
        if (m_bRunning)
            throw new Exception ("DB Server already started. Cant set new admin password then ...");

        m_sPassword = sPassword;
    }

    //--------------------------------------------------------------------------
    @Override
    public synchronized String getPassword()
        throws Exception
    {
        return m_sPassword;
    }

    //--------------------------------------------------------------------------
    @Override
    public synchronized boolean isRunning()
        throws Exception
    {
        return m_bRunning;
    }

    //--------------------------------------------------------------------------
    @Override
    public synchronized void start()
        throws Exception
    {
        if (m_bRunning)
            return;

        org.hsqldb.server.Server aServer = new org.hsqldb.server.Server ();
        
        aServer.setAddress     (m_sInterface);
        aServer.setPort        (m_nPort     );
        
        aServer.setDatabaseName(0, m_sDBName);
        aServer.setDatabasePath(0, mem_DbDataDir ().toURI ().toString ());
        
        aServer.start();
        
        m_aServer = aServer;

        m_bRunning = true;
    }

    //--------------------------------------------------------------------------
    @Override
    public synchronized void stop()
        throws Exception
    {
        if ( ! m_bRunning)
            return;

        org.hsqldb.server.Server aServer = m_aServer;
        m_aServer = null;
    	aServer.stop ();

        if (m_bRemoveDataAfterShutdown)
	        FileUtils.deleteQuietly(mem_WorkDir ());

        m_bRunning = false;
    }

    //--------------------------------------------------------------------------
    private File mem_WorkDir ()
            throws Exception
    {
        if (m_aWorkDir == null)
        {
            File aTempDir  = new File (System.getProperty(StandaloneHSQLDBServer.SYSPROP_TEMPDIR));
            File aCacheDir = new File (aTempDir , StandaloneHSQLDBServer.DIRNAME_LOCAL_CACHE_DATA);
            File aWorkDir  = new File (aCacheDir, m_sDBName                                      );
            m_aWorkDir = aWorkDir;
        }
        return m_aWorkDir;
    }

    //--------------------------------------------------------------------------
    private File mem_DbDataDir ()
            throws Exception
    {
        if (m_aDbDataDir == null)
        {
            File aTempDir   = mem_WorkDir ();
            File aDbRootDir = new File (aTempDir  , StandaloneHSQLDBServer.DIRNAME_LOCAL_DB_DATA);
            File aDbDataDir = new File (aDbRootDir, m_sDBName);
            m_aDbDataDir    = aDbDataDir;
        }
        return m_aDbDataDir;
    }

    //--------------------------------------------------------------------------
    private String mem_ConnectionUrl ()
            throws Exception
    {
        if (m_sConnectionUrl == null)
        {
            final StringBuffer sURL = new StringBuffer (256);
            
            sURL.append (HSQLDB_JDBC_PROTOCOL);

            if (m_bSecure)
            	sURL.append (HSQL_SECURE  );
            else
            	sURL.append (HSQL_INSECURE);
            
            sURL.append ("://"    );
            sURL.append (m_sHost  );
            sURL.append (":"      );
        	sURL.append (m_nPort  );
            sURL.append ("/"      );
            sURL.append (m_sDBName);

            m_sConnectionUrl = sURL.toString ();
        }
        return m_sConnectionUrl;
    }

    //--------------------------------------------------------------------------
    private org.hsqldb.server.Server m_aServer = null;

    //--------------------------------------------------------------------------
    private File m_aWorkDir = null;

    //--------------------------------------------------------------------------
    private File m_aDbDataDir = null;

    //--------------------------------------------------------------------------
    private String m_sConnectionUrl = null;

    //--------------------------------------------------------------------------
    private String m_sInterface = StandaloneHSQLDBServer.DEFAULT_INTERFACE;
    
    //--------------------------------------------------------------------------
    private String m_sHost = DEFAULT_HOST;
    
    //--------------------------------------------------------------------------
    private int m_nPort = StandaloneHSQLDBServer.DEFAULT_PORT;

    //--------------------------------------------------------------------------
    private boolean m_bSecure = DEFAULT_SECURE;
    
    //--------------------------------------------------------------------------
    private String m_sUser = StandaloneHSQLDBServer.DEFAULT_USER;

    //--------------------------------------------------------------------------
    private String m_sPassword = StandaloneHSQLDBServer.DEFAULT_PASSWORD;

    //--------------------------------------------------------------------------
    private String m_sDBName = DEFAULT_DB;

    //--------------------------------------------------------------------------
    /** enable/disable removing of all DB data after server was shutdown.
     *  It's on by default so it can be used without making your disc full.
     *  But you can change set by calling enableDataPersistence().
     */
    private boolean m_bRemoveDataAfterShutdown = true;
    
    //--------------------------------------------------------------------------
    private boolean m_bRunning = false;
}
