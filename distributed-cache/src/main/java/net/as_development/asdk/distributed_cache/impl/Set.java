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
package net.as_development.asdk.distributed_cache.impl;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

//=============================================================================
public class Set extends Message
{
	//-------------------------------------------------------------------------
	private Set ()
	    throws Exception
	{
		super.setAction(Message.ACTION_SET);
	}
	
	//-------------------------------------------------------------------------
	public static Set newSet (final String sKey  ,
							  final String sValue)
		throws Exception
	{
		final Set aSet = new Set ();
		aSet.set(sKey, sValue);
		return aSet;
	}

	//-------------------------------------------------------------------------
	public static Set fromMessage (final Message aMsg)
		throws Exception
	{
		final String   sData  = aMsg.getData();
		final String[] lParts = StringUtils.splitPreserveAllTokens(sData, '=');
		final Set      aSet   = new Set ();
		aSet.m_sKey   = lParts[0];
		aSet.m_sValue = lParts[1];
		return aSet;
	}

	//-------------------------------------------------------------------------
	public void set (final String sKey  ,
					 final String sValue)
	    throws Exception
	{
		m_sKey   = sKey  ;
		m_sValue = sValue;
		
		final StringBuffer sData = new StringBuffer (256);
		sData.append(sKey  );
		sData.append("="   );
		sData.append(sValue);
		setData (sData.toString ());
	}

	//-------------------------------------------------------------------------
	public String getKey ()
		throws Exception
	{
		return m_sKey;
	}

	//-------------------------------------------------------------------------
	public String getValue ()
		throws Exception
	{
		return m_sValue;
	}

	//-------------------------------------------------------------------------
	private String m_sKey = null;

	//-------------------------------------------------------------------------
	private String m_sValue = null;
}
