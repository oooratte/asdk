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

import com.jcraft.jsch.ChannelExec;

/**
 * @todo document me
 */
public class SSHShellExecute
{
    //--------------------------------------------------------------------------
    private static final int POLL_TIME_4_EXECUTE = 1000;

    //--------------------------------------------------------------------------
    /** create new instance.
     */
    public SSHShellExecute ()
        throws Exception
    {}

    //--------------------------------------------------------------------------
    protected void bind (final SSHServer aServer)
        throws Exception
    {
        m_aServer = aServer;
    }
    
    //--------------------------------------------------------------------------
    public int execute (final String sCommand)
        throws Exception
    {
    	System.out.println("exec '"+sCommand+"' ...");
    	
    	final SSHConnection      aConnection  = m_aServer.accessConnection();
    	final SSHStdOutErrSinkV2 aStdOutErr   = m_aServer.accessStdOutErr ();
    	final SSHIdentity        aIdentity    = m_aServer.accessIdentity  ();
    	final boolean            bNeedsSudo   = aIdentity.needsSudo();
        final ChannelExec        aShell       = (ChannelExec) aConnection.openChannel("exec");

        String sRealCmd = sCommand;
        if (bNeedsSudo)
        	sRealCmd = "sudo -i -H sh -c '"+sCommand+"'";
        	
        aStdOutErr.bind       (aShell  );
        aShell    .setPty     (true    );
        aShell    .setCommand (sRealCmd);

        aShell.connect ();
        
        while( ! aShell.isClosed())
        {
        	try
        	{
        		Thread.sleep(SSHShellExecute.POLL_TIME_4_EXECUTE);
        	}
        	catch(Exception ex)
        	{}
        }
        
        int nResult = aShell.getExitStatus();
        aShell.disconnect();
        aStdOutErr.unbind();

        m_aServer.doAutoFailIfNeeded(nResult);
        
        return nResult;
    }

    //--------------------------------------------------------------------------
    private SSHServer m_aServer = null;
}
