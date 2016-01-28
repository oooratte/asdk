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
package test.net.as_development.asdk.db_service.impl.sql;

import java.sql.PreparedStatement;

import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;

import net.as_development.asdk.db_service.IDBBackendQuery;
import net.as_development.asdk.db_service.ISqlGenerator;
import net.as_development.asdk.db_service.impl.Row;
import net.as_development.asdk.db_service.impl.sql.SqlStatementCache;
import net.as_development.asdk.tools.test.AssertEx;

//==============================================================================
public class SqlStatementCacheTest
{
	//--------------------------------------------------------------------------
	@Test
	public void testBuildCacheId ()
		throws Exception
	{
		SqlStatementCache aCache     = new SqlStatementCache ();
		
		Row               aMetaMock  = PowerMockito.mock(Row.class            );
		IDBBackendQuery   iQueryMock = PowerMockito.mock(IDBBackendQuery.class);
		
		impl_testBuildCacheId (aCache, aMetaMock, iQueryMock, "my_table", ISqlGenerator.EStatementType.E_INSERT, "my_query", "my_table_E_INSERT_my_query");
		impl_testBuildCacheId (aCache, aMetaMock, null      , "my_table", ISqlGenerator.EStatementType.E_INSERT, "my_query", "my_table_E_INSERT"         );
	}
	
	//--------------------------------------------------------------------------
	private void impl_testBuildCacheId (SqlStatementCache            aCache          ,
									    Row                          aMetaMock       ,
									    IDBBackendQuery              iQueryMock      ,
									    String                       sTable          ,
									    ISqlGenerator.EStatementType eStatement      ,
									    String                       sQueryId        ,
									    String                       sExpectedCacheId)
		throws Exception
	{
		Mockito.when(aMetaMock.getTable()).thenReturn (sTable);
		
		if (iQueryMock != null)
			Mockito.when(iQueryMock.getId()).thenReturn (sQueryId);
		
		String sCacheId = aCache.buildCacheId(eStatement, aMetaMock, iQueryMock);
		AssertEx.assertEquals ("testBuildCacheId failed", sExpectedCacheId, sCacheId);
	}
	
	//--------------------------------------------------------------------------
	@Test
	public void testClear()
		throws Exception
	{
		SqlStatementCache   aCache          = new SqlStatementCache ();
		int                 c               = 10;
		int                 i               =  0;
		PreparedStatement[] lStatementMocks = new PreparedStatement[c];
		
		for (i=0; i<c; ++i)
		{
			String            sCacheId       = "id_"+i;
			PreparedStatement aStatementMock = PowerMockito.mock(PreparedStatement.class);
			aCache.put(sCacheId, aStatementMock);
			
			if (i <= (c/2))
				Mockito.doThrow(new RuntimeException()).when(aStatementMock).close();
			
			lStatementMocks[i] = aStatementMock;
		}

		aCache.clear ();
		
		AssertEx.assertEquals("testClear [01] expect an empty cache.", 0, aCache.size());
		
		// @todo verify if close () was called on all mocks ...
	}
}
