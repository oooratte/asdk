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
     *  @throws Exception is creating the schema was not successfully.
     */
    public < TEntity extends IEntity > void createEntitySchema (Class< TEntity > aType)
            throws Exception;
    
    //-------------------------------------------------------------------------
    /** remove all data within DB back end related to the specified entity.
     * 
     *  @param  aType [IN]
     *          describe the entity type.
     *          
     *  @throws Exception if operation failed and entity data couldn't be removed.
     *  
     *  Note   It throws NO exception in case entity is unknown within these DB.
     */
    public < TEntity extends IEntity > void removeEntitySchema (Class< TEntity > aType)
            throws Exception;
}
