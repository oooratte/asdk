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

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import net.as_development.asdk.db_service.EntityBase;
import net.as_development.asdk.db_service.ISqlGenerator;
import net.as_development.asdk.db_service.impl.EntityClassParserAnnotations;
import net.as_development.asdk.db_service.impl.EntityHelper;
import net.as_development.asdk.db_service.impl.EntityMetaInfo;
import net.as_development.asdk.db_service.impl.Row;
import net.as_development.asdk.db_service.impl.sql.generator.AnsiSqlGenerator;
import net.as_development.asdk.tools.reflection.ObjectManipulation;
import net.as_development.asdk.tools.test.AssertEx;
import test.net.as_development.asdk.db_service.test.entities.TestEntity;

//==============================================================================
/**
 */
@RunWith(PowerMockRunner.class)
public class AnsiSqlGeneratorTest
{
    //--------------------------------------------------------------------------
    private static final String TEST_DB_SCHEMA = "test_schema";
    
    //--------------------------------------------------------------------------
    private static final String TABLE_NAME = "\""+AnsiSqlGeneratorTest.TEST_DB_SCHEMA+"\".\""+EntityMetaInfo.PREFIX_TABLES+TestEntity.TABLE_NAME+"\"";
    
    //--------------------------------------------------------------------------
    private static final String COLUMN_CHAR          = "\""+EntityMetaInfo.PREFIX_COLUMNS+TestEntity.COLUMN_NAME_CHARVALUE         +"\" "+AnsiSqlGenerator.SQLTYPE_CHAR   ;
    private static final String COLUMN_STRING        = "\""+EntityMetaInfo.PREFIX_COLUMNS+TestEntity.COLUMN_NAME_STRINGVALUE       +"\" varchar(40)"                      ; // dynamic and not real handy as constant value .-)
    private static final String COLUMN_BOOLEAN       = "\""+EntityMetaInfo.PREFIX_COLUMNS+TestEntity.COLUMN_NAME_BOOLEANVALUE      +"\" "+AnsiSqlGenerator.SQLTYPE_BOOLEAN;
    private static final String COLUMN_SIMPLEBOOLEAN = "\""+EntityMetaInfo.PREFIX_COLUMNS+TestEntity.COLUMN_NAME_SIMPLEBOOLEANVALUE+"\" "+AnsiSqlGenerator.SQLTYPE_BOOLEAN;
    private static final String COLUMN_BYTE          = "\""+EntityMetaInfo.PREFIX_COLUMNS+TestEntity.COLUMN_NAME_BYTEVALUE         +"\" "+AnsiSqlGenerator.SQLTYPE_BYTE   ;
    private static final String COLUMN_SIMPLEBYTE    = "\""+EntityMetaInfo.PREFIX_COLUMNS+TestEntity.COLUMN_NAME_SIMPLEBYTEVALUE   +"\" "+AnsiSqlGenerator.SQLTYPE_BYTE   ;
    private static final String COLUMN_SHORT         = "\""+EntityMetaInfo.PREFIX_COLUMNS+TestEntity.COLUMN_NAME_SHORTVALUE        +"\" "+AnsiSqlGenerator.SQLTYPE_SHORT  ;
    private static final String COLUMN_SIMPLESHORT   = "\""+EntityMetaInfo.PREFIX_COLUMNS+TestEntity.COLUMN_NAME_SIMPLESHORTVALUE  +"\" "+AnsiSqlGenerator.SQLTYPE_SHORT  ;
    private static final String COLUMN_INT           = "\""+EntityMetaInfo.PREFIX_COLUMNS+TestEntity.COLUMN_NAME_INTVALUE          +"\" "+AnsiSqlGenerator.SQLTYPE_INT    ;
    private static final String COLUMN_SIMPLEINT     = "\""+EntityMetaInfo.PREFIX_COLUMNS+TestEntity.COLUMN_NAME_SIMPLEINTVALUE    +"\" "+AnsiSqlGenerator.SQLTYPE_INT    ;
    private static final String COLUMN_LONG          = "\""+EntityMetaInfo.PREFIX_COLUMNS+TestEntity.COLUMN_NAME_LONGVALUE         +"\" "+AnsiSqlGenerator.SQLTYPE_LONG   ;
    private static final String COLUMN_SIMPLELONG    = "\""+EntityMetaInfo.PREFIX_COLUMNS+TestEntity.COLUMN_NAME_SIMPLELONGVALUE   +"\" "+AnsiSqlGenerator.SQLTYPE_LONG   ;
    private static final String COLUMN_FLOAT         = "\""+EntityMetaInfo.PREFIX_COLUMNS+TestEntity.COLUMN_NAME_FLOATVALUE        +"\" "+AnsiSqlGenerator.SQLTYPE_FLOAT  ;
    private static final String COLUMN_SIMPLEFLOAT   = "\""+EntityMetaInfo.PREFIX_COLUMNS+TestEntity.COLUMN_NAME_SIMPLEFLOATVALUE  +"\" "+AnsiSqlGenerator.SQLTYPE_FLOAT  ;
    private static final String COLUMN_DOUBLE        = "\""+EntityMetaInfo.PREFIX_COLUMNS+TestEntity.COLUMN_NAME_DOUBLEVALUE       +"\" "+AnsiSqlGenerator.SQLTYPE_DOUBLE ;
    private static final String COLUMN_SIMPLEDOUBLE  = "\""+EntityMetaInfo.PREFIX_COLUMNS+TestEntity.COLUMN_NAME_SIMPLEDOUBLEVALUE +"\" "+AnsiSqlGenerator.SQLTYPE_DOUBLE ;
    private static final String COLUMN_DATE          = "\""+EntityMetaInfo.PREFIX_COLUMNS+TestEntity.COLUMN_NAME_DATEVALUE         +"\" "+AnsiSqlGenerator.SQLTYPE_DATE   ;
    
    //--------------------------------------------------------------------------
    private static boolean impl_containSqlStringThisPart (String sSql ,
                                                          String sPart)
        throws Exception
    {
        return (StringUtils.contains(sSql, sPart));
    }
    
    //--------------------------------------------------------------------------
    private static boolean impl_containTableName (String sSql)
        throws Exception
    {
        return (AnsiSqlGeneratorTest.impl_containSqlStringThisPart(sSql, AnsiSqlGeneratorTest.TABLE_NAME));
    }
    
    //--------------------------------------------------------------------------
    private static boolean impl_containSqlStringAllTestEntityColumnsAndTypes (String sSql)
        throws Exception
    {
        if ( ! (impl_containSqlStringThisPart(sSql, AnsiSqlGeneratorTest.COLUMN_CHAR         )))
            return false;
        if ( ! (impl_containSqlStringThisPart(sSql, AnsiSqlGeneratorTest.COLUMN_STRING       )))
            return false;
        if ( ! (impl_containSqlStringThisPart(sSql, AnsiSqlGeneratorTest.COLUMN_BOOLEAN      )))
            return false;
        if ( ! (impl_containSqlStringThisPart(sSql, AnsiSqlGeneratorTest.COLUMN_SIMPLEBOOLEAN)))
            return false;
        if ( ! (impl_containSqlStringThisPart(sSql, AnsiSqlGeneratorTest.COLUMN_BYTE         )))
            return false;
        if ( ! (impl_containSqlStringThisPart(sSql, AnsiSqlGeneratorTest.COLUMN_SIMPLEBYTE   )))
            return false;
        if ( ! (impl_containSqlStringThisPart(sSql, AnsiSqlGeneratorTest.COLUMN_SHORT        )))
            return false;
        if ( ! (impl_containSqlStringThisPart(sSql, AnsiSqlGeneratorTest.COLUMN_SIMPLESHORT  )))
            return false;
        if ( ! (impl_containSqlStringThisPart(sSql, AnsiSqlGeneratorTest.COLUMN_INT          )))
            return false;
        if ( ! (impl_containSqlStringThisPart(sSql, AnsiSqlGeneratorTest.COLUMN_SIMPLEINT    )))
            return false;
        if ( ! (impl_containSqlStringThisPart(sSql, AnsiSqlGeneratorTest.COLUMN_LONG         )))
            return false;
        if ( ! (impl_containSqlStringThisPart(sSql, AnsiSqlGeneratorTest.COLUMN_SIMPLELONG   )))
            return false;
        if ( ! (impl_containSqlStringThisPart(sSql, AnsiSqlGeneratorTest.COLUMN_FLOAT        )))
            return false;
        if ( ! (impl_containSqlStringThisPart(sSql, AnsiSqlGeneratorTest.COLUMN_SIMPLEFLOAT  )))
            return false;
        if ( ! (impl_containSqlStringThisPart(sSql, AnsiSqlGeneratorTest.COLUMN_DOUBLE       )))
            return false;
        if ( ! (impl_containSqlStringThisPart(sSql, AnsiSqlGeneratorTest.COLUMN_SIMPLEDOUBLE )))
            return false;
        if ( ! (impl_containSqlStringThisPart(sSql, AnsiSqlGeneratorTest.COLUMN_DATE         )))
            return false;
        
        return true;
    }
    
    //--------------------------------------------------------------------------
    private static boolean impl_containSqlStringAllTestEntityColumns (String sSql)
        throws Exception
    {
        if ( ! (impl_containSqlStringThisPart(sSql, TestEntity.COLUMN_NAME_CHARVALUE         )))
            return false;
        if ( ! (impl_containSqlStringThisPart(sSql, TestEntity.COLUMN_NAME_STRINGVALUE       )))
            return false;
        if ( ! (impl_containSqlStringThisPart(sSql, TestEntity.COLUMN_NAME_BOOLEANVALUE      )))
            return false;
        if ( ! (impl_containSqlStringThisPart(sSql, TestEntity.COLUMN_NAME_SIMPLEBOOLEANVALUE)))
            return false;
        if ( ! (impl_containSqlStringThisPart(sSql, TestEntity.COLUMN_NAME_BYTEVALUE         )))
            return false;
        if ( ! (impl_containSqlStringThisPart(sSql, TestEntity.COLUMN_NAME_SIMPLEBYTEVALUE   )))
            return false;
        if ( ! (impl_containSqlStringThisPart(sSql, TestEntity.COLUMN_NAME_SHORTVALUE        )))
            return false;
        if ( ! (impl_containSqlStringThisPart(sSql, TestEntity.COLUMN_NAME_SIMPLESHORTVALUE  )))
            return false;
        if ( ! (impl_containSqlStringThisPart(sSql, TestEntity.COLUMN_NAME_INTVALUE          )))
            return false;
        if ( ! (impl_containSqlStringThisPart(sSql, TestEntity.COLUMN_NAME_SIMPLEINTVALUE    )))
            return false;
        if ( ! (impl_containSqlStringThisPart(sSql, TestEntity.COLUMN_NAME_LONGVALUE         )))
            return false;
        if ( ! (impl_containSqlStringThisPart(sSql, TestEntity.COLUMN_NAME_SIMPLELONGVALUE   )))
            return false;
        if ( ! (impl_containSqlStringThisPart(sSql, TestEntity.COLUMN_NAME_FLOATVALUE        )))
            return false;
        if ( ! (impl_containSqlStringThisPart(sSql, TestEntity.COLUMN_NAME_SIMPLEFLOATVALUE  )))
            return false;
        if ( ! (impl_containSqlStringThisPart(sSql, TestEntity.COLUMN_NAME_DOUBLEVALUE       )))
            return false;
        if ( ! (impl_containSqlStringThisPart(sSql, TestEntity.COLUMN_NAME_SIMPLEDOUBLEVALUE )))
            return false;
        if ( ! (impl_containSqlStringThisPart(sSql, TestEntity.COLUMN_NAME_DATEVALUE         )))
            return false;
        
        return true;
    }
    
    //--------------------------------------------------------------------------
	@Test
	public void testSqlStatementCreation ()
		throws Exception
	{
	    // create the test env
		EntityBase       aEntity       = new TestEntity ();
		AnsiSqlGenerator aSqlGenerator = new AnsiSqlGenerator ();
		Row              aMeta         = impl_generateMetaRowForEntity (aEntity);
		String           sSql          = null;
		
		// code under test (with checks)
		sSql = aSqlGenerator.createSql(ISqlGenerator.EStatementType.E_CREATE_TABLE, aMeta, null);
        AssertEx.assertTrue("testSqlStatementCreation [01]", sSql.startsWith("create table "));
		AssertEx.assertTrue("testSqlStatementCreation [02]", AnsiSqlGeneratorTest.impl_containTableName(sSql));
        AssertEx.assertTrue("testSqlStatementCreation [03]", AnsiSqlGeneratorTest.impl_containSqlStringAllTestEntityColumnsAndTypes(sSql));

        sSql = aSqlGenerator.createSql(ISqlGenerator.EStatementType.E_DELETE, aMeta, null);
        AssertEx.assertTrue("testSqlStatementCreation [04]", sSql.startsWith("delete from "));
        AssertEx.assertTrue("testSqlStatementCreation [05]", AnsiSqlGeneratorTest.impl_containTableName(sSql));
        
        sSql = aSqlGenerator.createSql(ISqlGenerator.EStatementType.E_DELETE_ALL, aMeta, null);
        AssertEx.assertTrue("testSqlStatementCreation [06]", sSql.startsWith("delete from "));
        AssertEx.assertTrue("testSqlStatementCreation [07]", AnsiSqlGeneratorTest.impl_containTableName(sSql));
        
        sSql = aSqlGenerator.createSql(ISqlGenerator.EStatementType.E_INSERT, aMeta, null);
        AssertEx.assertTrue("testSqlStatementCreation [08]", sSql.startsWith("insert into "));
        AssertEx.assertTrue("testSqlStatementCreation [09]", AnsiSqlGeneratorTest.impl_containTableName(sSql));
        
        sSql = aSqlGenerator.createSql(ISqlGenerator.EStatementType.E_QUERY_BY_ID, aMeta, null);
        AssertEx.assertTrue("testSqlStatementCreation [10]", sSql.startsWith("select * from "));
        AssertEx.assertTrue("testSqlStatementCreation [11]", AnsiSqlGeneratorTest.impl_containTableName(sSql));

        sSql = aSqlGenerator.createSql(ISqlGenerator.EStatementType.E_UPDATE, aMeta, null);
        AssertEx.assertTrue("testSqlStatementCreation [12]", sSql.startsWith("update "));
        AssertEx.assertTrue("testSqlStatementCreation [13]", AnsiSqlGeneratorTest.impl_containTableName(sSql));
        AssertEx.assertTrue("testSqlStatementCreation [14]", AnsiSqlGeneratorTest.impl_containSqlStringAllTestEntityColumns(sSql));
        
/*        
        // TODO Do we real need to check all possible combinations of queries ?! :-)
        sSql = aSqlGenerator.createSql(ISqlGenerator.EStatementType.E_QUERY_BY_PROPS, aMeta, null);
        System.out.println ("### sql '"+sSql+"'");
*/        
	}

    //--------------------------------------------------------------------------
    @Test
    public void testCreatingTableNames ()
            throws Exception
    {
        // test data
        TestEntity       aEntity             = new TestEntity ();
        String           sEntitySchema       = AnsiSqlGeneratorTest.TEST_DB_SCHEMA; 
        Row              aMeta               = impl_generateMetaRowForEntity (aEntity);
        String           sEntityTable        = EntityMetaInfo.PREFIX_TABLES+aMeta.getTable();
        AnsiSqlGenerator aSqlGenerator       = new AnsiSqlGenerator ();
        String           sExpectedTableName  = null;
        String           sGeneratedTableName = null;

        // code under test
        sExpectedTableName  = AnsiSqlGenerator.ANSI_QUOTE+sEntitySchema+AnsiSqlGenerator.ANSI_QUOTE+"."+AnsiSqlGenerator.ANSI_QUOTE+sEntityTable+AnsiSqlGenerator.ANSI_QUOTE; 
        sGeneratedTableName = ObjectManipulation.callPrivateMethod(aSqlGenerator, "impl_nameTable", aMeta);
        
        // checking the results
        AssertEx.assertEquals("testCreatingTableNames [01] check with valid schema", sExpectedTableName, sGeneratedTableName);
        
        // code under test
        aMeta.getEntityMetaInfo().setSchema(null);
        sExpectedTableName  = AnsiSqlGenerator.ANSI_QUOTE+sEntityTable+AnsiSqlGenerator.ANSI_QUOTE; 
        sGeneratedTableName = ObjectManipulation.callPrivateMethod(aSqlGenerator, "impl_nameTable", aMeta);
        
        // checking the results
        AssertEx.assertEquals("testCreatingTableNames [02] check when schema NULL", sExpectedTableName, sGeneratedTableName);
        
        // code under test
        aMeta.getEntityMetaInfo().setSchema("");
        sExpectedTableName  = AnsiSqlGenerator.ANSI_QUOTE+sEntityTable+AnsiSqlGenerator.ANSI_QUOTE; 
        sGeneratedTableName = ObjectManipulation.callPrivateMethod(aSqlGenerator, "impl_nameTable", aMeta);
        
        // checking the results
        AssertEx.assertEquals("testCreatingTableNames [03] check when schema empty", sExpectedTableName, sGeneratedTableName);
    }
    
    //--------------------------------------------------------------------------
    private Row impl_generateMetaRowForEntity (EntityBase aEntity)
        throws Exception
    {
        EntityMetaInfo aMeta = new EntityMetaInfo ();

        aMeta.setClassName(aEntity.getClass().getName ());
        aMeta.setSchema   (AnsiSqlGeneratorTest.TEST_DB_SCHEMA);
        
        EntityClassParserAnnotations.parse(aMeta);

        Row aRow = EntityHelper.createRowFromEntity(aMeta, aEntity);
        return aRow;
    }
}
