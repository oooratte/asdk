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
package test.net.as_development.asdk.db_service.impl;

import org.junit.Assert;
import org.junit.Test;

import net.as_development.asdk.db_service.impl.EntityMetaInfoProvider;
import net.as_development.asdk.db_service.impl.PersistenceUnit;
import net.as_development.asdk.db_service.impl.PersistenceUnitConst;
import test.net.as_development.asdk.db_service.test.entities.TestEntity;

//==============================================================================
public class EntityMetaInfoProviderTest
{
    //--------------------------------------------------------------------------
	@Test
	public void testIdentifierLengthConstraint ()
		throws Exception
	{
	    // prepare test env
	    PersistenceUnit aPersistenceUnit = new PersistenceUnit ();
	    aPersistenceUnit.setName    ("test_persistence_unit"    );
        aPersistenceUnit.addEntity  (TestEntity.class.getName ());
        aPersistenceUnit.setProperty(PersistenceUnitConst.CONSTRAINT_MAX_STRING_LENGTH, "100000");
	    
	    // code under test
        try
        {
            EntityMetaInfoProvider aProvider = new EntityMetaInfoProvider ();
            aProvider.setPersistenceUnit(aPersistenceUnit);
            
            aPersistenceUnit.setProperty(PersistenceUnitConst.CONSTRAINT_MAX_IDENTIFIER_LENGTH, "1000");
            aProvider.retrieveMetaInfo();
        }
        catch (Throwable ex)
        {
            Assert.fail ("testIdentifierLengthConstraint [01] no exception expected if constrain max_identifier_length is not violated. But got '"+ex.getMessage()+"'.");
        }
        
        try
        {
            EntityMetaInfoProvider aProvider = new EntityMetaInfoProvider ();
            aProvider.setPersistenceUnit(aPersistenceUnit);
            
            aPersistenceUnit.setProperty(PersistenceUnitConst.CONSTRAINT_MAX_IDENTIFIER_LENGTH, "10");
            aProvider.retrieveMetaInfo();
            Assert.fail ("testIdentifierLengthConstraint [02] miss exception if constrain max_identifier_length is violated.");
        }
        catch (IllegalArgumentException ex)
        {}
	}
	
    //--------------------------------------------------------------------------
    @Test
    public void testMaxStringLengthConstraint ()
        throws Exception
    {
        // prepare test env
        PersistenceUnit aPersistenceUnit = new PersistenceUnit ();
        aPersistenceUnit.setName    ("test_persistence_unit");
        aPersistenceUnit.addEntity  (TestEntity.class.getName ());
        aPersistenceUnit.setProperty(PersistenceUnitConst.CONSTRAINT_MAX_IDENTIFIER_LENGTH, "100000");
        
        // code under test
        try
        {
            EntityMetaInfoProvider aProvider = new EntityMetaInfoProvider ();
            aProvider.setPersistenceUnit(aPersistenceUnit);
            
            aPersistenceUnit.setProperty(PersistenceUnitConst.CONSTRAINT_MAX_STRING_LENGTH, "4000");
            aProvider.retrieveMetaInfo();
        }
        catch (Throwable ex)
        {
            Assert.fail ("testMaxStringLengthConstraint [01] no exception expected if constrain max_string_length is not violated. But got '"+ex.getMessage()+"'.");
        }
        
        try
        {
            EntityMetaInfoProvider aProvider = new EntityMetaInfoProvider ();
            aProvider.setPersistenceUnit(aPersistenceUnit);
            
            aPersistenceUnit.setProperty(PersistenceUnitConst.CONSTRAINT_MAX_STRING_LENGTH, "5");
            aProvider.retrieveMetaInfo();
            Assert.fail ("testMaxStringLengthConstraint [02] miss exception if constrain max_string_length is violated.");
        }
        catch (IllegalArgumentException ex)
        {}
    }
}
