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
