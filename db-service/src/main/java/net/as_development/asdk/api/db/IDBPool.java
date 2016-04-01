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
     *  @throws Exception if such persistence unit couldn't
     *  		be registered successfully.
     */
    public void registerPersistenceUnit (String sName)
    	throws Exception;
    
    //--------------------------------------------------------------------------
    /** register new persistence unit inside this pool.
     * 
     *  @param  aUnit [IN]
     *          the new persistence unit for this pool.
     *          
     *  @throws Exception if such persistence unit couldn't
     *  		be registered successfully.
     */
    public void registerPersistenceUnit (IPersistenceUnit aUnit)
        throws Exception;
}
