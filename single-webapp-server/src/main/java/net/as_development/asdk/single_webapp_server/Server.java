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
package net.as_development.asdk.single_webapp_server;

import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.server.ssl.SslSelectChannelConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

//=============================================================================
public class Server
{
	//-------------------------------------------------------------------------
	public Server ()
	    throws Exception
	{}

	//-------------------------------------------------------------------------
	public synchronized void configure (final ServerConfiguration aConfig)
	    throws Exception
	{
		m_aConfig = aConfig;
	}

	//-------------------------------------------------------------------------
	public /* no synchronized */ void start ()
	    throws Exception
	{
		mem_Server ().start();
	}

	//-------------------------------------------------------------------------
	public /* no synchronized */ void join ()
	    throws Exception
	{
		mem_Server ().join();
	}

	//-------------------------------------------------------------------------
	public /* no synchronized */ void stop ()
	    throws Exception
	{
		mem_Server ().stop();
	}

	//-------------------------------------------------------------------------
	private synchronized org.eclipse.jetty.server.Server mem_Server ()
	    throws Exception
	{
		if (m_aServer != null)
			return m_aServer;
		
		final ServerConfiguration             aConfig = m_aConfig;
		final org.eclipse.jetty.server.Server aServer = new org.eclipse.jetty.server.Server ();

		impl_defineInterfaces (aServer, aConfig);
		impl_registerApps     (aServer, aConfig);

		m_aServer = aServer;
		return m_aServer;
	}

	//-------------------------------------------------------------------------
	private /* no synchronized */ void impl_defineInterfaces (final org.eclipse.jetty.server.Server aServer,
			  												  final ServerConfiguration             aConfig)
	    throws Exception
	{
	    SelectChannelConnector    aHttpConnector  = null;
        SslSelectChannelConnector aHttpsConnector = null;
		
		if (aConfig.isHttps())
		{
			aHttpsConnector = new SslSelectChannelConnector ();
	
			// https://github.com/eclipse/jetty.project/blob/jetty-9.3.x/examples/embedded/src/main/java/org/eclipse/jetty/embedded/LikeJettyXml.java
			aHttpsConnector.setKeystore   ("/Users/andreas/.keystores/keystore-asdk-default");
			aHttpsConnector.setKeyPassword("123456"                                         );
			aHttpsConnector.setPort       (aConfig.getHttpsPort     ()                      );
			aHttpsConnector.setHost       (aConfig.getHttpsInterface()                      );
		}

		if (aConfig.isHttp())
		{
			aHttpConnector = new SelectChannelConnector ();
	
			aHttpConnector.setPort (aConfig.getHttpPort     ());
			aHttpConnector.setHost (aConfig.getHttpInterface());
		}

		Connector[] lConnectors = new Connector[0];
		if (aHttpsConnector != null)
			lConnectors = ArrayUtils.add(lConnectors, aHttpsConnector);
		if (aHttpConnector != null)
			lConnectors = ArrayUtils.add(lConnectors, aHttpConnector);
		
		aServer.setConnectors(lConnectors);
	}
	
	//-------------------------------------------------------------------------
	private /* no synchronized */ void impl_registerApps (final org.eclipse.jetty.server.Server aServer,
														  final ServerConfiguration             aConfig)
		throws Exception
	{
		final List< ServerAppDescriptor > lApps = aConfig.getApps();
		for (final ServerAppDescriptor aApp : lApps)
		{
			final ResourceConfig aAppConfig = new ResourceConfig();
	        aAppConfig.packages(aApp.getPackage());

			final ServletHolder aAppServlet = new ServletHolder(new ServletContainer(aAppConfig));

			final ServletContextHandler aAppContext = new ServletContextHandler();
			aAppContext.setContextPath(aApp.getContextPath());
			aAppContext.addServlet    (aAppServlet, "/*"    );

			aServer.setHandler(aAppContext);
		}
	}

	//-------------------------------------------------------------------------
	private ServerConfiguration m_aConfig = null;
	
	//-------------------------------------------------------------------------
	private org.eclipse.jetty.server.Server m_aServer = null;
}
