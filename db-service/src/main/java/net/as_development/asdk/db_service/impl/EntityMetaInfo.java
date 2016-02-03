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


import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;

import net.as_development.asdk.api.db.PersistentId;

//==============================================================================
/**
 * @todo document me
 */
public class EntityMetaInfo
{
    //--------------------------------------------------------------------------
    /// define the prefix used for naming DB tables
    public static final String PREFIX_TABLES = "t";
    
    //--------------------------------------------------------------------------
    /// define the prefix used for naming DB columns
    public static final String PREFIX_COLUMNS = "c";
    
    //--------------------------------------------------------------------------
    /// define the prefix used for naming DB constraints from type primary key
    public static final String PREFIX_CONSTRAINT_PRIMARY_KEY = "pk";
    
    //--------------------------------------------------------------------------
    /** create new instance.
     */
    public EntityMetaInfo ()
    {}

    //--------------------------------------------------------------------------
    public void setName (String sName)
        throws Exception
    {
        m_sName = sName;
    }

    //--------------------------------------------------------------------------
    public String getName ()
        throws Exception
    {
        return m_sName;
    }

    //--------------------------------------------------------------------------
    public void setClassName (String sClass)
        throws Exception
    {
        m_sClassName = sClass;
    }

    //--------------------------------------------------------------------------
    public String getClassName ()
        throws Exception
    {
        return m_sClassName;
    }

    //--------------------------------------------------------------------------
    public void setSchema (String sSchema)
        throws Exception
    {
        m_sSchema = sSchema;
    }
    
    //--------------------------------------------------------------------------
    public String getSchema ()
        throws Exception
    {
        return m_sSchema;
    }
    
    //--------------------------------------------------------------------------
    public void setTable (String sTable)
        throws Exception
    {
        m_sTable = sTable;
    }

    //--------------------------------------------------------------------------
    public String getTable ()
        throws Exception
    {
        // find fallback if table was not specified.
        if (StringUtils.isEmpty(m_sTable))
            m_sTable = m_sName;
        return m_sTable;
    }

    //--------------------------------------------------------------------------
    public void setIdAttribute (String sAttribute)
        throws Exception
    {
        m_sIdAttribute = sAttribute;
    }

    //--------------------------------------------------------------------------
    public String getIdAttribute ()
        throws Exception
    {
        return m_sIdAttribute;
    }

    //--------------------------------------------------------------------------
    public void setAttributes (AttributeListMetaInfo lAttributes)
        throws Exception
    {
        m_lAttributes = lAttributes;
    }

    //--------------------------------------------------------------------------
    public AttributeListMetaInfo getAttributes ()
        throws Exception
    {
        return m_lAttributes;
    }

    //--------------------------------------------------------------------------
    public void setIdGenerationStrategy (PersistentId.EStrategy eStrategy)
        throws Exception
    {
        m_eIdStrategy = eStrategy;
    }

    //--------------------------------------------------------------------------
    public PersistentId.EStrategy getIdGenerationStrategy ()
        throws Exception
    {
        return m_eIdStrategy;
    }
    
    //--------------------------------------------------------------------------
    public void verify ()
    	throws Exception
    {
    	Iterator< AttributeMetaInfo > pAttributes = m_lAttributes.iterator();
    	while (pAttributes.hasNext())
    	{
    		AttributeMetaInfo aAttribute = pAttributes.next();
    		aAttribute.verify ();
    		
    		boolean bIsIdAttribute = StringUtils.equals(getIdAttribute(), aAttribute.getApiName());
    		if (
    			(bIsIdAttribute           ) &&
    			(aAttribute.isScrambleOn())
    		   )
    			throw new IllegalArgumentException ("EntityMetaInfo.verify () : Scramble option cant be used for ID attribute.");
    	}
    }
    
    //--------------------------------------------------------------------------
    @Override
    public String toString ()
    {
        StringBuffer sString = new StringBuffer (256);

        sString.append (super.toString()     +"\n");
        sString.append ("name ="+m_sName     +"\n");
        sString.append ("class="+m_sClassName+"\n");
        sString.append ("table="+m_sTable    +"\n");

        sString.append ("attributes:\n"+m_lAttributes);

        return sString.toString ();
    }

    //--------------------------------------------------------------------------
    private String m_sName = null;

    //--------------------------------------------------------------------------
    private String m_sClassName = null;

    //--------------------------------------------------------------------------
    private String m_sSchema = null;
    
    //--------------------------------------------------------------------------
    private String m_sTable = null;

    //--------------------------------------------------------------------------
    private String m_sIdAttribute = null;

    //--------------------------------------------------------------------------
    private AttributeListMetaInfo m_lAttributes = null;
    
    //--------------------------------------------------------------------------
    private PersistentId.EStrategy m_eIdStrategy = null;
}
