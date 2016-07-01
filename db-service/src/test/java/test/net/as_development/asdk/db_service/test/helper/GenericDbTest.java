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
package test.net.as_development.asdk.db_service.test.helper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.junit.Ignore;

import junit.framework.Assert;
import net.as_development.asdk.api.db.BetweenQueryRange;
import net.as_development.asdk.api.db.EQueryPartBinding;
import net.as_development.asdk.api.db.EQueryPartOperation;
import net.as_development.asdk.api.db.IDB;
import net.as_development.asdk.api.db.IDBQuery;
import net.as_development.asdk.api.db.IDBSchema;
import net.as_development.asdk.db_service.EntityBase;
import net.as_development.asdk.db_service.impl.DB;
import net.as_development.asdk.tools.reflection.ObjectManipulation;
import net.as_development.asdk.tools.test.AssertEx;
import test.net.as_development.asdk.db_service.test.entities.TestEntity;

//==============================================================================
/**
 * TODO document me
 */
@Ignore
public class GenericDbTest
{
    //--------------------------------------------------------------------------
    /// force creating an empty db on calling impl_setUp () method
    private static final int MODE_EMPTY_DB = 1;

    //--------------------------------------------------------------------------
    /// force creating a db where e.g. a schema for our TestEntity class was already created
    private static final int MODE_ENTITY_SCHEMA_AVAILABLE = 2;

    //--------------------------------------------------------------------------
    private static final boolean TEST_ID_GENERATION     = true;
    private static final boolean TEST_ID_REUSING        = true;
    private static final boolean TEST_SCHEMA_GENERATION = true;
    private static final boolean TEST_SCHEMA_REMOVE     = true;
    private static final boolean TEST_STORING_ENTITIES  = true;
    private static final boolean TEST_UPDATE_ENTITIES   = true;
    private static final boolean TEST_REMOVING_ENTITIES = true;
    private static final boolean TEST_QUERING_ENTITIES  = true;
    private static final boolean TEST_TYPE_MAPPING      = true;
    private static final boolean TEST_PERFORMANCE       = false;
    private static final boolean TEST_MODIFYSTAMP       = true;

    //--------------------------------------------------------------------------
    private static final String STRINGVALUE_TEST_ENTITY_PREFIX = "test_entity";

    //--------------------------------------------------------------------------
    private static final String STRINGVALUE_TEST_ENTITY_A = GenericDbTest.STRINGVALUE_TEST_ENTITY_PREFIX+"_a_";

    //--------------------------------------------------------------------------
    private static final String STRINGVALUE_TEST_ENTITY_B = GenericDbTest.STRINGVALUE_TEST_ENTITY_PREFIX+"_b_";

    //--------------------------------------------------------------------------
    private static final String STRINGVALUE_TEST_ENTITY_B1 = GenericDbTest.STRINGVALUE_TEST_ENTITY_PREFIX+"_b_1";
    
    //--------------------------------------------------------------------------
    /** create new instance.
     */
    public GenericDbTest ()
    {}

    //--------------------------------------------------------------------------
    /** define the type of db environment used by this test instance.
     *
     *  @param  nDbEnv
     *          must be one @link{DbEnvProvider.const}
     */
    public void defineDbEnv (int nDbEnv)
        throws Exception
    {
        m_nDbEnv = nDbEnv;
    }

    //--------------------------------------------------------------------------
    public void testIdGeneration ()
        throws Exception
    {
    	if ( ! GenericDbTest.TEST_ID_GENERATION)
    		return;

        try
        {
            IDB iDb = impl_setUp (GenericDbTest.MODE_ENTITY_SCHEMA_AVAILABLE);

            TestEntity e1 = new TestEntity ();
            Assert.assertNull("Id of new created entity must be null.", e1.Id);

            iDb.storeEntities(e1);
            Assert.assertNotNull("Id of new persistent entity must not be null.", e1.Id);
        }
        finally
        {
            impl_tearDown ();
        }
    }
    
    //--------------------------------------------------------------------------
    public void testIdReUsing ()
        throws Exception
    {
    	if ( ! GenericDbTest.TEST_ID_REUSING)
    		return;

        try
        {
            IDB iDb = impl_setUp (GenericDbTest.MODE_ENTITY_SCHEMA_AVAILABLE);
            iDb.removeAllEntitiesOfType(TestEntity.class);
            
            String     sExternalID        = UUID.randomUUID().toString();
            TestEntity aEntity            = new TestEntity ();
            		   aEntity.ExternalId = sExternalID;
            		   
            iDb.storeEntities(aEntity);
            Assert.assertEquals("external ID seems not to be taken real", sExternalID, aEntity.Id);
            
            TestEntity aCheck = iDb.getEntityById(TestEntity.class, sExternalID);
            Assert.assertNotNull("can not find entity with external ID in DB"                   , aCheck               );
            Assert.assertEquals ("found entity for external ID but it has not the right ID set.", aEntity.Id, aCheck.Id);
        }
        finally
        {
            impl_tearDown ();
        }
    }
    
    //--------------------------------------------------------------------------
    public void testSchemaCreation ()
        throws Exception
    {
    	if ( ! GenericDbTest.TEST_SCHEMA_GENERATION)
    		return;

        try
        {
            IDB        iDb = impl_setUp (GenericDbTest.MODE_EMPTY_DB);
            TestEntity e1  = new TestEntity ();

            try
            {
                iDb.storeEntities(e1);
                Assert.fail("IDB.storeEntities () does not throw an exception where entity schema was not created before.");
            }
            catch (Throwable ex)
            {}

            ((IDBSchema)iDb).createEntitySchema(TestEntity.class);
            iDb.storeEntities(e1);
        }
        finally
        {
            impl_tearDown ();
        }
    }

    //--------------------------------------------------------------------------
    public void testSchemaRemove ()
        throws Exception
    {
        if ( ! GenericDbTest.TEST_SCHEMA_REMOVE)
            return;

        try
        {
            IDB        iDb      = impl_setUp (GenericDbTest.MODE_EMPTY_DB);
            IDBSchema iCreator = (IDBSchema) iDb;
            TestEntity aEntity  = new TestEntity ();

            iCreator.createEntitySchema(TestEntity.class);
            iDb.storeEntities(aEntity);
            iCreator.removeEntitySchema(TestEntity.class);
            // no exception expected on double call to this method !
            iCreator.removeEntitySchema(TestEntity.class);
        }
        finally
        {
            impl_tearDown ();
        }
    }
    
    //--------------------------------------------------------------------------
    public void testStoringEntities ()
        throws Exception
    {
    	if ( ! GenericDbTest.TEST_STORING_ENTITIES)
    		return;

        try
        {
            IDB            iDb       = impl_setUp (GenericDbTest.MODE_ENTITY_SCHEMA_AVAILABLE);
            int            c         = 10;
            TestEntity[]   lEntities = new TestEntity [c];
            int            i         = 0;

            for (i=0; i<c; ++i)
            {
                TestEntity aEntity = new TestEntity ();

                aEntity.StringValue = "test_entity_nr_"+i;
                aEntity.IntValue    = i;

                lEntities[i] = aEntity;
            }

            iDb.storeEntities(lEntities);

            for (i=0; i<c; ++i)
            {
                TestEntity aOriginal = lEntities[i];
                TestEntity aCheck    = iDb.getEntityById(TestEntity.class, aOriginal.Id);

                Assert.assertNotSame ("testStoringEntities [01] original stored and by ID new retrieved entities has not to be the same object.", aOriginal   , aCheck   );
                Assert.assertEquals  ("testStoringEntities [02] ... but they have to use the same ID."                                          , aOriginal.Id, aCheck.Id);
            }
        }
        finally
        {
            impl_tearDown ();
        }
    }

    //--------------------------------------------------------------------------
    public void testUpdateEntities ()
        throws Exception
    {
    	if ( ! GenericDbTest.TEST_UPDATE_ENTITIES)
    		return;

        try
        {
        	String     sValue1  = "value_1";
        	String     sValue2  = "value_2";

        	TestEntity aEntity1 = null;
        	TestEntity aCheck   = null;

            IDB        iDb      = impl_setUp (GenericDbTest.MODE_ENTITY_SCHEMA_AVAILABLE);

            aEntity1 = new TestEntity ();
            aEntity1.StringValue = sValue1;
            iDb.storeEntities(aEntity1);

            aCheck = iDb.getEntityById(TestEntity.class, aEntity1.Id);
            AssertEx.assertEquals ("testUpdateEntities [01] check string value of entity after initial store.", sValue1, aCheck.StringValue);

            aEntity1.StringValue = sValue2;
            iDb.storeEntities(aEntity1);

            aCheck = iDb.getEntityById(TestEntity.class, aEntity1.Id);
            AssertEx.assertEquals ("testUpdateEntities [02] check string value of entity after update.", sValue2, aCheck.StringValue);
        }
        finally
        {
            impl_tearDown ();
        }
    }

    //--------------------------------------------------------------------------
    public void testRemovingEntities ()
        throws Exception
    {
    	if ( ! GenericDbTest.TEST_REMOVING_ENTITIES)
    		return;

        try
        {
            IDB            iDb       = impl_setUp (GenericDbTest.MODE_ENTITY_SCHEMA_AVAILABLE);
            int            c         = 10;
            TestEntity[]   lEntities = new TestEntity [c];
            int            i         = 0;

            for (i=0; i<c; ++i)
            {
                TestEntity aEntity = new TestEntity ();

                aEntity.StringValue = "test_entity_nr_"+i;
                aEntity.IntValue    = i;

                lEntities[i] = aEntity;
            }

            iDb.storeEntities(lEntities);

            String     sId5    = lEntities[5].Id;
            String     sId7    = lEntities[7].Id;

            iDb.removeEntities(lEntities[5], lEntities[7]);

            TestEntity aCheck5 = iDb.getEntityById(TestEntity.class, sId5);
            TestEntity aCheck7 = iDb.getEntityById(TestEntity.class, sId7);

            AssertEx.assertFalse ("testRemovingEntities [01] entity '5' was removed .. but isPersistent() returns true."  , lEntities[5].isPersistent());
            AssertEx.assertFalse ("testRemovingEntities [02] entity '7' was removed .. but isPersistent() returns true."  , lEntities[7].isPersistent());
            AssertEx.assertNull  ("testRemovingEntities [03] entity '5' was removed .. but it's Id was not reseted."      , lEntities[5].Id);
            AssertEx.assertNull  ("testRemovingEntities [04] entity '7' was removed .. but it's Id was not reseted."      , lEntities[7].Id);
            AssertEx.assertNull  ("testRemovingEntities [05] entity '5' was removed .. but could be retrieved afterwards.", aCheck5);
            AssertEx.assertNull  ("testRemovingEntities [06] entity '7' was removed .. but could be retrieved afterwards.", aCheck7);
        }
        finally
        {
            impl_tearDown ();
        }
    }

    //--------------------------------------------------------------------------
    public void testQueringEntities ()
        throws Exception
    {
    	if ( ! GenericDbTest.TEST_QUERING_ENTITIES)
    		return;

    	impl_testMatchQueries    ();
    	impl_testLikeQueries     ();
    	impl_testBetweenQueries  ();
    	impl_testBetweenDates    ();
    	
    	impl_testQueryPagination ();
    }

    //--------------------------------------------------------------------------
    public void testTypeMapping ()
        throws Exception
    {
        if ( ! GenericDbTest.TEST_TYPE_MAPPING)
            return;
        
        try
        {
            IDB iDb = impl_setUp (GenericDbTest.MODE_ENTITY_SCHEMA_AVAILABLE);
            
            impl_checkBooleanTypeMapping (iDb);
            impl_checkCharTypeMapping    (iDb);
            impl_checkByteTypeMapping    (iDb);
            impl_checkShortTypeMapping   (iDb);
            impl_checkIntTypeMapping     (iDb);
            impl_checkLongTypeMapping    (iDb);
            impl_checkDoubleTypeMapping  (iDb);
            impl_checkFloatTypeMapping   (iDb);
            impl_checkDateTypeMapping    (iDb);
            impl_checkStringTypeMapping  (iDb);
        }
        finally
        {
            impl_tearDown ();
        }
    }
    
    //--------------------------------------------------------------------------
    public void testPerformance ()
        throws Exception
    {
        if ( ! GenericDbTest.TEST_PERFORMANCE)
            return;
        
        try
        {
            IDB iDb = impl_setUp (GenericDbTest.MODE_ENTITY_SCHEMA_AVAILABLE);
            
            long nStart = System.currentTimeMillis();
            /* [ms]        	100     200		avg[1]
             * -------------------------------------
             * derby    	62953   134250	650.39
             * mongo    	15595    39535	176.81
             * simpledb		60839	117992  599.18
             */
            for (int i=0; i<100; ++i)
            {
                TestEntity aEntity = new TestEntity ();
                iDb.storeEntities(aEntity);
            }
            
            long nEnd   = System.currentTimeMillis();
            long nTime  = nEnd - nStart;
            
            System.out.println ("time needed : "+nTime+" ms");
        }
        finally
        {
            impl_tearDown ();
        }
    }
    
    //--------------------------------------------------------------------------
    public void testModifyStamp ()
        throws Exception
    {
    	if ( ! GenericDbTest.TEST_MODIFYSTAMP)
    		return;

        try
        {
            IDB            iDb          = impl_setUp (GenericDbTest.MODE_ENTITY_SCHEMA_AVAILABLE);
            int            c            = 10;
            TestEntity[]   lEntities    = new TestEntity [c];
            int            i            = 0;

            for (i=0; i<c; ++i)
            {
                TestEntity aEntity = new TestEntity ();

                aEntity.StringValue = "test_entity_nr_"+i;
                aEntity.IntValue    = i;

                lEntities[i] = aEntity;
            }

            // check if modify stamp was created first time
            iDb.storeEntities(lEntities);
            for (i=0; i<c; ++i)
            {
                TestEntity aCheck = lEntities[i];
                Assert.assertNotNull ("testModifyStamp [01] modify stamp != null ?", aCheck.getModifyStamp());
            }
            
            // modify one entity and check its modify stamp value again
            TestEntity aModifyEntity    = lEntities[c-1]; 
            Date       aLastModifyStamp = aModifyEntity.getModifyStamp(); 
            
            synchronized (this)
            {
            	this.wait(100);
            }
            
            iDb.storeEntities(aModifyEntity);
            Date aNewModifyStamp = aModifyEntity.getModifyStamp();
            Assert.assertTrue ("testModifyStamp [02] modify stamp not updated ?.", aLastModifyStamp.getTime() != aNewModifyStamp.getTime());
            
            // check if query on modify stamp works as expected
            String                 sNextToken = null;
            List< TestEntity >     lResults   = new ArrayList< TestEntity  >(10);
            IDBQuery< TestEntity > iQuery     = iDb.prepareQuery(TestEntity.class, "query_for_modify_stamp");
            iQuery.defineQueryPart(0, EQueryPartBinding.E_AND, EQueryPartOperation.E_GREATER_THAN, EntityBase.ATTRIBUTE_NAME_MODIFY_STAMP, aLastModifyStamp);
            iDb.query(TestEntity.class, sNextToken, lResults, iQuery);
            
            Assert.assertTrue   ("testModifyStamp [03] unexpected size for result set"   , lResults.size() == 1                );
            Assert.assertEquals ("testModifyStamp [04] unexpected entity in query result", aModifyEntity.Id, lResults.get(0).Id);
        }
        finally
        {
            impl_tearDown ();
        }
    }

    //--------------------------------------------------------------------------
    private void impl_checkBooleanTypeMapping (IDB iDb)
        throws Exception
    {
        impl_checkNullMapping ("testTypeMapping - Boolean [01] check null"   , iDb, "BooleanValue"      );
        impl_checkTypeMapping ("testTypeMapping - Boolean [01] check 'false'", iDb, "BooleanValue"      , Boolean.FALSE);
        impl_checkTypeMapping ("testTypeMapping - Boolean [02] check 'true'" , iDb, "BooleanValue"      , Boolean.TRUE );
        impl_checkTypeMapping ("testTypeMapping - boolean [03] check 'false'", iDb, "SimpleBooleanValue", false        );
        impl_checkTypeMapping ("testTypeMapping - boolean [04] check 'true'" , iDb, "SimpleBooleanValue", true         );
    }
    
    //--------------------------------------------------------------------------
    private void impl_checkCharTypeMapping (IDB iDb)
        throws Exception
    {
        impl_checkTypeMapping ("testTypeMapping - Char [01] check ' '" , iDb, "CharValue", ' ');
        impl_checkTypeMapping ("testTypeMapping - Char [02] check '#'" , iDb, "CharValue", '#');
        impl_checkTypeMapping ("testTypeMapping - Char [03] check 'ö'" , iDb, "CharValue", 'ö');
    }
    
    //--------------------------------------------------------------------------
    private void impl_checkByteTypeMapping (IDB iDb)
        throws Exception
    {
        impl_checkNullMapping ("testTypeMapping - Byte [01] check null"  , iDb, "ByteValue"      );
        impl_checkTypeMapping ("testTypeMapping - Byte [02] check 'min'" , iDb, "ByteValue"      , Byte.MIN_VALUE          );
        impl_checkTypeMapping ("testTypeMapping - Byte [03] check '-100'", iDb, "ByteValue"      , Byte.valueOf((byte)-100));
        impl_checkTypeMapping ("testTypeMapping - Byte [04] check '0'"   , iDb, "ByteValue"      , Byte.valueOf((byte)   0));
        impl_checkTypeMapping ("testTypeMapping - Byte [05] check '100'" , iDb, "ByteValue"      , Byte.valueOf((byte) 100));
        impl_checkTypeMapping ("testTypeMapping - Byte [06] check 'max'" , iDb, "ByteValue"      , Byte.MAX_VALUE          );
        impl_checkTypeMapping ("testTypeMapping - byte [07] check 'min'" , iDb, "SimpleByteValue", Byte.MIN_VALUE          );
        impl_checkTypeMapping ("testTypeMapping - byte [08] check '-100'", iDb, "SimpleByteValue", (byte)-100              );
        impl_checkTypeMapping ("testTypeMapping - byte [09] check '0'"   , iDb, "SimpleByteValue", (byte)   0              );
        impl_checkTypeMapping ("testTypeMapping - byte [10] check '100'" , iDb, "SimpleByteValue", (byte) 100              );
        impl_checkTypeMapping ("testTypeMapping - byte [11] check 'max'" , iDb, "SimpleByteValue", Byte.MAX_VALUE          );
    }
    
    //--------------------------------------------------------------------------
    private void impl_checkShortTypeMapping (IDB iDb)
        throws Exception
    {
        impl_checkNullMapping ("testTypeMapping - Short [01] check null"  , iDb, "ShortValue"      );
        impl_checkTypeMapping ("testTypeMapping - Short [02] check 'min'" , iDb, "ShortValue"      , Short.MIN_VALUE           );
        impl_checkTypeMapping ("testTypeMapping - Short [03] check '-100'", iDb, "ShortValue"      , Short.valueOf((short)-100));
        impl_checkTypeMapping ("testTypeMapping - Short [04] check '0'"   , iDb, "ShortValue"      , Short.valueOf((short)   0));
        impl_checkTypeMapping ("testTypeMapping - Short [05] check '100'" , iDb, "ShortValue"      , Short.valueOf((short) 100));
        impl_checkTypeMapping ("testTypeMapping - Short [06] check 'max'" , iDb, "ShortValue"      , Short.MAX_VALUE           );
        impl_checkTypeMapping ("testTypeMapping - short [07] check 'min'" , iDb, "SimpleShortValue", Short.MIN_VALUE           );
        impl_checkTypeMapping ("testTypeMapping - short [08] check '-100'", iDb, "SimpleShortValue", (short)-100               );
        impl_checkTypeMapping ("testTypeMapping - short [09] check '0'"   , iDb, "SimpleShortValue", (short)   0               );
        impl_checkTypeMapping ("testTypeMapping - short [10] check '100'" , iDb, "SimpleShortValue", (short) 100               );
        impl_checkTypeMapping ("testTypeMapping - short [11] check 'max'" , iDb, "SimpleShortValue", Short.MAX_VALUE           );
    }
    
    //--------------------------------------------------------------------------
    private void impl_checkIntTypeMapping (IDB iDb)
        throws Exception
    {
        impl_checkNullMapping ("testTypeMapping - Integer [01] check null"  , iDb, "IntValue"      );
        impl_checkTypeMapping ("testTypeMapping - Integer [02] check 'min'" , iDb, "IntValue"      , Integer.MIN_VALUE         );
        impl_checkTypeMapping ("testTypeMapping - Integer [03] check '-100'", iDb, "IntValue"      , Integer.valueOf((int)-100));
        impl_checkTypeMapping ("testTypeMapping - Integer [04] check '0'"   , iDb, "IntValue"      , Integer.valueOf((int)   0));
        impl_checkTypeMapping ("testTypeMapping - Integer [05] check '100'" , iDb, "IntValue"      , Integer.valueOf((int) 100));
        impl_checkTypeMapping ("testTypeMapping - Integer [06] check 'max'" , iDb, "IntValue"      , Integer.MAX_VALUE         );
        impl_checkTypeMapping ("testTypeMapping - int     [07] check 'min'" , iDb, "SimpleIntValue", Integer.MIN_VALUE         );
        impl_checkTypeMapping ("testTypeMapping - int     [08] check '-100'", iDb, "SimpleIntValue", (int)-100                 );
        impl_checkTypeMapping ("testTypeMapping - int     [09] check '0'"   , iDb, "SimpleIntValue", (int)   0                 );
        impl_checkTypeMapping ("testTypeMapping - int     [10] check '100'" , iDb, "SimpleIntValue", (int) 100                 );
        impl_checkTypeMapping ("testTypeMapping - int     [11] check 'max'" , iDb, "SimpleIntValue", Integer.MAX_VALUE         );
    }
    
    //--------------------------------------------------------------------------
    private void impl_checkLongTypeMapping (IDB iDb)
        throws Exception
    {
        impl_checkNullMapping ("testTypeMapping - Long [01] check null"  , iDb, "LongValue"      );
        impl_checkTypeMapping ("testTypeMapping - Long [02] check 'min'" , iDb, "LongValue"      , Long.MIN_VALUE          );
        impl_checkTypeMapping ("testTypeMapping - Long [03] check '-100'", iDb, "LongValue"      , Long.valueOf((long)-100));
        impl_checkTypeMapping ("testTypeMapping - Long [04] check '0'"   , iDb, "LongValue"      , Long.valueOf((long)   0));
        impl_checkTypeMapping ("testTypeMapping - Long [05] check '100'" , iDb, "LongValue"      , Long.valueOf((long) 100));
        impl_checkTypeMapping ("testTypeMapping - Long [06] check 'max'" , iDb, "LongValue"      , Long.MAX_VALUE          );
        impl_checkTypeMapping ("testTypeMapping - long [07] check 'min'" , iDb, "SimpleLongValue", Long.MIN_VALUE          );
        impl_checkTypeMapping ("testTypeMapping - long [08] check '-100'", iDb, "SimpleLongValue", (long)-100              );
        impl_checkTypeMapping ("testTypeMapping - long [09] check '0'"   , iDb, "SimpleLongValue", (long)   0              );
        impl_checkTypeMapping ("testTypeMapping - long [10] check '100'" , iDb, "SimpleLongValue", (long) 100              );
        impl_checkTypeMapping ("testTypeMapping - long [11] check 'max'" , iDb, "SimpleLongValue", Long.MAX_VALUE          );
    }
    
    //--------------------------------------------------------------------------
    private void impl_checkDoubleTypeMapping (IDB iDb)
        throws Exception
    {
    	double MIN = Double.MIN_VALUE;
    	double MAX = Double.MAX_VALUE;
    	
    	if (m_nDbEnv == DbEnvProvider.ENV_EMBEDDED_SQL)
    	{
    		// tricky:
    		// Derby (used in embedded SQL mode) dont support full double range.
    		// So we must define a suitable range for this test ...
    		// This wont be relevant for product use ...
    		// excepting we use derby in production mode too .-)
    		
    		MIN = -1.79769E+308;
    		MAX =  1.79769E+308;
    	}
    	
        impl_checkNullMapping ("testTypeMapping - Double [01] check null"      , iDb, "DoubleValue"      );
        impl_checkTypeMapping ("testTypeMapping - Double [02] check 'min'"     , iDb, "DoubleValue"      , MIN                              );
        impl_checkTypeMapping ("testTypeMapping - Double [03] check '-100.78'" , iDb, "DoubleValue"      , Double.valueOf((double)-100.78  ));
        impl_checkTypeMapping ("testTypeMapping - Double [04] check '0.0'"     , iDb, "DoubleValue"      , Double.valueOf((double)   0.0   ));
        impl_checkTypeMapping ("testTypeMapping - Double [05] check '100.4633'", iDb, "DoubleValue"      , Double.valueOf((double) 100.4633));
        impl_checkTypeMapping ("testTypeMapping - Double [06] check 'max'"     , iDb, "DoubleValue"      , MAX                              );
        impl_checkTypeMapping ("testTypeMapping - double [07] check 'min'"     , iDb, "SimpleDoubleValue", MIN                              );
        impl_checkTypeMapping ("testTypeMapping - double [08] check '-90.1967'", iDb, "SimpleDoubleValue", (double) -90.1967                );
        impl_checkTypeMapping ("testTypeMapping - double [09] check '0.0'"     , iDb, "SimpleDoubleValue", (double)   0.0                   );
        impl_checkTypeMapping ("testTypeMapping - double [10] check '100.1674'", iDb, "SimpleDoubleValue", (double) 100.1674                );
        impl_checkTypeMapping ("testTypeMapping - double [11] check 'max'"     , iDb, "SimpleDoubleValue", MAX                              );
    }

    //--------------------------------------------------------------------------
    private void impl_checkFloatTypeMapping (IDB iDb)
        throws Exception
    {
        impl_checkNullMapping ("testTypeMapping - Float [01] check null"      , iDb, "FloatValue"      );
        impl_checkTypeMapping ("testTypeMapping - Float [02] check 'min'"     , iDb, "FloatValue"      , Float.MIN_VALUE                );
        impl_checkTypeMapping ("testTypeMapping - Float [03] check '-100.78'" , iDb, "FloatValue"      , Float.valueOf((float)-100.78  ));
        impl_checkTypeMapping ("testTypeMapping - Float [04] check '0.0'"     , iDb, "FloatValue"      , Float.valueOf((float)   0.0   ));
        impl_checkTypeMapping ("testTypeMapping - Float [05] check '100.4633'", iDb, "FloatValue"      , Float.valueOf((float) 100.4633));
        impl_checkTypeMapping ("testTypeMapping - Float [06] check 'max'"     , iDb, "FloatValue"      , Float.MAX_VALUE                );
        impl_checkTypeMapping ("testTypeMapping - float [07] check 'min'"     , iDb, "SimpleFloatValue", Float.MIN_VALUE                );
        impl_checkTypeMapping ("testTypeMapping - float [08] check '-90.1967'", iDb, "SimpleFloatValue", (float) -90.1967               );
        impl_checkTypeMapping ("testTypeMapping - float [09] check '0.0'"     , iDb, "SimpleFloatValue", (float)   0.0                  );
        impl_checkTypeMapping ("testTypeMapping - float [10] check '100.1674'", iDb, "SimpleFloatValue", (float) 100.1674               );
        impl_checkTypeMapping ("testTypeMapping - float [11] check 'max'"     , iDb, "SimpleFloatValue", Float.MAX_VALUE                );
    }

    //--------------------------------------------------------------------------
    private void impl_checkStringTypeMapping (IDB iDb)
        throws Exception
    {
        impl_checkNullMapping ("testTypeMapping - String [01] check null"                 , iDb, "StringValue");
        impl_checkTypeMapping ("testTypeMapping - String [02] check ''"                   , iDb, "StringValue", ""                   );
        impl_checkTypeMapping ("testTypeMapping - String [03] check ' '"                  , iDb, "StringValue", " "                  );
        impl_checkTypeMapping ("testTypeMapping - String [04] check 'any string you like'", iDb, "StringValue", "any string you like");
        impl_checkTypeMapping ("testTypeMapping - String [04] check 'öäüß#+´!§$%&/()=?'"  , iDb, "StringValue", "öäüß#+´!§$%&/()=?"  );
        impl_checkTypeMapping ("testTypeMapping - String [04] check '\n\t\r\\\""          , iDb, "StringValue", "\n\t\r\\\""         );
        // TODO test unicode
    }
    
    //--------------------------------------------------------------------------
    private void impl_checkDateTypeMapping (IDB iDb)
        throws Exception
    {
        impl_checkNullMapping ("testTypeMapping - Date [01] check null"     , iDb, "DateValue");
        impl_checkTypeMapping ("testTypeMapping - Date [02] check real Date", iDb, "DateValue", new Date ());
    }
    
    //--------------------------------------------------------------------------
    private void impl_checkTypeMapping (String sErrorMessage,
                                        IDB    iDb          ,
                                        String sEntityField ,
                                        Object aValue       )
        throws Exception
    {
        TestEntity aEntity = new TestEntity ();

        // set test value and update DB
        ObjectManipulation.setFieldValue(aEntity, sEntityField, aValue);
        iDb.storeEntities(aEntity);
        
        // read from DB back and check value against original value
        TestEntity aCheckEntity = iDb.getEntityById(TestEntity.class, aEntity.Id);
        Object     aCheckValue  = ObjectManipulation.getFieldValue(aCheckEntity, sEntityField);
        
        AssertEx.assertEquals(sErrorMessage, aCheckValue, aValue);
    }
    
    //--------------------------------------------------------------------------
    private void impl_checkNullMapping (String sErrorMessage,
                                        IDB    iDb          ,
                                        String sEntityField )
        throws Exception
    {
        TestEntity aEntity = new TestEntity ();

        // set test value and update DB
        ObjectManipulation.setFieldValue(aEntity, sEntityField, (Byte)null);
        iDb.storeEntities(aEntity);
        
        // read from DB back and check value against original value
        TestEntity aCheckEntity = iDb.getEntityById(TestEntity.class, aEntity.Id);
        Object     aCheckValue  = ObjectManipulation.getFieldValue(aCheckEntity, sEntityField);
        
        AssertEx.assertNull(sErrorMessage, aCheckValue);
    }
    
    //--------------------------------------------------------------------------
    private void impl_testMatchQueries ()
        throws Exception
    {
        try
        {
        	IDB iDb = impl_createTestDbForQueries (20);

            List< TestEntity >     lResults        = new ArrayList< TestEntity >();
            IDBQuery< TestEntity > iQuery          = null;
            String                 sFirstNextToken = null;

            // a) check for boolean match
            //    20 items ... but 10 boolean-true items only

            iQuery = iDb.prepareQuery(TestEntity.class, "match_a");
            iQuery.defineQueryPart(0, EQueryPartBinding.E_AND, EQueryPartOperation.E_MATCH, TestEntity.ATTRIBUTE_NAME_BOOLEANVALUE, true);
            iDb.query(TestEntity.class, sFirstNextToken, lResults, iQuery);

            AssertEx.assertEquals("testMatchQueries [a_01] expect 10 results", 10, lResults.size());
            for (TestEntity aResult : lResults)
            	AssertEx.assertTrue("testMatchQueries [a_02] check if boolean value is TRUE ...", aResult.BooleanValue == true);

            // b) Check for integer match
            //	  ask for items with number: 1, 5, 7, 18

            iQuery = iDb.prepareQuery(TestEntity.class, "match_b");
            iQuery.defineQueryPart(0, EQueryPartBinding.E_OR, EQueryPartOperation.E_MATCH, TestEntity.ATTRIBUTE_NAME_INTVALUE,  1);
            iQuery.defineQueryPart(1, EQueryPartBinding.E_OR, EQueryPartOperation.E_MATCH, TestEntity.ATTRIBUTE_NAME_INTVALUE,  5);
            iQuery.defineQueryPart(2, EQueryPartBinding.E_OR, EQueryPartOperation.E_MATCH, TestEntity.ATTRIBUTE_NAME_INTVALUE,  7);
            iQuery.defineQueryPart(3, EQueryPartBinding.E_OR, EQueryPartOperation.E_MATCH, TestEntity.ATTRIBUTE_NAME_INTVALUE, 18);
            iDb.query(TestEntity.class, sFirstNextToken, lResults, iQuery);

            AssertEx.assertEquals("testMatchQueries [b_01] expect 4 results", 4, lResults.size());
            List< Integer > lIntResults = new ArrayList< Integer >(4);
            for (TestEntity aResult : lResults)
            	lIntResults.add (aResult.IntValue);
        	AssertEx.assertTrue  ("testMatchQueries [b_02] check for integer result '1' ..." , lIntResults.contains( 1));
        	AssertEx.assertTrue  ("testMatchQueries [b_03] check for integer result '5' ..." , lIntResults.contains( 5));
        	AssertEx.assertTrue  ("testMatchQueries [b_04] check for integer result '7' ..." , lIntResults.contains( 7));
        	AssertEx.assertTrue  ("testMatchQueries [b_05] check for integer result '18' ...", lIntResults.contains(18));
        }
        finally
        {
            impl_tearDown ();
        }
    }

    //--------------------------------------------------------------------------
    private void impl_testLikeQueries ()
        throws Exception
    {
        try
        {
        	IDB iDb = impl_createTestDbForQueries (20);

            List< TestEntity >     lResults        = new ArrayList< TestEntity >();
            IDBQuery< TestEntity > iQuery          = null;
            String                 sFirstNextToken = null;

        	// a) Check 'like' for 'asterisk' ...
        	//    Ask for all test entries with "_b_" within it's string value.

            iQuery = iDb.prepareQuery(TestEntity.class, "like_a");
            iQuery.defineQueryPart(0, EQueryPartBinding.E_AND, EQueryPartOperation.E_LIKE, TestEntity.ATTRIBUTE_NAME_STRINGVALUE, "*_b_*");
            iDb.query(TestEntity.class, sFirstNextToken, lResults, iQuery);

            AssertEx.assertEquals("testLikeQueries [a_01] expect 10 results", 10, lResults.size());
            for (TestEntity aResult : lResults)
            	AssertEx.assertTrue("testLikeQueries [a_02] check if string value match ...", aResult.StringValue.startsWith(GenericDbTest.STRINGVALUE_TEST_ENTITY_B));

            // b) Check 'like' for 'question mark' ...
        	//    Ask for all test entries with "test_entity_a_1?" within it's string value.

            iQuery = iDb.prepareQuery(TestEntity.class, "like_b");
            iQuery.defineQueryPart(0, EQueryPartBinding.E_AND, EQueryPartOperation.E_LIKE, TestEntity.ATTRIBUTE_NAME_STRINGVALUE, GenericDbTest.STRINGVALUE_TEST_ENTITY_B1+"?");
            iDb.query(TestEntity.class, sFirstNextToken, lResults, iQuery);

            AssertEx.assertEquals("testLikeQueries [b_01] expect 10 results", 10, lResults.size());
            for (TestEntity aResult : lResults)
            	AssertEx.assertTrue("testLikeQueries [b_02] check if string value match ...", aResult.StringValue.startsWith(GenericDbTest.STRINGVALUE_TEST_ENTITY_B1));
        }
        finally
        {
            impl_tearDown ();
        }
    }
    
    //--------------------------------------------------------------------------
    private void impl_testBetweenQueries ()
        throws Exception
    {
        try
        {
            IDB iDb = impl_createTestDbForQueries (20);

            List< TestEntity >     lResults        = new ArrayList< TestEntity >();
            IDBQuery< TestEntity > iQuery          = null;
            String                 sFirstNextToken = null;
            
            // check around 'boundary' ...
            // where might special things can happen ...
            // e.g. when SimpleDB use string compare and '2' is > '10' .-)

            int nMin =  8;
            int nMax = 11;

            iQuery = iDb.prepareQuery(TestEntity.class, "between_8_and_11");
            iQuery.defineQueryPart(0, EQueryPartBinding.E_AND, EQueryPartOperation.E_BETWEEN, TestEntity.ATTRIBUTE_NAME_INTVALUE, new BetweenQueryRange (nMin, nMax));
            iDb.query(TestEntity.class, sFirstNextToken, lResults, iQuery);

            AssertEx.assertEquals("testBetweenQueries [a_01] expect 4 results", 4, lResults.size());
            for (TestEntity aResult : lResults)
                AssertEx.assertTrue("testBetweenQueries [a_02] check if int value match ...", aResult.IntValue >= nMin && aResult.IntValue <= nMax);
        }
        finally
        {
            impl_tearDown ();
        }
    }
    
    //--------------------------------------------------------------------------
    private void impl_testBetweenDates ()
        throws Exception
    {
        try
        {
            IDB iDb = impl_createTestDbForQueries (20);

            List< TestEntity >     lResults        = new ArrayList< TestEntity >();
            IDBQuery< TestEntity > iQuery          = null;
            String                 sFirstNextToken = null;
            
            long nReferenceDate = m_aReferenceDate.getTime();
            long nMin           = nReferenceDate +  8;
            long nMax           = nReferenceDate + 11;
            Date aMin           = new Date (nMin);
            Date aMax           = new Date (nMax);

            iQuery = iDb.prepareQuery(TestEntity.class, "between_date_8_and_date_11");
            iQuery.defineQueryPart(0, EQueryPartBinding.E_AND, EQueryPartOperation.E_BETWEEN, TestEntity.ATTRIBUTE_NAME_DATEVALUE, new BetweenQueryRange (aMin, aMax));
            iDb.query(TestEntity.class, sFirstNextToken, lResults, iQuery);

            AssertEx.assertEquals("testBetweenDates [01] expect 4 results", 4, lResults.size());
            for (TestEntity aResult : lResults)
                AssertEx.assertTrue("testBetweenQueries [02] check if Date value is between ...", aResult.DateValue.getTime() >= nMin && aResult.DateValue.getTime() <= nMax);
        }
        finally
        {
            impl_tearDown ();
        }
    }

    //--------------------------------------------------------------------------
    private void impl_testQueryPagination ()
        throws Exception
    {
    	/*
        try
        {
            int nTableSize = 100; 
            int nPageSize  =  10;
            int nPage      =   0;
            
            NextToken.DEFAULT_PAGESIZE = nPageSize;
            IDB iDb = impl_createTestDbForQueries (nTableSize);

            List< TestEntity >     lResults   = new ArrayList< TestEntity >();
            IDBQuery< TestEntity > iQuery     = null;
            String                 sNextToken = null;

            iQuery = iDb.prepareQuery(TestEntity.class, "pagination_query");
            iQuery.setQueryPart(0, EQueryPartBinding.E_AND, EQueryPartOperation.E_LIKE, TestEntity.ATTRIBUTE_NAME_STRINGVALUE, "*");
            
            do
            {
                sNextToken = iDb.query(TestEntity.class, sNextToken, lResults, iQuery);
                
                ++nPage;
                
                int nMin = (nPage-1) * nPageSize;
                int nMax =  nPage    * nPageSize;
                
                for (TestEntity aEntity : lResults)
                {
                    boolean bInPage = (
                                       (aEntity.IntValue >= nMin) &&
                                       (aEntity.IntValue <  nMax)
                                      );
                    AssertEx.assertTrue("impl_testQueryPagination [01] resulting entity '"+aEntity.IntValue+"' not in right page ("+nMin+"-"+nMax+")", bInPage);
                }
            }
            while ( ! StringUtils.isEmpty(sNextToken));
            
            int nExpectedPages = nTableSize / nPageSize;
            AssertEx.assertEquals("impl_testQueryPagination [02] wrong page count", nExpectedPages, nPage);
        }
        finally
        {
            impl_tearDown ();
        }
        */
    }
    
    //--------------------------------------------------------------------------
    private IDB impl_setUp (int nMode)
        throws Exception
    {
        m_aDbEnv = new DbEnvProvider ();
        m_aDbEnv.setUp(m_nDbEnv);

        DB iDb = new DB ();
        iDb.setPersistenceUnit(m_aDbEnv.getPersistenceUnit());

        iDb.removeEntitySchema(TestEntity.class);
        if (nMode == GenericDbTest.MODE_ENTITY_SCHEMA_AVAILABLE)
            iDb.createEntitySchema(TestEntity.class);

        return iDb;
    }

    //--------------------------------------------------------------------------
    private void impl_tearDown ()
        throws Exception
    {
        if (m_aDbEnv != null)
            m_aDbEnv.tearDown(m_nDbEnv);
        m_aDbEnv = null;
    }

    //--------------------------------------------------------------------------
    private IDB impl_createTestDbForQueries (int nCount)
    	throws Exception
    {
    	// create some test data ...
        IDB          iDb              = impl_setUp (GenericDbTest.MODE_ENTITY_SCHEMA_AVAILABLE);
        int          c                = nCount;
        int          i                = 0;
        TestEntity[] lEntities        = new TestEntity[c];
                     m_aReferenceDate = new Date ();
        long         nReferenceDate   = m_aReferenceDate.getTime();
        
        for (i=0; i<c; ++i)
        {
            TestEntity aEntity = new TestEntity ();

            aEntity.IntValue = i;

            if ((i % 2) == 0)
                aEntity.BooleanValue = true;
            else
                aEntity.BooleanValue = false;

            aEntity.DateValue = new Date (nReferenceDate+i);
            
            if (i < 10)
            	aEntity.StringValue = GenericDbTest.STRINGVALUE_TEST_ENTITY_A;
            else
            	aEntity.StringValue = GenericDbTest.STRINGVALUE_TEST_ENTITY_B;
            aEntity.StringValue += i;

            lEntities[i] = aEntity;
        }

        iDb.storeEntities(lEntities);
        return iDb;
    }

    //--------------------------------------------------------------------------
    private int m_nDbEnv = 0;

    //--------------------------------------------------------------------------
    private DbEnvProvider m_aDbEnv = null;
    
    //--------------------------------------------------------------------------
    private Date m_aReferenceDate = null;
}
