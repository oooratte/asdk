/**
 * Copyright 2012 Andreas Schlüns - as-development.net
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.as_development.tools.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;

//==============================================================================
/** Provide functions to find annotations of any type on any class
 *  using reflection.
 */
public class AnnotationSearch
{
    //--------------------------------------------------------------------------
    /** find all fields of a given class which are annotated by the given
     *  annotation class.
     *
     *  @param  aClass [IN]
     *          the class where such fields must be searched.
     *
     *  @param  aAnnotation [IN]
     *          the annotation class which must be bound to searched fields.
     *
     *  @param  bRecursive [IN]
     *          enable/disable search on all super classes.
     *
     *  @param  lFoundFields [OUT]
     *          return list of all found fields annotated by the given annotation class.
     */
    public static void findAnnotatedFields (Class< ? >    				  aClass      ,
                                            Class< ? extends Annotation > aAnnotation ,
                                            boolean       				  bRecursive  ,
                                            List< Field > 				  lFoundFields)
        throws Exception
    {
        if (aClass == null)
            return;

        Field[] lFields = aClass.getDeclaredFields();
        for (Field aField : lFields)
        {
            if (aField.isAnnotationPresent(aAnnotation))
                lFoundFields.add (aField);
        }

        if (bRecursive)
        {
            Class< ? > aSuperClass = aClass.getSuperclass();
            AnnotationSearch.findAnnotatedFields(aSuperClass, aAnnotation, bRecursive, lFoundFields);
        }
    }
}
