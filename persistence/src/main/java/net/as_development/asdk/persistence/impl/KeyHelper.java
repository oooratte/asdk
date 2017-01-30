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
package net.as_development.asdk.persistence.impl;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

//=============================================================================
public class KeyHelper
{
	//-------------------------------------------------------------------------
	public static final String KEY_SEPARATOR = ".";

	//-------------------------------------------------------------------------
	private KeyHelper ()
	{}

	//-------------------------------------------------------------------------
	public static String nameKey (final String... lKeys)
		throws Exception
	{
		final StringBuffer sFullKey      = new StringBuffer (256);
		      boolean      bAddSeparator = false;
		
		for (final String sKey : lKeys)
		{
			if (StringUtils.isEmpty(sKey))
				continue;
			
			if (bAddSeparator)
				sFullKey.append(KeyHelper.KEY_SEPARATOR);
			else
				bAddSeparator = true;
			
			sFullKey.append(sKey);
		}
		
		return sFullKey.toString();
	}

	//-------------------------------------------------------------------------
	public static String makeKeyAbsolute (final String sScope ,
										  final String sSubSet,
										  final String sRelKey)
	    throws Exception
	{
		final String sFullKey = nameKey (sScope, sSubSet, sRelKey);
		return sFullKey;
	}

	//-------------------------------------------------------------------------
	/** @return the relative key for the given full key
	 *          (stripped from also given scope and subset)
	 *          
	 *  Note: If full key do not fit into given scope and subset ...
	 * 
	 *  a) ... and no scope nor subset is defined :
	 *     the full key is the relative key already and will be returned.
	 * 
	 *  b) ... but scope or subset are defined :
	 *     an error is thrown.
	 * 
	 *	@param	sScope [IN, OPTIONAL]
	 *			the scope where this key is bound to.
	 *			Can be null/empty if no scope is defined.
	 *
	 *	@param	sSubSet [IN, OPTIONAL]
	 *			the subset where this key is bound to.
	 *			Can be null/empty if no subset is defined.
	 *
	 *	@param	sFullKey [IN]
	 *			the key to be made relative.
	 *
	 *	@throws	an error in case scope/subset are defined ...
	 *        	but full key do not starts with those value.
	 */
	public static String makeKeyRelative (final String sScope  ,
										  final String sSubSet ,
										  final String sFullKey)
	    throws Exception
	{
		final String sKeyPrefix = nameKey (sScope, sSubSet) + KEY_SEPARATOR;
		
		// a) full key without scope/subset is the relative key !
		if (StringUtils.equals(sKeyPrefix, KEY_SEPARATOR))
			return sFullKey;

		// b) scope/subset was defined ... but key do not fit into
		Validate.isTrue (StringUtils.startsWith(sFullKey, sKeyPrefix), "Full key '"+sFullKey+"' is out of scope/subset '"+sKeyPrefix+"'.");

		// c) ok. make it relative
		final String sRelKey = StringUtils.removeStart(sFullKey, sKeyPrefix);
		return sRelKey;
	}

	//-------------------------------------------------------------------------
	public static boolean isAbsoluteKeyInScopeSubset (final String sScope  ,
										              final String sSubSet ,
										              final String sFullKey)
	    throws Exception
	{
		final String sKeyPrefix = nameKey (sScope, sSubSet) + KEY_SEPARATOR;
		
		// a) no scope/subset defined ... key is "in scope by definition" ;-)
		if (StringUtils.equals(sKeyPrefix, KEY_SEPARATOR))
			return true;
		
		final boolean bIsInScopeSubset = StringUtils.startsWith(sFullKey, sKeyPrefix);
		return bIsInScopeSubset;
	}
}
