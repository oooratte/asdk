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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang3.ObjectUtils;

import com.openexchange.office.rt2.persistence.ISimplePersistenceTransacted;
import com.openexchange.office.rt2.persistence.IWrapper;
import com.openexchange.office.tools.logging2.ELogLevel;
import com.openexchange.office.tools.logging2.v2.LogConst;
import com.openexchange.office.tools.logging2.v2.LogContext;
import com.openexchange.office.tools.logging2.v2.Logger;
import com.openexchange.office.tools.logging2.v2.Slf4JLogger;

//=============================================================================
public class TransactedPersistence implements ISimplePersistenceTransacted
											, IWrapper< ISimplePersistenceTransacted >
{
	//-------------------------------------------------------------------------
	private static final Logger LOG = Slf4JLogger.create(TransactedPersistence.class);

	//-------------------------------------------------------------------------
	public TransactedPersistence ()
		throws Exception
	{}

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
		mem_Changes ().clear();
	}

	//-------------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	@Override
	public synchronized List< String > listKeys()
		throws Exception
	{
		final List < String > lChangedKeys    = new ArrayList< String >(mem_Changes ().keySet());
		final List< String >  lPersistentKeys = m_iPersistence.listKeys();
		final List< String >  lKeys           = ListUtils.sum(lPersistentKeys, lChangedKeys);
		
		LOG.forLevel   (ELogLevel.E_TRACE)
		   .setVar     (LogConst.THIS, ObjectUtils.identityToString(this))
		   .setVar     ("persistent keys", lKeys)
		   .withMessage("list keys ...")
		   .log        ();
		
		return lKeys;
	}

	//-------------------------------------------------------------------------
	@Override
	public synchronized < T > void set(final String sKey  ,
									   final T      aValue)
		throws Exception
	{
		LOG.forLevel   (ELogLevel.E_TRACE)
		   .setVar     (LogConst.THIS, ObjectUtils.identityToString(this))
		   .withMessage("set '"+sKey+"' = '"+aValue+"'")
		   .log        ();

		mem_Changes ().put(sKey, aValue);
	}

	//-------------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	@Override
	public synchronized < T > T get(final String sKey)
		throws Exception
	{
		LogContext aLog = LOG.forLevel(ELogLevel.E_TRACE)
							 .setVar  (LogConst.THIS, ObjectUtils.identityToString(this));
		
		T aValue = (T) mem_Changes ().get(sKey);
		if (aValue != null)
		{
			aLog.withMessage("get '"+sKey+"' = '"+aValue+"' (from change-set)")
			    .log        ();
			return aValue;
		}

		aValue = (T) m_iPersistence.get(sKey);
		return aValue;
	}

	//-------------------------------------------------------------------------
	@Override
	public synchronized void begin()
		throws Exception
	{
		LOG.forLevel   (ELogLevel.E_TRACE)
		   .setVar     (LogConst.THIS, ObjectUtils.identityToString(this))
		   .withMessage("new transaction ...")
		   .log        ();
	}

	//-------------------------------------------------------------------------
	@Override
	public synchronized void commit()
		throws Exception
	{
		LogContext aLog = LOG.forLevel(ELogLevel.E_TRACE)
							 .setVar  (LogConst.THIS, ObjectUtils.identityToString(this));

		final Map< String, Object >               lChanges        = mem_Changes ();
		final Iterator< Entry< String, Object > > rChanges        = lChanges.entrySet().iterator();
		final List< String >                      lPersistentKeys = listKeys    ();
		final List< String >                      lChangedKeys    = new ArrayList< String > (lChanges.keySet());

		aLog.forLevel   (ELogLevel.E_TRACE)
		    .setVar     ("persistent keys", lPersistentKeys)
		    .setVar     ("changed keys"   , lChangedKeys   )
		    .withMessage("commit changes ...")
		    .log        ();
		
		while (rChanges.hasNext())
		{
			final Entry< String, Object > aChange = rChanges.next    ();
			final String                  sKey    = aChange .getKey  ();
			final Object                  aValue  = aChange .getValue();
			m_iPersistence.set(sKey, aValue);
		}

		m_lChanges = null;
	}

	//-------------------------------------------------------------------------
	@Override
	public synchronized void rollback()
		throws Exception
	{
		LOG.forLevel   (ELogLevel.E_TRACE)
		   .setVar     (LogConst.THIS, ObjectUtils.identityToString(this))
		   .withMessage("rollback changes ...")
		   .log        ();
		m_lChanges = null;
	}

	//-------------------------------------------------------------------------
	private Map< String, Object > mem_Changes ()
		throws Exception
	{
		if (m_lChanges == null)
			m_lChanges = new HashMap< String, Object > ();
		return m_lChanges;
	}

	//-------------------------------------------------------------------------
	private ISimplePersistenceTransacted m_iPersistence = null;

	//-------------------------------------------------------------------------
	private Map< String, Object > m_lChanges = null;
}
