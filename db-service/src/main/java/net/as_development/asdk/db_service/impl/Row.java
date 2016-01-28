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
