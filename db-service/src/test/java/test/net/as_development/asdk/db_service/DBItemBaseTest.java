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
package test.net.as_development.asdk.db_service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import net.as_development.asdk.api.db.IDB;
import net.as_development.asdk.api.db.IDBPool;
import net.as_development.asdk.api.service.env.IServiceEnv;
import net.as_development.asdk.db_service.DBItemBase;
import net.as_development.asdk.tools.test.AssertEx;

//==============================================================================
public class DBItemBaseTest
{
    //--------------------------------------------------------------------------
	@Before
	public void setUp ()
		throws Exception
	{
		IServiceEnv iSmgr = Mockito.mock(IServiceEnv.class);
		IDBPool     iPool = Mockito.mock(IDBPool.class);
		IDB         iDB   = Mockito.mock(IDB.class);
		
		Mockito.when(iPool.getDbForPersistenceUnit(Mockito.anyString())).thenReturn(iDB  );
		Mockito.when(iSmgr.getService             (IDBPool.class      )).thenReturn(iPool);
		
//		ServiceManager.setMock(iSmgr);
	}
	
    //--------------------------------------------------------------------------
	@After
	public void tearDown ()
		throws Exception
	{
//		ServiceManager.setMock(null);
	}
	
//    //--------------------------------------------------------------------------
//	/** test if creating the internal 'hosted' DB instance work as expected
//	 *  and use the right persistence unit configuration
//	 */
//	@Test
//	public void testDBInstCreation ()
//		throws Exception
//	{
//		String              sPersistenceUnit = "a.persistence.unit";
//		DBItemBaseTestClass aBase            = new DBItemBaseTestClass (sPersistenceUnit);
//		IDB                 iDB              = aBase.mem_DB();
//
//		AssertEx.assertNotNull("testDBInstCreation [01] must not be null"                                         , iDB);
//		AssertEx.assertEquals ("testDBInstCreation [01] derived class was not called within getPersistenceUnit ()", 1  , aBase.m_nCalls);
//	}
	
    //--------------------------------------------------------------------------
	/** test if naming of persistence units follow the expected schema.
	 */
	@Test
	public void testPUNaming ()
		throws Exception
	{
		String     sName         = null;
		Class< ? > aTestClass    = DBItemBase.class;
		String     sExpectedName = aTestClass.getName();
		
		sName = DBItemBase.namePersistenceUnit(aTestClass);
		AssertEx.assertEquals("testPUNaming [01]", sExpectedName, sName);
	}
	
    //--------------------------------------------------------------------------
	/** test if naming of DB queries follow the expected schema.
	 */
	@Test
	public void testQueryNaming ()
		throws Exception
	{
		String     sName         = null;
		Class< ? > aTestClass    = DBItemBase.class;
		String     sQuery        = "my-query";
		String     sExpectedName = aTestClass.getName()+":"+sQuery;
		
		sName = DBItemBase.nameQuery(aTestClass, sQuery);
		AssertEx.assertEquals("testQueryNaming [01]", sExpectedName, sName);
	}

    //--------------------------------------------------------------------------
	/** we need an inner test class ... DBItemBase is abstract and can't
	 *  be created directly .-)
	 */
	private class DBItemBaseTestClass extends DBItemBase
	{
	    //----------------------------------------------------------------------
		public DBItemBaseTestClass (String sPersistenceUnit)
		{
			m_sPersistenceUnit = sPersistenceUnit;
		}
		
	    //----------------------------------------------------------------------
		@Override
		protected String getPersistenceUnitName()
			throws Exception
		{
			m_nCalls++;
			return m_sPersistenceUnit;
		}

	    //----------------------------------------------------------------------
		public int m_nCalls = 0;

	    //----------------------------------------------------------------------
		public String m_sPersistenceUnit = null;
	}
}
