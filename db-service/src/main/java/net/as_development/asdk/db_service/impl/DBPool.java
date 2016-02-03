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

import java.util.HashMap;
import java.util.Map;

import net.as_development.asdk.api.db.IDB;
import net.as_development.asdk.api.db.IDBPool;
import net.as_development.asdk.api.db.IPersistenceUnit;

//==============================================================================
public class DBPool implements IDBPool
{
    //--------------------------------------------------------------------------
    public DBPool ()
    {}

    //--------------------------------------------------------------------------
    @Override
    public synchronized IDB getDbForPersistenceUnit(String sPersistenceUnit)
        throws Exception
    {
        Map< String, IDB > lPool = mem_Dbs ();
        IDB                iDB   = null;

        // get existing DB
        if (lPool.containsKey(sPersistenceUnit))
            iDB = lPool.get(sPersistenceUnit);

        // reset invalid/expired DB
        // todo: implement iDB.isValidOrExpired () ... or something similar

        // create new DB (and pool it)
        if (iDB == null)
        {
            IPersistenceUnit aPu = mem_Units ().get(sPersistenceUnit);
            iDB = new DB ();
            iDB.setPersistenceUnit(aPu);

            lPool.put(sPersistenceUnit, iDB);
        }

        return iDB;
    }

    //--------------------------------------------------------------------------
	@Override
	public void registerPersistenceUnit(String sName)
		throws Exception
	{
		IPersistenceUnit iUnit = PersistenceUnit.loadUnit(sName);
		registerPersistenceUnit (iUnit);
	}
	
    //--------------------------------------------------------------------------
    @Override
    public synchronized void registerPersistenceUnit(IPersistenceUnit aUnit)
        throws Exception
    {
        mem_Units ().put (aUnit.getName(), aUnit);
    }

    //--------------------------------------------------------------------------
    private Map< String, IPersistenceUnit > mem_Units ()
        throws Exception
    {
        if (m_lUnits == null)
            m_lUnits = new HashMap< String, IPersistenceUnit >(10);
        return m_lUnits;
    }

    //--------------------------------------------------------------------------
    private Map< String, IDB > mem_Dbs ()
        throws Exception
    {
        if (m_lDbs == null)
            m_lDbs = new HashMap< String, IDB >(10);
        return m_lDbs;
    }

    //--------------------------------------------------------------------------
    private Map< String, IPersistenceUnit > m_lUnits = null;

    //--------------------------------------------------------------------------
    private Map< String, IDB > m_lDbs = null;
}
