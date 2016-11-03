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
package net.as_development.asdk.persistence.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.as_development.asdk.persistence.ISimplePersistenceImpl;

//=============================================================================
public class MemoryPersistence implements ISimplePersistenceImpl
{
	//-------------------------------------------------------------------------
	public static final String ENCODING = "utf-8";

	//-------------------------------------------------------------------------
	public MemoryPersistence ()
		throws Exception
	{}

	//-------------------------------------------------------------------------
	@Override
	public synchronized void configure (final String... lConfig)
		throws Exception
	{
		// no config needed
	}

	//-------------------------------------------------------------------------
	@Override
	public synchronized void clear ()
		throws Exception
	{
		mem_Data ().clear();
	}

	//-------------------------------------------------------------------------
	@Override
	public synchronized List< String > listKeys()
		throws Exception
	{
		final Map< String, Object > lValues = mem_Data ();
		final List< String >        lKeys   = new ArrayList< String >();
		lKeys.addAll(lValues.keySet());
		return lKeys;
	}

	//-------------------------------------------------------------------------
	@Override
	public synchronized void set(final Map< String, Object > lChanges)
		throws Exception
	{
		final Map< String, Object >               lData    = mem_Data ();
		final Iterator< Entry< String, Object > > rChanges = lChanges.entrySet().iterator();
		
		while (rChanges.hasNext())
		{
			final Entry< String, Object > aChange = rChanges.next    ();
			final String                  sKey    = aChange .getKey  ();
			final Object                  aValue  = aChange .getValue();
			
			lData.put(sKey, aValue);
		}
	}

	//-------------------------------------------------------------------------
	@Override
	public synchronized Object get(final String sKey)
		throws Exception
	{
		final Object aValue = mem_Data ().get(sKey);
		return aValue;
	}

	//-------------------------------------------------------------------------
	private Map< String, Object > mem_Data ()
		throws Exception
	{
		if (m_lData == null)
			m_lData = new HashMap< String, Object > ();
		return m_lData;
	}

	//-------------------------------------------------------------------------
	private Map< String, Object > m_lData = null;
}
