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

import net.as_development.asdk.db_service.EntityBase;

//==============================================================================
public class PersistentStateHandler implements Cloneable
{
    //--------------------------------------------------------------------------
	public PersistentStateHandler ()
	{}
	
    //--------------------------------------------------------------------------
	public PersistentStateHandler (EntityBase aEntity)
		throws Exception
	{
		setEntity (aEntity);
	}
	
    //--------------------------------------------------------------------------
	public void setEntity (EntityBase aEntity)
		throws Exception
	{
		m_aEntity = aEntity;
	}
	
    //--------------------------------------------------------------------------
	public void setPersistent ()
		throws Exception
	{
		m_bPersistent = true;
		if (m_aEntity != null)
			m_aEntity.setPersistent(true);
	}
	
    //--------------------------------------------------------------------------
	public void setTransient ()
		throws Exception
	{
		m_bPersistent = false;
		if (m_aEntity != null)
			m_aEntity.setPersistent(false);
	}
	
    //--------------------------------------------------------------------------
	public boolean isPersistent ()
		throws Exception
	{
		return m_bPersistent;
	}

    //--------------------------------------------------------------------------
    @Override
	protected Object clone()
    	throws CloneNotSupportedException
	{
    	PersistentStateHandler aClone = new PersistentStateHandler ();
    	aClone.m_bPersistent = m_bPersistent;
    	/*
    	if (m_aEntity != null)
    		aClone.m_aEntity = (EntityBase) m_aEntity.clone ();
    	*/    		
    	return aClone;
	}

	//--------------------------------------------------------------------------
	private EntityBase m_aEntity = null;
	
    //--------------------------------------------------------------------------
	private boolean m_bPersistent = false;
}
