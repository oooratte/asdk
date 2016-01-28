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



//==============================================================================
/**
 * 
 */
public class QueryPartValue
{
    //-------------------------------------------------------------------------
	public QueryPartValue ()
	{
	}
	
    //-------------------------------------------------------------------------
	public void setPart (QueryPart aPart)
	    throws Exception
	{
	    m_aPart = aPart;
	}
	
    //-------------------------------------------------------------------------
    public QueryPart getPart ()
        throws Exception
    {
        return m_aPart;
    }
    
    //-------------------------------------------------------------------------
    public void setValue(Object aValue)
        throws Exception
    {
        m_aValue = aValue;
    }
    
    //-------------------------------------------------------------------------
    public Object getValue()
        throws Exception
    {
        return m_aValue;
    }
    
    //-------------------------------------------------------------------------
	private QueryPart m_aPart = null;
	
    //-------------------------------------------------------------------------
	private Object m_aValue = null;
}