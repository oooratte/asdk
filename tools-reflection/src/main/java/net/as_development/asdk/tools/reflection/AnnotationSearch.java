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
package net.as_development.asdk.tools.reflection;

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
