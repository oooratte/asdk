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
package net.as_development.asdk.tools.reflection;

import java.io.Serializable;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

//=============================================================================
public class SerializationUtils extends org.apache.commons.lang3.SerializationUtils
{
	//-------------------------------------------------------------------------
	private SerializationUtils ()
	{}

	//-------------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	public static < T extends Serializable > T mapString2Object (final String sString)
	    throws Exception
	{
		if (StringUtils.isEmpty(sString))
			return (T) null;

		final byte[] lRawData = Base64.decodeBase64(sString);
		final T      aObject  = (T) SerializationUtils.deserialize(lRawData);
		
		return aObject;
	}
	
	//-------------------------------------------------------------------------
	public static < T extends Serializable > String mapObject2String (final T aObject)
	    throws Exception
	{
		if (aObject == null)
			return null;
		
		final byte[] lRawData = SerializationUtils.serialize(aObject);
		final String sString  = Base64.encodeBase64String(lRawData);
		
		return sString;
	}
}