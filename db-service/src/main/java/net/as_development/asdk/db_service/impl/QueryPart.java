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