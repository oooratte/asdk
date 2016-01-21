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
