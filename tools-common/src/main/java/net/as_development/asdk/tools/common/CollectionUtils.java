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

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

public class CollectionUtils
{
	//-------------------------------------------------------------------------
	private CollectionUtils ()
	{}

	//-------------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	public static < T > T[] listToArray (final List< T > aList,
			final Class< T > aType)
	{
		final int c      = aList.size();
		final T[] aArray = (T[])Array.newInstance(aType, c);
		
		for (int i=0; i<c; ++i)
		{
			final T aEntry    = aList.get(i);
			        aArray[i] = aEntry;
		}
		
		return (T[]) aArray;
	}

	//-------------------------------------------------------------------------
	public static < T > String toString (final T[]  aArray    ,
									     final char aSeparator)
		throws Exception
	{
		final StringBuffer sString = new StringBuffer (256);
		sString.append ("["                                 );
		sString.append (StringUtils.join(aArray, aSeparator));
		sString.append ("]"                                 );
		return sString.toString ();
	}

	//-------------------------------------------------------------------------
	public static String formatAsProperties (final Map< String, String > aMap)
		throws Exception
	{
		final StringBuffer                       sString  = new StringBuffer (256);
		final Iterator< Entry< String, String >> rEntries = aMap.entrySet().iterator();
		while (rEntries.hasNext())
		{
			final Entry< String, String > rEntry = rEntries.next();
			final String                  sKey   = rEntry.getKey();
			final String                  sValue = rEntry.getValue();
			sString.append(sKey  );
			sString.append("="   );
			sString.append(sValue);
			sString.append("\n"  );
		}
		return sString.toString ();
	}

	//-------------------------------------------------------------------------
	public static boolean isEmpty (final Collection< ? > aCollection)
	    throws Exception
	{
		if (aCollection == null)
			return true;
		
		return aCollection.isEmpty();
	}

	//-------------------------------------------------------------------------
	public static Map< String, String > flat2MappedArguments (final String... lFlat)
	    throws Exception
	{
		final Map< String, String > aMap = new HashMap< String, String > ();

		if (lFlat == null)
			return aMap;
		
		final int nLastItem = lFlat.length-1;
		if (nLastItem < 1)
			return aMap;
		
		int nItem = 0;
		while (true)
		{
			final int nKeyItem   = nItem  ;
			final int nValueItem = nItem+1;
			
			if (nValueItem > nLastItem)
				break;

			final String sKey   = lFlat[nKeyItem  ];
			final String sValue = lFlat[nValueItem];
			
			if (StringUtils.isEmpty(sKey))
				continue;
			
			aMap.put(sKey, sValue);
			
			nItem += 2;
		}
		
		return aMap;
	}
}
