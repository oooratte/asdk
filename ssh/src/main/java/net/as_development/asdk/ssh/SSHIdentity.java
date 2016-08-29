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
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import com.jcraft.jsch.UserInfo;

//==============================================================================
/**
 * TODO document me
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
                throw new IllegalArgumentException ("Key file '"+sFile+"' seems not to be a valid file.");
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
