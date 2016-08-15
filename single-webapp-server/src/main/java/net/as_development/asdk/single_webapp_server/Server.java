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

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.util.resource.Resource;

//=============================================================================
public class Server
{
	//-------------------------------------------------------------------------
	public Server ()
	    throws Exception
	{}

	//-------------------------------------------------------------------------
	public void init ()
	    throws Exception
	{
		final int nPort = 8080; // TODO read from config
		
		m_aServer = new org.eclipse.jetty.server.Server (nPort);

		final ContextHandler  aContext4Content = new ContextHandler ("/"   );
		final ResourceHandler aContentHandler  = new ResourceHandler ();
		final Resource        aResBase         = Resource.newClassPathResource("/net/as_development/asdk/single_webapp_server/res");
		aContentHandler .setBaseResource(aResBase       );
		aContext4Content.setHandler     (aContentHandler);

		final ServletContextHandler aContext4Servlet = new ServletContextHandler (ServletContextHandler.NO_SESSIONS);
		aContext4Servlet.setContextPath("/api");
		aContext4Servlet.addServlet    (TestTheRest.class, "/ttr");
		
		final ContextHandlerCollection aContextRoot = new ContextHandlerCollection();
		aContextRoot.setHandlers(new Handler[] {aContext4Content, aContext4Servlet});
		
		m_aServer.setHandler(aContextRoot);
	}

	//-------------------------------------------------------------------------
	public void start ()
	    throws Exception
	{
		m_aServer.start();
	}

	//-------------------------------------------------------------------------
	public void join ()
	    throws Exception
	{
		m_aServer.join();
	}

	//-------------------------------------------------------------------------
	public void stop ()
	    throws Exception
	{}

	//-------------------------------------------------------------------------
	private org.eclipse.jetty.server.Server m_aServer = null;
}
