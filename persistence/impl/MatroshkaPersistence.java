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

import java.util.List;

import com.openexchange.office.rt2.persistence.ISimplePersistenceTransacted;
import com.openexchange.office.rt2.persistence.IWrapper;

//=============================================================================
public class MatroshkaPersistence implements ISimplePersistenceTransacted
										   , IWrapper< ISimplePersistenceTransacted >
{
	//-------------------------------------------------------------------------
	public MatroshkaPersistence ()
		throws Exception
	{}

	//-------------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	public static ISimplePersistenceTransacted build (final ISimplePersistenceTransacted... lOrderedPersistenceList)
	    throws Exception
	{
		final ISimplePersistenceTransacted iMatroshka = new MatroshkaPersistence ();
		      ISimplePersistenceTransacted iLast      = iMatroshka;

		for (final ISimplePersistenceTransacted iPersistence : lOrderedPersistenceList)
		{
			IWrapper< ISimplePersistenceTransacted > iWrapper = null;

			if (IWrapper.class.isAssignableFrom(iLast.getClass()))
				iWrapper = (IWrapper< ISimplePersistenceTransacted >) iLast;
			else
				throw new Error ("Chain of persistence objects broken. This instance do not support needed interface IWrapper : "+iLast.getClass ());

			iWrapper.wrap(iPersistence);
			iLast = iPersistence;
		}
		
		return iMatroshka;
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
		m_iPersistence.clear ();
	}

	//-------------------------------------------------------------------------
	@Override
	public synchronized List< String > listKeys()
		throws Exception
	{
		return m_iPersistence.listKeys ();
	}

	//-------------------------------------------------------------------------
	@Override
	public synchronized < T > void set(final String sKey  ,
									   final T      aValue)
		throws Exception
	{
		m_iPersistence.set (sKey, aValue);
	}

	//-------------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	@Override
	public synchronized < T > T get(final String sKey)
		throws Exception
	{
		return (T) m_iPersistence.get (sKey);
	}

	//-------------------------------------------------------------------------
	@Override
	public synchronized void begin()
		throws Exception
	{
		m_iPersistence.begin ();
	}

	//-------------------------------------------------------------------------
	@Override
	public synchronized void commit()
		throws Exception
	{
		m_iPersistence.commit ();
	}

	//-------------------------------------------------------------------------
	@Override
	public synchronized void rollback()
		throws Exception
	{
		m_iPersistence.rollback ();
	}

	//-------------------------------------------------------------------------
	private ISimplePersistenceTransacted m_iPersistence = null;
}
