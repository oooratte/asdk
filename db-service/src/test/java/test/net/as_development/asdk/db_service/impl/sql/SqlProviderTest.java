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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Vector;

import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;

import net.as_development.asdk.db_service.IDBBackendQuery;
import net.as_development.asdk.db_service.ISqlGenerator;
import net.as_development.asdk.db_service.impl.EntityMetaInfo;
import net.as_development.asdk.db_service.impl.Row;
import net.as_development.asdk.db_service.impl.sql.SqlProvider;
import net.as_development.asdk.db_service.impl.sql.SqlStatementCache;
import net.as_development.asdk.tools.reflection.ObjectManipulation;
import net.as_development.asdk.tools.test.AssertEx;

//==============================================================================
public class SqlProviderTest
{
    //--------------------------------------------------------------------------
	@Test
	public void testResetOfBrokenConnection ()
		throws Exception
	{
		SqlProvider aSql              = new SqlProvider ();
		Connection  aConnection       = PowerMockito.mock(Connection.class);
		Connection  aCheck            = null;
		String      sConnectionMember = "m_aConnection";
		String      sTestMethod       = "impl_resetConnectionIfBroken";
		
		// a) valid connection ... member shouldn't be reset
		PowerMockito.when(aConnection.isClosed()               ).thenReturn(false);
		PowerMockito.when(aConnection.isValid(Mockito.anyInt())).thenReturn(true );
		
		         ObjectManipulation.setFieldValue    (aSql, sConnectionMember, aConnection);
		         ObjectManipulation.callPrivateMethod(aSql, sTestMethod);
		aCheck = ObjectManipulation.getFieldValue    (aSql, sConnectionMember);
		AssertEx.assertSame("testResetOfBrokenConnection [01] valid connection -> no reset", aConnection, aCheck);
		
		// b) connection is closed ... member must be reset to null
		PowerMockito.when(aConnection.isClosed()               ).thenReturn(true);
		PowerMockito.when(aConnection.isValid(Mockito.anyInt())).thenReturn(true);
		
		         ObjectManipulation.setFieldValue    (aSql, sConnectionMember, aConnection);
		         ObjectManipulation.callPrivateMethod(aSql, sTestMethod);
		aCheck = ObjectManipulation.getFieldValue    (aSql, sConnectionMember);
		AssertEx.assertNull("testResetOfBrokenConnection [02] closed connection -> reset", aCheck);
		
		// c) connection is invalid ... member must be reset to null
		PowerMockito.when(aConnection.isClosed()               ).thenReturn(false);
		PowerMockito.when(aConnection.isValid(Mockito.anyInt())).thenReturn(false);
		
		         ObjectManipulation.setFieldValue    (aSql, sConnectionMember, aConnection);
		         ObjectManipulation.callPrivateMethod(aSql, sTestMethod);
		aCheck = ObjectManipulation.getFieldValue    (aSql, sConnectionMember);
		AssertEx.assertNull("testResetOfBrokenConnection [03] invalid connection -> reset", aCheck);
	}
	
    //--------------------------------------------------------------------------
    @Test
    public void testResetOfClosedPreparedStatements ()
        throws Exception
    {
        SqlStatementCache            aSqlCache         = new SqlStatementCache ();
        SqlProvider                  aSqlProvider      = new SqlProvider ();
        String                       sConnectionMember = "m_aConnection";
        String                       sCacheMember      = "m_aSqlCache";
        String                       sTestMethod       = "impl_getSql";
        Connection                   aConnection       = PowerMockito.mock(Connection.class);
        EntityMetaInfo               aMetaInfo         = PowerMockito.mock(EntityMetaInfo.class);
        Row                          aMeta             = PowerMockito.mock(Row.class);
        IDBBackendQuery              iQuery            = PowerMockito.mock(IDBBackendQuery.class);
        ISqlGenerator.EStatementType eSqlFunc          = ISqlGenerator.EStatementType.E_CREATE_TABLE;
        PreparedStatement            aStatement1       = PowerMockito.mock(PreparedStatement.class);
        PreparedStatement            aStatement2       = PowerMockito.mock(PreparedStatement.class);
        String                       sStatementId      = aSqlCache.buildCacheId(eSqlFunc, aMeta, iQuery);

        // simulate ...
        // - a valid working connection
        // - an already filled SQL cache where the one and only statement inside is closed already
        // - a connection which returns a different (non closed statement) if it's called by method prepareStatement()
        PowerMockito.when(aStatement1.isClosed()).thenReturn(true );
        PowerMockito.when(aStatement2.isClosed()).thenReturn(false);
        
        PowerMockito.when(aConnection.isClosed()               ).thenReturn(false);
        PowerMockito.when(aConnection.isValid(Mockito.anyInt())).thenReturn(true );
        PowerMockito.when(aConnection.prepareStatement(Mockito.anyString())).thenReturn(aStatement2);
        PowerMockito.when(aConnection.prepareStatement(Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(aStatement2);
        
        PowerMockito.when(aMeta.getEntityMetaInfo()).thenReturn(aMetaInfo);
        PowerMockito.when(aMeta.listColumns()      ).thenReturn(new Vector< String >().iterator());
        
        PowerMockito.when(aMetaInfo.getSchema()).thenReturn("test_schema");
        
        aSqlCache.put(sStatementId, aStatement1);
        
        ObjectManipulation.setFieldValue (aSqlProvider, sConnectionMember, aConnection);
        ObjectManipulation.setFieldValue (aSqlProvider, sCacheMember     , aSqlCache  );
        
        // Method under test
        // Normal such method has to return our prepared statement 1 ...
        // but caused by the fact it's already closed code has to create a new one (also faked by us)
        // and return statement 2 instead.
        PreparedStatement aResultStatement = ObjectManipulation.callPrivateMethod(aSqlProvider, sTestMethod, eSqlFunc, (Row)aMeta, (IDBBackendQuery)iQuery);
        
        AssertEx.assertNotNull("testResetOfClosedPreparedStatements [01] resulting statement has not to be null."   , aResultStatement             );
        AssertEx.assertEquals ("testResetOfClosedPreparedStatements [02] resulting statement isnt the expected one.", aStatement2, aResultStatement);
    }
}
