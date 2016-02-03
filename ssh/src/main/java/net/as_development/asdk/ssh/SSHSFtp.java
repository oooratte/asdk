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
