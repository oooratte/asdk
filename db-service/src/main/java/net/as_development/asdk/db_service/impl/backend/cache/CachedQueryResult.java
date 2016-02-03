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
