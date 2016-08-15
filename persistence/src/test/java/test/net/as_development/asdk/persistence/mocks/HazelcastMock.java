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
package test.net.as_development.asdk.persistence.mocks;

import java.lang.reflect.Method;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IAtomicReference;
import com.hazelcast.core.IMap;
import com.hazelcast.transaction.TransactionContext;
import com.hazelcast.transaction.TransactionOptions;

//=============================================================================
/** Helper to mock the Hazelcast instances
 * 
 *  This do not implement the functionality manually.
 *  It uses mocking to simulate it.
 *  
 *  Several create() functions can be designed to define
 *  several aspects of this interface.
 */
public class HazelcastMock
{
	//-------------------------------------------------------------------------
	public HazelcastMock ()
	    throws Exception
	{}

	//-------------------------------------------------------------------------
	/** create a mock which behaves normally ...
	 *
	 *  No exceptions.
	 *  All set/get operations are mapped to the provided data sink.
	 *  You can analyze the results there.
	 * 
	 *  @param	aDataSink [IN]
	 *  		the 'data store' where all set/get requests are forwarded too.
	 *  
	 *  @return the operational mock.
	 */
	@SuppressWarnings("unchecked")
	public static HazelcastInstance createFullFunctionalMock (final Map< Object, Object > aDataSink)
		throws Exception
	{
		PowerMockito.mockStatic(Hazelcast      .class);
		PowerMockito.mockStatic(HazelcastClient.class);
		
		// create the mock instance itself
		final HazelcastInstance aHazelcastMock = PowerMockito.mock(HazelcastInstance.class);
		
		// ensure static factory method return OUR mock !
		PowerMockito.when      (Hazelcast.newHazelcastInstance(Mockito.any(Config.class)))
	                .thenReturn(aHazelcastMock);		
		PowerMockito.when      (HazelcastClient.newHazelcastClient(Mockito.any(ClientConfig.class)))
					.thenReturn(aHazelcastMock);		
		
		// forward all requests from Hazelcast to the user provided data sink !
		final IMap< Object, Object > iMapMock     = Mockito.mock(IMap.class);
		final Answer< Object >       iMapMockImpl = new Answer< Object > ()
		{
			@Override
			public Object answer(final InvocationOnMock aCall)
				throws Throwable
			{
				final Method   aMethod = aCall  .getMethod   ();
				final String   sMethod = aMethod.getName     ();
				final Object[] lArgs   = aCall  .getArguments();

				if (StringUtils.equals(sMethod, "containsKey"))
					return aDataSink.containsKey (lArgs[0]);
				else
				if (StringUtils.equals(sMethod, "get"))
					return aDataSink.get (lArgs[0]);
				else
				if (StringUtils.equals(sMethod, "put"))
					return aDataSink.put (lArgs[0], lArgs[1]);
				else
				if (StringUtils.equals(sMethod, "keySet"))
					return aDataSink.keySet();
				else
					throw new UnsupportedOperationException ("No support for method '"+sMethod+"' implemented yet.");
			}
		};

		Mockito.doAnswer(iMapMockImpl).when(iMapMock).get        (Mockito.any());
		Mockito.doAnswer(iMapMockImpl).when(iMapMock).put        (Mockito.any(), Mockito.any());
		Mockito.doAnswer(iMapMockImpl).when(iMapMock).containsKey(Mockito.any());
		Mockito.doAnswer(iMapMockImpl).when(iMapMock).keySet     ();

		// bind those data sink wrapper to our Hazelcast mock
		Mockito.when      (aHazelcastMock.getMap (Mockito.anyString()))
	           .thenReturn(iMapMock);
		
		// bind atomicref mock to HZ mock
		Mockito.doAnswer (new Answer< IAtomicReference< Object > >()
		{
			@Override
			public IAtomicReference< Object > answer(final InvocationOnMock aCall)
				throws Throwable
			{
				final Object[] lArgs = aCall.getArguments();
				final String   sKey  = (String) lArgs[0];
				
				final Answer< Object > iAtomRefMockImpl = new Answer< Object > ()
				{
					private final String m_sKey4ThisMock = sKey;

					@Override
					public Object answer(final InvocationOnMock aCall)
						throws Throwable
					{
						final Method   aMethod = aCall  .getMethod   ();
						final String   sMethod = aMethod.getName     ();
						final Object[] lArgs   = aCall  .getArguments();

						if (StringUtils.equals(sMethod, "get"))
							return aDataSink.get (m_sKey4ThisMock);
						else
						if (StringUtils.equals(sMethod, "set"))
							return aDataSink.put (m_sKey4ThisMock, lArgs[0]);
						else
							throw new UnsupportedOperationException ("No support for method '"+sMethod+"' implemented yet.");
					}
				};
				
				final IAtomicReference< Object > iAtomRefMock = Mockito.mock(IAtomicReference.class);
				Mockito.doAnswer(iAtomRefMockImpl).when(iAtomRefMock).get();
				Mockito.doAnswer(iAtomRefMockImpl).when(iAtomRefMock).set(Mockito.any());
				return iAtomRefMock;
			}
		}).when(aHazelcastMock).getAtomicReference(Mockito.anyString ());
		
		// mock the transaction context
		TransactionContext aTransactionMock = PowerMockito.mock(TransactionContext.class);

		Mockito.when      (aHazelcastMock.newTransactionContext())
        	   .thenReturn(aTransactionMock);
		Mockito.when      (aHazelcastMock.newTransactionContext(Mockito.any(TransactionOptions.class)))
        	   .thenReturn(aTransactionMock);

		return aHazelcastMock;
	}
}
