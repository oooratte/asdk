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
package test.net.as_development.asdk.db_service.impl.server;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import net.as_development.asdk.api.db.IPersistenceUnit;
import net.as_development.asdk.api.sql.ISqlServer;
import net.as_development.asdk.db_service.impl.DB;
import net.as_development.asdk.db_service.impl.PersistenceUnit;
import net.as_development.asdk.db_service.impl.PersistenceUnitConst;
import net.as_development.asdk.db_service.impl.server.DBBackup;
import net.as_development.asdk.db_service.impl.sql.SqlProvider;
import net.as_development.asdk.sql.server.impl.EmbeddedDerbyServer;
import test.net.as_development.asdk.db_service.test.entities.TestEntity;

//==============================================================================
@Ignore
public class DBBackupTest
{
    //--------------------------------------------------------------------------
	@Before
	public void setUp ()
		throws Exception
	{
		File aTemp         = FileUtils.getTempDirectory();
		File aSourceDBPath = new File (aTemp, "junit_source_db");
		File aTargetDBPath = new File (aTemp, "junit_target_db");
		
		EmbeddedDerbyServer iSource = new EmbeddedDerbyServer ();
		EmbeddedDerbyServer iTarget = new EmbeddedDerbyServer ();
		
		iSource.enablePersistentData(false);
		iTarget.enablePersistentData(false);
		
		iSource.setWorkDir(aSourceDBPath.getAbsolutePath());
		iTarget.setWorkDir(aTargetDBPath.getAbsolutePath());

		m_iSourceConnection = impl_getConnectionData (iSource);
		m_iTargetConnection = impl_getConnectionData (iTarget);

		PersistenceUnit iEntityPU = new PersistenceUnit ();
		iEntityPU.setName    ("test_entity"               );
		iEntityPU.setProvider(SqlProvider.class.getName ());
		iEntityPU.addEntity  (TestEntity.class.getName  ());
		m_iEntityPU = iEntityPU;
		
		iSource.start ();
		iTarget.start ();

		m_iSourceDBServer   = iSource;
		m_iTargetDBServer   = iTarget;
		
		impl_cleanDB      (m_iSourceConnection);
		impl_cleanDB      (m_iTargetConnection);
		impl_createTestDB (m_iSourceConnection);
	}
	
    //--------------------------------------------------------------------------
	@After
	public void tearDown ()
		throws Exception
	{
		if (m_iSourceDBServer != null)
			m_iSourceDBServer.stop ();
		
		if (m_iTargetDBServer != null)
			m_iTargetDBServer.stop ();
		
		m_iSourceDBServer = null;
		m_iTargetDBServer = null;
	}
	
    //--------------------------------------------------------------------------
	@Test
	public void testBackup ()
		throws Exception
	{
		Date     aLastBackup = new Date (0);
		DBBackup aBackup     = new DBBackup ();
		
		aBackup.setSourceConnection(m_iSourceConnection);
		aBackup.setTargetConnection(m_iTargetConnection);
		aBackup.setEntities        (m_iEntityPU        );
		aBackup.setLastBackupDate  (aLastBackup        );
		
		System.out.println ("backup 1");
		aBackup.run();
		
		aLastBackup = aBackup.getNewBackupDate();
		aBackup.setLastBackupDate(aLastBackup);
		
		TestEntity[] lCheck = impl_readDB (m_iTargetConnection);
		Assert.assertEquals ("testBackup [01] not all entities seems to be handled by backup 1", m_lTestEntities.length, lCheck.length);
		impl_cleanDB (m_iTargetConnection);
		
		synchronized (this)
		{this.wait (1000);}
		
		impl_updateTestDB (m_iSourceConnection, m_lTestEntities [5]);

		System.out.println ("backup 2");
		aBackup.run();

		lCheck = impl_readDB (m_iTargetConnection);
		Assert.assertEquals ("testBackup [02] unexpected count of backup items", 1, lCheck.length);
	}

    //--------------------------------------------------------------------------
	private void impl_cleanDB (IPersistenceUnit iConnection)
		throws Exception
	{
		PersistenceUnit aPU = new PersistenceUnit (m_iEntityPU);
		aPU.merge(iConnection);
		
        DB iDB = new DB ();
        iDB.setPersistenceUnit(aPU);
        iDB.removeEntitySchema(TestEntity.class);
        iDB.createEntitySchema(TestEntity.class);
	}

    //--------------------------------------------------------------------------
	private void impl_createTestDB (IPersistenceUnit iConnection)
		throws Exception
	{
        int c           = 10;
        int i           = 0;
        m_lTestEntities = new TestEntity [c];
        
        for (i=0; i<c; ++i)
        	m_lTestEntities [i] = new TestEntity ();
        
        impl_updateTestDB(iConnection, m_lTestEntities);
	}
	
    //--------------------------------------------------------------------------
	private void impl_updateTestDB (IPersistenceUnit iConnection,
									TestEntity...    lEntities  )
		throws Exception
	{
		PersistenceUnit aPU = new PersistenceUnit (m_iEntityPU);
		aPU.merge(iConnection);
		
        DB iDB = new DB ();
        iDB.setPersistenceUnit(aPU);
        
        iDB.storeEntities(lEntities);
	}
	
    //--------------------------------------------------------------------------
	private TestEntity[] impl_readDB (IPersistenceUnit iConnection)
		throws Exception
	{
		PersistenceUnit aPU = new PersistenceUnit (m_iEntityPU);
		aPU.merge(iConnection);
		
        DB iDB = new DB ();
        iDB.setPersistenceUnit(aPU);
        
        List< TestEntity > lResults = new ArrayList< TestEntity > ();
        iDB.getAllEntitiesOfType(TestEntity.class, null, lResults);
        
        int          c         = lResults.size ();
        int          i         = 0;
        TestEntity[] lEntities = new TestEntity[c];
        for (i=0; i<c; ++i)
        	lEntities[i] = lResults.get(i);
        
        return lEntities;
	}

    //--------------------------------------------------------------------------
	private static IPersistenceUnit impl_getConnectionData (ISqlServer iServer)
		throws Exception
	{
		PersistenceUnit iPU = new PersistenceUnit ();
		iPU.setName     (UUID.randomUUID().toString());
		iPU.setSchema   (UUID.randomUUID().toString());
		iPU.setProperty (PersistenceUnitConst.JDBC_DRIVER       , iServer.getDriverClass  ());
		iPU.setProperty (PersistenceUnitConst.JDBC_CONNECTIONURL, iServer.getConnectionUrl());
		iPU.setProperty (PersistenceUnitConst.DB_USER           , iServer.getUser         ());
		iPU.setProperty (PersistenceUnitConst.DB_PASSWORD       , iServer.getPassword     ());
		return iPU;
	}
	
    //--------------------------------------------------------------------------
	private ISqlServer m_iSourceDBServer = null;
	
    //--------------------------------------------------------------------------
	private ISqlServer m_iTargetDBServer = null;
	
    //--------------------------------------------------------------------------
	private IPersistenceUnit m_iSourceConnection = null;
	
    //--------------------------------------------------------------------------
	private IPersistenceUnit m_iTargetConnection = null;
	
    //--------------------------------------------------------------------------
	private IPersistenceUnit m_iEntityPU = null;
	
    //--------------------------------------------------------------------------
	private TestEntity[] m_lTestEntities = null;
}
