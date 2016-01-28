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

import java.util.List;
import java.util.Set;

//==============================================================================
/** knows all informations regarding a persistence unit.
 *  Provide read only access to those settings where the real implementation
 *  behind can also provide setter functions too.
 */
public interface IPersistenceUnit
{
    //--------------------------------------------------------------------------
    /** @return current name of these persistence unit.
     */
    public String getName ()
        throws Exception;

    //--------------------------------------------------------------------------
    /** @return class name of provider implementation.
     */
    public String getProvider ()
        throws Exception;
    
    //--------------------------------------------------------------------------
    public String getUser ()
        throws Exception;
    
    //--------------------------------------------------------------------------
    public String getPassword ()
        throws Exception;
    
    //--------------------------------------------------------------------------
    /** @return list of all entities registered for these unit.
     *
     *  @note   returned list wont be NULL ... but it can be empty.
     */
    public List< String > getEntities ()
        throws Exception;

    //--------------------------------------------------------------------------
    /** @return value of requested property.
     *
     *  @note   if property is unknown an empty string will be returned.
     */
    public String getProperty (String sProperty)
        throws Exception;

    //--------------------------------------------------------------------------
    public boolean getBooleanProperty (String sProperty)
    	throws Exception;

    //--------------------------------------------------------------------------
    /** @return set of all property names.
     *
     *  @note   set wont be null ... but can be empty.
     */
    public Set< String > getPropertNames ()
        throws Exception;
}
