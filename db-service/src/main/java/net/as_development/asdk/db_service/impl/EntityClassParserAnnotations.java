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
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import net.as_development.asdk.api.db.PersistentAttribute;
import net.as_development.asdk.api.db.PersistentEntity;
import net.as_development.asdk.api.db.PersistentId;
import net.as_development.asdk.tools.reflection.AnnotationSearch;

//==============================================================================
/**
 * TODO document me
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
