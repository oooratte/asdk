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
import java.util.Enumeration;
import java.util.Properties;

import org.apache.commons.io.IOUtils;

//==============================================================================
public class SSHPropertiesFile
{
    //--------------------------------------------------------------------------
    /** create new instance.
     */
    public SSHPropertiesFile ()
    {
    }
    
    //--------------------------------------------------------------------------
    public void bind (final SSHServer aServer)
        throws Exception
    {
        m_aServer = aServer;
    }
    
    //--------------------------------------------------------------------------
    @SuppressWarnings("unchecked")
	public int setProperties (final String     sRemoteDir ,
                              final String     sRemoteFile,
                              final Properties lProps     )
        throws Exception
    {
    	final StringBuffer          sContent = new StringBuffer (256);
    	final Enumeration< String > lKeys    = (Enumeration< String >)lProps.propertyNames();
    	      
    	while (lKeys.hasMoreElements())
    	{
    		final String sKey   = lKeys.nextElement();
    		final String sValue = lProps.getProperty(sKey);
    		sContent.append(sKey  );
    		sContent.append("="   );
    		sContent.append(sValue);
    		sContent.append("\n"  );
    	}
    	
    	final InputStream     aContent = IOUtils.toInputStream(sContent.toString(), "utf-8");
    	final SSHShellExecute aShell   = m_aServer.accessShell ();
    	final SSHSFtp         aUpload  = m_aServer.accessSFTP  ();
    	
    	aShell.execute("mkdir -p "+sRemoteDir);
    	aUpload.uploadStream(aContent, sRemoteDir, sRemoteFile);

    	return 0;
    }

    //--------------------------------------------------------------------------
    private SSHServer m_aServer = null;
}
