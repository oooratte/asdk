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
import java.util.Iterator;

import net.as_development.asdk.db_service.EntityBase;

//==============================================================================
/**
 * @todo document me
 */
public class EntityHelper
{
    //--------------------------------------------------------------------------
    /** create new instance.
     */
    public EntityHelper ()
    {}

    //--------------------------------------------------------------------------
    public static String[] mapAttribute2ColumnNames (EntityMetaInfo aMeta          ,
                                                     String[]       lAttributeNames)
        throws Exception
    {
        String[]              lColumnNames = new String[lAttributeNames.length];
        AttributeListMetaInfo lAttributes  = aMeta.getAttributes();
        int                   i            = 0;

        for (String sAttribute : lAttributeNames)
        {
            AttributeMetaInfo aAttribute = lAttributes.getForAttributeName(sAttribute);
            String            sColumn    = aAttribute.getColumnName();
            lColumnNames[i++] = sColumn;
        }

        return lColumnNames;
    }
    
    //--------------------------------------------------------------------------
	public static EntityBase createNewEntityInstance (EntityMetaInfo aMeta)
        throws Exception
    {
        String     sClass = aMeta.getClassName();
        Class< ? > aClass = Class.forName(sClass);
        return (EntityBase) aClass.newInstance();
    }

    //--------------------------------------------------------------------------
    public static AttributeMetaInfo[] getEntityAttributesStableOrdered (EntityMetaInfo aMeta)
            throws Exception
    {
        AttributeListMetaInfo lAttributes        = aMeta.getAttributes();
        int                   c                  = lAttributes.count();
        AttributeMetaInfo[]   lOrderedAttributes = new AttributeMetaInfo[c];
        Iterator< String >    pAttributes        = lAttributes.getApiNamesOrdered().iterator();

        int i = 0;
        while (pAttributes.hasNext())
        {
            String            sAttribute = pAttributes.next();
            AttributeMetaInfo aAttribute = lAttributes.getForAttributeName(sAttribute);
            lOrderedAttributes[i] = aAttribute;
            i++;
        }

        return lOrderedAttributes;
    }

    //--------------------------------------------------------------------------
    public static AttributeMetaInfo getAttributeForColumn (EntityMetaInfo aMeta  ,
                                                           String         sColumn)
        throws Exception
    {
        AttributeListMetaInfo lAttributes = aMeta.getAttributes();
        return lAttributes.getForColumnName(sColumn);
    }

    //--------------------------------------------------------------------------
    public static void setAttributeValueOnEntity(Object            aEntity   ,
                                                 AttributeMetaInfo aMeta     ,
                                                 Object            aValue    )
        throws Exception
    {
        Field aField = aMeta.getClassField();
        aField.setAccessible(true);
        aField.set (aEntity, aValue);
    }

    //--------------------------------------------------------------------------
    public static Object getAttributeValueFromEntity(Object            aEntity,
                                                     AttributeMetaInfo aMeta  )
        throws Exception
    {
        Field aField = aMeta.getClassField();
        aField.setAccessible(true);
        return aField.get(aEntity);
    }

    //--------------------------------------------------------------------------
    public static EntityBase createEntityFromRow (EntityMetaInfo aMeta,
                                                  Row            aRow )
        throws Exception
    {
        EntityBase                    aEntity     = EntityHelper.createNewEntityInstance(aMeta);
        AttributeListMetaInfo         lAttributes = aMeta.getAttributes();
        Iterator< AttributeMetaInfo > pAttributes = lAttributes.iterator();

        while (pAttributes.hasNext())
        {
            AttributeMetaInfo aAttribute = pAttributes.next ();
            Field             aField     = aAttribute.getClassField();
            String            sColumn    = aAttribute.getColumnName();
            Object            aValue     = aRow.getColumnValue(sColumn);
            aField.setAccessible(true);
            aField.set(aEntity, aValue);
        }

        return aEntity;
    }

    //--------------------------------------------------------------------------
    public static Row createRowFromEntity (EntityMetaInfo aMeta  ,
                                           EntityBase     aEntity)
        throws Exception
    {
        Row                           aRow        = new Row (aMeta);
        AttributeListMetaInfo         lAttributes = aMeta.getAttributes();
        Iterator< AttributeMetaInfo > pAttributes = lAttributes.iterator();

        // Handle normal attributes
        while (pAttributes.hasNext())
        {
            AttributeMetaInfo aAttribute = pAttributes.next ();
            Field             aField     = aAttribute.getClassField();
            String            sColumn    = aAttribute.getColumnName();

            aField.setAccessible(true);
            Object aValue = aField.get(aEntity);

            aRow.setColumnValue(sColumn, aValue);
        }
        
        // Handle ID attribute
        aRow.setIdValue(aEntity.getIDForStore());

        return aRow;
    }
}
