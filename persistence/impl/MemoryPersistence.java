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
package com.openexchange.office.rt2.persistence.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.openexchange.office.rt2.persistence.ISimplePersistenceTransacted;
import com.openexchange.office.tools.logging2.ELogLevel;
import com.openexchange.office.tools.logging2.v2.Logger;
import com.openexchange.office.tools.logging2.v2.Slf4JLogger;

//=============================================================================
public class MemoryPersistence implements ISimplePersistenceTransacted
{
	//-------------------------------------------------------------------------
	private static final Logger LOG = Slf4JLogger.create(MemoryPersistence.class);

	//-------------------------------------------------------------------------
	public MemoryPersistence ()
		throws Exception
	{}

	//-------------------------------------------------------------------------
	public static MemoryPersistence create ()
	    throws Exception
	{
		final MemoryPersistence aInst = new MemoryPersistence ();
		return aInst;
	}

	//-------------------------------------------------------------------------
	@Override
	public synchronized void configure (final String... lConfig)
		throws Exception
	{
	}

	//-------------------------------------------------------------------------
	@Override
	public synchronized void clear ()
		throws Exception
	{
		LOG	.forLevel(ELogLevel.E_TRACE)
			.log	 ();
		
		mem_Data ().clear();
	}

	//-------------------------------------------------------------------------
	@Override
	public synchronized List< String > listKeys()
		throws Exception
	{
		LOG	.forLevel(ELogLevel.E_TRACE)
			.log	 ();
		
		final List< String > lKeys = new ArrayList< String >(mem_Data().keySet());

		LOG	.forLevel(ELogLevel.E_TRACE)
			.setVar  ("return", lKeys )
			.log	 ();
		return lKeys;
	}

	//-------------------------------------------------------------------------
	@Override
	public synchronized < T > void set(final String sKey  ,
									   final T      aValue)
		throws Exception
	{
		LOG	.forLevel(ELogLevel.E_TRACE)
			.setVar  ("key"  , sKey    )
			.setVar  ("value", aValue  )
			.log	 ();
		
		mem_Data ().put(sKey, aValue);
	}

	//-------------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	@Override
	public synchronized < T > T get(final String sKey)
		throws Exception
	{
		LOG	.forLevel(ELogLevel.E_TRACE)
			.setVar  ("key", sKey      )
			.log	 ();
		
		T aValue = (T) mem_Data ().get(sKey);

		LOG	.forLevel(ELogLevel.E_TRACE)
			.setVar  ("return", aValue )
			.log	 ();
		return aValue;
	}

	//-------------------------------------------------------------------------
	@Override
	public void begin()
		throws Exception
	{
		LOG	.forLevel(ELogLevel.E_TRACE)
			.log	 ();
	}

	//-------------------------------------------------------------------------
	@Override
	public void commit()
		throws Exception
	{
		LOG	.forLevel(ELogLevel.E_TRACE)
			.log	 ();
	}

	//-------------------------------------------------------------------------
	@Override
	public void rollback()
		throws Exception
	{
		LOG	.forLevel(ELogLevel.E_TRACE)
			.log	 ();
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
