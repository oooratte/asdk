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

import org.apache.commons.lang3.StringUtils;

import net.as_development.asdk.api.db.EQueryPartBinding;
import net.as_development.asdk.api.db.EQueryPartOperation;
import net.as_development.asdk.api.db.IDBQuery;
import net.as_development.asdk.api.db.IEntity;

//==============================================================================
/** Because IDBQuery (implemented as class DBQuery) can't be used within multi-threaded
 *  environments if it contains both (meta structure and real values) we split
 *  those functionality into two pieces:
 *  
 *  - the template part which contains all meta and static parts of a query
 *  - the value part referring the template and adding real query values only
 *  
 *  Where the template can be cached and used multi-threaded the value part
 *  must not be cached.
 */
public class DBQueryTemplate< TEntity extends IEntity >
{
    //--------------------------------------------------------------------------
	/** create new query instance and bind it to the right set of meta information.
	 *  Those meta information knows nearly everything about the bound
	 *  entity class where this query is good for.
	 *
	 *  @param	sId [IN]
	 *  		the id for this query.
	 *  		Used outside for identification and might be caching ...
	 *
	 *  @param	aMeta [IN]
	 *  		the set of meta information.
	 */
	public DBQueryTemplate (String         sId  ,
	                        EntityMetaInfo aMeta)
	{
		m_sId   = sId;
		m_aMeta = aMeta;
	}

    //--------------------------------------------------------------------------
	/** compile this query.
	 *
	 *  It can be called as often you want ... but it compiles these query one times only.
	 *  The outside code must not track if its worth or needed to call compile or not.
	 *  He can call this method - we do the right things here - thats it.
	 *
	 *  Compile means - prepare and cache anything needed for later use of this query instance.
	 */
	public synchronized void compile ()
		throws Exception
	{
		if (m_bIsCompiled)
			return;

		AttributeListMetaInfo         lAttributes = m_aMeta.getAttributes();
		DynamicArrayList< QueryPart > lParts      = mem_QueryParts ();

		if (lParts.isEmpty())
		    throw new RuntimeException ("Empty queries not supported. Please add query parts or use different API method of interface IDB to reach your goal.");
		
		for (QueryPart aPart : lParts)
		{
			String            sAttribute = aPart.getAttribute();
            AttributeMetaInfo aAttribute = lAttributes.getForAttributeName(sAttribute);
			String            sColumn    = aAttribute.getColumnName();
			
			aPart.setColumn(sColumn);
			
			// now all informations we can provide complete ...
			// set part as final (means readonly mode ... where not set will be allowed further)
			aPart.setFinal ();
		}

		m_bIsCompiled = true;
	}

    //--------------------------------------------------------------------------
	/** @see IDBQuery.setQueryPart ()
	 * 
	 *  ... but it define those part without any value.
	 *  Here we define the structure of the query ...
	 *  Values are hold in outside class DBQuery.
	 */
	public synchronized void setQueryPart (int                 nPosition ,
	                                       EQueryPartBinding   eBinding  ,
	                                       EQueryPartOperation eOperation,
	                                       String              sAttribute)
		throws Exception
	{
	    if (m_bIsCompiled)
	        throw new RuntimeException ("DBQueryTemplate already compiled ... but somewhere try to change it by calling setQueryPart().");
	    
	    // @todo ifdef DEBUG ?
		if (nPosition < 0)
			throw new IllegalArgumentException ("IDBQuery.setQueryPart () does not position < 0.");

		if (StringUtils.isEmpty(sAttribute))
			throw new IllegalArgumentException ("IDBQuery.setQueryPart () does not accept <empty> attribute name.");
		
		DynamicArrayList< QueryPart > lParts = mem_QueryParts ();
		QueryPart                     aPart  = new QueryPart ();

		aPart.setLogicBinding(eBinding  );
		aPart.setOperation   (eOperation);
		aPart.setAttribute   (sAttribute);
		
		lParts.set(nPosition, aPart);
		
		// don't set part final here ...
		// that has to be done within compile() step !
	}

    //--------------------------------------------------------------------------
	public synchronized String getId ()
		throws Exception
	{
		return m_sId;
	}

    //--------------------------------------------------------------------------
	public synchronized DynamicArrayList< QueryPart > getQueryParts ()
		throws Exception
	{
		return mem_QueryParts ();
	}

    //--------------------------------------------------------------------------
	/** @return if this query template was 'compiled' already.
	 *  What does it mean 'compiled' ? see compile() method for further informations ...
	 */
	public synchronized boolean isCompiled ()
	    throws Exception
	{
	    return m_bIsCompiled;
	}
	
	//--------------------------------------------------------------------------
	private DynamicArrayList< QueryPart > mem_QueryParts ()
		throws Exception
	{
		if (m_lQueryParts == null)
			m_lQueryParts = new DynamicArrayList< QueryPart >();
		return m_lQueryParts;
	}
	
    //--------------------------------------------------------------------------
	private EntityMetaInfo m_aMeta = null;

    //--------------------------------------------------------------------------
	private boolean m_bIsCompiled = false;

    //--------------------------------------------------------------------------
	private String m_sId = null;

    //--------------------------------------------------------------------------
	private DynamicArrayList< QueryPart > m_lQueryParts = null;
}
