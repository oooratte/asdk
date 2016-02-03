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
