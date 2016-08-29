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
package net.as_development.asdk.jms.core;

import java.net.URI;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import net.as_development.asdk.tools.common.UriBuilder;

public class JMSEnv
{
	//-------------------------------------------------------------------------
	public static final String CONFIG_FILE = "jms.properties";

	//-------------------------------------------------------------------------
	public static final String CFGPROP_JMS_SERVER_IP        = "jms.server.ip"       ;
	public static final String CFGPROP_JMS_SERVER_PORT      = "jms.server.port"     ;
	public static final String CFGPROP_JMS_SERVER_DATA_PATH = "jms.server.data.path";
	public static final String CFGPROP_JMS_FEATURE_FAILOVER = "jms.feature.failover";
	
	//-------------------------------------------------------------------------
	public JMSEnv ()
	{}
	
	//-------------------------------------------------------------------------
	public synchronized void init (final Properties aConfig)
	    throws Exception
	{
		Validate.notNull(aConfig, "Miss configuration file '"+CONFIG_FILE+"'.");
	
		// a) mandatory values
		
		final String sServerIP   = aConfig.getProperty (CFGPROP_JMS_SERVER_IP  );
		final String sServerPort = aConfig.getProperty (CFGPROP_JMS_SERVER_PORT);
		
		Validate.notEmpty(sServerIP  , "Miss configuration value for '"+CFGPROP_JMS_SERVER_IP  +"'.");
		Validate.notEmpty(sServerPort, "Miss configuration value for '"+CFGPROP_JMS_SERVER_PORT+"'.");
		
		m_sServerIP   = sServerIP;
		m_nServerPort = Integer.parseInt(sServerPort); // mandatory ! at least AMQ requires that .-)
		
		// b) optional values
		
		String sServerDataPath = aConfig.getProperty(CFGPROP_JMS_SERVER_DATA_PATH);
		m_sServerDataPath = sServerDataPath;
	}
	
	//-------------------------------------------------------------------------
	public synchronized void setServerIP (final String sIP)
	    throws Exception
	{
		m_sServerIP = sIP;
	}
	
	//-------------------------------------------------------------------------
	public synchronized void setServerPort (final int nPort)
	    throws Exception
	{
		m_nServerPort = nPort;
	}

	//-------------------------------------------------------------------------
	public synchronized final URI getServerURI ()
	    throws Exception
	{
		final URI aURI = UriBuilder.newUri()
							.scheme        ("tcp"        )
							.host          (m_sServerIP  )
							.port          (m_nServerPort)
							.toUri         (             );
		System.err.println("AMQ URI = '"+aURI+"'");
		return aURI;
	}

	//-------------------------------------------------------------------------
	public synchronized final String getServerURIString ()
	    throws Exception
	{
		final URI    aURI = getServerURI ();
		final String sURI = aURI.toASCIIString ();
		return sURI;
	}
	
	//-------------------------------------------------------------------------
	public synchronized final String getContextFactoryName ()
	    throws Exception
	{
		// TODO configure me ... depending from used server implementation .-)
		return "org.apache.activemq.jndi.ActiveMQInitialContextFactory";
	}

	//-------------------------------------------------------------------------
	public synchronized void setLocalDataPath (final String sDataPath)
	    throws Exception
	{
		m_sServerDataPath = sDataPath;
	}

	//-------------------------------------------------------------------------
	public synchronized String getLocalDataPath ()
	    throws Exception
	{
		return m_sServerDataPath;
	}

	//-------------------------------------------------------------------------
	public synchronized boolean isPersistent ()
		throws Exception
	{
		final boolean bIsPersistent = ( ! StringUtils.isEmpty(m_sServerDataPath));
		return bIsPersistent;
	}
	
	//-------------------------------------------------------------------------
	public synchronized void enableFailOver (final boolean bFailOver)
	    throws Exception
	{
		m_bFailOver = bFailOver;
	}
	
	//-------------------------------------------------------------------------
	public synchronized boolean isFailOver ()
	    throws Exception
	{
		return m_bFailOver;
	}

	//-------------------------------------------------------------------------
	public synchronized void setJMXPort (final int nPort)
	    throws Exception
	{
		m_nJMXPort = nPort;
	}
	
	//-------------------------------------------------------------------------
	public synchronized int getJMXPort ()
	    throws Exception
	{
		return m_nJMXPort;
	}

	//-------------------------------------------------------------------------
	public synchronized boolean isJMX ()
	    throws Exception
	{
		final boolean bIsJMX = (m_nJMXPort != null);
		return bIsJMX;
	}

	//-------------------------------------------------------------------------
	public synchronized void setGCFrequency (final int nFrequency)
	    throws Exception
	{
		m_nGCFrequency = nFrequency;
	}

	//-------------------------------------------------------------------------
	public synchronized int getGCFrequency ()
	    throws Exception
	{
		return m_nGCFrequency;
	}

	//-------------------------------------------------------------------------
	public synchronized boolean isGCFrequency ()
	    throws Exception
	{
		final boolean bIsFrequency = (m_nGCFrequency != null);
		return bIsFrequency;
	}

	//-------------------------------------------------------------------------
	private static JMSEnv m_gEnv = null;

	//-------------------------------------------------------------------------
	private String m_sServerIP = null;

	//-------------------------------------------------------------------------
	private Integer m_nServerPort = null;

	//-------------------------------------------------------------------------
	private String m_sServerDataPath = null;

	//-------------------------------------------------------------------------
	private boolean m_bFailOver = false;

	//-------------------------------------------------------------------------
	private Integer m_nJMXPort = null;

	//-------------------------------------------------------------------------
	private Integer m_nGCFrequency = null;
}
