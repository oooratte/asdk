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
package net.as_development.asdk.tools.common.type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

//=============================================================================
public class StringConvertibleUtils
{
	//-------------------------------------------------------------------------
	public static final String SEPARATOR_VALUES      = "%|%"   ;
	public static final String SEPARATOR_COLLECTIONS = "%C%"   ;
	public static final String SEPARATOR_MAPS        = "%M%"   ;
	public static final String SEPARATOR_KEYVALUE    = "%KV%"  ;
	public static final String SEPARATOR_TYPE        = "%T%"   ;
	public static final String ENCODING              = "utf-8" ;
	public static final int    BUFSIZE               = 4096    ;
	public static final String NULL_TYPE             = "<null>";

	//-------------------------------------------------------------------------
	public static String convertToString (final Object... lValues)
	    throws Exception
	{
		if (lValues == null)
			return null;
		
		final List< Object > aValueList = new ArrayList< Object > ();
		for (final Object aValue : lValues)
			aValueList.add(aValue);

		final String sB64 = StringConvertibleUtils.convertToString (aValueList);
		return sB64;
	}
	
	//-------------------------------------------------------------------------
	public static String convertToString (final List< Object > lValues)
	    throws Exception
	{
		final String sB64 = impl_convertToString(lValues, SEPARATOR_VALUES);
		return sB64;
	}

	//-------------------------------------------------------------------------
	public static List< Object > convertFromString (final String sB64)
	    throws Exception
	{
		final List< Object > lValues = impl_convertFromString (sB64, SEPARATOR_VALUES, SEPARATOR_TYPE, /*complex parts*/false);
		return lValues;
	}

	//-------------------------------------------------------------------------
	private static < T > String impl_convertToString (final Collection< T > lValues   ,
											          final String          sSeparator)
	    throws Exception
	{
		final StringBuffer sBuf          = new StringBuffer (256);
			  boolean      bAddSeparator = false;

		if (lValues == null)
			return null;
			  
		for (final Object aValue : lValues)
		{
		    if (bAddSeparator)
				sBuf.append(sSeparator);
		    else
		    	bAddSeparator = true;

		    final Class< ? > aType  = aValue != null ? aValue.getClass() : null     ;
		    final String     sType  = aType  != null ? aType .getName () : NULL_TYPE;
		          String     sValue = null;

	  		if (aType == null)
	  		{
	  			sValue = "";
	  		}
	  		else
		    if (Collection.class.isAssignableFrom(aType))
		    {
		    	sValue = impl_convertToString ((Collection< ? >)aValue, SEPARATOR_COLLECTIONS);
		    }
	  		else
		    if (Map.class.isAssignableFrom(aType))
		    {
		    	sValue = impl_convertToString ((Map< ?, ? >)aValue, SEPARATOR_MAPS);
		    }
		    else
		    {
		    	sValue = TypeConverter.toString(aValue);
		    }
		    
		    final String sFull = StringUtils.join(new String[] {sType, sValue}, SEPARATOR_TYPE);
		    sBuf.append(sFull);
		}

		final String sRaw = sBuf.toString ();
		final String sB64 = Base64.encodeBase64String(sRaw.getBytes(ENCODING));
		
		return sB64;
	}

	//-------------------------------------------------------------------------
	private static < K, V > String impl_convertToString (final Map< K, V > aMap      ,
											             final String      sSeparator)
	    throws Exception
	{
		final StringBuffer sBuf          = new StringBuffer (256);
			  boolean      bAddSeparator = false;

		if (aMap == null)
			return null;
		
		final Iterator< Entry< K, V > > rEntries = aMap.entrySet().iterator();
		while (rEntries.hasNext())
		{
			final Entry< K, V > aEntry = rEntries.next  ();
			final K             aKey   = aEntry.getKey  ();
			final V             aValue = aEntry.getValue();
			final String        sKey   = convertToString (aKey  );
			final String        sValue = convertToString (aValue);
			
		    if (bAddSeparator)
				sBuf.append(sSeparator);
		    else
		    	bAddSeparator = true;

		    final String sFull = StringUtils.join(new String[] {sKey, sValue}, SEPARATOR_KEYVALUE);
		    sBuf.append(sFull);
		}

		final String sRaw = sBuf.toString ();
		final String sB64 = Base64.encodeBase64String(sRaw.getBytes(ENCODING));
		
		return sB64;
	}

	//-------------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	private static List< Object > impl_convertFromString (final String  sB64         ,
														  final String  sSeparator   ,
														  final String  sSeparator2  ,
														  final boolean bComplexParts)
	    throws Exception
	{
		final List< Object > lValues = new ArrayList< Object > ();
		final String         sRaw    = new String(Base64.decodeBase64(sB64), ENCODING);
		      String[]       lParts  = null;

		if (StringUtils.isEmpty(sSeparator))
			lParts = new String[] {sRaw};
		else
			lParts = StringUtils.splitByWholeSeparatorPreserveAllTokens(sRaw, sSeparator);

		for (final String sPart : lParts)
		{
			final String[] lTypeValue = StringUtils.splitByWholeSeparatorPreserveAllTokens(sPart, sSeparator2);
			if (lTypeValue.length != 2)
				continue;
			
			String sType  = lTypeValue[0];
			String sValue = lTypeValue[1];
			Object aValue = null;

			if (bComplexParts)
			{
				lValues.add(sType );
				lValues.add(sValue);
			}
			else
			if (StringUtils.equals(sType, NULL_TYPE))
			{
				lValues.add (null);
			}
			else
			{
				final Class< ? > aType = Class.forName(sType);

				if (Collection.class.isAssignableFrom(aType))
				{
					final List< Object >       lCollectionValues = impl_convertFromString (sValue, SEPARATOR_COLLECTIONS, SEPARATOR_TYPE, /*complex parts*/false);
					final Collection< Object > aCollection       = (Collection< Object >) aType.newInstance();
					aCollection.addAll(lCollectionValues);
					aValue = aCollection;
				}
				else
				if (Map.class.isAssignableFrom(aType))
				{
					final List< Object >        lMapValues = impl_convertFromString (sValue, SEPARATOR_MAPS, SEPARATOR_KEYVALUE, /*complex parts*/true);
					final Map< Object, Object > aMap       = (Map< Object, Object >) aType.newInstance();
					final int                   nLastIndex = lMapValues.size() - 1;
						  int                   i          = 0;

					while (true)
					{
						if (i > nLastIndex)
							break;

						final String         sMapKey   = (String) lMapValues.get(i++);
						final String         sMapValue = (String) lMapValues.get(i++);
						final List< Object > lMapKey   = impl_convertFromString (sMapKey  , "", SEPARATOR_TYPE, /*complex parts*/false);
						final List< Object > lMapValue = impl_convertFromString (sMapValue, "", SEPARATOR_TYPE, /*complex parts*/false);
						final Object         aMapKey   = lMapKey  .get(0);
						final Object         aMapValue = lMapValue.get(0);

						aMap.put(aMapKey, aMapValue);
					}
					
					aValue = aMap;
				}
				else
				{
					aValue = TypeConverter.fromString(sValue, aType);
				}
				lValues.add (aValue);				
			}
		}
		
		return lValues;
	}
}
