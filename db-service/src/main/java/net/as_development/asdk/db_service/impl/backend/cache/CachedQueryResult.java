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
import java.util.List;
import java.util.Vector;

//==============================================================================
/** Implements an object which can be used to cache complete query results.
 * 
 *  The problem.
 *  
 *  If we cache real values we must support updates of cached queries in case any
 *  entity which can be part of a query result will be removed or updated.
 *  Thats nearly impossible.
 *  
 *  The solution.
 *  
 *  We do not cache real values here ... instead we cache the primary keys
 *  of all result entities only. Those list of ID's can be used then to get the real
 *  cache entities and values. Of course missing entities must be handled gracefully then.
 */
public class CachedQueryResult implements Serializable
{
    //--------------------------------------------------------------------------
    private static final long serialVersionUID = 9086946900425793242L;

    //--------------------------------------------------------------------------
    public CachedQueryResult ()
    {}
    
    //--------------------------------------------------------------------------
    public void clear ()
        throws Exception
    {
        mem_IDs ().clear ();
        m_sNextToken = null;
    }
    
    //--------------------------------------------------------------------------
    public void putID (String sID)
        throws Exception
    {
        List< String > lIDs = mem_IDs ();
        lIDs.add(sID);
    }
    
    //--------------------------------------------------------------------------
    public List< String > getIDs ()
        throws Exception
    {
        return mem_IDs ();
    }
    
    //--------------------------------------------------------------------------
    public void setNextToken (String sToken)
        throws Exception
    {
        m_sNextToken = sToken;
    }
    
    //--------------------------------------------------------------------------
    public String getNextToken ()
        throws Exception
    {
        return m_sNextToken;
    }
    
    //--------------------------------------------------------------------------
    private List< String > mem_IDs ()
        throws Exception
    {
        if (m_lIDs == null)
            m_lIDs = new Vector< String >(10);
        return m_lIDs;
    }
    
    //--------------------------------------------------------------------------
    private List< String > m_lIDs = null;
    
    //--------------------------------------------------------------------------
    private String m_sNextToken = null;
}
