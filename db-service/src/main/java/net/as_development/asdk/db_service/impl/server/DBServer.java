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
import net.as_development.asdk.api.db.IPersistenceUnitRegistry;
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
	public void setDBPool (final IDBPool iDBPool)
	    throws Exception
	{
		m_iDBPool = iDBPool;
	}
	
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
	public void registerPersistenceRegistryModule (IPersistenceUnitRegistry... lModules)
		throws Exception
	{
		for (IPersistenceUnitRegistry iModule : lModules)
			registerPersistenceUnit(iModule.listPersistenceUnits());
	}
		
    //-------------------------------------------------------------------------
	@Override
	public void registerPersistenceRegistryModule (List< IPersistenceUnitRegistry > lModules)
		throws Exception
	{
		for (IPersistenceUnitRegistry iModule : lModules)
			registerPersistenceUnit(iModule.listPersistenceUnits());
	}

    //-------------------------------------------------------------------------
	public void initRuntime ()
		throws Exception
	{
		throw new UnsupportedOperationException ("fix me");
//		IDBPool                      iDBPool = mem_DBPool ();
//		Iterator< IPersistenceUnit > lPUs    = mem_PUs ().values().iterator();
//		while (lPUs.hasNext())
//		{
//			IPersistenceUnit iPU     = lPUs.next();
//			IPersistenceUnit iFullPU = impl_weaveInServerData (iPU);
//			iDBPool.registerPersistenceUnit(iFullPU);
//		}
	}
	
    //-------------------------------------------------------------------------
	@Override
	public void createSchema()
		throws Exception
	{
		IDBPool iPool = mem_DBPool ();
		// TODO do we need listing of persistence units within pool ?
		
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
			String sValue = m_iConnectionData.getStringProperty(sProperty);
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
	private IDBPool mem_DBPool ()
	    throws Exception
	{
		if (m_iDBPool == null)
			m_iDBPool = ServiceEnv.get ().getService (IDBPool.class);
		return m_iDBPool;
	}
	
    //-------------------------------------------------------------------------
	private IDBPool m_iDBPool = null;
	
    //-------------------------------------------------------------------------
	private IPersistenceUnit m_iConnectionData = null;
	
    //-------------------------------------------------------------------------
	private Map< String, IPersistenceUnit > m_lPUs = null;
}
