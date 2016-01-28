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
package net.as_development.asdk.db_service.impl.server;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.as_development.asdk.api.db.IDB;
import net.as_development.asdk.api.db.IDBPool;
import net.as_development.asdk.api.db.IDBSchema;
import net.as_development.asdk.api.db.IDBServer;
import net.as_development.asdk.api.db.IEntity;
import net.as_development.asdk.api.db.IPersistenceUnit;
import net.as_development.asdk.api.db.IPersistenceUnitRegistryModule;
import net.as_development.asdk.db_service.impl.PersistenceUnit;
import net.as_development.asdk.db_service.impl.PersistenceUnitConst;
import net.as_development.asdk.service.env.ServiceEnv;

//=============================================================================
/** The main implementation of the interface IDBServer.
 *  Real implementations for real server bindings will be hidden by this instance.
 */
public class DBServer implements IDBServer
{
    //-------------------------------------------------------------------------
	public DBServer ()
	{}
	
    //-------------------------------------------------------------------------
	@Override
	public void setServerConnection (IPersistenceUnit iData)
		throws Exception
	{
		m_iConnectionData = iData;
	}
	
    //-------------------------------------------------------------------------
	@Override
	public void registerPersistenceUnit(List< IPersistenceUnit > lNewPUs)
		throws Exception
	{
		Map< String, IPersistenceUnit > lPUs = mem_PUs ();
		for (IPersistenceUnit iPU : lNewPUs)
			lPUs.put (iPU.getName(), iPU);
	}
	
    //-------------------------------------------------------------------------
	@Override
	public void registerPersistenceUnit(IPersistenceUnit... lNewPUs)
		throws Exception
	{
		Map< String, IPersistenceUnit > lPUs = mem_PUs ();
		for (IPersistenceUnit iPU : lNewPUs)
			lPUs.put (iPU.getName(), iPU);
	}
	
    //-------------------------------------------------------------------------
	@Override
	public void registerPersistenceRegistryModule (IPersistenceUnitRegistryModule... lModules)
		throws Exception
	{
		for (IPersistenceUnitRegistryModule iModule : lModules)
			registerPersistenceUnit(iModule.listPersistenceUnits());
	}
		
    //-------------------------------------------------------------------------
	@Override
	public void registerPersistenceRegistryModule (List< IPersistenceUnitRegistryModule > lModules)
		throws Exception
	{
		for (IPersistenceUnitRegistryModule iModule : lModules)
			registerPersistenceUnit(iModule.listPersistenceUnits());
	}

    //-------------------------------------------------------------------------
	public void initRuntime ()
		throws Exception
	{
		IDBPool                      iDBPool = ServiceEnv.get ().getService (IDBPool.class);
		Iterator< IPersistenceUnit > lPUs    = mem_PUs ().values().iterator();
		while (lPUs.hasNext())
		{
			IPersistenceUnit iPU     = lPUs.next();
			IPersistenceUnit iFullPU = impl_weaveInServerData (iPU);
			iDBPool.registerPersistenceUnit(iFullPU);
		}
	}
	
    //-------------------------------------------------------------------------
	@Override
	public void createSchema()
		throws Exception
	{
		IDBPool iPool = ServiceEnv.get ().getService (IDBPool.class);
		// @todo do we need listing of persistence units within pool ?
		
		Iterator< IPersistenceUnit > lPUs = mem_PUs ().values().iterator();
		while (lPUs.hasNext())
		{
			final IPersistenceUnit iPU     = lPUs.next();
			final String           sPU     = iPU.getName();
			      IDB              iDB     = iPool.getDbForPersistenceUnit(sPU);
			      IDBSchema        iSchema = (IDBSchema)iDB; 
		
			List< String > lEntities = iPU.getEntities();
			for (String sEntity : lEntities)
			{
				@SuppressWarnings("unchecked")
				Class< ? extends IEntity > aEntityClass = (Class< ? extends IEntity >)Class.forName(sEntity); 
				iSchema.createEntitySchema(aEntityClass);
			}
		}
	}
	
    //-------------------------------------------------------------------------
	private IPersistenceUnit impl_weaveInServerData (IPersistenceUnit iPU)
		throws Exception
	{
		PersistenceUnit aPU = new PersistenceUnit (iPU);
		
		aPU.setProvider(m_iConnectionData.getProvider());
        
		for (String sProperty : m_iConnectionData.getPropertNames())
		{
			String sValue = m_iConnectionData.getProperty(sProperty);
	        aPU.setProperty(sProperty, sValue);
		}

		return aPU;
	}
	
    //-------------------------------------------------------------------------
	private Map< String, IPersistenceUnit > mem_PUs ()
		throws Exception
	{
		if (m_lPUs == null)
			m_lPUs = new HashMap< String, IPersistenceUnit > ();
		return m_lPUs;
	}
	
    //-------------------------------------------------------------------------
	private IPersistenceUnit m_iConnectionData = null;
	
    //-------------------------------------------------------------------------
	private Map< String, IPersistenceUnit > m_lPUs = null;
}
