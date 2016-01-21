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


import java.io.InputStream;

import org.apache.commons.io.FilenameUtils;

import com.jcraft.jsch.ChannelSftp;

//==============================================================================
public class SSHSFtp
{
    //--------------------------------------------------------------------------
    /** create new instance.
     */
    public SSHSFtp ()
    {
    }
    
    //--------------------------------------------------------------------------
    public void bind (final SSHServer aServer)
        throws Exception
    {
        m_aServer = aServer;
    }

    //--------------------------------------------------------------------------
    public int uploadStream (InputStream aStream    ,
                             String      sRemoteDir ,
                             String      sRemoteName)
        throws Exception
    {
    	final SSHIdentity aIdentity  = m_aServer.accessIdentity ();
    	final boolean     bNeedsSudo = aIdentity.needsSudo();
    	
    	if (bNeedsSudo)
    		return uploadStreamIndirect (aStream, sRemoteDir, sRemoteName);
    	else
    		return uploadStreamDirect   (aStream, sRemoteDir, sRemoteName);
    }

    //--------------------------------------------------------------------------
    public int uploadStreamIndirect (final InputStream aStream    ,
                             	     final String      sRemoteDir ,
                             	     final String      sRemoteName)
        throws Exception
    {
    	final SSHConnection   aConnection = m_aServer.accessConnection ();
        final ChannelSftp     aChannel    = (ChannelSftp) aConnection.openChannel("sftp");
        final String          sTEMP       = SSHMacros.getEnvTEMP(m_aServer);
        final String          sTempDir    = FilenameUtils.concat(sTEMP, "sshsftp-temp");

        aChannel.connect();
        
        SSHMacros.mkdir(m_aServer, sTempDir);
        SSHMacros.chmod(m_aServer, sTempDir, "777");
        SSHMacros.touch(m_aServer, sTempDir, sRemoteName);
        SSHMacros.chmod(m_aServer, sTempDir+"/"+sRemoteName, "777");

        aChannel.cd   (sTempDir);
        aChannel.put  (aStream, sRemoteName);

        SSHMacros.mkdir   (m_aServer, sRemoteDir);
        SSHMacros.moveFile(m_aServer, sTempDir, sRemoteName, sRemoteDir, sRemoteName);

        aChannel.disconnect();

        final int nState = impl_getCorrectExitStatus(aChannel);
        m_aServer.doAutoFailIfNeeded(nState);
        return nState;
    }

    //--------------------------------------------------------------------------
    public int uploadStreamDirect (final InputStream aStream    ,
                             	   final String      sRemoteDir ,
                             	   final String      sRemoteName)
        throws Exception
    {
    	final SSHConnection aConnection = m_aServer.accessConnection ();
        final ChannelSftp   aChannel    = (ChannelSftp) aConnection.openChannel("sftp");

        aChannel.setPty(true);

        aChannel.connect();

        aChannel.cd  (sRemoteDir);
        aChannel.put (aStream   , sRemoteName);

        aChannel.disconnect();

        final int nState = impl_getCorrectExitStatus(aChannel);
        m_aServer.doAutoFailIfNeeded(nState);
        return nState;
    }

    //--------------------------------------------------------------------------
    /** hacky ...
     *  
     *  seems that ChannelSftp has default of -1 as exit state ...
     *  and it's internal setExitStatus() methods is not called always.
     *  So at the end we get -1 as final result for nearly all requests we do.
     *  
     *  The only chance we have is to ignore -1 and use 0 as "override" instead.
     */
    private int impl_getCorrectExitStatus (final ChannelSftp aChannel)
    	throws Exception
    {
    	final int nOrgState = aChannel.getExitStatus();
    	if (nOrgState == -1)
    		return 0;
    	else
    		return nOrgState;
    }

    //--------------------------------------------------------------------------
    private SSHServer m_aServer = null;
}
