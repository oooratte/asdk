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

//==============================================================================

import java.lang.reflect.Field;
import java.util.Iterator;

/**
 * @todo document me
 */
public class EntityClassParserSimple
{
    //--------------------------------------------------------------------------
    /** create new instance.
     */
    public EntityClassParserSimple ()
    {}

    //--------------------------------------------------------------------------
	public static void parse (EntityMetaInfo aEntity)
        throws Exception
    {
        String      sClass = aEntity.getClassName();
        Class< ? >  aClass = Class.forName(sClass);

        AttributeListMetaInfo         lAttributes = aEntity.getAttributes();
        Iterator< AttributeMetaInfo > pAttributes = lAttributes.iterator();

        while (pAttributes.hasNext())
        {
            AttributeMetaInfo aAttribute = pAttributes.next();
            String            sAttribute = aAttribute.getApiName();
            Field             aField     = EntityClassParserSimple.impl_getField(aClass, sAttribute);

            if (aField == null)
                throw new RuntimeException ("You configured an attribute '"+sAttribute+"' for class '"+sClass+"' but those class does not provide such field.");

            aAttribute.setClassField(aField);
        }
    }

    //--------------------------------------------------------------------------
    // @todo move to more generic ferlection helper .-)
	private static Field impl_getField (Class< ? >  aClass,
                                        String      sField)
        throws Exception
    {
        try
        {
            return aClass.getDeclaredField(sField);
        }
        catch(Throwable ex)
        {}

        try
        {
            return EntityClassParserSimple.impl_getField(aClass.getSuperclass(), sField);
        }
        catch(Throwable ex)
        {}

        return null;
    }
}
