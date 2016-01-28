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
package net.as_development.asdk.db_service.impl;

//==============================================================================
/** Use such constant keys to retrieve properties from a PersistenceUnit.
 */
public class PersistenceUnitConst
{
    //--------------------------------------------------------------------------
    /// define the AWS access key used to connect to the SimpleDB server
    public static final String SIMPLEDB_ACCESSKEY = "simpledb.key.access";
    
    //--------------------------------------------------------------------------
    /// define the AWS secret key used to connect to the SimpleDB server
    public static final String SIMPLEDB_SECRETKEY = "simpledb.key.secret";
    
    //--------------------------------------------------------------------------
    /// define the AWS region where this application should use it's SimpleDB instance
    public static final String SIMPLEDB_REGION = "simpledb.region";
    
    //--------------------------------------------------------------------------
    /// knows the JDBC driver class name for 'standard JDBC connections'
    public static final String JDBC_DRIVER = "jdbc.driver";

    //--------------------------------------------------------------------------
    /// the JDBC connection URL for connecting to a DB server
    public static final String JDBC_CONNECTIONURL = "jdbc.connection.url";
    
    //--------------------------------------------------------------------------
    /// define the DB schema used by all entities bound to one persistence unit
    public static final String DB_SCHEMA = "db.schema";
    
    //--------------------------------------------------------------------------
    /// knows the user used to establish a DB connection
    public static final String DB_USER = "db.user";

    //--------------------------------------------------------------------------
    /// contains the password for DB connections
    public static final String DB_PASSWORD = "db.password";

    //--------------------------------------------------------------------------
    /// define the constraint to check max length for DB identifier
    public static final String CONSTRAINT_MAX_IDENTIFIER_LENGTH = "constraint.max_identifier_length";
    
    //--------------------------------------------------------------------------
    /// define the constraint to check max length for string values
    public static final String CONSTRAINT_MAX_STRING_LENGTH = "constraint.max_string_length";
    
    //--------------------------------------------------------------------------
    /// enable/disable the feature where entities wont be removed real
    /// ... will be marked as removed first ...
    /// ... and might be removed real later .-)
    public static final String FEATURE_SPECIAL_REMOVE = "feature.special_remove";
}
