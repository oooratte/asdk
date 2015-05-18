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
