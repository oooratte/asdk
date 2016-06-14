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

import org.apache.commons.io.FileUtils;
import org.h2.tools.Server;

import net.as_development.asdk.api.sql.ISqlServer;

//==============================================================================
/**
 * TODO document me
 */
public class StandaloneH2Server implements ISqlServer
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
    private static final String DEFAULT_NETWORK_INTERFACE = "127.0.0.1";

    //--------------------------------------------------------------------------
    private static final int    DEFAULT_NETWORK_PORT = 9092;
    
    //--------------------------------------------------------------------------
    private static final String DEFAULT_USER = "sa";

    //--------------------------------------------------------------------------
    private static final String DEFAULT_PASSWORD = "";

    //--------------------------------------------------------------------------
    private static final String DEFAULT_DB = "db";

    //--------------------------------------------------------------------------
    /** create new instance.
     */
    public StandaloneH2Server ()
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
    	FileUtils.forceMkdir(aDir);
    	
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
        if (isRunning ())
            throw new Exception ("DB Server already started. Cant set new network interface then ...");

        m_sNetworkInterface = sInterface;
    }
    
    //--------------------------------------------------------------------------
    @Override
    public synchronized void setHost (final String sHost)
    	throws Exception
    {
    	// not thought to be connected against remote DB
    }

    //--------------------------------------------------------------------------
    @Override
    public synchronized void setPort (final int nPort)
    	throws Exception
    {
        if (isRunning ())
            throw new Exception ("DB Server already started. Cant set new network port then ...");

        m_nNetworkPort = nPort;
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
        return (m_aServer != null);
    }

    //--------------------------------------------------------------------------
    @Override
    public synchronized void start()
        throws Exception
    {
        if (m_aServer != null)
            return;

        final String[] lArgs = new String[10];

        lArgs[0] = "-tcp"           ;
        lArgs[1] = "-tcpAllowOthers";
        lArgs[2] = "-tcpDaemon"     ;

        // define port for remote connections
        
        lArgs[3] = "-tcpPort"                      ;
        lArgs[4] = Integer.toString(m_nNetworkPort);

        // define root working dir (Note : Data path needs to ne child of that working dir !)
        
        lArgs[5] = "-baseDir"                      ;
        lArgs[6] = mem_WorkDir ().getAbsolutePath();
        
        // define alias "DB-Name" -> "DB-Path"
        lArgs[7] = "-key"                           ;
        lArgs[8] = m_sDBName                        ;
        lArgs[9] = mem_DbDataDir().getAbsolutePath();
        
        final Server aServer = Server.createTcpServer(lArgs);
        aServer.start();
        m_aServer = aServer;
    }

    //--------------------------------------------------------------------------
    @Override
    public synchronized void stop()
        throws Exception
    {
        if (m_aServer == null)
            return;
        
        final Server aServer = m_aServer;
                   m_aServer = null;
        
        if (aServer != null)
        	aServer.stop();
        
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
            final File aRootDir  = new File (aTempDir, StandaloneH2Server.DIRNAME_DB_ROOT);
            final File aWorkDir  = new File (aRootDir, m_sDBName                       );
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
            File aDataDir = new File (aWorkDir  , StandaloneH2Server.DIRNAME_DB_DATA);
            System.err.println(aDataDir);
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
            final File aLogDir  = new File (aWorkDir, StandaloneH2Server.DIRNAME_LOGS);
            final File aLogFile = new File (aLogDir , StandaloneH2Server.DB_LOGFILE  );
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
            final File         aDir = mem_DbDataDir ();
            final String       sDir = aDir.getAbsolutePath();
            final StringBuffer sURL = new StringBuffer (256);
            
            sURL.append(StandaloneH2Server.JDBC_PROTOCOL);
            sURL.append("tcp://"      );
            sURL.append(":"           );
            sURL.append(m_nNetworkPort);
            sURL.append("/"           );
            sURL.append(m_sDBName     );

            m_sConnectionUrl = sURL.toString ();
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
    private Server m_aServer = null;
    
    //--------------------------------------------------------------------------
    private String m_sNetworkInterface = DEFAULT_NETWORK_INTERFACE;

    //--------------------------------------------------------------------------
    private int m_nNetworkPort = DEFAULT_NETWORK_PORT;
    
    //--------------------------------------------------------------------------
    private String m_sDBName = StandaloneH2Server.DEFAULT_DB;
    
    //--------------------------------------------------------------------------
    private String m_sUser = StandaloneH2Server.DEFAULT_USER;

    //--------------------------------------------------------------------------
    private String m_sPassword = StandaloneH2Server.DEFAULT_PASSWORD;

    //--------------------------------------------------------------------------
    /** enable/disable removing of all DB data after server was shutdown.
     *  It's on by default so it can be used without making your disc full.
     *  But you can change set by calling enableDataPersistence().
     */
    private boolean m_bRemoveDataAfterShutdown = true;
}
