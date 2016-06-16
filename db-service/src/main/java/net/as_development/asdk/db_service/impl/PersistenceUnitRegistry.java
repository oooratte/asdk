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
package net.as_development.asdk.db_service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.as_development.asdk.api.db.IPersistenceUnit;
import net.as_development.asdk.api.db.IPersistenceUnitRegistry;

//==============================================================================
public class PersistenceUnitRegistry implements IPersistenceUnitRegistry
{
	//--------------------------------------------------------------------------
	public PersistenceUnitRegistry ()
	    throws Exception
	{}
	
	//--------------------------------------------------------------------------
	public static synchronized IPersistenceUnitRegistry get ()
	    throws Exception
	{
		if (m_gSingleton == null)
			m_gSingleton = new PersistenceUnitRegistry ();
		return m_gSingleton;
	}
	
	//--------------------------------------------------------------------------
	@Override
	public synchronized void addPersistenceUnits (final IPersistenceUnit... lUnits)
		throws Exception
	{
		final Map< String, IPersistenceUnit > aRegistry = mem_Registry ();
		for (final IPersistenceUnit iUnit : lUnits)
		{
			final String sName = iUnit.getName();
			aRegistry.put(sName, iUnit);
		}
	}

	//--------------------------------------------------------------------------
	@Override
	public synchronized IPersistenceUnit getPersistenceUnitByName (final String sName)
		throws Exception
	{
		final Map< String, IPersistenceUnit > aRegistry = mem_Registry ();
		final IPersistenceUnit                iPU       = aRegistry.get(sName);
		return iPU;
	}

	//--------------------------------------------------------------------------
	@Override
	public synchronized List< IPersistenceUnit > listPersistenceUnits()
		throws Exception
	{
		final List< IPersistenceUnit > lUnits = new ArrayList< IPersistenceUnit >();
		lUnits.addAll(mem_Registry ().values());
		return lUnits;
	}

	//--------------------------------------------------------------------------
	private synchronized Map< String, IPersistenceUnit > mem_Registry ()
	    throws Exception
	{
		if (m_aRegistry == null)
			m_aRegistry = new HashMap< String, IPersistenceUnit > ();
		return m_aRegistry;
	}

	//--------------------------------------------------------------------------
	private static IPersistenceUnitRegistry m_gSingleton = null;

	//--------------------------------------------------------------------------
	private Map< String, IPersistenceUnit > m_aRegistry = null;
}
