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
/** provides functionality around user management
 */
public interface IDBUser
{
    //-------------------------------------------------------------------------
    /** create new user.
     *
     *  @param  sName [IN]
     *          name of the user.
     *
     *  @param  sPassword [IN]
     *          password of the user.
     *
     *  @param  bAdministrativeRights [IN]
     *          enable/disable administrative rights.
     *
     *  @param  lSchemas [IN]
     *          register user for the list of given DB schemas/entitiues.
     *
     *  @throws Exception is creating the user was not successfully
     *  		or user with same credentials already exists.
     */
    public void createUser (final String    sName                ,
				    		final String    sPassword            ,
				    		final boolean   bAdministrativeRights,
				    		final String... lSchemas             )
            throws Exception;

    //-------------------------------------------------------------------------
    /** remove an existing user.
     *  Does nothing if such user do not exists ! 
     *
     *  @param  sName [IN]
     *          name of the user.
     *
     *  @throws Exception is removing the user was not successfully.
     */
    public void removeUser (final String sName)
    	throws Exception;
}
