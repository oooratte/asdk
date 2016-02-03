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

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

//==============================================================================
/** provide several functions where you can manipulate objects at runtime.
 */
public class ObjectManipulation
{
    //--------------------------------------------------------------------------
	/** set a new value on the specified object field ... even it's private
	 *  or final .-)
	 *
	 * 	@param	aTargetObject [IN]
	 * 			the target object where the field value must be set.
	 * 
	 * 	@param	sFieldName [IN]
	 * 			the name of the field.
	 * 
	 * 	@param	aValue [IN]
	 * 			the new value.
	 */
	public static < TType > void setFieldValue (Object aTargetObject,
					 		     	            String sFieldName   ,
					 		    	            TType  aValue       )
		throws Exception
	{
		Class< ? > aClass = aTargetObject.getClass ();
		Field      aField = aClass.getDeclaredField(sFieldName);
		aField.setAccessible(true);
		aField.set(aTargetObject, aValue);
	}
	
    //--------------------------------------------------------------------------
	/** get the current value from specified field.
	 * 
	 * 	@param	aSourceObject [IN]
	 * 			the source object where the field value must be read.
	 * 
	 * 	@param	sFieldName [IN]
	 * 			the name of the field.
	 * 
	 * 	@return	the current value of these field.
	 * 			Can be null .-)
	 */
	@SuppressWarnings("unchecked")
	public static < TType > TType getFieldValue (Object aSourceObject,
										         String sFieldName   )
		throws Exception
	{
		Class< ? > aClass = aSourceObject.getClass ();
		Field      aField = aClass.getDeclaredField(sFieldName);
		aField.setAccessible(true);
		return (TType) aField.get(aSourceObject);
	}
	
    //--------------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	public static < TReturnType > TReturnType callPrivateMethod (Object    aTargetObject,
						  				  		     			 String    sMethodName  ,
						  				  		     			 Object... lParameter   )
		throws Exception
	{
		Class< ? >[] lParameterTypes = ObjectManipulation.impl_getParameterTypes(lParameter);
		Class< ? >   aClass          = aTargetObject.getClass ();
		Method[]     lMethods        = aClass.getDeclaredMethods();
		Method       aTargetMethod   = null;
		
		for (Method aMethod : lMethods)
		{
		    String sNameCheck = aMethod.getName();
		    if ( ! StringUtils.equals(sNameCheck, sMethodName))
		        continue;
		    
		    aTargetMethod = aMethod;
		    
		    Class< ? >[] lParamCheck = aMethod.getParameterTypes();
		    if (ArrayUtils.isEquals(lParamCheck, lParameterTypes))
		        break;
		}
		
/* ifdef DEBUG .-)		
		System.out.println ("ObjectManipulation.callPrivateMethod () invoke '"+aTargetObject+"'.'"+aTargetMethod+"' () ...");
		for (Object aParameter : lParameter)
		    System.out.println (aParameter + " type["+aParameter.getClass ()+"]");
*/
		
		aTargetMethod.setAccessible(true);
		return (TReturnType) aTargetMethod.invoke(aTargetObject, lParameter);
	}
	
    //--------------------------------------------------------------------------
	private static Class< ? >[] impl_getParameterTypes (Object... lParameter)
		throws Exception
	{
		int          i               = 0;
		int          c               = lParameter.length;
		Class< ? >[] lParameterTypes = new Class[c];
		
		for (Object aParameter : lParameter)
			lParameterTypes[i++] = aParameter.getClass ();
		
		return lParameterTypes;
	}
}
