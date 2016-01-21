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
