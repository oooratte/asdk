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
package test.net.as_development.asdk.db_service.impl.server;

import org.junit.Ignore;
import org.junit.Test;

import net.as_development.asdk.api.db.IPersistenceUnitRegistry;
import net.as_development.asdk.db_service.impl.DBPool;
import net.as_development.asdk.db_service.impl.PersistenceUnit;
import net.as_development.asdk.db_service.impl.PersistenceUnitConst;
import net.as_development.asdk.db_service.impl.PersistenceUnitRegistry;
import net.as_development.asdk.db_service.impl.backend.creator.DBCreator;
import net.as_development.asdk.db_service.impl.sql.MysqlProvider;
import test.net.as_development.asdk.db_service.test.entities.TestEntity;

//=============================================================================
@Ignore
public class DBCreatorTest
{
	//-------------------------------------------------------------------------
	private static final String DB_SCHEMA   = "test-db";
	private static final String DB_USER     = "root";
	private static final String DB_PASSWORD = "secret";
	private static final String PU_NAME     = DB_SCHEMA+"-pu";
	
	//-------------------------------------------------------------------------
	@Test
	public void test()
		throws Exception
	{
		final IPersistenceUnitRegistry iPURegistry = PersistenceUnitRegistry.get();
		iPURegistry.addPersistenceUnits(impl_getAdminPU ());
		
		final DBPool    aDBPool  = new DBPool    ();
		final DBCreator aCreator = new DBCreator ();
	
		aDBPool .setPersistenceUnitRegistry(iPURegistry         );
		aCreator.setDBPool                 (aDBPool             );
		aCreator.setAdminCredentials       (DB_USER, DB_PASSWORD);
		aCreator.setPersistenceUnits       (PU_NAME             );
		
		aCreator.doOperation(DBCreator.EOperation.E_CREATE);
		aCreator.doOperation(DBCreator.EOperation.E_REMOVE);
	}

	//-------------------------------------------------------------------------
	private PersistenceUnit impl_getAdminPU ()
		throws Exception
	{
		final PersistenceUnit aPU = new PersistenceUnit ();
		aPU.setName    (PU_NAME);
		aPU.setProvider(MysqlProvider.class.getName());
		aPU.addEntity  (TestEntity   .class.getName());
		aPU.setProperty(PersistenceUnitConst.DB_SCHEMA         , DB_SCHEMA               );
		aPU.setProperty(PersistenceUnitConst.JDBC_DRIVER       , "com.mysql.jdbc.Driver" );
		aPU.setProperty(PersistenceUnitConst.JDBC_CONNECTIONURL, "jdbc:mysql://localhost");

		return aPU;
	}
}
