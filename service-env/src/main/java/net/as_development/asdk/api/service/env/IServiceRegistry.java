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

import net.as_development.asdk.api.service.env.IServiceRegistryModule;

//=============================================================================
/** Knows the binding between interfaces and real classes.
 *
 *  An instance of this interface must be set on an service manager instance.
 *  Its used internal to map services/interfaces/classes to it's real implementation
 *  and instance. But additional to a simple IServiceRegistryModule these IServiceRegistry
 *  can be used to register different modules inside.
 *  
 *  What this good for ?
 *  
 *  Instead of writing one big registry module where all classes are registered
 *  you can write several of them. E.g. every gwt module can have it's own one.
 *  At the end you decide which modules will be registered within the global registry.
 *
 *  <code>
 *  ...
 *  IServiceRegistryModule iModule_01 = UIServiceModule.get   ();
 *  IServiceRegistryModule iModule_02 = CoreServiceModule.get ();
 *  ...
 *  IServiceRegistry iRegistry = new ServiceRegistry ();
 *  iRegistry.registerModule (iModule_01);
 *  iRegistry.registerModule (iModule_02);
 *  ...
 *  IServiceEnv iSmgr = ServiceEnv.get ();
 *  iSmgr.setServiceRegistry (iRegistry);
 *  ...  
 *  </code>
 */
public interface IServiceRegistry extends IServiceRegistryModule
{
    //--------------------------------------------------------------------------
    /** register a new module within this service registry.
     *
     *  @param	iModule [IN]
     *  		the new module for registration.
     */
    public void registerModule (final IServiceRegistryModule iModule)
        throws Exception;
}
