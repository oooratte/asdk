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
