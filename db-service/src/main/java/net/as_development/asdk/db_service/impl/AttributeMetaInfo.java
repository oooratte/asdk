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

import java.lang.reflect.Field;
import org.apache.commons.lang3.StringUtils;

//==============================================================================
/**
 * TODO document me
 */
public class AttributeMetaInfo
{
    //--------------------------------------------------------------------------
    /** create new instance.
     */
    public AttributeMetaInfo ()
    {}

    //--------------------------------------------------------------------------
    public void setApiName (String sName)
        throws Exception
    {
        m_sApiName = sName;
    }

    //--------------------------------------------------------------------------
    public String getApiName ()
        throws Exception
    {
        return m_sApiName;
    }

    //--------------------------------------------------------------------------
    public void setColumnName (String sColumn)
        throws Exception
    {
        m_sColumnName = sColumn;
    }

    //--------------------------------------------------------------------------
    public String getColumnName ()
        throws Exception
    {
        if (StringUtils.isEmpty(m_sColumnName))
            m_sColumnName = m_sApiName;

        return m_sColumnName;
    }

    //--------------------------------------------------------------------------
    public void setClassField (Field aField)
        throws Exception
    {
        m_aField = aField;
        m_aType  = aField.getType();
    }

    //--------------------------------------------------------------------------
    public Field getClassField ()
        throws Exception
    {
        return m_aField;
    }

    //--------------------------------------------------------------------------
	public Class< ? > getType ()
        throws Exception
    {
        return m_aType;
    }

    //--------------------------------------------------------------------------
    public void setLength (int nLength)
        throws Exception
    {
        m_nLength = nLength;
    }

    //--------------------------------------------------------------------------
    public int getLength ()
        throws Exception
    {
        return m_nLength;
    }

    //--------------------------------------------------------------------------
    public void setAsIdReference (boolean bIsReference)
        throws Exception
    {
        m_bIsIdReference = bIsReference;
    }
    
    //--------------------------------------------------------------------------
    public boolean isIdReference ()
        throws Exception
    {
        return m_bIsIdReference;
    }
    
    //--------------------------------------------------------------------------
    public void setNullAllowed (boolean bNullAllowed)
        throws Exception
    {
        m_bNullAllowed = bNullAllowed;
    }
    
    //--------------------------------------------------------------------------
    public boolean isNullAllowed ()
        throws Exception
    {
        return m_bNullAllowed;
    }
    
    //--------------------------------------------------------------------------
    public void setScramble (boolean bScramble)
        throws Exception
    {
        m_bScramble = bScramble;
    }
    
    //--------------------------------------------------------------------------
    public boolean isScrambleOn ()
        throws Exception
    {
        return m_bScramble;
    }
    
    //--------------------------------------------------------------------------
    public void verify ()
    	throws Exception
    {
    	if (
    		(m_bNullAllowed       ) &&
    		(m_aType.isPrimitive())
    	   )
    		throw new IllegalArgumentException ("AttributeMetaInfo.verify (): Attribute '"+m_sApiName+"' - a primitive type - cant use option 'can_be_null=true'.");
    	
    	if (
    		(m_bScramble     ) &&
    		(m_bIsIdReference)
    	   )
    		throw new IllegalArgumentException ("AttributeMetaInfo.verify (): ID-reference attributes cant use scramble option.");
    }
    
    //--------------------------------------------------------------------------
    @Override
    public String toString ()
    {
        StringBuffer sString = new StringBuffer (256);

        sString.append (super.toString ()         +"\n" );
        sString.append ("name   = '"+m_sApiName   +"'\n");
        sString.append ("column = '"+m_sColumnName+"'\n");
        sString.append ("Field  = '"+m_aField     +"'\n");

        return sString.toString ();
    }

    //--------------------------------------------------------------------------
    private String m_sApiName = null;

    //--------------------------------------------------------------------------
    private String m_sColumnName = null;

    //--------------------------------------------------------------------------
	private Class< ? > m_aType = null;

    //--------------------------------------------------------------------------
    private int m_nLength = 0;

    //--------------------------------------------------------------------------
    private boolean m_bIsIdReference = false;
    
    //--------------------------------------------------------------------------
    private boolean m_bNullAllowed = false;
    
    //--------------------------------------------------------------------------
    private boolean m_bScramble = false;
    
    //--------------------------------------------------------------------------
    private Field m_aField = null;
}
