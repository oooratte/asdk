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

import java.io.File;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import com.jcraft.jsch.UserInfo;

//==============================================================================
/**
 * @todo document me
 */
public class SSHIdentity implements UserInfo
{
    //--------------------------------------------------------------------------
    /** create new instance.
     */
    public SSHIdentity ()
    {}

    //--------------------------------------------------------------------------
    /** create new instance with parameter.
     */
    public SSHIdentity (String sUser      ,
                        String sPassword  ,
                        String sPassphrase,
                        String sKeyFile   )
        throws Exception
    {
        setUser      (sUser      );
        setPassword  (sPassword  );
        setPassphrase(sPassphrase);
        setKeyFileName(sKeyFile  );
    }

    //--------------------------------------------------------------------------
    public void setNeedsSudo(final boolean bNeeds)
        throws Exception
    {
        m_bNeedsSudo = bNeeds;
    }

    //--------------------------------------------------------------------------
    public boolean needsSudo()
        throws Exception
    {
        return m_bNeedsSudo;
    }

    //--------------------------------------------------------------------------
    public void setUser(String sUser)
        throws Exception
    {
        m_sUser = sUser;
    }

    //--------------------------------------------------------------------------
    public void setPassword(String sPassword)
        throws Exception
    {
        m_sPassword = sPassword;
    }

    //--------------------------------------------------------------------------
    public void setKeyFileName(String sFile)
        throws Exception
    {
        if ( ! StringUtils.isEmpty(sFile))
        {
            m_aKeyFile = new File (sFile);
            if ( ! m_aKeyFile.isFile())
                throw new IllegalArgumentException ("Key file seems not to be a valid file.");
        }
    }

    //--------------------------------------------------------------------------
    public void setPassphrase(String sPassphrase)
        throws Exception
    {
        m_sPassphrase = sPassphrase;
    }

    //--------------------------------------------------------------------------
    public void sayYesToAllQuestions (boolean bYes)
        throws Exception
    {
        m_bYes = bYes;
    }

    //--------------------------------------------------------------------------
    public String getUser()
        throws Exception
    {
        return m_sUser;
    }

    //--------------------------------------------------------------------------
    public String getKeyFileName()
        throws Exception
    {
    	if (m_aKeyFile != null)
    		return m_aKeyFile.getAbsolutePath();
    	return null;
    }

    //--------------------------------------------------------------------------
    @Override
    public String getPassword()
    {
        return m_sPassword;
    }

    //--------------------------------------------------------------------------
    @Override
    public String getPassphrase()
    {
        return m_sPassphrase;
    }

    //--------------------------------------------------------------------------
    @Override
    public boolean promptPassword(String sMessage)
    {
        m_aLog.info ("prompt passwort for: '"+sMessage+"'");
        return (m_sPassword != null);
    }

    //--------------------------------------------------------------------------
    @Override
    public boolean promptPassphrase(String sMessage)
    {
        m_aLog.info ("prompt passphrase for: '"+sMessage+"'");
        return (m_sPassphrase != null);
    }

    //--------------------------------------------------------------------------
    @Override
    public boolean promptYesNo(String sMessage)
    {
        m_aLog.info ("prompt yes/no for: '"+sMessage+"'");
        return m_bYes;
    }

    //--------------------------------------------------------------------------
    @Override
    public void showMessage(String sMessage)
    {
        m_aLog.info (sMessage);
    }

    //--------------------------------------------------------------------------
    private boolean m_bNeedsSudo = false;
    
    //--------------------------------------------------------------------------
    private String m_sUser = null;

    //--------------------------------------------------------------------------
    private String m_sPassword = null;

    //--------------------------------------------------------------------------
    private String m_sPassphrase = null;

    //--------------------------------------------------------------------------
    private boolean m_bYes = false;

    //--------------------------------------------------------------------------
    private File m_aKeyFile = null;

    //--------------------------------------------------------------------------
    private Logger m_aLog = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
}
