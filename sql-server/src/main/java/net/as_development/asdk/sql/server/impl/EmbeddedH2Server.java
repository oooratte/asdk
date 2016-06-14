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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.commons.io.FileUtils;

import net.as_development.asdk.api.sql.ISqlServer;

//==============================================================================
/**
 * TODO document me
 */
public class EmbeddedH2Server implements ISqlServer
{
    //--------------------------------------------------------------------------
    private static final String JDBC_PROTOCOL = "jdbc:h2:";

    //--------------------------------------------------------------------------
    private static final String DIRNAME_DB_ROOT = "h2";

    //--------------------------------------------------------------------------
    private static final String DIRNAME_DB_DATA = "data";

    //--------------------------------------------------------------------------
    private static final String DIRNAME_LOGS = "log";

    //--------------------------------------------------------------------------
    private static final String DB_LOGFILE = "db.log";

    //--------------------------------------------------------------------------
    private static final String DEFAULT_USER = "sa";

    //--------------------------------------------------------------------------
    private static final String DEFAULT_PASSWORD = "";

    //--------------------------------------------------------------------------
    private static final String DEFAULT_DB = "db";

    //--------------------------------------------------------------------------
    /** create new instance.
     */
    public EmbeddedH2Server ()
    {}

    //--------------------------------------------------------------------------
    /** bind this server to a special place on disc.
     *  We will use that directory instead of using temp. one.
     *
     *  @param	sDir [IN]
     *  		the new working directory.
     */
    public synchronized void setWorkDir (final String sDir)
    	throws Exception
    {
        if (isRunning ())
            throw new Exception ("DB Server already started. Cant set new workdir then ...");
        
    	final File aDir = new File (sDir);
    	
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
    public synchronized void enablePersistentData (final boolean bPersistent)
    	throws Exception
    {
    	m_bRemoveDataAfterShutdown = ! bPersistent;
    }

    //--------------------------------------------------------------------------
    @Override
    public synchronized String getDriverClass()
        throws Exception
    {
        return org.h2.Driver.class.getName();
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
    	// embedded Db not bound to any interface
    }
    
    //--------------------------------------------------------------------------
    @Override
    public synchronized void setHost (final String sHost)
    	throws Exception
    {
    	// embedded Db not bound to any host
    }

    //--------------------------------------------------------------------------
    @Override
    public synchronized void setPort (final int nPort)
    	throws Exception
    {
    	// embedded Db not bound to any port
    }

    //--------------------------------------------------------------------------
    @Override
    public synchronized void setDBName(final String sDBName)
        throws Exception
    {
        if (isRunning ())
            throw new Exception ("DB Server already started. Cant set new DB name then ...");

        m_sDBName = sDBName;
    }

    //--------------------------------------------------------------------------
    @Override
    public synchronized void setUser(final String sUser)
        throws Exception
    {
        if (isRunning ())
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
    public synchronized void setPassword(final String sPassword)
        throws Exception
    {
        if (isRunning ())
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
        return (m_aConnection != null);
    }

    //--------------------------------------------------------------------------
    @Override
    public synchronized void start()
        throws Exception
    {
        if (m_aConnection != null)
            return;

        final String     sStartUrl   = mem_ConnectionUrl ();
        final Connection aConnection = DriverManager.getConnection(sStartUrl, m_sUser, m_sPassword);
        			   m_aConnection = aConnection;
    }

    //--------------------------------------------------------------------------
    @Override
    public synchronized void stop()
        throws Exception
    {
        if (m_aConnection == null)
            return;
        
        final Connection aConnection = m_aConnection;
        			   m_aConnection = null;
        
        if (aConnection != null)
        	aConnection.close();
        
        if (m_bRemoveDataAfterShutdown)
	        FileUtils.deleteQuietly(mem_WorkDir ());
    }

    //--------------------------------------------------------------------------
    private synchronized File mem_WorkDir ()
            throws Exception
    {
        if (m_aWorkDir == null)
        {
            final File aTempDir  = FileUtils.getTempDirectory();
            final File aRootDir  = new File (aTempDir, EmbeddedH2Server.DIRNAME_DB_ROOT);
            final File aWorkDir  = new File (aRootDir, m_sDBName                       );
            System.err.println(aWorkDir);
            FileUtils.forceMkdir(aWorkDir);
            m_aWorkDir = aWorkDir;
        }
        return m_aWorkDir;
    }

    //--------------------------------------------------------------------------
    private synchronized File mem_DbDataDir ()
            throws Exception
    {
        if (m_aDbDataDir == null)
        {
            File aWorkDir = mem_WorkDir ();
            File aDataDir = new File (aWorkDir  , EmbeddedH2Server.DIRNAME_DB_DATA);
            FileUtils.forceMkdir(aDataDir);
            m_aDbDataDir  = aDataDir;
        }
        return m_aDbDataDir;
    }

    //--------------------------------------------------------------------------
    private synchronized File mem_LogFile ()
            throws Exception
    {
        if (m_aLogFile == null)
        {
            final File aWorkDir = mem_WorkDir ();
            final File aLogDir  = new File (aWorkDir, EmbeddedH2Server.DIRNAME_LOGS);
            final File aLogFile = new File (aLogDir , EmbeddedH2Server.DB_LOGFILE  );
            FileUtils.forceMkdir(aLogDir);
            m_aLogFile = aLogFile;
        }
        return m_aLogFile;
    }

    //--------------------------------------------------------------------------
    private synchronized String mem_ConnectionUrl ()
            throws Exception
    {
        if (m_sConnectionUrl == null)
        {
            File   aDir = mem_DbDataDir ();
            String sDir = aDir.getAbsolutePath();

            m_sConnectionUrl = EmbeddedH2Server.JDBC_PROTOCOL+sDir;
        }
        return m_sConnectionUrl;
    }

    //--------------------------------------------------------------------------
    private File m_aWorkDir = null;

    //--------------------------------------------------------------------------
    private File m_aDbDataDir = null;

    //--------------------------------------------------------------------------
    private File m_aLogFile = null;

    //--------------------------------------------------------------------------
    private String m_sConnectionUrl = null;

    //--------------------------------------------------------------------------
    private Connection m_aConnection = null;
    
    //--------------------------------------------------------------------------
    private String m_sDBName = EmbeddedH2Server.DEFAULT_DB;
    
    //--------------------------------------------------------------------------
    private String m_sUser = EmbeddedH2Server.DEFAULT_USER;

    //--------------------------------------------------------------------------
    private String m_sPassword = EmbeddedH2Server.DEFAULT_PASSWORD;

    //--------------------------------------------------------------------------
    /** enable/disable removing of all DB data after server was shutdown.
     *  It's on by default so it can be used without making your disc full.
     *  But you can change set by calling enableDataPersistence().
     */
    private boolean m_bRemoveDataAfterShutdown = true;
}
