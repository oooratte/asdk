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
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryEventType;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.IMap;

//=============================================================================
public class IMapMock
{
	//-------------------------------------------------------------------------
	public IMapMock ()
	    throws Exception
	{}

	//-------------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	public static < TKey, TValue > IMap< TKey, TValue > createFullFunctionalMock (final Map< TKey, TValue > aDataSink,
																				  final String              sMapName )
		throws Exception
	{
		// create the mock instance itself
		final IMap< TKey, TValue > iMapMock = (IMap< TKey, TValue >) PowerMockito.mock(IMap.class);
		
		// forward all requests to the user provided data sink !
		final Answer< Object > iMapMockImpl = new Answer< Object > ()
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
				{
					final TKey   aKey      = (TKey  )lArgs[0];
					final TValue aNewValue = (TValue)lArgs[1];
					
					impl_put (aKey, aNewValue);
					
					return null;
				}
				else
				if (StringUtils.equals(sMethod, "putAll"))
				{
					final Map< TKey, TValue >                aMap = (Map< TKey, TValue >)lArgs[0];
					final Iterator< Entry < TKey, TValue > > rMap = aMap.entrySet().iterator();
					
					while (rMap.hasNext())
					{
						final Entry < TKey, TValue > aEntry = rMap.next();
						final TKey                   aKey   = aEntry.getKey  ();
						final TValue                 aValue = aEntry.getValue();
						
						impl_put (aKey, aValue);
					}
					
					return null;
				}
				else
				if (StringUtils.equals(sMethod, "keySet"))
					return aDataSink.keySet();
				else
				if (StringUtils.equals(sMethod, "getName"))
					return sMapName;
				else
				if (StringUtils.equals(sMethod, "addEntryListener"))
				{
					aMockListener = (EntryListener<TKey, TValue>) lArgs[0];
					return null;
				}
				else
					throw new UnsupportedOperationException ("No support for method '"+sMethod+"' implemented yet.");
			}
			
			private void impl_put (final TKey   aKey     ,
					  			   final TValue aNewValue)
				throws Exception
			{
				final TValue aOldValue = aDataSink.get(aKey);
				aDataSink.put (aKey, aNewValue);
				impl_notifyListener (aKey, aOldValue, aNewValue);
			}
			
			private void impl_notifyListener (final TKey   aKey     ,
											  final TValue aOldValue,
											  final TValue aNewValue)
			    throws Exception
			{
				if (aMockListener == null)
					return;
				
				final String sEventSource = ObjectUtils.identityToString(iMapMock);
				
				if (aOldValue == null)
				{
					final EntryEvent< TKey, TValue> aEvent = new EntryEvent< TKey, TValue>(sEventSource, null, EntryEventType.ADDED.ordinal(), aKey, aOldValue, aNewValue);					
					aMockListener.entryAdded(aEvent);
				}
				else
				if (aNewValue == null)
				{
					final EntryEvent< TKey, TValue> aEvent = new EntryEvent< TKey, TValue>(sEventSource, null, EntryEventType.REMOVED.ordinal(), aKey, aOldValue, aNewValue);					
					aMockListener.entryRemoved(aEvent);
				}
				else
				{
					final EntryEvent< TKey, TValue> aEvent = new EntryEvent< TKey, TValue>(sEventSource, null, EntryEventType.UPDATED.ordinal(), aKey, aOldValue, aNewValue);					
					aMockListener.entryUpdated(aEvent);
				}
			}

			private EntryListener< TKey, TValue > aMockListener = null;
		};

		Mockito.doAnswer(iMapMockImpl).when(iMapMock).get             (Mockito.any());
		Mockito.doAnswer(iMapMockImpl).when(iMapMock).put             ((TKey)Mockito.any(), (TValue)Mockito.any());
		Mockito.doAnswer(iMapMockImpl).when(iMapMock).putAll          ((Map< TKey, TValue >)Mockito.any());
		Mockito.doAnswer(iMapMockImpl).when(iMapMock).containsKey     (Mockito.any());
		Mockito.doAnswer(iMapMockImpl).when(iMapMock).keySet          ();
		Mockito.doAnswer(iMapMockImpl).when(iMapMock).getName         ();
		Mockito.doAnswer(iMapMockImpl).when(iMapMock).addEntryListener((EntryListener<TKey,TValue>)Mockito.any(), Mockito.anyBoolean());
		
		return iMapMock;
	}
}
