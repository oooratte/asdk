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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

//=============================================================================
/** collection of helper functions for compress/decompress strings ...
 */
public class StringCompress
{
	//-------------------------------------------------------------------------
	// static helper only - instances of this class not needed
	private StringCompress ()
	{}

	//-------------------------------------------------------------------------
	/** compress the given string.
	 * 
	 *	@param	sIn [IN]
	 *			the string for compression
	 *
	 *	@param	sEncoding []
	 *			the encoding of the given input string
	 *
	 *	@return	a base64 encoded string containing the compressed data.
	 */
	public static String compress (final String sIn      ,
								   final String sEncoding)
		throws Exception
	{
		ByteArrayOutputStream aSink     = null;
		DeflaterOutputStream  aCompress = null;
        
        try
        {
        	final byte[] lRawIn = sIn.getBytes(sEncoding);
        	
	        aSink     = new ByteArrayOutputStream(     );
	        aCompress = new DeflaterOutputStream (aSink);

	        aCompress.write(lRawIn);
	        aCompress.close(      );

	        final byte[] lRawOut = aSink.toByteArray();
	        final String sOut    = Base64.encodeBase64String(lRawOut);
	        return sOut;
        }
        finally
        {
        	IOUtils.closeQuietly(aSink    );
        	IOUtils.closeQuietly(aCompress);
        }
	}

	//-------------------------------------------------------------------------
	/** uncompress the given string.
	 * 
	 *	@param	sIn [IN]
	 *			the string for uncompress
	 *
	 *	@param	sEncoding []
	 *			the encoding of the resulting output string
	 *
	 *	@return	a uncompressed string.
	 */
	public static String uncompress (final String sIn      ,
			   						 final String sEncoding)
		throws Exception
	{
		ByteArrayInputStream  aIn         = null;
		ByteArrayOutputStream aSink       = null;
		InflaterInputStream   aUncompress = null;
        
        try
        {
        	final byte[] lRawIn = Base64.decodeBase64(sIn);

        	aIn         = new ByteArrayInputStream (lRawIn);
	        aSink       = new ByteArrayOutputStream(      );
	        aUncompress = new InflaterInputStream  (aIn   );

	        IOUtils.copy(aUncompress, aSink);

	        final byte[] lRawOut = aSink.toByteArray ();
	        final String sOut    = new String(lRawOut, sEncoding);
	        return sOut;
        }
        finally
        {
        	IOUtils.closeQuietly(aIn        );
        	IOUtils.closeQuietly(aUncompress);
        	IOUtils.closeQuietly(aSink      );
        }
	}
}
