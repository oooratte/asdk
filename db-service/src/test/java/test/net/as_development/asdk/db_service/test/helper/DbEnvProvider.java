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


import org.junit.Ignore;

import net.as_development.asdk.api.sql.ISqlServer;
import net.as_development.asdk.db_service.impl.PersistenceUnit;
import net.as_development.asdk.db_service.impl.PersistenceUnitConst;
import net.as_development.asdk.db_service.impl.backend.cache.CacheProviderConfigConst;
import net.as_development.asdk.db_service.impl.backend.mongodb.MongoDbProvider;
import net.as_development.asdk.db_service.impl.simpledb.AwsEndPointDefinitions;
import net.as_development.asdk.db_service.impl.simpledb.SimpleDbProvider;
import net.as_development.asdk.db_service.impl.sql.SqlProvider;
import net.as_development.asdk.sql.server.impl.EmbeddedDerbyServer;
import test.net.as_development.asdk.db_service.test.entities.TestEntity;

//==============================================================================
/**
 * @todo document me
 */
@Ignore
public class DbEnvProvider
{
    //--------------------------------------------------------------------------
    public static final int ENV_EMBEDDED_SQL     = 1;
    public static final int ENV_REMOTE_SQL       = 2;
    public static final int ENV_AMAZON_SIMPLEDB  = 3;
    public static final int ENV_MONGODB          = 4;

    //--------------------------------------------------------------------------
    /** create new instance.
     */
    public DbEnvProvider ()
    {}

    //--------------------------------------------------------------------------
    public void setUp (int nEnv)
        throws Exception
    {
        switch (nEnv)
        {
            case DbEnvProvider.ENV_EMBEDDED_SQL :
                    impl_setUpEmbeddedSql ();
                    break;

            case DbEnvProvider.ENV_REMOTE_SQL :
                    impl_setUpRemoteSql ();
                    break;

            case DbEnvProvider.ENV_AMAZON_SIMPLEDB :
                    impl_setUpAmazonSimpleDb ();
                    break;
                    
            case DbEnvProvider.ENV_MONGODB :
                    impl_setUpMongoDb ();
                    break;

            default: throw new IllegalArgumentException ("Unsupported environment id specified.");
        }
    }

    //--------------------------------------------------------------------------
    public void tearDown (int nEnv)
        throws Exception
    {
        switch (nEnv)
        {
            case DbEnvProvider.ENV_EMBEDDED_SQL :
                    impl_tearDownEmbeddedSql ();
                    break;

            case DbEnvProvider.ENV_REMOTE_SQL :
                    impl_tearDownRemoteSql ();
                    break;

            case DbEnvProvider.ENV_AMAZON_SIMPLEDB :
                    impl_tearDownAmazonSimpleDb ();
                    break;
                    
            case DbEnvProvider.ENV_MONGODB :
                    impl_tearDownMongoDb ();
                    break;

            default: throw new IllegalArgumentException ("Unsupported environment id specified.");
        }
    }

    //--------------------------------------------------------------------------
    public PersistenceUnit getPersistenceUnit ()
        throws Exception
    {
        return m_aPersistenceUnit;
    }

    //--------------------------------------------------------------------------
    private void impl_setUpEmbeddedSql ()
        throws Exception
    {
        System.out.println ("set up db env for 'embedded sql' ...");

        ISqlServer iServer = new EmbeddedDerbyServer ();

        PersistenceUnit aUnit = new PersistenceUnit ();

        aUnit.setName    ("embedded_sql"             );
        //aUnit.setMappFile("/META-INF/orm.xml"        ); // @todo make orm.xml generation more flexible .-)
        aUnit.setProvider(SqlProvider.class.getName());
        
        aUnit.addEntity(TestEntity.class.getName());

        aUnit.setProperty(PersistenceUnitConst.JDBC_DRIVER       , iServer.getDriverClass()  );
        aUnit.setProperty(PersistenceUnitConst.DB_USER           , iServer.getUser()         );
        aUnit.setProperty(PersistenceUnitConst.DB_PASSWORD       , iServer.getPassword()     );
        aUnit.setProperty(PersistenceUnitConst.JDBC_CONNECTIONURL, iServer.getConnectionUrl());
        
        aUnit.setProperty(CacheProviderConfigConst.PROP_DB_IMPLEMENTATION   , SqlProvider.class.getName()     );
//      aUnit.setProperty(CacheProviderConfigConst.PROP_CACHE_IMPLEMENTATION, MemCacheInMemory.class.getName());

        m_iSqlServer       = iServer;
        m_aPersistenceUnit = aUnit;

        System.out.println ("start server ...");
        iServer.start();
        System.out.println ("OK");
    }

    //--------------------------------------------------------------------------
    private void impl_setUpRemoteSql ()
        throws Exception
    {
        System.out.println ("set up db env for 'remote sql' (postgresql at localhost) ...");

        PersistenceUnit aUnit = new PersistenceUnit ();

        aUnit.setName    ("remote_sql"               );
        aUnit.setProvider(SqlProvider.class.getName());
        aUnit.addEntity  (TestEntity.class.getName() );

        aUnit.setProperty(PersistenceUnitConst.JDBC_DRIVER       , "org.postgresql.Driver");
        aUnit.setProperty(PersistenceUnitConst.DB_USER           , "postgres");
        aUnit.setProperty(PersistenceUnitConst.DB_PASSWORD       , "xxx");
        aUnit.setProperty(PersistenceUnitConst.JDBC_CONNECTIONURL, "jdbc:postgresql://localhost/postgres");

        m_iSqlServer       = null;
        m_aPersistenceUnit = aUnit;

        System.out.println ("OK");
    }

    //--------------------------------------------------------------------------
    private void impl_setUpAmazonSimpleDb ()
        throws Exception
    {
    	throw new UnsupportedOperationException ("fix me");
    	
//        System.out.println ("set up db env for 'Amazon SimpleDB' ...");
//
//        PersistenceUnit aUnit = new PersistenceUnit ();
//
//        aUnit.setName    ("amazon_simpledb"               );
//        aUnit.setProvider(SimpleDbProvider.class.getName());
//        aUnit.addEntity  (TestEntity.class.getName()      );
//
//        ICredentialsProvider2 iCredentials = new ScrambledCredentialsProvider ();
//        String                sAccessKey   = iCredentials.getCredential("aws.accesskey");
//        String                sSecretKey   = iCredentials.getCredential("aws.secretkey");
//        
//        aUnit.setProperty(PersistenceUnitConst.SIMPLEDB_ACCESSKEY, sAccessKey                               );
//        aUnit.setProperty(PersistenceUnitConst.SIMPLEDB_SECRETKEY, sSecretKey                               );
//        aUnit.setProperty(PersistenceUnitConst.SIMPLEDB_REGION   , AwsEndPointDefinitions.REGIONNAME_IRELAND);
//
//        m_iSqlServer       = null;
//        m_aPersistenceUnit = aUnit;
//
//        System.out.println ("OK");
    }

    //--------------------------------------------------------------------------
    private void impl_setUpMongoDb ()
        throws Exception
    {
        System.out.println ("set up db env for 'MongoDB' ...");

        PersistenceUnit aUnit = new PersistenceUnit ();

        aUnit.setName    ("mongodb"                      );
        aUnit.setProvider(MongoDbProvider.class.getName());
        aUnit.addEntity  (TestEntity.class.getName()     );

        String sServer = null;
        sServer = "192.168.1.191";
        sServer = "129.157.138.48";
        
        aUnit.setProperty(PersistenceUnitConst.DB_SCHEMA  , "db-sevice-mongodb-unit-test");
        aUnit.setProperty(MongoDbProvider.PUPROP_DB_SERVER, sServer                      );
        aUnit.setProperty(MongoDbProvider.PUPROP_DB_PORT  , "27017"                      );

        m_iSqlServer       = null;
        m_aPersistenceUnit = aUnit;

        System.out.println ("OK");
    }
    
    //--------------------------------------------------------------------------
    private void impl_tearDownEmbeddedSql ()
        throws Exception
    {
        System.out.println ("tear down db env for 'embedded sql' ...");

        ISqlServer iServer = m_iSqlServer;

        m_iSqlServer       = null;
        m_aPersistenceUnit = null;

        System.out.println ("stop server ...");
        iServer.stop();
        System.out.println ("OK");
    }

    //--------------------------------------------------------------------------
    private void impl_tearDownRemoteSql ()
        throws Exception
    {
        System.out.println ("tear down db env for 'remote SQL' ...");

        m_iSqlServer       = null;
        m_aPersistenceUnit = null;
    }

    //--------------------------------------------------------------------------
    private void impl_tearDownAmazonSimpleDb ()
        throws Exception
    {
        System.out.println ("tear down db env for 'Amazon SimpleDB' ...");

        m_iSqlServer       = null;
        m_aPersistenceUnit = null;
    }

    //--------------------------------------------------------------------------
    private void impl_tearDownMongoDb ()
        throws Exception
    {
        System.out.println ("tear down db env for 'MongoDB' ...");

        m_iSqlServer       = null;
        m_aPersistenceUnit = null;
    }
    
    //--------------------------------------------------------------------------
    private ISqlServer m_iSqlServer;

    //--------------------------------------------------------------------------
    private PersistenceUnit m_aPersistenceUnit = null;
}
