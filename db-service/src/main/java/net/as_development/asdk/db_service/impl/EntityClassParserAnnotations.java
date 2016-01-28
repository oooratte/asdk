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
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import net.as_development.asdk.api.db.PersistentAttribute;
import net.as_development.asdk.api.db.PersistentEntity;
import net.as_development.asdk.api.db.PersistentId;
import net.as_development.asdk.tools.reflection.AnnotationSearch;

//==============================================================================
/**
 * @todo document me
 */
public class EntityClassParserAnnotations
{
    //--------------------------------------------------------------------------
    /** create new instance.
     */
    public EntityClassParserAnnotations ()
    {}

    //--------------------------------------------------------------------------
    public static void parse (EntityMetaInfo aMetaInfo)
        throws Exception
    {
        String      sClass = aMetaInfo.getClassName();
        Class< ? >  aClass = Class.forName(sClass);

        // mandatory annotation ... mandatory properties
        PersistentEntity aEntityAnnotation = (PersistentEntity) aClass.getAnnotation(PersistentEntity.class);
        String           sEntityName       = aEntityAnnotation.name  ();
        String           sTableName        = aEntityAnnotation.table ();

        aMetaInfo.setName (sEntityName);
        aMetaInfo.setTable(sTableName );

        // start new persistent attribute list ...
        AttributeListMetaInfo lAttributesMeta = new AttributeListMetaInfo ();
        aMetaInfo.setAttributes(lAttributesMeta);

        List< Field > lFields    = new ArrayList< Field >(10);
        final boolean bRecursive = true;

        // retrieve primary key field(s)
        lFields.clear ();
        AnnotationSearch.findAnnotatedFields(aClass, PersistentId.class, bRecursive, lFields);
        for (Field aField : lFields)
        {
            PersistentId           aIdAnnotation = aField.getAnnotation(PersistentId.class);
            AttributeMetaInfo      aAttribute    = new AttributeMetaInfo ();
            String                 sApiName      = aIdAnnotation.name();
            String                 sColumn       = aIdAnnotation.column();
            PersistentId.EStrategy eIdStrategy   = aIdAnnotation.strategy();

            if (! aField.getType().equals(String.class))
                throw new IllegalArgumentException ("Primary key '"+aField.getName()+"' is not from type String. (Sorry - but currently we do not provide more PK-types then String.)");
            if (StringUtils.isEmpty (sApiName))
                throw new IllegalArgumentException ("Primary key '"+aField.getName()+"' does not define api name.");
            if (StringUtils.isEmpty (sColumn))
                throw new IllegalArgumentException ("primary key '"+aField.getName()+"' does not define table column.");

            aAttribute.setApiName   (sApiName);
            aAttribute.setColumnName(sColumn );
            aAttribute.setClassField(aField  );
        	aAttribute.setLength    (IdStrategy.getIdLengthForStrategy(eIdStrategy));

            lAttributesMeta.put (sApiName, aAttribute);

            aMetaInfo.setIdAttribute         (sApiName   );
            aMetaInfo.setIdGenerationStrategy(eIdStrategy);
        }

        // retrieve all other persistent fields
        lFields.clear ();
        AnnotationSearch.findAnnotatedFields(aClass, PersistentAttribute.class, bRecursive, lFields);
        for (Field aField : lFields)
        {
            PersistentAttribute aAttributeAnnotation = aField.getAnnotation(PersistentAttribute.class);
            AttributeMetaInfo   aAttribute           = new AttributeMetaInfo ();
            String              sApiName             = aAttributeAnnotation.name           ();
            String              sColumn              = aAttributeAnnotation.column         ();
            int                 nLength              = aAttributeAnnotation.length         ();
            boolean             bIsIdReference       = aAttributeAnnotation.is_id_reference();
            boolean             bCanBeNull           = aAttributeAnnotation.can_be_null    ();
            boolean             bScramble            = aAttributeAnnotation.scramble       ();
            
            if (StringUtils.isEmpty (sApiName))
                throw new IllegalArgumentException ("Persistent property '"+aField.getName()+"' does not define api name.");
            if (StringUtils.isEmpty (sColumn))
                throw new IllegalArgumentException ("Persistent property '"+aField.getName()+"' does not define table column.");

            aAttribute.setApiName      (sApiName      );
            aAttribute.setColumnName   (sColumn       );
            aAttribute.setClassField   (aField        );
            aAttribute.setLength       (nLength       );
            aAttribute.setAsIdReference(bIsIdReference);
            aAttribute.setNullAllowed  (bCanBeNull    );
            aAttribute.setScramble     (bScramble     );

            lAttributesMeta.put(sApiName, aAttribute);
        }
    }
    
    public static boolean isSimpleType (Class< ? > aType)
    	throws Exception
    {
    	return false;
    }
}
