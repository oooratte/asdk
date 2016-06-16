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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.lang3.StringUtils;

import net.as_development.asdk.api.db.EQueryPartBinding;
import net.as_development.asdk.api.db.EQueryPartOperation;
import net.as_development.asdk.api.db.IDB;
import net.as_development.asdk.api.db.IDBPool;
import net.as_development.asdk.api.db.IDBQuery;
import net.as_development.asdk.api.db.IEntity;
import net.as_development.asdk.api.db.IPersistenceUnit;
import net.as_development.asdk.api.db.IPersistenceUnitRegistry;
import net.as_development.asdk.db_service.EntityBase;
import net.as_development.asdk.db_service.impl.DBPool;
import net.as_development.asdk.db_service.impl.PersistenceUnit;
import net.as_development.asdk.db_service.impl.PersistenceUnitRegistry;

//=============================================================================
/** the try to automate backup of the whole product DB .-)
 */
public class DBBackup
{
    //-------------------------------------------------------------------------
	public DBBackup ()
	{}

    //-------------------------------------------------------------------------
	public void setLastBackupDate (Date aDate)
		throws Exception
	{
		m_aLastBackupDate = aDate;
	}
	
    //-------------------------------------------------------------------------
	public Date getNewBackupDate ()
		throws Exception
	{
        m_aNewBackupDate = new Date ();
		return m_aNewBackupDate;
	}
	
    //-------------------------------------------------------------------------
	public void setSourceConnection (IPersistenceUnit iPU)
		throws Exception
	{
		m_iSourceConnection = iPU;
	}
	
    //-------------------------------------------------------------------------
	public void setTargetConnection (IPersistenceUnit iPU)
		throws Exception
	{
		m_iTargetConnection = iPU;
	}

    //-------------------------------------------------------------------------
	public void setEntities (IPersistenceUnit... lPUs)
		throws Exception
	{
		impl_setEntityPUs (mem_EntityPUs (), lPUs);
	}

    //-------------------------------------------------------------------------
	public void run ()
		throws Exception
	{
		Map< String, IPersistenceUnit > lEntityPUs = mem_EntityPUs ();
		if (lEntityPUs.isEmpty())
			throw new Exception ("No entities specified.");
		
		IDBPool iSourcePool = impl_buildPool (m_iSourceConnection, lEntityPUs);
		IDBPool iTargetPool = impl_buildPool (m_iTargetConnection, lEntityPUs);

		for (String sPU : lEntityPUs.keySet())
		{
			IPersistenceUnit iPU       = lEntityPUs.get(sPU);
			IDB              iSourceDB = iSourcePool.getDbForPersistenceUnit(sPU);
			IDB              iTargetDB = iTargetPool.getDbForPersistenceUnit(sPU);
			int              nNr       = 0;
			
			for (String sEntity : iPU.getEntities())
			{
				nNr++;
				impl_backupEntity (sEntity, iSourceDB, iTargetDB, nNr);
			}
		}
	}

	//-------------------------------------------------------------------------
	@SuppressWarnings({"unchecked"})
	private < TEntity extends IEntity > void impl_backupEntity (String sEntity  ,
																IDB    iSourceDB,
																IDB    iTargetDB,
																int    nNr      )
	    throws Exception
	{
		String              sNextToken = null;
		Class< TEntity >    aType      = (Class< TEntity >)Class.forName(sEntity);
		IDBQuery< TEntity > iQuery     = iSourceDB.prepareQuery(aType, "query_new_modified_entities_"+nNr);
		
		iQuery.setQueryPart(0, EQueryPartBinding.E_AND, EQueryPartOperation.E_GREATER_THAN, EntityBase.ATTRIBUTE_NAME_MODIFY_STAMP, m_aLastBackupDate);

		do
		{
			List< TEntity > lResults = new Vector< TEntity > ();
			sNextToken = iSourceDB.query(aType, sNextToken, lResults, iQuery);

			IEntity[] lBackup = new IEntity[lResults.size()];
			int       i       = 0;
			for (IEntity iResult : lResults)
			{
				EntityBase aEntity = (EntityBase)iResult;
				String     sID     = aEntity.Id;
				TEntity    aCheck  = iTargetDB.getEntityById(aType, sID);
				
				System.out.println ("backup ["+i+"] : "+sID);
				
				if (aCheck == null)
				{
					aEntity.Id         = null;
					aEntity.ExternalId = sID;
				}
				
				lBackup[i++] = aEntity;
			}
			
			iTargetDB.storeEntities(lBackup);
		}
		while ( ! StringUtils.isEmpty(sNextToken));
	}
	
	//-------------------------------------------------------------------------
	private static IDBPool impl_buildPool (IPersistenceUnit                iConnection,
										   Map< String, IPersistenceUnit > lEntityPUs )
		throws Exception
	{
		final DBPool                   iPool       = new DBPool ();
		final IPersistenceUnitRegistry iPURegistry = new PersistenceUnitRegistry ();
		
		iPool.setPersistenceUnitRegistry(iPURegistry);
		
		for (IPersistenceUnit iEntity : lEntityPUs.values ())
		{
			final IPersistenceUnit iPU = impl_mergePUs (iEntity, iConnection);
			iPURegistry.addPersistenceUnits(iPU);
		}
		
		return iPool;
	}
	
	//-------------------------------------------------------------------------
	private static IPersistenceUnit impl_mergePUs (IPersistenceUnit iPU1,
												   IPersistenceUnit iPU2)
	   throws Exception
	{
		PersistenceUnit aPU = new PersistenceUnit (iPU1);
		aPU.merge(iPU2);
		return aPU;
	}
	
	//-------------------------------------------------------------------------
	private static void impl_setEntityPUs (Map< String, IPersistenceUnit > lRegistry,
										   IPersistenceUnit...             lPUs     )
	    throws Exception
	{
		lRegistry.clear ();
		for (IPersistenceUnit iPU : lPUs)
			lRegistry.put(iPU.getName(), iPU);
	}
	
	//-------------------------------------------------------------------------
	private Map< String, IPersistenceUnit > mem_EntityPUs ()
		throws Exception
	{
		if (m_lEntityPUs == null)
			m_lEntityPUs = new HashMap< String, IPersistenceUnit >();
		return m_lEntityPUs;
	}

    //-------------------------------------------------------------------------
	private Date m_aLastBackupDate = null;
	
    //-------------------------------------------------------------------------
	private Date m_aNewBackupDate = null;
	
    //-------------------------------------------------------------------------
	private IPersistenceUnit m_iSourceConnection = null;

    //-------------------------------------------------------------------------
	private IPersistenceUnit m_iTargetConnection = null;

	//-------------------------------------------------------------------------
	private Map< String, IPersistenceUnit > m_lEntityPUs = null;
}
