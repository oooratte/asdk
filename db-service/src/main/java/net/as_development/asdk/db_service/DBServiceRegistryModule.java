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
package net.as_development.asdk.db_service;

import java.util.List;
import java.util.Vector;

import net.as_development.asdk.api.db.IDBPool;
import net.as_development.asdk.api.db.IDBServer;
import net.as_development.asdk.db_service.impl.DBPool;
import net.as_development.asdk.db_service.impl.backend.cache.CacheProvider;
import net.as_development.asdk.db_service.impl.backend.mongodb.MongoDbProvider;
import net.as_development.asdk.db_service.impl.server.DBServer;
import net.as_development.asdk.db_service.impl.simpledb.SimpleDbProvider;
import net.as_development.asdk.db_service.impl.sql.SqlProvider;
import net.as_development.asdk.service.env.impl.ServiceRegistryModule;

import org.apache.commons.lang3.StringUtils;

//==============================================================================
/** The service registry module for the db-service module.
 */
public class DBServiceRegistryModule extends ServiceRegistryModule
{
    //--------------------------------------------------------------------------
	public DBServiceRegistryModule ()
		throws Exception
	{
		markServiceSingleton(IDBPool.class);
	}
	
    //--------------------------------------------------------------------------
    @Override
    public List< String > listServices()
        throws Exception
    {
        List< String > lServices = new Vector< String >(10);
        
        // general
        lServices.add(IDBPool.class.getName   ());
        lServices.add(IDBServer.class.getName ());
        
        // back ends
        lServices.add(CacheProvider.class.getName    ());
        lServices.add(SqlProvider.class.getName      ());
        lServices.add(SimpleDbProvider.class.getName ());
        lServices.add(MongoDbProvider.class.getName  ());
        
        return lServices;
    }

    //--------------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    @Override
    public < T > T mapServiceToImplementation(String sService)
        throws Exception
    {
        Object aService = null;
        
        if (StringUtils.equals(sService, IDBPool.class.getName ()))
            aService = new DBPool ();
        else
        if (StringUtils.equals(sService, IDBServer.class.getName ()))
            aService = new DBServer ();
        else
        if (
        	(StringUtils.equals(sService, CacheProvider.class.getName    ())) ||
        	(StringUtils.equals(sService, SqlProvider.class.getName      ())) ||
        	(StringUtils.equals(sService, SimpleDbProvider.class.getName ())) ||
        	(StringUtils.equals(sService, MongoDbProvider.class.getName  ()))
           )
            aService = Class.forName (sService).newInstance();
        
        return (T) aService;
    }
}
