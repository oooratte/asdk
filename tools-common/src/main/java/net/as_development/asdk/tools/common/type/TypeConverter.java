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
package net.as_development.asdk.tools.common.type;

import org.apache.commons.lang3.StringUtils;

//=============================================================================
public class TypeConverter
{
	//-------------------------------------------------------------------------
	private TypeConverter ()
	    throws Exception
	{}

	//-------------------------------------------------------------------------
	public static <T> String toString (final T          aValue,
								       final Class< ? > aType )
	    throws Exception
	{
		if (aValue == null)
			return null;
		
		if (String.class.equals(aType))
			return (String) aValue;

		if (
			(boolean.class.equals(aType)) ||
			(Boolean.class.equals(aType))
		   )
			return Boolean.toString((Boolean)aValue);

		if (
			(byte.class.equals(aType)) ||
			(Byte.class.equals(aType))
		   )
			return Byte.toString((Byte)aValue);

		if (
			(short.class.equals(aType)) ||
			(Short.class.equals(aType))
		   )
			return Short.toString((Short)aValue);

		if (
			(int    .class.equals(aType)) ||
			(Integer.class.equals(aType))
		   )
			return Integer.toString((Integer)aValue);
		
		if (
			(long.class.equals(aType)) ||
			(Long.class.equals(aType))
		   )
			return Long.toString((Long)aValue);

		if (
			(float.class.equals(aType)) ||
			(Float.class.equals(aType))
		   )
			return Float.toString((Float)aValue);

		if (
			(double.class.equals(aType)) ||
			(Double.class.equals(aType))
		   )
			return Double.toString((Double)aValue);
		
		if (aType.isEnum())
			return ((Enum< ? >)aValue).name();
			
		if (Convertible.class.isAssignableFrom(aType))
			return ((Convertible)aValue).convertToString();

		throw new UnsupportedOperationException ("no support for type '"+aType+"' implemented yet");
	}

	//-------------------------------------------------------------------------
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> T fromString (final String     sValue,
								    final Class< T > aType )
	    throws Exception
	{
		if (sValue == null)
			return null;
		
		if (String.class.equals(aType))
			return (T) sValue;
		
		if (StringUtils.isEmpty(sValue))
			return null;
		
		if (
			(boolean.class.equals(aType)) ||
			(Boolean.class.equals(aType))
		   )
			return (T) new Boolean(Boolean.parseBoolean(sValue));

		if (
			(byte.class.equals(aType)) ||
			(Byte.class.equals(aType))
		   )
			return (T) new Byte(Byte.parseByte(sValue));

		if (
			(short.class.equals(aType)) ||
			(Short.class.equals(aType))
		   )
			return (T) new Short(Short.parseShort(sValue));

		if (
			(int    .class.equals(aType)) ||
			(Integer.class.equals(aType))
		   )
			return (T) new Integer(Integer.parseInt(sValue));
		
		if (
			(long.class.equals(aType)) ||
			(Long.class.equals(aType))
		   )
			return (T) new Long(Long.parseLong(sValue));

		if (
			(float.class.equals(aType)) ||
			(Float.class.equals(aType))
		   )
			return (T) new Float(Float.parseFloat(sValue));

		if (
			(double.class.equals(aType)) ||
			(Double.class.equals(aType))
		   )
			return (T) new Double(Double.parseDouble(sValue));

		if (aType.isEnum())
			return (T) Enum.valueOf((Class< ? extends Enum >)aType, sValue);
		
		if (Convertible.class.isAssignableFrom(aType))
		{
			final T aInst = (T) aType.newInstance();
			((Convertible)aInst).convertFromString(sValue);
			return aInst;
		}

		throw new UnsupportedOperationException ("no support for type '"+aType+"' implemented yet");
	}

	//-------------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	public static <T> T mapValue (final Object     aValue     ,
								  final Class< T > aTargetType)
	    throws Exception
	{
		if (aValue == null)
			return (T) null;

		final Class< ? > aSourceType = aValue.getClass ();
		
		if (aSourceType.equals(String.class))
			return (T) TypeConverter.fromString((String)aValue, aTargetType);

		if (aTargetType.equals(String.class))
			return (T) TypeConverter.toString(aValue, aSourceType);
		
		final String sTempValue   = TypeConverter.toString   (aValue    , aSourceType);
		final Object aMappedValue = TypeConverter.fromString (sTempValue, aTargetType);
		
		return (T) aMappedValue;
	}
}