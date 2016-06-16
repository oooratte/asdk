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
    public static final String FLAG_IS_ADMINISTRATIVE = "flag.is-administrative";
    
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
