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
package net.as_development.asdk.ssh;

//=============================================================================
public class SSHServer
{
    //-------------------------------------------------------------------------
    public SSHServer ()
    {}

    //-------------------------------------------------------------------------
    public void setHost (String sHost)
        throws Exception
    {
        m_sHost = sHost;
    }

    //-------------------------------------------------------------------------
    public void setPort (int nPort)
        throws Exception
    {
        m_nPort = nPort;
    }

    //-------------------------------------------------------------------------
    public void setIdentity (SSHIdentity aIdentity)
        throws Exception
    {
        m_aIdentity = aIdentity;
    }

    //-------------------------------------------------------------------------
    public void setRemoteWorkingPath (String sPath)
        throws Exception
    {
        m_sRemoteWorkingPath = sPath;
    }

    //-------------------------------------------------------------------------
    public String getRemoteWorkingPath ()
        throws Exception
    {
    	return m_sRemoteWorkingPath;
    }

    //-------------------------------------------------------------------------
    public boolean hasToFailOnErrorAutomatically ()
        throws Exception
    {
    	return m_bFailOnErrorAutomatically;
    }

    //-------------------------------------------------------------------------
    public void connect ()
        throws Exception
    {
        mem_Connection ().connect();
    }

    //-------------------------------------------------------------------------
    public void disconnect ()
        throws Exception
    {
        mem_Connection ().disconnect();
    }

    //-------------------------------------------------------------------------
    public SSHIdentity accessIdentity ()
        throws Exception
    {
    	return m_aIdentity;
    }

    //-------------------------------------------------------------------------
    public SSHStdOutErrSinkV2 accessStdOutErr ()
        throws Exception
    {
    	return mem_StdOutErrSink ();
    }
   
    //-------------------------------------------------------------------------
    public SSHConnection accessConnection ()
        throws Exception
    {
    	return mem_Connection ();
    }

    //-------------------------------------------------------------------------
    public SSHSFtp accessSFTP ()
        throws Exception
    {
    	final SSHSFtp aSFTP = new SSHSFtp ();
    	aSFTP.bind(this);
    	return aSFTP;
    }

    //-------------------------------------------------------------------------
    public SSHShellExecute accessShell ()
        throws Exception
    {
    	final SSHShellExecute aShell = new SSHShellExecute ();
    	aShell.bind(this);
    	return aShell;
    }

    //-------------------------------------------------------------------------
    public SSHPropertiesFile accessProperties ()
        throws Exception
    {
    	final SSHPropertiesFile aSFTP = new SSHPropertiesFile ();
    	aSFTP.bind(this);
    	return aSFTP;
    }

    //-------------------------------------------------------------------------
    public void doAutoFailIfNeeded (final int nLastCommandResult)
    	throws Exception
    {
    	if ( ! m_bFailOnErrorAutomatically)
    		return;
    	
    	if (nLastCommandResult != 0)
    		throw new Exception("Auto-Fail : Result of last command was "+nLastCommandResult);
    }
    
    //-------------------------------------------------------------------------
    private SSHConnection mem_Connection ()
        throws Exception
    {
        if (m_aConnection == null)
        {
            SSHConnection aConnection = new SSHConnection ();
            aConnection.setHost    (m_sHost    );
            aConnection.setIdentity(m_aIdentity);
            aConnection.setPort    (m_nPort    );
            
            m_aConnection = aConnection;
        }
        return m_aConnection;
    }

    //-------------------------------------------------------------------------
    private SSHStdOutErrSinkV2 mem_StdOutErrSink ()
        throws Exception
    {
        if (m_aStdOutErrSink == null)
        	m_aStdOutErrSink = new SSHStdOutErrSinkV2 ();
        return m_aStdOutErrSink;
    }

    //-------------------------------------------------------------------------
    private String m_sHost = null;

    //-------------------------------------------------------------------------
    private int m_nPort = 22;

    //-------------------------------------------------------------------------
    private String m_sRemoteWorkingPath = null;

    //-------------------------------------------------------------------------
    private SSHConnection m_aConnection = null;

    //-------------------------------------------------------------------------
    private SSHStdOutErrSinkV2 m_aStdOutErrSink = null;

    //-------------------------------------------------------------------------
    private SSHIdentity m_aIdentity = null;

    //-------------------------------------------------------------------------
    private boolean m_bFailOnErrorAutomatically = true;
}
