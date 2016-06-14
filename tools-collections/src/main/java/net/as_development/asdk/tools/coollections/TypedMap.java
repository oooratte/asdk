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
package net.as_development.asdk.tools.coollections;

import java.util.HashMap;
import java.util.Map;

//=============================================================================
public class TypedMap
{
	//-------------------------------------------------------------------------
	public TypedMap ()
		throws Exception
	{}

	//-------------------------------------------------------------------------
	public < T > void set (final String sKey  ,
						   final T      aValue)
		throws Exception
	{
		final Map< String, Object > aMap = mem_Map ();
		aMap.put (sKey, aValue);
	}
	
	//-------------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	public < T > T get (final String sKey)
		throws Exception
	{
		final Map< String, Object > aMap   = mem_Map ();
		final T                     aValue = (T) aMap.get(sKey);
		return aValue;
	}

	//-------------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	public < T > T get (final String sKey    ,
						final T      aDefault)
		throws Exception
	{
		final T aValue = (T) get (sKey);
		if (aValue != null)
			return aValue;
		return aDefault;
	}

	//-------------------------------------------------------------------------
	public < T extends Number > void inc (final String     sKey ,
										  final Class< T > aType)
		throws Exception
	{
		final Map< String, Object > aMap    = mem_Map ();
			  Number                aNumber = (Number) aMap.get(sKey);
		
		if (aNumber == null)
			aNumber = aType.newInstance();

		
	}
	
	//-------------------------------------------------------------------------
	private Map< String, Object > mem_Map ()
		throws Exception
	{
		if (m_aMap == null)
			m_aMap = new HashMap< String, Object > ();
		return m_aMap;
	}

	//-------------------------------------------------------------------------
	private Map< String, Object > m_aMap = null;
}
