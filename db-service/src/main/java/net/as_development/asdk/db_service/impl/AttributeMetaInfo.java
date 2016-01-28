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

import java.lang.reflect.Field;
import org.apache.commons.lang3.StringUtils;

//==============================================================================
/**
 * @todo document me
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
