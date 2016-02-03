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
