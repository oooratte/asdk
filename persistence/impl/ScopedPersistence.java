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

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.commons.lang3.StringUtils;

import com.openexchange.office.rt2.persistence.ISimplePersistenceTransacted;
import com.openexchange.office.rt2.persistence.IWrapper;
import com.openexchange.office.tools.logging2.ELogLevel;
import com.openexchange.office.tools.logging2.v2.Logger;
import com.openexchange.office.tools.logging2.v2.Slf4JLogger;

//=============================================================================
public class ScopedPersistence implements ISimplePersistenceTransacted
										, IWrapper< ISimplePersistenceTransacted >
{
	//-------------------------------------------------------------------------
	private static final Logger LOG = Slf4JLogger.create(ScopedPersistence.class);

	//-------------------------------------------------------------------------
	public static final String SEPARATOR = ".";

	//-------------------------------------------------------------------------
	public ScopedPersistence ()
		throws Exception
	{}

	//-------------------------------------------------------------------------
	public static ScopedPersistence create (final String sScope)
	    throws Exception
	{
		final ScopedPersistence aInst = new ScopedPersistence ();
		aInst.m_sScope = sScope;
		return aInst;
	}
	
	//-------------------------------------------------------------------------
	@Override
	public void wrap (final ISimplePersistenceTransacted iPersistence)
	    throws Exception
	{
		m_iPersistence = iPersistence;
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
			.log 	 ();
		
		m_iPersistence.clear ();
	}

	//-------------------------------------------------------------------------
	@Override
	public synchronized List< String > listKeys()
		throws Exception
	{
		LOG	.forLevel(ELogLevel.E_TRACE)
			.log 	 ();
	
		final List< String >     lKeys           = new Vector< String > ();
		final Iterator< String > rPersistentKeys = m_iPersistence.listKeys().iterator();
		while (rPersistentKeys.hasNext())
		{
			final String sFullKey = rPersistentKeys.next();
			if ( ! impl_isKeyInCurrentSubset (sFullKey))
				continue;
			
			final String sRelKey = impl_makeKeyRelative (sFullKey);
			lKeys.add (sRelKey);
		}

		LOG	.forLevel(ELogLevel.E_TRACE)
			.setVar  ("return", lKeys  )
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
			.log 	 ();
		
		final String sFullKey = impl_makeKeyAbsolute (sKey);
		m_iPersistence.set(sFullKey, aValue);
	}

	//-------------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	@Override
	public synchronized < T > T get(final String sKey)
		throws Exception
	{
		LOG	.forLevel(ELogLevel.E_TRACE)
			.setVar  ("key"  , sKey    )
			.log 	 ();

		final String sFullKey = impl_makeKeyAbsolute (sKey);
		final T      aValue   = (T) m_iPersistence.get(sFullKey);

		LOG	.forLevel(ELogLevel.E_TRACE)
			.setVar  ("return", aValue )
			.log	 ();
		return aValue;
	}

	//-------------------------------------------------------------------------
	@Override
	public synchronized void begin()
		throws Exception
	{
		LOG	.forLevel(ELogLevel.E_TRACE)
			.log 	 ();

		m_iPersistence.begin ();
	}

	//-------------------------------------------------------------------------
	@Override
	public synchronized void commit()
		throws Exception
	{
		LOG	.forLevel(ELogLevel.E_TRACE)
			.log 	 ();
	
		m_iPersistence.commit ();
	}

	//-------------------------------------------------------------------------
	@Override
	public synchronized void rollback()
		throws Exception
	{
		LOG	.forLevel(ELogLevel.E_TRACE)
			.log 	 ();
	
		m_iPersistence.rollback ();
	}

	//-------------------------------------------------------------------------
	private String impl_makeKeyAbsolute (final String sRelKey)
		throws Exception
	{
		if (StringUtils.isEmpty(m_sScope))
			return sRelKey;
		
		final StringBuffer sFullKey = new StringBuffer (256);
		sFullKey.append (m_sScope);
		sFullKey.append (SEPARATOR);
		sFullKey.append (sRelKey  );
		return sFullKey.toString ();
	}

	//-------------------------------------------------------------------------
	private String impl_makeKeyRelative (final String sAbsKey)
		throws Exception
	{
		if (StringUtils.isEmpty(m_sScope))
			return sAbsKey;
		
		final String sRelKey = StringUtils.substringAfter(sAbsKey, m_sScope+SEPARATOR);
		return sRelKey;
	}

	//-------------------------------------------------------------------------
	private boolean impl_isKeyInCurrentSubset (final String sKey)
		throws Exception
	{
		final boolean bIs = (
							 (StringUtils.isEmpty   (m_sScope          )) ||
							 (StringUtils.startsWith(sKey    , m_sScope))
							);
		return bIs;
	}

	//-------------------------------------------------------------------------
	private ISimplePersistenceTransacted m_iPersistence = null;

	//-------------------------------------------------------------------------
	private String m_sScope = null;
}
