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
package test.net.as_development.asdk.db_service.impl;

import org.junit.Test;

import net.as_development.asdk.api.db.IDBQuery;
import net.as_development.asdk.db_service.impl.DB;
import net.as_development.asdk.db_service.impl.PersistenceUnit;
import net.as_development.asdk.tools.test.AssertEx;
import test.net.as_development.asdk.db_service.test.entities.TestEntity;

//==============================================================================
public class DBTest
{
    //--------------------------------------------------------------------------
	@Test
	public void testPrepareQuery ()
		throws Exception
	{
		DB              aDB = new DB ();
		PersistenceUnit aPu = new PersistenceUnit ();
		
		aPu.setName  ("testPrepareQuery");
		aPu.addEntity(TestEntity.class.getName());
		aDB.setPersistenceUnit(aPu);
		
		String sQueryId_A = "query_a";
		String sQueryId_B = "query_b";
		
		IDBQuery< TestEntity > iQuery_A1 = aDB.prepareQuery(TestEntity.class, sQueryId_A);
		IDBQuery< TestEntity > iQuery_A2 = aDB.prepareQuery(TestEntity.class, sQueryId_A);
		IDBQuery< TestEntity > iQuery_B  = aDB.prepareQuery(TestEntity.class, sQueryId_B);
		
		// a) of course queries must be valid and not null.
		AssertEx.assertNotNull ("testPrepareQuery [01] query a1 must not be null", iQuery_A1);
		AssertEx.assertNotNull ("testPrepareQuery [02] query a2 must not be null", iQuery_A2);
		AssertEx.assertNotNull ("testPrepareQuery [03] query b  must not be null", iQuery_B );
		
		// b) query with same ID must not point to the same query object
		//    The template behind is cached and used in shared mode. But queries
		//    itself has to be created new always to support using it within a
		//    multi-threaded environment.
		AssertEx.assertNotSame ("testPrepareQuery [04] query a1 != query a2 ?"   , iQuery_A1, iQuery_A2);
		AssertEx.assertNotSame ("testPrepareQuery [05] query b  != query a1 ?"   , iQuery_B , iQuery_A1);
		AssertEx.assertNotSame ("testPrepareQuery [05] query b  != query a2 ?"   , iQuery_B , iQuery_A2);
	}
	
    //--------------------------------------------------------------------------
	@Test
	public void testQuery ()
		throws Exception
	{
		/*
		String      sNextToken_1 = "token_1";
		String      sNextToken_2 = "token_2";
		
		List< Row > lResults_1   = new ArrayList< Row >(1);
		List< Row > lResults_2   = new ArrayList< Row >(1);
		
		DB              aDB      = new DB ();
		PersistenceUnit aPu      = new PersistenceUnit ();
		IDBBackend      iBackend = PowerMockito.mock(IDBBackend.class);

		aPu.setName  ("testQuery");
		aPu.addEntity(TestEntity.class.getName());
		aDB.setPersistenceUnit(aPu);
		
		PowerMockito.when(iBackend.queryRows(Mockito.any(Row.class), sNextToken, lResults, iQuery));
		ObjectManipulation.setFieldValue (aDB, "m_iBackend", iBackend);
		

		IDBQuery< TestEntity > iQuery = aDB.prepareQuery(TestEntity.class, "test_query");
		iQuery.match("StringValue", "my_value");
		
		List< TestEntity > lResults   = new ArrayList< TestEntity > (10);
		String             sNextToken = null;
		
		sNextToken = aDB.query(TestEntity.class, sNextToken, lResults, iQuery);
		*/
	}
}
