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
package test.net.as_development.asdk.tools.jmx;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

import net.as_development.asdk.tools.jmx.JMXClient;

//=============================================================================
public class JMXClientTest
{
	//-------------------------------------------------------------------------
	@Test
	public void test()
		throws Exception
	{
		JMXClient aClient = new JMXClient ();
		aClient.setURL    ("service:jmx:rmi://10.20.28.149:1098/jndi/rmi://10.20.28.149:1099/jmxrmi");
		aClient.connect   ();
		
		//System.out.println(aClient.dumpAll());
		// org.apache.activemq:type=Broker,brokerName=localhost,destinationType=Queue,destinationName=doc-03252d48-f992-4b69-8d58-1ec6fa65b43f-doc-in

		/*
org.apache.activemq:type=Broker,brokerName=localhost,destinationType=Queue,destinationName=doc-03252d48-f992-4b69-8d58-1ec6fa65b43f-doc-in,
endpoint=Consumer,clientId=ID_rt2-cluster-backend-3-40131-1461848573145-2_4,consumerId=ID_rt2-cluster-backend-3-40131-1461848573145-3_4_1_1		 
		 */
		
		final String DOMAIN_QUEUES  = "org.apache.activemq:type=Broker,brokerName=localhost,destinationType=Queue,destinationName=doc-b35aa8d7*";
		final String DOMAIN_CLIENTS = "org.apache.activemq:type=Broker,brokerName=localhost,connector=clientConnectors,connectorName=openwire,connectionViewType=clientId,*";
		
		Map< String, List< Object > > lResults = aClient.query(DOMAIN_QUEUES, "Name", "EnqueueCount", "DequeueCount", "ProducerCount", "ConsumerCount");
		
		final Iterator< Entry < String, List< Object > > > rResults = lResults.entrySet().iterator();
		while (rResults.hasNext())
		{
			final Entry < String, List< Object > > aResult = rResults.next    ();
			final List< Object >                   lValues = aResult .getValue();
			
			final String sID            = aResult.getKey();
			final String sName          = (String) lValues.get(0);
			final Long   nEnqueueCount  = (Long  ) lValues.get(1);
			final Long   nDequeueCount  = (Long  ) lValues.get(2);
			final Long   nProducerCount = (Long  ) lValues.get(3);
			final Long   nConsumerCount = (Long  ) lValues.get(4);
			
			System.out.println("["+sName+"] : pro="+nProducerCount+" con="+nConsumerCount+" in="+nEnqueueCount+" out="+nDequeueCount);

			Map< String, List< Object > > lConsumeResults = aClient.query(sID+",endpoint=Consumer,*", "ClientId");
			Map< String, List< Object > > lProduceResults = aClient.query(sID+",endpoint=Producer,*", "ClientId");
			System.err.println(lConsumeResults);
			System.err.println(lProduceResults);
		}
		
		aClient.disconnect();
	}
}
