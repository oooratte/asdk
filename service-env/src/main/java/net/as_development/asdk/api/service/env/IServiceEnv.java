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

import net.as_development.asdk.api.service.env.IServiceRegistry;

//==============================================================================
/** Implements central factory where you can get instances for interfaces.
*  Use such service environment as global instance.
*
*  <code>
*  // define service mappings before you call createInstance () first time ...
*  IServiceEnv iSmgr = ServiceEnv.get ();
*  iSmgr.setServiceRegistry (new YourServiceRegistryImpl ());
*
*  // create a service
*  IFoo iFoo = iSmgr.createInstance (IFoo.class);
*  </code>
*
*  Internal those service manager provides a full edge DI (dependency injection)
*  framework ... so the returned instance will be already full initialized with
*  all depending services/interfaces/objects it needs to work.
*/
public interface IServiceEnv
{
  //--------------------------------------------------------------------------
  /** Provides access to the underlying service registry where all
   *  service implementations are	bound to it's interface counter part.
   *
   *  Has to be called and initialized BEFORE anyone calls createInstance ()
   *  on these IServiceEnv instance.
   *
   *  It's a kind of bootstrap a dependency injection framework.
   *  Those service manager needs a service registry to work.
   *  So bootstrap code must be the following one:
   *  
   *  <code>
   *  IServiceEnv      iManager  = ServiceEnv.get ();
   *  IServiceRegistry iRegistry = iManager.getServiceRegistry ();
   *  
   *  iRegistry.registerModule (new CorModule ());
   *  iRegistry.registerModule (new UiModule  ());
   *  ...
   *  </code>
   *
   *  @return	the registry where service bindings are available.
   */
  public IServiceRegistry getServiceRegistry ()
      throws Exception;

  //--------------------------------------------------------------------------
  /** Creates an instance implementing the specified interface.
   *
   *  An internal lookup into the @link{ServiceRegistry} will be used
   *  to retrieve a real implementation class for the given interface.
   *  If lookup will be successfully such impl. class will be created
   *  and returned.
   *
   *  The returned object will be full initialized already. DI (dependency injection)
   *  was used doing such stuff .-)
   *
   *  @param  <T>
   *          defines the return type of this method so an explicit cast isn't necessary.
   *
   *  @param  aService
   *          the service where an implementation is searched for.
   *
   *  @throws Exception if service registry lookup or service creation failed.
   */
  public < T > T getService (final Class< ? > aService)
      throws Exception;
  
  //--------------------------------------------------------------------------
  /** same then getService(Class) ... but using a string parameter instead.
   * 
   *  Makes it possible to create service on demand where those names are e.g.
   *  read from a configuration. In such cases no class object will be available.
   *  
   *  @param  <T>
   *          defines the return type of this method so an explicit cast isn't necessary.
   *
   *  @param  sService
   *          the service where an implementation is searched for.
   *
   *  @throws Exception if service registry lookup or service creation failed.
   */
  public < T > T getService (final String sService)
      throws Exception;
}
