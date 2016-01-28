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
