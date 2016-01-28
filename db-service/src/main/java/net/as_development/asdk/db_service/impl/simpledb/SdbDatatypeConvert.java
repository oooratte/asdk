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
package net.as_development.asdk.db_service.impl.simpledb;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

//==============================================================================
/**
 * @todo document me
 */
public class SdbDatatypeConvert
{
    //--------------------------------------------------------------------------
	public static final String NULL = "NULL";

    //--------------------------------------------------------------------------
    /** create new instance.
     */
    public SdbDatatypeConvert ()
    {}

    //--------------------------------------------------------------------------
	public static String toSdbValue (Class< ? >  aType    ,
                                     Object      aValue   ,
                                     boolean     bScramble)
        throws Exception
    {
        if (aValue == null)
            return SdbDatatypeConvert.NULL;

        if (aType.equals(Date.class))
        {
        	Date aDate = (Date) aValue;
        	long nDate = aDate.getTime();
        	return NumberStringUtils.mapToString(nDate);
        }

        if (aType.equals(String.class))
        	return (String)aValue;
        
        if (
            (aType.equals(char.class   )) ||
        	(aType.equals(boolean.class)) ||
        	(aType.equals(Boolean.class))
           )
        	return String.valueOf(aValue);
        
        if (
        	(aType.equals(byte.class   )) ||
        	(aType.equals(Byte.class   )) ||
        	(aType.equals(short.class  )) ||
        	(aType.equals(Short.class  )) ||
        	(aType.equals(int.class    )) ||
        	(aType.equals(Integer.class)) ||
        	(aType.equals(long.class   )) ||
        	(aType.equals(Long.class   ))
           )
        	return NumberStringUtils.mapToString((Number)aValue);
        
        if (
        	(aType.equals(double.class )) ||
        	(aType.equals(Double.class )) ||
        	(aType.equals(float.class  )) ||
        	(aType.equals(Float.class  ))
           )
        	return String.valueOf(aValue);
        
        throw new RuntimeException ("Not implemented yet. Support data type '"+aType+"' for SimpleDB please.");
    }

    //--------------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	public static Object fromSdbValue (Class< ? > aType    ,
                                       String     sValue   ,
                                       boolean    bScramble)
        throws Exception
    {
        if (
        	(sValue == null                                     ) ||
        	(StringUtils.equals(sValue, SdbDatatypeConvert.NULL))
           )
        {
        	if (aType.isPrimitive())
        		throw new Exception ("A primitive entity attribute can not be set to null ... but was tried as such.");
            return null;
        }

        if (aType.equals(String.class))
            return sValue;

        if (StringUtils.isEmpty(sValue))
        {
        	if (aType.isPrimitive())
        		throw new Exception ("A primitive entity attribute can not be set to null ... but was tried as empty string.");
        	return null;
        }
        
        if (aType.equals(char.class))
            return sValue.charAt(0);
                
        if (
        	(aType.equals(boolean.class)) ||
        	(aType.equals(Boolean.class))
           )
            return Boolean.parseBoolean(sValue);
        
        if (
            (aType.equals(byte.class   )) ||
            (aType.equals(Byte.class   )) ||
        	(aType.equals(short.class  )) ||
        	(aType.equals(Short.class  )) ||
        	(aType.equals(int.class    )) ||
        	(aType.equals(Integer.class)) ||
        	(aType.equals(long.class   )) ||
        	(aType.equals(Long.class   ))
           )
            return NumberStringUtils.mapToNumber(sValue, (Class< Number >)aType);
        
        if (
        	(aType.equals(double.class)) ||
        	(aType.equals(Double.class))
           )
            return Double.parseDouble(sValue);
        
        if (
        	(aType.equals(float.class)) ||
        	(aType.equals(Float.class))
           )
            return Float.parseFloat(sValue);
        
        if (aType.equals(Date.class))
        {
        	long nDate = NumberStringUtils.mapToNumber(sValue, Long.class);
            return new Date(nDate);
        }
        
        throw new RuntimeException ("Not implemented yet. Support more data types for SimpleDB please. Miss type '"+aType+"'.");
    }
}
