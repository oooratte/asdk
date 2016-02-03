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

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

//==============================================================================
public class SSHConnection
{
    //--------------------------------------------------------------------------
    public static final int DEFAULT_PORT = 22;

    //--------------------------------------------------------------------------
    public static final int DEFAULT_CONNECTION_TIMEOUT = 300000; // 5 min

    //--------------------------------------------------------------------------
    /** create new instance.
     */
    public SSHConnection ()
    {}
    
    //--------------------------------------------------------------------------
    public void setHost (String sHost)
        throws Exception
    {
        m_sHost = sHost;
    }
    
    //--------------------------------------------------------------------------
    public void setPort (int nPort)
        throws Exception
    {
        m_nPort = nPort;
    }

    //--------------------------------------------------------------------------
    public void setIdentity (SSHIdentity aIdentity)
        throws Exception
    {
        m_aIdentity = aIdentity;
    }
    
    //--------------------------------------------------------------------------
    public void setKnownHostsFile (String sFile)
        throws Exception
    {
        File aKnownHosts = new File (sFile);
        if ( ! aKnownHosts.isFile())
            throw new IllegalArgumentException ("Known hosts file is not a valid file.");

        m_aKnownHosts = aKnownHosts;
    }

    //--------------------------------------------------------------------------
    public SSHIdentity getIdentity ()
        throws Exception
    {
        return m_aIdentity;
    }

    //--------------------------------------------------------------------------
    public void connect ()
        throws Exception
    {
        connect (SSHConnection.DEFAULT_CONNECTION_TIMEOUT);
    }
    
    //--------------------------------------------------------------------------
    public void connect (int nTimeout)
        throws Exception
    {
        if (m_aConnection != null)
            return;

        JSch aSSH = mem_SSHImpl ();
        aSSH.setKnownHosts(mem_KnownHostsFile ().getAbsolutePath());

        String sUser       = m_aIdentity.getUser();
        String sPassphrase = m_aIdentity.getPassphrase();
        String sKeyFile    = m_aIdentity.getKeyFileName();
        String sHost       = m_sHost;
        int    nPort       = m_nPort;
/*
        System.out.println ("******** ssh user     = '"+sUser      +"'");
        System.out.println ("******** ssh password = '"+sPassword  +"'");
        System.out.println ("******** ssh phrase   = '"+sPassphrase+"'");
*/
        if ( ! StringUtils.isEmpty(sKeyFile))
            aSSH.addIdentity(sKeyFile, sPassphrase);

        m_aConnection = aSSH.getSession(sUser, sHost, nPort);
        m_aConnection.setUserInfo (m_aIdentity);

        boolean bKnownHostsHandlingEnabled = (m_aKnownHosts != null);
        String  sOptionOn                  = (bKnownHostsHandlingEnabled ? "yes": "no");
        m_aConnection.setConfig ("HashKnownHosts"       , sOptionOn);
        m_aConnection.setConfig ("StrictHostKeyChecking", "no");

        try
        {
        	m_aConnection.setOutputStream(System.out);
        	m_aConnection.connect(nTimeout);
        }
        catch(Throwable ex)
        {
        	ex.printStackTrace();
        }
    }

    //--------------------------------------------------------------------------
    public void disconnect ()
        throws Exception
    {
        if (m_aConnection == null)
            return;

        Session aConnection = m_aConnection;
        m_aConnection = null;
        aConnection.disconnect();
    }

    //--------------------------------------------------------------------------
    public Object openChannel (String sChannel)
        throws Exception
    {
        connect ();
        return m_aConnection.openChannel(sChannel);
    }

    //--------------------------------------------------------------------------
    private JSch mem_SSHImpl ()
        throws Exception
    {
        if (m_aSSHImpl == null)
            m_aSSHImpl = new JSch ();
        return m_aSSHImpl;
    }

    //--------------------------------------------------------------------------
    private File mem_KnownHostsFile ()
        throws Exception
    {
        if (m_aKnownHosts == null)
        {
            String sTempDir      = System.getProperty("java.io.tmpdir");
                   m_aKnownHosts = new File (sTempDir, "known_hosts.data");
                   
            if ( ! m_aKnownHosts.isFile())
            	FileUtils.write(m_aKnownHosts, "", "utf-8");
        }
        return m_aKnownHosts;
    }

    //--------------------------------------------------------------------------
    private String m_sHost = null;
    
    //--------------------------------------------------------------------------
    private int m_nPort = SSHConnection.DEFAULT_PORT;

    //--------------------------------------------------------------------------
    private File m_aKnownHosts = null;
    
    //--------------------------------------------------------------------------
    private SSHIdentity m_aIdentity = null;

    //--------------------------------------------------------------------------
    private JSch m_aSSHImpl = null;

    //--------------------------------------------------------------------------
    private Session m_aConnection = null;
}
