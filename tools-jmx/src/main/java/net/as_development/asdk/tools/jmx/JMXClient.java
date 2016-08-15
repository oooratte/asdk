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
package net.as_development.asdk.tools.jmx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.Query;
import javax.management.QueryExp;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.naming.Context;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

//=============================================================================
public class JMXClient
{
	//-------------------------------------------------------------------------
	public JMXClient ()
		throws Exception
	{}

	//-------------------------------------------------------------------------
	public void setURL (final String sURL)
		throws Exception
	{
		m_sURL = sURL;
	}

	//-------------------------------------------------------------------------
	public void setAuth (final String sUser    ,
						 final String sPassword)
	    throws Exception
	{
		m_sAuthUser     = sUser;
		m_sAuthPassword = sPassword;
	}

	//-------------------------------------------------------------------------
	public void connect ()
	    throws Exception
	{
		if (m_aConnection != null)
			return;

		JMXConnector aConnection = null;
		if ( ! StringUtils.isEmpty(m_sAuthUser))
			aConnection = impl_connectWithAuth (m_sURL, m_sAuthUser, m_sAuthPassword);
		else
			aConnection = impl_connectNoAuth   (m_sURL);
		
		Validate.notNull(aConnection, "Could not connectt to '"+m_sURL+"'.");
		
		m_aConnection  = aConnection;
		m_aMBeanServer = aConnection.getMBeanServerConnection();
	}

	//-------------------------------------------------------------------------
	public void disconnect ()
	    throws Exception
	{
		if (m_aConnection == null)
			return;

		final JMXConnector   aConnection  = m_aConnection;
		                   m_aConnection  = null;
		                   m_aMBeanServer = null;
		aConnection.close();
	}

	//-------------------------------------------------------------------------
	public String dumpAll ()
	    throws Exception
	{
		final StringBuffer           sDump        = new StringBuffer (256);
	    final Iterator< ObjectName > lObjectNames = m_aMBeanServer.queryNames( null, null ).iterator();
	    
	    while (lObjectNames.hasNext())
	    {
	    	final ObjectName aObjectName = lObjectNames.next();
	    	sDump.append(aObjectName);
	    	sDump.append("\n"       );
	    }

		return sDump.toString ();
	}

	//-------------------------------------------------------------------------
	public String dumpSome (final String sRoot)
		throws Exception
	{
		final ObjectName             aRoot        = new ObjectName   (sRoot);
		final StringBuffer           sDump        = new StringBuffer (256  );
	    final Iterator< ObjectName > lObjectNames = m_aMBeanServer.queryNames( aRoot, null ).iterator();
	    
	    while (lObjectNames.hasNext())
	    {
	    	final ObjectName aObjectName = lObjectNames.next();
	    	sDump.append(aObjectName);
	    	sDump.append("\n"       );
	    }

		return sDump.toString ();
	}
	
	//-------------------------------------------------------------------------
	public List< String > list (final String sRoot)
		throws Exception
	{
		final List< String >         lResults     = new ArrayList< String > ();
		final ObjectName             aRoot        = new ObjectName   (sRoot);
	    final Iterator< ObjectName > lObjectNames = m_aMBeanServer.queryNames( aRoot, null ).iterator();

	    while (lObjectNames.hasNext())
	    {
	    	final ObjectName aObjectName = lObjectNames.next();
	    	final String     sObjectName = aObjectName.getCanonicalName();
	    	lResults.add(sObjectName);
	    }

		return lResults;
	}

	//-------------------------------------------------------------------------
	public Map< String, List< Object > > query (final String    sRoot     ,
									            final String... lAttrNames)
		throws Exception
	{
		final Map< String, List< Object > > lResults     = new HashMap< String, List< Object > > ();
		final ObjectName                    aRoot        = new ObjectName   (sRoot);
	    final Iterator< ObjectName >        lObjectNames = m_aMBeanServer.queryNames( aRoot, null ).iterator();

	    while (lObjectNames.hasNext())
	    {
	    	final ObjectName     aObjectName = lObjectNames.next();
	    	final String         sObjectName = aObjectName.getCanonicalName();
	    	final AttributeList  lObjectAttr = m_aMBeanServer.getAttributes( aObjectName, lAttrNames );

	    	final Iterator< Object > pObjectAttr = lObjectAttr.iterator();
	    	final List< Object > lAttr = new ArrayList< Object > ();
	    	
	    	while (pObjectAttr.hasNext())
	    	{
	    		final Attribute aObjectAttr = (Attribute) pObjectAttr.next(); 
	    		lAttr.add(aObjectAttr.getValue());
	    	}

	    	lResults.put(sObjectName, lAttr);
	    }

		return lResults;
	}

	//-------------------------------------------------------------------------
	public Map< String, List< Object > > query2 (final String    sRoot      ,
												 final String    sQueryPath ,
												 final String    sQueryValue,
									             final String... lAttrNames )
		throws Exception
	{
		final Map< String, List< Object > > lResults     = new HashMap< String, List< Object > > ();
		final ObjectName                    aRoot        = new ObjectName   (sRoot);
		final QueryExp                      aQuery       = Query.match(Query.attr(sQueryPath), Query.value(sQueryValue));
	    final Iterator< ObjectName >        lObjectNames = m_aMBeanServer.queryNames( aRoot, aQuery ).iterator();

	    while (lObjectNames.hasNext())
	    {
	    	final ObjectName     aObjectName = lObjectNames.next();
	    	final String         sObjectName = aObjectName.getCanonicalName();
	    	final AttributeList  lObjectAttr = m_aMBeanServer.getAttributes( aObjectName, lAttrNames );

	    	final Iterator< Object > pObjectAttr = lObjectAttr.iterator();
	    	final List< Object > lAttr = new ArrayList< Object > ();
	    	
	    	while (pObjectAttr.hasNext())
	    	{
	    		final Attribute aObjectAttr = (Attribute) pObjectAttr.next(); 
	    		lAttr.add(aObjectAttr.getValue());
	    	}

	    	lResults.put(sObjectName, lAttr);
	    }

		return lResults;
	}
	//-------------------------------------------------------------------------
	private JMXConnector impl_connectWithAuth (final String sURL     ,
											   final String sUser    ,
											   final String sPassword)
		throws Exception
	{
		final JMXServiceURL         aURL  = new JMXServiceURL(sURL);
	    final Map< String, Object > aAuth = new HashMap< String, Object >();

	    aAuth.put( "jmx.remote.credentials"    , new String[] { sUser, sPassword } );
	    aAuth.put( Context.SECURITY_PRINCIPAL  , sUser                             );
	    aAuth.put( Context.SECURITY_CREDENTIALS, sPassword                         );
		
		final JMXConnector  aConnection = JMXConnectorFactory.connect(aURL, aAuth);
		return aConnection;
	}
	
	//-------------------------------------------------------------------------
	private JMXConnector impl_connectNoAuth (final String sURL)
		throws Exception
	{
		final JMXServiceURL aURL        = new JMXServiceURL(sURL);
		final JMXConnector  aConnection = JMXConnectorFactory.connect(aURL);
		return aConnection;
	}

	//-------------------------------------------------------------------------
	private String m_sURL = null;

	//-------------------------------------------------------------------------
	private String m_sAuthUser = null;

	//-------------------------------------------------------------------------
	private String m_sAuthPassword = null;

	//-------------------------------------------------------------------------
	private JMXConnector m_aConnection = null;
	
	//-------------------------------------------------------------------------
    private MBeanServerConnection m_aMBeanServer = null;
}
