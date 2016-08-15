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
package test.net.as_development.asdk.persistence.real;

import org.junit.Ignore;

import net.as_development.asdk.persistence.ISimplePersistence;
import net.as_development.asdk.persistence.SimplePersistenceFactory;
import net.as_development.asdk.persistence.impl.HZClient;

//=============================================================================
@Ignore
public class RealHZClient02
{
	//-------------------------------------------------------------------------
	public static void main (final String[] args)
		throws Exception
	{
		final ISimplePersistence iClient02 = impl_newClient ();
		//System.out.println ("[02] k.a = " + iClient02.get("k.a"));
		
		System.out.println (iClient02.listKeys());
	}

	//-------------------------------------------------------------------------
	private static ISimplePersistence impl_newClient ()
	    throws Exception
	{
		final ISimplePersistence iClient = SimplePersistenceFactory.get(HZClient.class.getName (),
				ISimplePersistence.CFG_PERSISTENCE_SCOPE,                  RealHZServer.PERSIST_SCOPE   ,
				HZClient.CFG_SERVER_HOST                ,                  RealHZServer.SERVER_HOST     ,
				HZClient.CFG_SERVER_PORT                , Integer.toString(RealHZServer.SERVER_PORT    ),
				HZClient.CFG_SERVER_ID                  ,                  RealHZServer.SERVER_ID       ,
				HZClient.CFG_SERVER_PASSWORD            ,                  RealHZServer.SERVER_PASSWORD);
		return iClient;
	}
}
