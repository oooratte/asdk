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
/** Can be used to create needed DB structures.
 */
public interface IDBSchema
{
    //-------------------------------------------------------------------------
    /** create necessary structures inside DB for specified entity type.
     *  Means: it will create the table inside data base backend.
     *  
     *  It's separated because we do not wish to create such structures on demand
     *  if an entity should be made persistent. YOU should decide when it's time
     *  to do so ..
     *
     *  @param  aType [IN]
     *          describe the entity type.
     *
     *  @throws an exception is creating the schema was not successfully.
     */
    public < TEntity extends IEntity > void createEntitySchema (Class< TEntity > aType)
            throws Exception;
    
    //-------------------------------------------------------------------------
    /** remove all data within DB back end related to the specified entity.
     * 
     *  @param  aType [IN]
     *          describe the entity type.
     *          
     *  @throws an exception if operation failed and entity data couldn't be removed.
     *  
     *  @note   It throws NO exception in case entity is unknown within these DB.
     */
    public < TEntity extends IEntity > void removeEntitySchema (Class< TEntity > aType)
            throws Exception;
}
