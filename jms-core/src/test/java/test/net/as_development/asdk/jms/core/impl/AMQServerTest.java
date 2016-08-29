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
package test.net.as_development.asdk.jms.core.impl;

import org.junit.Ignore;

import net.as_development.asdk.jms.core.JMSClientBase;
import net.as_development.asdk.jms.core.JMSEnv;
import net.as_development.asdk.jms.core.beans.JMSMessageBean;
import net.as_development.asdk.jms.core.impl.AMQServer;

//=============================================================================
public class AMQServerTest
{
	//-------------------------------------------------------------------------
	@Ignore
	public void test()
		throws Exception
	{
		final JMSEnv aEnv = new JMSEnv ();
		aEnv.setServerIP     ("localhost"    );
		aEnv.setServerPort   (4711           );
		aEnv.enableFailOver  (true           );
		aEnv.setLocalDataPath("/tmp/amq-data");
		aEnv.setJMXPort      (1099           );
		
		final AMQServer aServer = new AMQServer ();
		aServer.setEnv(aEnv);
		
		final JMSClientBase aClient = new JMSClientBase ();
		aClient.setEnv           (aEnv            );
		aClient.setClientID      ("client-01"     );
		aClient.setQueueNamespace("test.client.01");
		aClient.setTargetQueueType("foo");

		aServer.start();
		aClient.start();

		System.err.println("... send request");
		JMSMessageBean aRequest  = new JMSMessageBean ();
		System.err.println("... wait for response");
		JMSMessageBean aResponse = aClient.requestSynchronous(aRequest);
		System.err.println("... got response");
		
		System.out.println("request  : "+aRequest );
		System.out.println("response : "+aResponse);
		
		Thread.sleep(1000);
		aClient.stop();
		aServer.stop();
	}
}
