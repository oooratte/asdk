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
package net.as_development.asdk.tools.common.pattern.observation;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.Validate;

//=============================================================================
public class ObservableBase< T > implements Observable< T >
{
	//-------------------------------------------------------------------------
	public ObservableBase ()
	{}
	
	//-------------------------------------------------------------------------
	public synchronized void addObserver (final Observer< T > aObserver)
		throws Exception
	{
		Validate.notNull(aObserver, "Invalid argument 'observer'.");
		mem_Observer ().add(aObserver);
	}

	//-------------------------------------------------------------------------
	public synchronized void removeObserver (final Observer< T > aObserver)
		throws Exception
	{
		Validate.notNull(aObserver, "Invalid argument 'observer'.");
		mem_Observer ().remove(aObserver);
	}

	//-------------------------------------------------------------------------
	public /* no synchronized */ void fire (final T aEvent)
		throws Exception
	{
		final Set< Observer< T > > lObserver = new HashSet< Observer< T > >(mem_Observer ());
		for (final Observer< T > aObserver : lObserver)
			aObserver.notify(aEvent);
	}
	
	//-------------------------------------------------------------------------
	private synchronized Set< Observer< T > > mem_Observer ()
		throws Exception
	{
		if (m_lObserver == null)
			m_lObserver = new HashSet< Observer< T > > ();
		return m_lObserver;
	}

	//-------------------------------------------------------------------------
	private Set< Observer< T > > m_lObserver = null;
}
