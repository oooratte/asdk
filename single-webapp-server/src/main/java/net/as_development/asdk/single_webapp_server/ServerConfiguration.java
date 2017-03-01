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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.as_development.tools.configuration.ConfigBase;

//=============================================================================
public class ServerConfiguration extends ConfigBase
{
	//-------------------------------------------------------------------------
	private ServerConfiguration ()
		throws Exception
	{}

	//-------------------------------------------------------------------------
	public static ServerConfiguration get (final String sConfigPathURL,
										   final String sConfigPackage)
		throws Exception
	{
		final ServerConfiguration aConfig = new ServerConfiguration ();
		aConfig.defineConfig(sConfigPathURL, sConfigPackage);
		return aConfig;
	}

	//-------------------------------------------------------------------------
	public boolean isHttp ()
		throws Exception
	{
		final boolean bIsHttp = (getHttpPort () != null);
		return bIsHttp;
	}
	
	//-------------------------------------------------------------------------
	public Integer getHttpPort ()
		throws Exception
	{
		return get("server.http.port", Integer.class);
	}
	
	//-------------------------------------------------------------------------
	public String getHttpInterface ()
		throws Exception
	{
		return get("server.http.interface", String.class);
	}

	//-------------------------------------------------------------------------
	public boolean isHttps ()
		throws Exception
	{
		final boolean bIsHttps = (getHttpsPort () != null);
		return bIsHttps;
	}
	
	//-------------------------------------------------------------------------
	public Integer getHttpsPort ()
		throws Exception
	{
		return get("server.https.port", Integer.class);
	}
	
	//-------------------------------------------------------------------------
	public String getHttpsInterface ()
		throws Exception
	{
		return get("server.https.interface", String.class);
	}

	//-------------------------------------------------------------------------
	public List< ServerAppDescriptor > getApps ()
	    throws Exception
	{
		final List< ServerAppDescriptor >  lApps   = new ArrayList< ServerAppDescriptor > ();
		final Set< Map< String, String > > aAppSet = gets ("apps", "app");

		for (final Map< String, String > aAppDef : aAppSet)
		{
			final ServerAppDescriptor aApp = new ServerAppDescriptor ();
			aApp.setContextPath(aAppDef.get("context-path"));
			aApp.setPackage    (aAppDef.get("package"     ));
			
			System.err.println(aApp);
			
			lApps.add(aApp);
		}
		
		return lApps;
	}
}
