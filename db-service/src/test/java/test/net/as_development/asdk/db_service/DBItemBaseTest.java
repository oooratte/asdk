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
