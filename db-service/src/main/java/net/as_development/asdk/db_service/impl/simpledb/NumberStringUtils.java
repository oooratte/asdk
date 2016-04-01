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

import org.apache.commons.lang3.StringUtils;

//==============================================================================
/**
 * TODO document me
 */
public class NumberStringUtils
{
	//--------------------------------------------------------------------------
	public static final byte BYTE_LENTH_BYTE    =  3;
	public static final byte BYTE_LENTH_SHORT   =  5;
	public static final byte BYTE_LENTH_INTEGER = 10;
	public static final byte BYTE_LENTH_LONG    = 19;
	
    //--------------------------------------------------------------------------
	public static final String SIGNUM_NEGATIVE = "0";
	public static final String SIGNUM_POSITIVE = "1";
	
    //--------------------------------------------------------------------------
	public NumberStringUtils (String                    sNumber,
			                  Class< ? extends Number > aType  )
		throws Exception
	{
		m_sValue    = sNumber;
		m_aType     = aType;
		m_bNegative = StringUtils.startsWith(m_sValue, NumberStringUtils.SIGNUM_NEGATIVE);
		impl_anaylzeType ();
	}

    //--------------------------------------------------------------------------
	public NumberStringUtils (Number aNumber)
		throws Exception
	{
		m_nValue    = aNumber.longValue();
		m_bNegative = (m_nValue < 0);
		m_aType     = aNumber.getClass ();
		impl_anaylzeType ();
	}

    //--------------------------------------------------------------------------
	public static < T extends Number > String mapToString (T aNumber)
		throws Exception
	{
		NumberStringUtils aConvert = new NumberStringUtils (aNumber);
		return aConvert.mapToString();
	}
	
    //--------------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	public static < T extends Number > T mapToNumber (String     sNumber,
													  Class< T > aType  )
		throws Exception
	{
		NumberStringUtils aConvert = new NumberStringUtils (sNumber, aType);
		return (T) aConvert.mapToNumber();
	}
	
    //--------------------------------------------------------------------------
	public String mapToString ()
		throws Exception
	{
		StringBuffer sValue       = new StringBuffer (m_nByteLength+1);
		long         nNormedValue = 0;
		String       sPureValue   = null;
		int          nPadding     = 0; 
		
		if (m_bNegative)
		{
			sValue.append (NumberStringUtils.SIGNUM_NEGATIVE);
			nNormedValue = m_nValue-m_nOffsetToZero;
		}
		else
		{
			sValue.append (NumberStringUtils.SIGNUM_POSITIVE);
			nNormedValue = m_nValue;
		}

		sPureValue = Long.toString(nNormedValue, 10);
		nPadding   = m_nByteLength - sPureValue.length();
		
		for (int n=0; n<nPadding; ++n)
			sValue.append ("0");
		
		sValue.append (sPureValue);
		return sValue.toString ();
	}

    //--------------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	public <N> N mapToNumber ()
		throws Exception
	{
		String sPureValue   = StringUtils.substring(m_sValue, 1);
		long   nNormedValue = Long.parseLong(sPureValue, 10);

		if (m_bNegative)
			m_nValue = nNormedValue-m_nOffsetToZero;
		else
			m_nValue = nNormedValue;
		
		if (m_aType.equals(byte.class) || m_aType.equals(Byte.class))
			return (N)new Byte(m_nValue.byteValue());
		else
		if (m_aType.equals(short.class) || m_aType.equals(Short.class))
			return (N)new Short(m_nValue.shortValue());
		else
		if (m_aType.equals(int.class) || m_aType.equals(Integer.class))
			return (N)new Integer(m_nValue.intValue());
		else
		if (m_aType.equals(long.class) || m_aType.equals(Long.class))
			return (N)new Long(m_nValue.longValue());
		
		throw new UnsupportedOperationException ("Please add support for type '"+m_aType+"'.");
	}
	
    //--------------------------------------------------------------------------
	private void impl_anaylzeType ()
		throws Exception
	{
		if (m_aType.equals (byte.class) || m_aType.equals (Byte.class))
		{
			m_nOffsetToZero = Byte.MIN_VALUE;
			m_nByteLength   = NumberStringUtils.BYTE_LENTH_BYTE;
		}
		else
		if (m_aType.equals (short.class) || m_aType.equals (Short.class))
		{
			m_nOffsetToZero = Short.MIN_VALUE;
			m_nByteLength   = NumberStringUtils.BYTE_LENTH_SHORT;
		}
		else
		if (m_aType.equals (int.class) || m_aType.equals (Integer.class))
		{
			m_nOffsetToZero = Integer.MIN_VALUE;
			m_nByteLength   = NumberStringUtils.BYTE_LENTH_INTEGER;
		}
		else
		if (m_aType.equals (long.class) || m_aType.equals (Long.class))
		{
			m_nOffsetToZero = Long.MIN_VALUE;
			m_nByteLength   = NumberStringUtils.BYTE_LENTH_LONG;
		}
		else
			throw new UnsupportedOperationException ("Please add support for type '"+m_aType+"'.");
	}
	
    //--------------------------------------------------------------------------
	private Class< ? extends Number > m_aType = null;
	
    //--------------------------------------------------------------------------
	private String m_sValue = null;
	
    //--------------------------------------------------------------------------
	private Long m_nValue = null;
	
    //--------------------------------------------------------------------------
	private long m_nOffsetToZero = 0;
	
    //--------------------------------------------------------------------------
	private boolean m_bNegative = false;
	
    //--------------------------------------------------------------------------
	private byte m_nByteLength = 0;
}
