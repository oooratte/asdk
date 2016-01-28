/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
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
