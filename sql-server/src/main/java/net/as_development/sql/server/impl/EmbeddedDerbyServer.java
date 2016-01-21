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
package net.as_development.sql.server.impl;


import java.io.File;
import java.sql.DriverManager;
import java.sql.SQLException;

import net.as_development.api.sql.ISqlServer;

import org.apache.commons.io.FileUtils;

//==============================================================================
/**
 * @todo document me
 */
public class EmbeddedDerbyServer implements ISqlServer
{
    //--------------------------------------------------------------------------
    private static final String DERBY_JDBC_PROTOCOL = "jdbc:derby:";

    //--------------------------------------------------------------------------
    private static final String DERBY_LOGFILE_PROPERTY = "derby.stream.error.file";

    //--------------------------------------------------------------------------
    private static final String SYSPROP_TEMPDIR = "java.io.tmpdir";

    //--------------------------------------------------------------------------
    private static final String DIRNAME_LOCAL_CACHE_DATA = "derby_embedded_sql_server_cache";

    //--------------------------------------------------------------------------
    private static final String DIRNAME_LOCAL_DB_DATA = "data";

    //--------------------------------------------------------------------------
    private static final String LOGFILE_NAME = "db.log";

    //--------------------------------------------------------------------------
    private static final String DB_ARG_CREATE_DB = ";create=true";

    //--------------------------------------------------------------------------
    private static final String DB_ARG_SHUTDOWN_DB = ";shutdown=true";

    //--------------------------------------------------------------------------
    private static final String DEFAULT_USER = "app";

    //--------------------------------------------------------------------------
    private static final String DEFAULT_PASSWORD = "app";

    //--------------------------------------------------------------------------
    private static final String DEFAULT_DB = "db";

    //--------------------------------------------------------------------------
    /** create new instance.
     */
    public EmbeddedDerbyServer ()
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
        return org.apache.derby.jdbc.EmbeddedDriver.class.getName();
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

        // make sure temp dir does NOT exists !
        // Derby will create it ... and will fail if it already exists .-)
        File aTempDir = mem_WorkDir ();
        if (aTempDir.exists())
            FileUtils.deleteQuietly(aTempDir);

        // derby will use those global property to know where it's log file
        // should be placed .-)
        File aLogFile = mem_LogFile ();
        System.setProperty(EmbeddedDerbyServer.DERBY_LOGFILE_PROPERTY, aLogFile.getAbsolutePath());

        // "start" derby and let him create it's "lokal cache files" inside temp dir
        String sStartUrl = mem_ConnectionUrl ()+EmbeddedDerbyServer.DB_ARG_CREATE_DB;
        DriverManager.getConnection(sStartUrl);

        m_bRunning = true;
    }

    //--------------------------------------------------------------------------
    @Override
    public synchronized void stop()
        throws Exception
    {
        if ( ! m_bRunning)
            return;
        
        String sStopUrl = mem_ConnectionUrl ()+EmbeddedDerbyServer.DB_ARG_SHUTDOWN_DB;

        try
        {
            DriverManager.getConnection(sStopUrl);
        }
        catch(SQLException exIgnore)
        {
        	// shutdown via special argument return state by throwing an exception
        	// strange - but true.
        	// ignore such sql exception as it's expected.
        	// TODO : check if right state is return in exception to be sure ...
        }

        if (m_bRemoveDataAfterShutdown)
	        FileUtils.deleteQuietly(mem_WorkDir ());

        System.setProperty(EmbeddedDerbyServer.DERBY_LOGFILE_PROPERTY, "");
        m_bRunning = false;
    }

    //--------------------------------------------------------------------------
    private File mem_WorkDir ()
            throws Exception
    {
        if (m_aWorkDir == null)
        {
            File aTempDir  = new File (System.getProperty(EmbeddedDerbyServer.SYSPROP_TEMPDIR));
            File aCacheDir = new File (aTempDir , EmbeddedDerbyServer.DIRNAME_LOCAL_CACHE_DATA);
            File aWorkDir  = new File (aCacheDir, m_sDBName                                   );
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
            File aDbRootDir = new File (aTempDir  , EmbeddedDerbyServer.DIRNAME_LOCAL_DB_DATA);
            File aDbDataDir = new File (aDbRootDir, m_sDBName);
            m_aDbDataDir = aDbDataDir;
        }
        return m_aDbDataDir;
    }

    //--------------------------------------------------------------------------
    private File mem_LogFile ()
            throws Exception
    {
        if (m_aLogFile == null)
        {
            File aTempDir = mem_WorkDir ();
            File aLogFile = new File (aTempDir, m_sDBName+"_"+EmbeddedDerbyServer.LOGFILE_NAME);
            m_aLogFile = aLogFile;
        }
        return m_aLogFile;
    }

    //--------------------------------------------------------------------------
    private String mem_ConnectionUrl ()
            throws Exception
    {
        if (m_sConnectionUrl == null)
        {
            File   aDir = mem_DbDataDir ();
            String sDir = aDir.getAbsolutePath();

            m_sConnectionUrl = EmbeddedDerbyServer.DERBY_JDBC_PROTOCOL+sDir;
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
    private String m_sDBName = EmbeddedDerbyServer.DEFAULT_DB;
    
    //--------------------------------------------------------------------------
    private String m_sUser = EmbeddedDerbyServer.DEFAULT_USER;

    //--------------------------------------------------------------------------
    private String m_sPassword = EmbeddedDerbyServer.DEFAULT_PASSWORD;

    //--------------------------------------------------------------------------
    /** enable/disable removing of all DB data after server was shutdown.
     *  It's on by default so it can be used without making your disc full.
     *  But you can change set by calling enableDataPersistence().
     */
    private boolean m_bRemoveDataAfterShutdown = true;
    
    //--------------------------------------------------------------------------
    private boolean m_bRunning = false;
}
