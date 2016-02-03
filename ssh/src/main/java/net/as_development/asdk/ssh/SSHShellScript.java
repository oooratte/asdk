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

public class SSHShellScript
{
    //--------------------------------------------------------------------------
    public SSHShellScript ()
    {}
    
//    //--------------------------------------------------------------------------
//    public SSHShellScript (SSHConnection aConnection,
//                           ShellScript   aScript    )
//        throws Exception
//    {
//        m_aConnection = aConnection;
//        m_aScript     = aScript;
//    }
//
//    //--------------------------------------------------------------------------
//    public void setConnection (SSHConnection aConnection)
//        throws Exception
//    {
//        m_aConnection = aConnection;
//    }
//    
//    //--------------------------------------------------------------------------
//    public void setScript (ShellScript aScript)
//        throws Exception
//    {
//        m_aScript = aScript;
//    }
//    
//    //--------------------------------------------------------------------------
//    public void setRemotePath (String sPath)
//        throws Exception
//    {
//        m_sRemotePath = sPath;
//    }
//
//    //--------------------------------------------------------------------------
//    public void setRemoteFile (String sFile)
//        throws Exception
//    {
//        m_sRemoteFile = sFile;
//    }
//
//    //--------------------------------------------------------------------------
//    public int execute ()
//        throws Exception
//    {
//        StringBuffer            aStdOut  = new StringBuffer (256);
//        StringBuffer            aStdErr  = new StringBuffer (256);
//        SSHSFtp 		        aUpload  = new SSHSFtp         (m_aConnection);
//        SSHShellExecute         aShell   = new SSHShellExecute (m_aConnection);
//        OperatingSystem.EOSType eOS      = impl_getOS ();
//
//        aUpload.connectStdOut(aStdOut);
//        aUpload.connectStdErr(aStdErr);
//        aShell.connectStdOut (aStdOut);
//        aShell.connectStdErr (aStdErr);
//
//        if (StringUtils.isEmpty(m_sRemoteFile))
//            m_sRemoteFile = m_aScript.getName();
//        
//        System.out.println ("execute script on '"+m_sRemotePath+"/"+m_sRemoteFile+"'");
//
//        int nState = 1;
//        if (eOS == OperatingSystem.EOSType.E_UNIX)
//            nState = impl_executeOnUnixBash (aUpload, aShell);
//        else
//        if (eOS == OperatingSystem.EOSType.E_WINDOWS)
//            nState = impl_executeOnWindowsDOS (aUpload, aShell);
//        else
//            throw new UnsupportedOperationException ("SSHShellScript.execute () for OS = '"+eOS+"' not implemented yet.");
//
//        System.out.println (aStdOut);
//        System.err.println (aStdErr);
//
//        return nState;
//    }
//    
//    //--------------------------------------------------------------------------
//    private int impl_executeOnUnixBash (SSHSFtp         aUpload,
//                                        SSHShellExecute aShell )
//        throws Exception
//    {
//        int nInternalState = 0;
//        int nScriptState   = 0;
//        
//        nInternalState = aShell.execute      ("mkdir -p "+m_sRemotePath                           );
//        nInternalState = aUpload.uploadStream(m_aScript.getStream(), m_sRemotePath, m_sRemoteFile );
//        nInternalState = aShell.execute      ("chmod 700 "+m_sRemotePath+"/"+m_sRemoteFile        );
//        nScriptState   = aShell.execute      ("sudo '"+m_sRemotePath+"/"+m_sRemoteFile+"'"        );
//        nInternalState = aShell.execute      ("rm "+m_sRemotePath+"/"+m_sRemoteFile               );
//        
//        if (nScriptState != 0)
//            return nScriptState;
//        
//        if (nInternalState != 0)
//            return nInternalState;
//        
//        return 0;
//    }
//
//    //--------------------------------------------------------------------------
//    private int impl_executeOnWindowsDOS (SSHSFtp         aUpload,
//                                          SSHShellExecute aShell )
//        throws Exception
//    {
//        int nInternalState = 0;
//        int nScriptState   = 0;
//        
//        nInternalState = aShell.execute      ("mkdir "+m_sRemotePath                             );
//        nInternalState = aUpload.uploadStream(m_aScript.getStream(), m_sRemotePath, m_sRemoteFile);
//        nScriptState   = aShell.execute      (m_sRemotePath+"/"+m_sRemoteFile                    );
//        nInternalState = aShell.execute      ("del "+m_sRemotePath+"/"+m_sRemoteFile             );
//        
//        if (nScriptState != 0)
//            return nScriptState;
//        
//        if (nInternalState != 0)
//            return nInternalState;
//        
//        return 0;
//    }
//    
//    //--------------------------------------------------------------------------
//    private OperatingSystem.EOSType impl_getOS ()
//    	throws Exception
//    {
//    	return m_aConnection.getOS().getOSType();
//    }
//    
//    //--------------------------------------------------------------------------
//    private SSHConnection m_aConnection = null;
//
//    //--------------------------------------------------------------------------
//    private ShellScript m_aScript = null;
//
//    //--------------------------------------------------------------------------
//    private String m_sRemotePath = null;
//
//    //--------------------------------------------------------------------------
//    private String m_sRemoteFile = null;
}
