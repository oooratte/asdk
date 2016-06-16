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
package net.as_development.asdk.api.db;

import java.util.List;


//=============================================================================
/** Can be used for setup/tear down of the DB server.
 */
public interface IDBServer
{
    //-------------------------------------------------------------------------
	public void setServerConnection (IPersistenceUnit iData)
		throws Exception;
	
    //-------------------------------------------------------------------------
	/** register the given set of persistence units within this server instance.
	 *  Must be called BEFORE createSchema () will be called !
	 * 
	 *	@param	lPUs [IN]
	 *			the list of persistence units for registration.
	 */
	public void registerPersistenceUnit (IPersistenceUnit... lPUs)
		throws Exception;
	
    //-------------------------------------------------------------------------
	public void registerPersistenceUnit (List< IPersistenceUnit > lPUs)
		throws Exception;
	
    //-------------------------------------------------------------------------
	/** register the given module within this server instance.
	 *  Must be called BEFORE createSchema () will be called !
	 * 
	 *  @param	lModules [IN]
	 *  		the set of modules to be registered.
	 */
	public void registerPersistenceRegistryModule (IPersistenceUnitRegistry... lModules)
		throws Exception;
	
    //-------------------------------------------------------------------------
	public void registerPersistenceRegistryModule (List< IPersistenceUnitRegistry > lModules)
		throws Exception;
	
    //-------------------------------------------------------------------------
	/** initialize the runtime.
	 *  E.g. the global DBPool will be filled with valid data so
	 *  YOU can use it .-)
	 * 
	 *  @throws Exception if initialization failed by any reason.
	 */
	public void initRuntime ()
		throws Exception;
	
    //-------------------------------------------------------------------------
	/** create all schema within this server.
	 *  Schema will be defined by all persistence units registered before ...
	 *  
	 *  Note	this will remove ALL schema/data already existing within this server instance !
	 *  
	 *  @throws Exception if (at least one) schema could not be created successfully.
	 */
	public void createSchema ()
		throws Exception;
}
