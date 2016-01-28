/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
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
