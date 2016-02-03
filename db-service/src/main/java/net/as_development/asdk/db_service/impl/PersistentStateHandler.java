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
