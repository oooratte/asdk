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
package net.as_development.asdk.db_service.impl;

import java.util.ArrayList;

//==============================================================================
/** Extends the original ArrayList implementation in a way so it resize the
 *  array automatic on calling indexed set/get methods. There is no IndexOutOfBoundsException
 *  any longer .-)
 *  But note: missing array entries (that is if you dont fill the array complete)
 *  will be set to null ... so you might have to deal with those null values later.
 */
public class DynamicArrayList< E > extends ArrayList< E >
{
    //--------------------------------------------------------------------------
    private static final long serialVersionUID = -7864713642114244197L;

    //--------------------------------------------------------------------------
    /** Create new array list with size of 0.
     */
    public DynamicArrayList ()
    {
        super ();
    }
    
    //--------------------------------------------------------------------------
    /** Create new array list with size of X.
     * 
     *  @param  nSize [IN]
     *          the initial size.
     */
    public DynamicArrayList (int nSize)
    {
        this ();
        impl_ensureCapacity (nSize);
    }
    
    //--------------------------------------------------------------------------
    /** override the original set so the underlying array is resized automatic and
     *  the new item is placed at the specified position.
     *  NO IndexOutOfBoundsException is thrown .-)
     * 
     *  @param  nIndex [IN]
     *          the position within the array where the value has to be set.
     *          
     *  @param  aValue [IN]
     *          the value to be set at the specified position.
     */
    @Override
    public E set (int nIndex,
                  E   aValue)
    {
        impl_ensureCapacity (nIndex+1);
        return super.set(nIndex, aValue);
    }
    
    //--------------------------------------------------------------------------
    /** do both:
     *  - resizing the array
     *  - filling the array with NULL items
     */
    private void impl_ensureCapacity (int nCapacity)
    {
        if (nCapacity < 0)
            throw new IllegalArgumentException ("Capacity less then 0 not allowed.");
        
        int nOldSize = size ();
        int nNewSize = nCapacity;
        
        ensureCapacity(nCapacity);
        
        for (int i=nOldSize; i<nNewSize; ++i)
            add (null);
    }
}
