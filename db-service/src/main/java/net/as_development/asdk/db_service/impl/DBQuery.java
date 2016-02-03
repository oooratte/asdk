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

import net.as_development.asdk.api.db.BetweenQueryRange;
import net.as_development.asdk.api.db.EQueryPartBinding;
import net.as_development.asdk.api.db.EQueryPartOperation;
import net.as_development.asdk.api.db.IDBQuery;
import net.as_development.asdk.api.db.IEntity;
import net.as_development.asdk.db_service.IDBBackendQuery;

//==============================================================================
/** An implementation for the interface IDBQuery.
 *  One example:
 *  <code>
 *  	YourDbEntity aEntity = new YourDbEntity ();
 *  	IDB          iDb     = iDbPool.getDbForPersistenceUnit ();
 *
 *		// If this is called the first time (you must not track that outside !)
 *		// it creates a new query ...
 *		// If its called the second time it reuses the internal cached query object.
 *		// But it does not compile these query !
 *  	IDBQuery iQuery = iDb.prepareQuery ("YourQueryId");
 *
 *  	// Then you can define your query attributes and values.
 *  	// If you call it the first time ... you do two things at the same time:
 *  	// a ) you define the structure of the query by adding new attribute names
 *  	// b ) you define the first set of query values by setting new values
 *		//
 *  	// If you call it the second time ...
 *  	// a') we check if your attribute is still known on that query
 *  	//     - in case it's not ... an exception is thrown
 *  	//     - otherwise ...
 *  	// b') you define the set of query values by setting new values
 *  	//     for existing attributes here
 *  	iQuery.setQueryPart (0, EQueryPartBinding.E_AND, EQueryPartOperation.E_MATCH, "your_attribute", your_value);
 *
 *  	// If this is the first request for this query ...
 *  	// it will be compiled and executed then.
 *  	// If it is the second request for this query ...
 *  	// it wont be compiled ... but executed.
 *		iDb.query(..., iQuery);
 *	</code>
 */
public class DBQuery< TEntity extends IEntity > implements IDBQuery< TEntity >
													     , IDBBackendQuery
{
    //--------------------------------------------------------------------------
    public DBQuery ()
    {}

    //--------------------------------------------------------------------------
	/** bind this query instance to it's template where all meta information exists.
	 *  Those meta information knows nearly everything about the bound
	 *  entity class where this query is good for.
	 *  
	 *  @note  Those template will might be cached elsewhere and referenced by more
	 *         then one DBQuery instance. DONT use it in writable mode. Use it READONLY.
	 *         On the other side ... after query template was compiled first time
	 *         it throws exceptions in case you try to call any set method on it ...
	 *         so - try it :-)
	 *
	 *  @param aTemplate [IN]
	 *         the template where this query is bound to.         
	 */
	public void bindToQueryTemplate (DBQueryTemplate< TEntity > aTemplate)
	{
		m_aTemplate = aTemplate;
	}

    //--------------------------------------------------------------------------
	/** @return the bound query template of this query.
	 * 
	 *  Used e.g. by the DB implementation to cache or compile the query
	 *  template on the fly...
	 */
	public DBQueryTemplate< TEntity > getTemplate ()
	    throws Exception
	{
	    return m_aTemplate;
	}
	
    //--------------------------------------------------------------------------
	@Override
    public String getId()
        throws Exception
    {
        return m_aTemplate.getId();
    }
    
    //--------------------------------------------------------------------------
	@Override
	public synchronized void setQueryPart (int                 nPosition ,
	                                       EQueryPartBinding   eBinding  ,
	                                       EQueryPartOperation eOperation,
	                                       String              sAttribute,
	                                       Object              aValue    )
		throws Exception
	{
	    boolean bIsCompiled = m_aTemplate.isCompiled();
	    
		if (bIsCompiled)
		{
		    // template was still compiled and is in 'final' mode.
		    // We should check if the user of the query use same query
		    // parameter for nPosition as he used before compile.
		    
		    //  @todo enable this check for debug builds only .-)
		    
		    String                        sId    = m_aTemplate.getId();
		    DynamicArrayList< QueryPart > lParts = m_aTemplate.getQueryParts();
			if (nPosition >= lParts.size())
				throw new RuntimeException ("Wrong index ["+nPosition+"] for compiled query '"+sId+"'.");

			QueryPart aPart = lParts.get(nPosition);
			if ( ! aPart.hasSettings(eBinding, eOperation, sAttribute))
				throw new RuntimeException ("Different attribute bindings for attribute ["+nPosition+", '"+sAttribute+"'] for compiled query '"+sId+"'.");
		}
		else
		{
            m_aTemplate.setQueryPart(nPosition, eBinding, eOperation, sAttribute);
		}
		
		// @todo ifdef DEBUG ?
        if (aValue != null)
        {
            if (
                (eOperation == EQueryPartOperation.E_LIKE) &&
                ( ! (aValue instanceof String)           )
               )
                throw new IllegalArgumentException ("IDBQuery.setQueryPart () : like operation works on string values only.");
            else
            if (
                (eOperation == EQueryPartOperation.E_BETWEEN) &&
                ( ! (aValue instanceof BetweenQueryRange)   )
               )
                throw new IllegalArgumentException ("IDBQuery.setQueryPart () : between operation needs specialized value object of type BetweenQueryRange.");
        }
		
		// cache new value for this query independent from the set of meta
		// data which are hold within the template
		// @note order of values must match order of attributes within template !
		DynamicArrayList< Object > lValues = mem_Values ();
		lValues.set (nPosition, aValue);
	}
    
    //--------------------------------------------------------------------------
    @Override
    public QueryPartValue[] getQueryParts()
        throws Exception
    {
        // merge together template and values
        // Order of both (attributes and values) is important
        // and must be in sync.
        
        DynamicArrayList< QueryPart >  lParts      = m_aTemplate.getQueryParts();
        QueryPartValue[]               lValueParts = new QueryPartValue[lParts.size()];
        DynamicArrayList< Object >     lValues     = mem_Values ();
        int                            i           = 0;
        
        if (lParts.size() != lValues.size())
            throw new RuntimeException ("Race condition ? Size of part and value list is different for query '"+m_aTemplate.getId()+"'.");
        
        for (QueryPart aPart : lParts)
        {
            QueryPartValue aValuePart = new QueryPartValue ();
            Object         aValue     = lValues.get (i);
            
            aValuePart.setPart (aPart );
            aValuePart.setValue(aValue);
            
            lValueParts[i] = aValuePart;
            ++i;
        }
        
        return lValueParts;
    }

    //--------------------------------------------------------------------------
    public int getPartCount ()
    	throws Exception
    {
	    DynamicArrayList< QueryPart > lParts = m_aTemplate.getQueryParts();
	    return lParts.size();
    }
    
	//--------------------------------------------------------------------------
	private DynamicArrayList< Object > mem_Values ()
		throws Exception
	{
		if (m_lValues == null)
		    m_lValues = new DynamicArrayList< Object >();
		return m_lValues;
	}

    //--------------------------------------------------------------------------
	private DBQueryTemplate< TEntity > m_aTemplate = null;
	
    //--------------------------------------------------------------------------
	private DynamicArrayList< Object > m_lValues = null;
}
