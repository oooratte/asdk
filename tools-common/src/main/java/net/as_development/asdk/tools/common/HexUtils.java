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

import org.apache.commons.lang3.ArrayUtils;

public class HexUtils
{
	//-------------------------------------------------------------------------
	public static int hexToDecimal (final int nHex)
		throws Exception
	{
		return nHex;
	}
	
	//-------------------------------------------------------------------------
	public static int hexToDecimal (final byte[] lHex)
		throws Exception
	{
		      long nResult = 0;
		final int  c       = lHex.length;
		      int  i       = 0;
		      int  nPow    = 0;

		// hex numbers are read from right to left ;-)
		ArrayUtils.reverse(lHex);
		
		for (i=0; i<c; i++)
		{
			// it can happen somewhere set 0xFF for one byte in array
			// -> then we need Low AND High byte of this !

			// but in case somewhere set 0xF only
			// -> High value will be null within the following calculation !
			
			final long nPure    = lHex[i];
			final long nDecL    =  (nPure & 0x0F);
			final long nDecH    = ((nPure & 0xF0) >>> 4);
			final long n16L     = (long) Math.pow(16, nPow+0);
			final long n16H     = (long) Math.pow(16, nPow+1);
			final long nValL    = (n16L * nDecL);
			final long nValH    = (n16H * nDecH);

//			System.out.println("byte : "+nPure);
//			System.out.println("low  : x="+nPow+"  16^x="+n16L+"  dec="+nDecL+"  ="+nValL);
//			System.out.println("high : x="+nPow+"  16^x="+n16H+"  dec="+nDecH+"  ="+nValH);
			
			nResult += nValL + nValH;
			nPow    += 2;
		}
		
		return (int) nResult;
	}

	//-------------------------------------------------------------------------
    public static byte[] convertHex2ByteArray (int nHex)
        throws Exception
    {
    	final byte[] lBuffer = new byte[8];
    	      int    nBytes  = 0;

    	while (nHex != 0)
        {
    		int nByte           = nHex & 0xff;
    		    lBuffer[nBytes] = (byte) nByte;
    		    nHex            = nHex >>> 8;

    		nBytes += 1;
        }

    	final byte[] lBytes = ArrayUtils.subarray(lBuffer, 0, nBytes);
    	                      ArrayUtils.reverse (lBytes);

    	return lBytes;
    }

    //-------------------------------------------------------------------------
    public static byte[] convertStringToByteArray (final String sString)
        throws Exception
    {
    	final byte[] lBytes = sString.getBytes("us-ascii");
    	return lBytes;
    }

	//-------------------------------------------------------------------------
    public static byte calculateCRC (final int     nOffset,
    								 final int     nLength,
    								 final byte... lBytes )
    	throws Exception
    {
    	if (lBytes == null)
    		return 0;

    	final int    nStart  = nOffset;
    	final int    nEnd    = nStart + nLength;
    	final byte[] lSubset = ArrayUtils.subarray(lBytes, nStart, nEnd);
    	final byte   nCRC    = calculateCRC (lSubset);

    	return nCRC;
    }

    //-------------------------------------------------------------------------
    public static byte calculateCRC (final byte... lBytes)
    	throws Exception
    {
    	if (lBytes == null)
    		return 0;

    	int nCRC = 0;
    	
    	for (final byte nByte : lBytes)
    		nCRC += nByte;
    	
    	return (byte) nCRC;
    }
    
    //-------------------------------------------------------------------------
    public static String formatDecimalHex (final int nHex)
        throws Exception
    {
    	final byte[] lBytes = HexUtils.convertHex2ByteArray(nHex  );
    	final String sHex   = HexUtils.formatByteArrayHex  (lBytes);
    	return sHex;
    }
    
    //-------------------------------------------------------------------------
	public static String formatByteArrayHex (final byte... lBytes)
		throws Exception
	{
		final StringBuffer sString = new StringBuffer (256);
		for (final Byte nByte : lBytes)
		{
			final String sHex = String.format("0x%02X", nByte);
			sString.append(sHex);
			sString.append(" " );
		}
		return sString.toString ();
	}
}
