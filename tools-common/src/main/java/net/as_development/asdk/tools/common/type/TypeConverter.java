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