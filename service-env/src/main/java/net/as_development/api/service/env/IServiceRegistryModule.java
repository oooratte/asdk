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
package net.as_development.api.service.env;

import java.util.List;

import net.as_development.api.service.env.ServiceDescriptor;

//=============================================================================
/** Knows the binding between interfaces and real classes.
 *
 *  It has to be used to instanciate real implementations for requested services.
 *  How this will be done depends from the real implementation of this interface.
 *  
 *  E.g. rich platforms use reflection to create new service instances.
 *  E.g. simple platforms use hard-coded stuff like new Xyz() instead.
 */
public interface IServiceRegistryModule
{
    //--------------------------------------------------------------------------
    /** @return a list of service names where this module knows special meta data for.
     *  @note   Those list wont be null - but can be empty.
     */
    public List< String > listMetaServices ()
        throws Exception;
    
    //--------------------------------------------------------------------------
    /** @return a list of service names where this module can map a real implementation.
     *  @note   Those list wont be null - but can be empty.
     */
    public List< String > listServices ()
        throws Exception;
    
    //--------------------------------------------------------------------------
    /** @return	a meta object which describe the asked service more in detail.
     *
     * 	@note	return value will be null in case service is not registered here.
     *
     *  @param	aService [IN]
     *  		the service those meta are asked for.
     */
    public ServiceDescriptor getServiceMeta (final Class< ? > aService)
        throws Exception;

    //--------------------------------------------------------------------------
    /** @return a meta object which describe the asked service more in detail.
     *
     *  @note   return value will be null in case service is not registered here.
     *
     *  @param  sService [IN]
     *          the full qualified service name where meta data are asked for.
     */
    public ServiceDescriptor getServiceMeta (final String sService)
        throws Exception;
    
    //--------------------------------------------------------------------------
    /** maps the given service interface to a real implementation.
     *
     *  @param	aService [IN]
     *  		the service (interface) type.
     *
     *  @return	the mapped service object.
     */
    public < T > T mapServiceToImplementation (final Class< ? > aService)
        throws Exception;
    
    //--------------------------------------------------------------------------
    /** maps the given service interface to a real implementation.
     *
     *  @param  sService [IN]
     *          the full qualified service (interface) name.
     *
     *  @return the mapped service object.
     */
    public < T > T mapServiceToImplementation (final String sService)
        throws Exception;
}
