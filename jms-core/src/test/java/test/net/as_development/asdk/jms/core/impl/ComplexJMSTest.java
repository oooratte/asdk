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

import java.util.concurrent.CountDownLatch;

import org.junit.Test;

import net.as_development.asdk.jms.core.IJMSServer;
import net.as_development.asdk.jms.core.JMSEnv;
import net.as_development.asdk.jms.core.JMSServer;
import net.as_development.asdk.jms.core.simple.ISubscriber;
import net.as_development.asdk.jms.core.simple.SimpleMessaging;

//=============================================================================
public class ComplexJMSTest
{
	//-------------------------------------------------------------------------
	@Test
	public void test()
		throws Exception
	{
		final JMSEnv aEnv = new JMSEnv ();
		aEnv.setServerIP     ("localhost"    );
		aEnv.setServerPort   (4711           );
		aEnv.enableFailOver  (true           );
		aEnv.setLocalDataPath("/tmp/amq-data");
		aEnv.setJMXPort      (1099           );
		
		final IJMSServer iServer = JMSServer.newServer();
		iServer.setEnv(aEnv);
		iServer.start ();

		final String         sChannelA     = "topic://my-channel-01";
		final String         sChannelB     = "queue://my-channel-02";
		final CountDownLatch aShutdownSync = new CountDownLatch (14);

		final SimpleMessaging aClient01 = new SimpleMessaging ();
		aClient01.setEnv   (aEnv);
		aClient01.subscribe(sChannelA, new ISubscriber ()
		{
			@Override
			public void recieve(final String sMessage)
				throws Exception
			{
				System.err.println("##### DBG : client-01 : got message : "+sMessage);
				aShutdownSync.countDown();
			}
		});
		
		final SimpleMessaging aClient02 = new SimpleMessaging ();
		aClient02.setEnv   (aEnv);
		aClient02.subscribe(sChannelA, new ISubscriber ()
		{
			@Override
			public void recieve(final String sMessage)
				throws Exception
			{
				System.err.println("##### DBG : client-02 : got message : "+sMessage);
				aShutdownSync.countDown();
			}
		});

		final SimpleMessaging aClient03 = new SimpleMessaging ();
		aClient03.setEnv   (aEnv);
		aClient03.subscribe(sChannelB, new ISubscriber ()
		{
			@Override
			public void recieve(final String sMessage)
				throws Exception
			{
				System.err.println("##### DBG : client-03 : got message : "+sMessage);
				aShutdownSync.countDown();
			}
		});

		aClient01.publish(sChannelA, "Hello World (01->xx).");
		aClient02.publish(sChannelA, "Hello World (02->xx).");
		
		for (int i=0; i<10; ++i)
			aClient02.publish(sChannelB, "Hello World ["+i+"] (02->03).");
		
		aShutdownSync.await();
		
		aClient01.close();
		aClient02.close();
		iServer.stop ();
	}
}
