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

import org.apache.commons.io.FileUtils;
import org.junit.Ignore;

import net.as_development.asdk.persistence.impl.HZServer;

//=============================================================================
@Ignore
public class RealHZServer
{
	//-------------------------------------------------------------------------
	public static final String SERVER_INTERFACE = "127.0.0.1";
	public static final String SERVER_HOST      = "127.0.0.1";
	public static final int    SERVER_PORT      = 7890;
	public static final String SERVER_ID        = "hz-server-test";
	public static final String SERVER_PASSWORD  = "test";

	public static final String PERSIST_SCOPE    = "test-crash-recovery";

	//-------------------------------------------------------------------------
	public static void main (final String[] args)
		throws Exception
	{
		HZServer aServer = new HZServer ();
		aServer.setInterface         (SERVER_INTERFACE);
		aServer.setPort              (SERVER_PORT     );
		aServer.setId                (SERVER_ID       );
		aServer.setPassword          (SERVER_PASSWORD );
		aServer.enablePersistence    (FileUtils.getTempDirectoryPath()+"/test-crash-recovery");
		aServer.cleanPersistenceLayer();

		aServer.start ();
		System.err.println ("HZ Server started ...");
		synchronized (aServer)
		{
			aServer.join();
		}
	}
}
