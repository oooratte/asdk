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
package net.as_development.asdk.api.db;


//==============================================================================
/** Obtaining an instance of type IDB can be tricky ... e.g. in case
 *  performance aspects like multiplexing data bases or tables should be used
 *  for performance reasons. Creating such IDB instances normally by calling new()
 *  wont work then. Only a specialized instance can know (if it's configured to
 *  know such things) how and if a new instance must be created.
 */
public interface IDBPool
{
    //--------------------------------------------------------------------------
    /** return a new created/reseted or reused IDB instance from the pool
     * which is bound to a persistence unit matching the given name.
     *
     *  @param  sPersistenceUnit [IN]
     *          name of the persistence unit (configuration) where the searched
     *          DB instance is bound to.
     *
     *  @return the right DB instance.
     */
    public IDB getDbForPersistenceUnit (String sPersistenceUnit)
        throws Exception;

    //--------------------------------------------------------------------------
    /** load the specified persistence unit from the persistence.xml file
     *  and register those unit inside this pool.
     * 
     *  @param  sName [IN]
     *          the name of the persistence unit.
     *          
     *  @throws an exception if such persistence unit couldn't
     *  		be registered successfully.
     */
    public void registerPersistenceUnit (String sName)
    	throws Exception;
    
    //--------------------------------------------------------------------------
    /** register new persistence unit inside this pool.
     * 
     *  @param  aPersistenceUnit [IN]
     *          the new persistence unit for this pool.
     *          
     *  @throws an exception if such persistence unit couldn't
     *  		be registered successfully.
     */
    public void registerPersistenceUnit (IPersistenceUnit aUnit)
        throws Exception;
}
