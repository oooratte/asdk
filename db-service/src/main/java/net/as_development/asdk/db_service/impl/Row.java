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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

//==============================================================================
/**
 * @todo document me
 */
public class Row
{
    //--------------------------------------------------------------------------
    /** create new instance.
     */
    public Row ()
    {}

    //--------------------------------------------------------------------------
    public Row (EntityMetaInfo aMetaInfo)
        throws Exception
    {
        setEntityMetaInfo (aMetaInfo);
    }
    
    //--------------------------------------------------------------------------
    public void setEntityMetaInfo (EntityMetaInfo aMetaInfo)
        throws Exception
    {
        m_aMetaInfo = aMetaInfo;
        impl_initRow ();
    }

    //--------------------------------------------------------------------------
    public EntityMetaInfo getEntityMetaInfo ()
        throws Exception
    {
        return m_aMetaInfo;
    }

    //--------------------------------------------------------------------------
    public void setPersistentStateHandler (PersistentStateHandler aEntity)
        throws Exception
    {
    	m_aPersistentStateHandler = aEntity;
    }
    
    //--------------------------------------------------------------------------
    public PersistentStateHandler getPersistentStateHandler ()
        throws Exception
    {
    	return m_aPersistentStateHandler;
    }
    
    //--------------------------------------------------------------------------
    public Row newRow ()
        throws Exception
    {
        Row aNew = new Row ();
        
        aNew.m_aMetaInfo               = m_aMetaInfo;
        aNew.m_sIdColumn               = m_sIdColumn;
        aNew.m_aPersistentStateHandler = (PersistentStateHandler) m_aPersistentStateHandler.clone();

        // note: Dont take over column values from "template row" ...
        aNew.m_lColumns = null;
        // ... but generate meta data as e.g. column names, column types etcpp !
        aNew.impl_initRow();
        
        return aNew;
    }

    //--------------------------------------------------------------------------
    public String getTable ()
        throws Exception
    {
        return m_aMetaInfo.getTable();
    }

    //--------------------------------------------------------------------------
    public String getIdColumn ()
        throws Exception
    {
        return m_sIdColumn;
    }

    //--------------------------------------------------------------------------
	public Class< ? > getIdType ()
        throws Exception
    {
        return mem_Columns ().get(m_sIdColumn).Type;
    }

    //--------------------------------------------------------------------------
    public Object getIdValue ()
        throws Exception
    {
        return mem_Columns ().get(m_sIdColumn).Value;
    }

    //--------------------------------------------------------------------------
    public void setIdValue (Object aValue)
        throws Exception
    {
        mem_Columns ().get(m_sIdColumn).Value = aValue;
    }

    //--------------------------------------------------------------------------
    public Iterator< String > listColumns ()
        throws Exception
    {
        return mem_Columns ().keySet().iterator();
    }

    //--------------------------------------------------------------------------
    public int getColumnCount ()
        throws Exception
    {
        return mem_Columns ().size();
    }

    //--------------------------------------------------------------------------
    public Column getColumn (String sColumn)
        throws Exception
    {
        return mem_Columns ().get(sColumn);
    }

    //--------------------------------------------------------------------------
	public Class< ? > getColumnType (String sColumn)
        throws Exception
    {
        Column aColumn = mem_Columns ().get(sColumn);
        return aColumn.Type;
    }

    //--------------------------------------------------------------------------
    public Object getColumnValue (String sColumn)
        throws Exception
    {
        Column aColumn = mem_Columns ().get(sColumn);
        return aColumn.Value;
    }

    //--------------------------------------------------------------------------
    public void setColumnValue (String sColumn,
                                Object aValue )
        throws Exception
    {
        Column aColumn = mem_Columns ().get(sColumn);
        aColumn.Value = aValue;
    }

    //--------------------------------------------------------------------------
	private void impl_initRow ()
        throws Exception
    {
        Map< String, Column > lColumns     = mem_Columns ();
        AttributeMetaInfo[]   lAttributes  = EntityHelper.getEntityAttributesStableOrdered(m_aMetaInfo);
        String                sIdAttribute = m_aMetaInfo.getIdAttribute();

        lColumns.clear ();

        for (AttributeMetaInfo aAttribute : lAttributes)
        {
            String sAttribute = aAttribute.getApiName();
            String sColumn    = aAttribute.getColumnName();
            Class< ? >  aType      = aAttribute.getType();
            int    nLength    = aAttribute.getLength();

            Column aColumn = new Column (sColumn, aType, null, nLength);
            lColumns.put (sColumn, aColumn);

            if (sAttribute.equals(sIdAttribute))
                m_sIdColumn = sColumn;
        }
    }

    //--------------------------------------------------------------------------
    private Map< String, Column > mem_Columns ()
        throws Exception
    {
        if (m_lColumns == null)
            m_lColumns = new HashMap< String, Column >(10);
        return m_lColumns;
    }

    //--------------------------------------------------------------------------
    private EntityMetaInfo m_aMetaInfo = null;

    //--------------------------------------------------------------------------
    private PersistentStateHandler m_aPersistentStateHandler = null;
    
    //--------------------------------------------------------------------------
    private Map< String, Column > m_lColumns = null;

    //--------------------------------------------------------------------------
    private String m_sIdColumn = null;
}
