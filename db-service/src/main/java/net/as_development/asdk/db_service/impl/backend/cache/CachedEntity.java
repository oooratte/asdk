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
package net.as_development.asdk.db_service.impl.backend.cache;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.as_development.asdk.db_service.impl.Row;


//==============================================================================
/** Implements an object which can be used to cache one entity and it's values
 *  within the DB cache.
 */
public class CachedEntity implements Serializable
{
    //--------------------------------------------------------------------------
    private static final long serialVersionUID = -4450896134461535255L;

    //--------------------------------------------------------------------------
    public CachedEntity ()
    {}
    
    //--------------------------------------------------------------------------
    /** take over all values describing the real entity (not it's meta data!)
     * 
     *  @param  aRow [IN]
     *          contain all values to be cached here.
     */
    public void fromRow(Row aRow)
        throws Exception
    {
        Iterator< String >    pColumns = aRow.listColumns();
        Map< String, Object > lCache   = mem_Cache ();
        
        while (pColumns.hasNext())
        {
            String sColumn        = pColumns.next();
            Object aOriginalValue = aRow.getColumnValue(sColumn);
            Object aCacheValue    = CachedEntity.impl_copyValue (aOriginalValue);
            
            lCache.put(sColumn, aCacheValue);
        }
    }
    
    //--------------------------------------------------------------------------
    /** export all values (no meta data) from this cache item to the given row object.
     * 
     *  @param  aRow [IN, OUT]
     *          contain all values after the request finished.
     */
    public void toRow(Row aRow)
        throws Exception
    {
        Iterator< String >    pColumns = aRow.listColumns();
        Map< String, Object > lCache   = mem_Cache ();
        
        while (pColumns.hasNext())
        {
            String sColumn        = pColumns.next();
            Object aCacheValue    = lCache.get(sColumn);
            Object aOriginalValue = CachedEntity.impl_copyValue(aCacheValue);
            
            aRow.setColumnValue(sColumn, aOriginalValue);
        }
    }
    
    //--------------------------------------------------------------------------
    /** Makes it easy to change strategy for copying cache values ...
     *
     *  @see    using of this method.
     *  
     *  @param  aValue [IN]
     *          the original value to be copied here.
     *          
     *  @return the copied value.
     *          Must be equals - not same .-)
     * 
     *  @todo   Think about making deep copies of values here.
     *  
     *          If outside code calls fromRow() and CacheEntity.serialize()
     *          without the chance somewhere else change the original row object
     *          in between flat copies are enough.
     *          
     *          If serialization is done later (where row object might was changed already
     *          we need deep copies. Otherwise we serialize a different content then
     *          it was intended.
     */
    private static Object impl_copyValue (Object aValue)
        throws Exception
    {
        // current strategy: flat copy .-)
        return aValue;
    }
    
    //--------------------------------------------------------------------------
    private Map< String, Object > mem_Cache ()
        throws Exception
    {
        if (m_lCache == null)
            m_lCache = new HashMap< String, Object >(10);
        return m_lCache;
    }
    
    //--------------------------------------------------------------------------
    private Map< String, Object > m_lCache = null;
}
