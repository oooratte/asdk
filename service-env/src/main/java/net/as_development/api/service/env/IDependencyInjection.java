/**
 * Copyright 2012 Andreas Schlüns - as-development.net
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.as_development.api.service.env;

//=============================================================================
/**
*/
public interface IDependencyInjection
{
  //--------------------------------------------------------------------------
	/** is asked by the ServiceEnv to know if those service needs injections
	 *  or not.
	 *
	 *  If those list will be null or empty ... nothing will happen.
	 *
	 *  @return	a list of classes where this service needs suitable implementations
	 *          injected.
	 */
	public Class< ? >[] getRequiredInjections ()
		throws Exception;

  //--------------------------------------------------------------------------
	/** inject a resource to this service.
	 *
	 * 	@param	aService [IN]
	 * 			the service class the injection is for.
	 *
	 *  @param	aImplementation [IN]
	 *  		the real instance for that service class.
	 */
	public void inject (final Class< ? > aService       ,
					    final Object     aImplementation)
		throws Exception;
}
