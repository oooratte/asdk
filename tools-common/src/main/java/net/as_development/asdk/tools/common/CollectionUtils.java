/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
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
