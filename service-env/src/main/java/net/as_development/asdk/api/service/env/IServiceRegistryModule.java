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
package net.as_development.asdk.api.service.env;

import java.util.List;

import net.as_development.asdk.api.service.env.ServiceDescriptor;

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
     *  Note   Those list wont be null - but can be empty.
     */
    public List< String > listMetaServices ()
        throws Exception;
    
    //--------------------------------------------------------------------------
    /** @return a list of service names where this module can map a real implementation.
     *  Note   Those list wont be null - but can be empty.
     */
    public List< String > listServices ()
        throws Exception;
    
    //--------------------------------------------------------------------------
    /** @return	a meta object which describe the asked service more in detail.
     *
     * 	Note	return value will be null in case service is not registered here.
     *
     *  @param	aService [IN]
     *  		the service those meta are asked for.
     */
    public ServiceDescriptor getServiceMeta (final Class< ? > aService)
        throws Exception;

    //--------------------------------------------------------------------------
    /** @return a meta object which describe the asked service more in detail.
     *
     *  Note   return value will be null in case service is not registered here.
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
