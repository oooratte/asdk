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
import java.math.MathContext;
import java.text.DecimalFormat;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

//==============================================================================
/**
 * TODO document me
 */
public class DecimalStringUtils
{
    //--------------------------------------------------------------------------
	public DecimalStringUtils (String                    sNumber,
			                   Class< ? extends Number > aType  )
		throws Exception
	{
		m_sValue    = sNumber;
		m_aType     = aType;
		m_bNegative = StringUtils.startsWith (m_sValue, "0"); 
	}

    //--------------------------------------------------------------------------
	public DecimalStringUtils (Number aNumber)
		throws Exception
	{
		m_fValue    = aNumber.doubleValue();
		m_aType     = aNumber.getClass ();
		m_bNegative = (m_fValue<0);
	}

    //--------------------------------------------------------------------------
	private double impl_normalize (double fOrg)
		throws Exception
	{
		DecimalFormat aFormat = (DecimalFormat) DecimalFormat.getInstance(Locale.ENGLISH);
		              aFormat.applyPattern ("0.00000");
		String        sNorm   = aFormat.format(fOrg);
		double        fNorm   = Double.parseDouble(sNorm);
		return fNorm;
	}
	
    //--------------------------------------------------------------------------
	public static < T extends Number > String mapToString (T aNumber)
		throws Exception
	{
		DecimalStringUtils aConvert = new DecimalStringUtils (aNumber);
		return aConvert.mapToString();
	}
	
    //--------------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	public static < T extends Number > T mapToNumber (String     sNumber,
													  Class< T > aType  )
		throws Exception
	{
		DecimalStringUtils aConvert = new DecimalStringUtils (sNumber, aType);
		return (T) aConvert.mapToNumber();
	}
	
    //--------------------------------------------------------------------------
	public String mapToString ()
		throws Exception
	{
		/*
		DecimalFormat aFormat = (DecimalFormat) DecimalFormat.getInstance(Locale.ENGLISH);
        			  aFormat.applyPattern ("0.00000");
        			  aFormat.setMaximumFractionDigits(5);
        */

		System.out.println ("mapToString ("+m_fValue+")");
		
		if (m_fValue < 1.00 && m_fValue > -1.00)
			System.out.println ("==== ????? :-)");
		
		BigDecimal   fValue  = new BigDecimal (m_fValue);

		if (fValue.compareTo(new BigDecimal(1.0))<0)
			System.out.println ("#### fValue 0.x");
		else
			System.out.println ("#### fValue 1.x !!!!!!!!!!");
		
		long         nLeft   = fValue.longValue ();
		String       sLeft   = NumberStringUtils.mapToString(nLeft);
		StringBuffer sValue  = new StringBuffer (32);
		BigDecimal   fLeft   = new BigDecimal (nLeft); 
		BigDecimal   fRight  = fValue.subtract(fLeft).abs();
		String       sRight  = fRight.toPlainString();

		if (fRight.compareTo(new BigDecimal(1.0))<0)
			System.out.println ("#### 0.x");
		else
			System.out.println ("#### 1.x !!!!!!!!!!");
		
		System.out.println ("#### R "+sRight);
		             sRight  = StringUtils.removeStart(sRight, "0.");
 		System.out.println ("#### R "+sRight);
		             
		sValue.append (sLeft );
		sValue.append ("."   );
		sValue.append (sRight);
		
		m_sValue = sValue.toString ();
		return m_sValue;
	}

    //--------------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	public <N> N mapToNumber ()
		throws Exception
	{
		String[]   lSplit = StringUtils.split(m_sValue, ".");
		long       nLeft  = NumberStringUtils.mapToNumber(lSplit[0], Long.class);
		String     sRight = "0."+lSplit[1];
		BigDecimal fLeft  = new BigDecimal (nLeft );
		BigDecimal fRight = new BigDecimal (sRight);
		BigDecimal fComb  = fLeft.add(fRight);
		double     fValue = fComb.doubleValue();
		
//		double fValue = SimpleDBUtils.decodeRealNumberRangeFloat(m_sValue, 19, Integer.MAX_VALUE-1);
		/*
		long   nValue = NumberStringUtils.mapToNumber(m_sValue, Long.class);
		double fValue = Double.longBitsToDouble(nValue);
		*/
		/*
		String sPureValue = StringUtils.substring(m_sValue, 1);
		long   nValue     = Long.parseLong(sPureValue);
		double fValue     = Double.longBitsToDouble(nValue);
		
		if (m_bNegative)
			fValue = 0.00 - fValue;
		*/
		
		/*
		DecimalFormat aFormat = (DecimalFormat) DecimalFormat.getInstance(Locale.ENGLISH);
		aFormat.applyPattern ("0.00000");
		m_sValue = StringUtils.removeStart(m_sValue, "+");
		double fValue = aFormat.parse(m_sValue).doubleValue();
		*/
		
		if (m_aType.equals(float.class) || m_aType.equals(Float.class))
			return (N)new Float(fValue);
		else
		if (m_aType.equals(double.class) || m_aType.equals(Double.class))
			return (N)new Double(fValue);
		
		throw new UnsupportedOperationException ("Please add support for type '"+m_aType+"'.");
	}
	
    //--------------------------------------------------------------------------
	private Class< ? extends Number > m_aType = null;
	
    //--------------------------------------------------------------------------
	private String m_sValue = null;
	
    //--------------------------------------------------------------------------
	private Double m_fValue = null;
	
    //--------------------------------------------------------------------------
	private boolean m_bNegative = false;
}
