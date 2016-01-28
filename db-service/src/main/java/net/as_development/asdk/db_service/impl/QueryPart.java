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


//==============================================================================
/**
 * 
 */
public class QueryPart
{
    //-------------------------------------------------------------------------
	public QueryPart ()
	{
	}
	
    //-------------------------------------------------------------------------
	public void setFinal ()
	{
	    m_bFinal = true;
	}
	
    //-------------------------------------------------------------------------
	public void setLogicBinding (EQueryPartBinding eBinding)
		throws Exception
	{
	    if (m_bFinal)
	        throw new RuntimeException ("QueryPart is final .. but you call set operation on it.");
		m_eBinding = eBinding;
	}
	
    //-------------------------------------------------------------------------
	public void setOperation (EQueryPartOperation eOperation)
		throws Exception
	{
        if (m_bFinal)
            throw new RuntimeException ("QueryPart is final .. but you call set operation on it.");
		m_eOperation = eOperation;
	}
	
    //-------------------------------------------------------------------------
	public void setAttribute (String sAttribute)
		throws Exception
	{
        if (m_bFinal)
            throw new RuntimeException ("QueryPart is final .. but you call set operation on it.");
		m_sAttribute = sAttribute;
	}
	
    //-------------------------------------------------------------------------
	public void setColumn (String sColumn)
		throws Exception
	{
        if (m_bFinal)
            throw new RuntimeException ("QueryPart is final .. but you call set operation on it.");
		m_sColumn = sColumn;
	}
	
    //-------------------------------------------------------------------------
    public boolean isFinal ()
    {
        return m_bFinal;
    }
    
    //-------------------------------------------------------------------------
    public EQueryPartBinding getLogicBinding ()
        throws Exception
    {
        return m_eBinding;
    }
    
    //-------------------------------------------------------------------------
    public EQueryPartOperation getOperation ()
        throws Exception
    {
        return m_eOperation;
    }
    
    //-------------------------------------------------------------------------
    public String getAttribute ()
        throws Exception
    {
        return m_sAttribute;
    }
    
    //-------------------------------------------------------------------------
    public String getColumn ()
        throws Exception
    {
        return m_sColumn;
    }
    
    //-------------------------------------------------------------------------
	public boolean hasSettings (EQueryPartBinding   eBinding  ,
	                            EQueryPartOperation eOperation,
	                            String              sAttribute)
		throws Exception
	{
		return (
				 (eBinding   == m_eBinding                    ) &&
				 (eOperation == m_eOperation                  ) &&
				 (StringUtils.equals(sAttribute, m_sAttribute))
			   );
	}
	
    //-------------------------------------------------------------------------
	private boolean m_bFinal = false;
	
    //-------------------------------------------------------------------------
	private EQueryPartBinding m_eBinding = EQueryPartBinding.E_AND;
	
    //-------------------------------------------------------------------------
	private EQueryPartOperation m_eOperation = EQueryPartOperation.E_MATCH;
	
    //-------------------------------------------------------------------------
	private String m_sAttribute = null;
	
    //-------------------------------------------------------------------------
	private String m_sColumn = null;
}