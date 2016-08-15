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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;

import net.as_development.asdk.persistence.ISimplePersistenceTransacted;

//=============================================================================
/** Helper to mock the ISimplePersist interface
 * 
 *  This do not implement the functionality manually.
 *  It uses mocking to simulate it.
 *  
 *  Several create() functions can be designed to define
 *  several aspects of this interface.
 */
public class ISimplePersistTransactedMock
{
	//-------------------------------------------------------------------------
	private ISimplePersistTransactedMock ()
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
	public static ISimplePersistenceTransacted createFullFunctionalMock (final Map< String, Object > aDataSink)
		throws Exception
	{
		final ISimplePersistenceTransacted iPersist = PowerMockito.mock(ISimplePersistenceTransacted.class);
		
		// map ISimplePersist.get ()
		
		PowerMockito.doAnswer(new Answer< Object > ()
		{
			@Override
			public Object answer(InvocationOnMock aCall)
				throws Throwable
			{
				final String sKey   = (String) aCall.getArguments() [0];
				final Object aValue = aDataSink.get (sKey);
				return aValue;
			}
			
		}).when(iPersist).get(Mockito.anyString());
		
		// map ISimplePersist.set ()
		
		PowerMockito.doAnswer(new Answer< Void > ()
		{
			@Override
			public Void answer(InvocationOnMock aCall)
				throws Throwable
			{
				final String sKey   = (String) aCall.getArguments() [0];
				final Object aValue =          aCall.getArguments() [1];
				if (aValue != null)
					aDataSink.put (sKey, aValue);
				else
					aDataSink.remove(sKey);
				return null;
			}
			
		}).when(iPersist).set(Mockito.anyString(), Mockito.anyString());

		// map ISimplePersist.listKeys ()
		
		PowerMockito.doAnswer(new Answer< List< String > > ()
		{
			@Override
			public List< String > answer(InvocationOnMock aCall)
				throws Throwable
			{
				final List< String > lKeys = new ArrayList< String > ();
				lKeys.addAll(aDataSink.keySet());
				return lKeys;
			}
			
		}).when(iPersist).listKeys();

		return iPersist;
	}
}
