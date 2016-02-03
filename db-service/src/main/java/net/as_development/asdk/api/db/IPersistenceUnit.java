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
