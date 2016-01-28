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
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;

//==============================================================================
/** map attributes by using her Api name to a struct which describe
 *  those attribute more in detail.
 */
public class AttributeListMetaInfo
{
    //--------------------------------------------------------------------------
    /** create new instance.
     */
    public AttributeListMetaInfo ()
    {}

    //--------------------------------------------------------------------------
    public void put(String            sAttributeName,
                    AttributeMetaInfo aMeta         )
        throws Exception
    {
    	Map< String, AttributeMetaInfo > lAttributes = mem_Attributes ();
    	if (lAttributes.containsKey(sAttributeName))
    		throw new IllegalArgumentException ("Found duplicate attribute '"+sAttributeName+"' within same entity.");
    	
        lAttributes.put (sAttributeName, aMeta);

        // make sure optimized ordered list will be rebuild next time it's called !
        m_lOrderedList = null;
    }

    //--------------------------------------------------------------------------
    public AttributeMetaInfo getForAttributeName (String sAttributeName)
        throws Exception
    {
        return mem_Attributes ().get (sAttributeName);
    }

    //--------------------------------------------------------------------------
    public AttributeMetaInfo getForColumnName (String sColumnName)
        throws Exception
    {
        Map< String, AttributeMetaInfo > lAttributes    = mem_Attributes ();
        Map< String, String >            lColumnMapping = mem_Column2AttributeNameMapping ();
        String                           sAttributeName = lColumnMapping.get(sColumnName);

        return lAttributes.get (sAttributeName);
    }

    //--------------------------------------------------------------------------
    public int count ()
        throws Exception
    {
        return mem_Attributes ().size();
    }

    //--------------------------------------------------------------------------
    public Iterator< AttributeMetaInfo > iterator ()
        throws Exception
    {
        return mem_Attributes ().values().iterator();
    }

    //--------------------------------------------------------------------------
    /** return a sorted list of attribute names.
     *
     *  Order of those list will be stable.
     *  Doing so it can be used to generate e.g. sql statements
     *  where order or column names and values will be important.
     *
     *  @return list of attribute names in an ordered manner.
     */
    public List< String > getApiNamesOrdered ()
        throws Exception
    {
        if (m_lOrderedList == null)
        {
            // @todo implement me right ! :-)
            List< String > lOrdered = new Vector< String >(10);
            lOrdered.addAll(mem_Attributes ().keySet());

            m_lOrderedList = lOrdered;
        }

        return m_lOrderedList;
    }

    //--------------------------------------------------------------------------
    private Map< String, AttributeMetaInfo > mem_Attributes ()
        throws Exception
    {
        if (m_lAttributes == null)
            m_lAttributes = new HashMap< String, AttributeMetaInfo >(10);
        return m_lAttributes;
    }

    //--------------------------------------------------------------------------
    private Map< String, String > mem_Column2AttributeNameMapping ()
        throws Exception
    {
        if (m_lColumn2AttributeNameMapping == null)
        {
            Map< String, String >                         lMapping    = new HashMap< String, String >(10);
            Map< String, AttributeMetaInfo >              lAttributes = mem_Attributes ();
            Iterator< Entry< String, AttributeMetaInfo >> pAttributes = lAttributes.entrySet().iterator();

            while (pAttributes.hasNext())
            {
            	Entry< String, AttributeMetaInfo > aNext      = pAttributes.next(); 
                String                             sAttribute = aNext.getKey();
                AttributeMetaInfo                  aAttribute = aNext.getValue();
                String                             sColumn    = aAttribute.getColumnName();

                lMapping.put (sColumn, sAttribute);
            }

            m_lColumn2AttributeNameMapping = lMapping;
        }
        return m_lColumn2AttributeNameMapping;
    }

    //--------------------------------------------------------------------------
    private Map< String, AttributeMetaInfo > m_lAttributes = null;

    //--------------------------------------------------------------------------
    private Map< String, String > m_lColumn2AttributeNameMapping = null;

    //--------------------------------------------------------------------------
    private List< String > m_lOrderedList = null;

}
