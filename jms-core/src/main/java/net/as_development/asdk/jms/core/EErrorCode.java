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
package net.as_development.asdk.jms.core;

import org.apache.commons.lang3.StringUtils;

//=============================================================================
public enum EErrorCode
{
    //-------------------------------------------------------------------------
	E_OK,
	E_GENERAL,
	E_TIMEOUT,
	E_CUSTOM;
	
    //-------------------------------------------------------------------------
	public static final String STR_OK      = E_OK     .name();
	public static final String STR_GENERAL = E_GENERAL.name();
	public static final String STR_TIMEOUT = E_TIMEOUT.name();
	public static final String STR_CUSTOM  = E_CUSTOM .name();

    //-------------------------------------------------------------------------
	public static EErrorCode fromString (final String sResult)
	    throws Exception
	{
		EErrorCode eResult = null;

		if (StringUtils.equalsIgnoreCase(sResult, STR_OK))
			eResult = E_OK;
		else
		if (StringUtils.equalsIgnoreCase(sResult, STR_GENERAL))
			eResult = E_GENERAL;
		else
		if (StringUtils.equalsIgnoreCase(sResult, STR_TIMEOUT))
			eResult = E_TIMEOUT;
		else
		if (StringUtils.equalsIgnoreCase(sResult, STR_CUSTOM))
			eResult = E_CUSTOM;
		else
			throw new UnsupportedOperationException ("no support for '"+sResult+"' implemented yet");

		return eResult;
	}

    //-------------------------------------------------------------------------
	public static String toString (final EErrorCode eResult)
	    throws Exception
	{
		String sResult = null;
		
		if (eResult == E_OK)
			sResult = STR_OK;
		else
		if (eResult == E_GENERAL)
			sResult = STR_GENERAL;
		else
		if (eResult == E_TIMEOUT)
			sResult = STR_TIMEOUT;
		else
		if (eResult == E_CUSTOM)
			sResult = STR_CUSTOM;
		else
			throw new UnsupportedOperationException ("no support for '"+eResult+"' implemented yet");
		
		return sResult;
	}
}
