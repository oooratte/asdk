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
import java.util.Iterator;

import net.as_development.asdk.db_service.EntityBase;

//==============================================================================
/**
 * TODO document me
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
