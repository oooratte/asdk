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
package net.as_development.asdk.tools.common;

//=============================================================================
public class NumberUtils
{
	//-------------------------------------------------------------------------
	private NumberUtils ()
	{}

	//-------------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	public static < T extends Number > T defaultNumber (final T                         nValue,
														final Class< ? extends Number > aType )
		throws Exception
	{
		if (nValue != null)
			return nValue;

		Number nDefault = null;
		if (
			(aType.equals(Double.class)) ||
			(aType.equals(double.class))
	       )
		{
			nDefault = (double) 0.0;
		}
		else
		if (
			(aType.equals(Float.class)) ||
			(aType.equals(float.class))
	       )
		{
			nDefault = (float) 0.0;
		}
		else
		if (
			(aType.equals(Byte.class)) ||
			(aType.equals(byte.class))
	       )
		{
			nDefault = (byte) 0;
		}
		else
		if (
			(aType.equals(Short.class)) ||
			(aType.equals(short.class))
	       )
		{
			nDefault = (short) 0;
		}
		else
		if (
			(aType.equals(Integer.class)) ||
			(aType.equals(int    .class))
	       )
		{
			nDefault = (int) 0;
		}
		else
		if (
			(aType.equals(Long.class)) ||
			(aType.equals(long.class))
	       )
		{
			nDefault = (long) 0;
		}
		else
			throw new UnsupportedOperationException ("No support for number format '"+aType+"' implemented yet.");		
		
		return (T)nDefault;
	}
	
	//-------------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	public static < T extends Number > T increment (final T nNumber   ,
										            final T nIncrement)
	    throws Exception
	{
		final Class< T > aType     = (Class< T >)nIncrement.getClass ();
		      Number     nOldValue = NumberUtils.defaultNumber(nNumber, aType);
		      Number     nNewValue = null;

		if (
			(aType.equals(Double.class)) ||
			(aType.equals(double.class))
	       )
		{
			nNewValue = (Double) nOldValue + (Double) nIncrement;
		}
		else
		if (
			(aType.equals(Float.class)) ||
			(aType.equals(float.class))
	       )
		{
			nNewValue = (Float) nOldValue + (Float) nIncrement;
		}
		else
		if (
			(aType.equals(Byte.class)) ||
			(aType.equals(byte.class))
	       )
		{
			nNewValue = (Byte) nOldValue + (Byte) nIncrement;
		}
		else
		if (
			(aType.equals(Short.class)) ||
			(aType.equals(short.class))
	       )
		{
			nNewValue = (Short) nOldValue + (Short) nIncrement;
		}
		else
		if (
			(aType.equals(Integer.class)) ||
			(aType.equals(int    .class))
	       )
		{
			nNewValue = (Integer) nOldValue + (Integer) nIncrement;
		}
		else
		if (
			(aType.equals(Long.class)) ||
			(aType.equals(long.class))
	       )
		{
			nNewValue = (Long) nOldValue + (Long) nIncrement;
		}
		else
			throw new UnsupportedOperationException ("No support for number format '"+aType+"' implemented yet.");

		return (T) nNewValue;
	}

	//-------------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	public static < T extends Number > T decrement (final T nNumber   ,
										            final T nDecrement)
	    throws Exception
	{
		final Class< ? > aType     = nDecrement.getClass ();
		      Number     nOldValue = nNumber;
		      Number     nNewValue = null   ;

		if (
			(aType.equals(Double.class)) ||
			(aType.equals(double.class))
	       )
		{
			nNewValue = (Double) nOldValue - (Double) nDecrement;
		}
		else
		if (
			(aType.equals(Float.class)) ||
			(aType.equals(float.class))
	       )
		{
			nNewValue = (Float) nOldValue - (Float) nDecrement;
		}
		else
		if (
			(aType.equals(Byte.class)) ||
			(aType.equals(byte.class))
	       )
		{
			nNewValue = (Byte) nOldValue - (Byte) nDecrement;
		}
		else
		if (
			(aType.equals(Short.class)) ||
			(aType.equals(short.class))
	       )
		{
			nNewValue = (Short) nOldValue - (Short) nDecrement;
		}
		else
		if (
			(aType.equals(Integer.class)) ||
			(aType.equals(int    .class))
	       )
		{
			nNewValue = (Integer) nOldValue - (Integer) nDecrement;
		}
		else
		if (
			(aType.equals(Long.class)) ||
			(aType.equals(long.class))
	       )
		{
			nNewValue = (Long) nOldValue - (Long) nDecrement;
		}
		else
			throw new UnsupportedOperationException ("No support for number format '"+aType+"' implemented yet.");

		return (T) nNewValue;
	}
}
