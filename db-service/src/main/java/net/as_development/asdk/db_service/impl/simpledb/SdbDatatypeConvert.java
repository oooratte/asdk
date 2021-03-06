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
package net.as_development.asdk.db_service.impl.simpledb;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

//==============================================================================
/**
 * TODO document me
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
