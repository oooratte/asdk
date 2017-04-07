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
package net.as_development.asdk.monitoring.app.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

//=============================================================================
public class ConfigUtils
{
    //-------------------------------------------------------------------------
    private ConfigUtils ()
    {}

    //-------------------------------------------------------------------------
    public static Integer readInt (final Map< String, String > aConfig ,
    							   final String                sKey    ,
    							   final Integer               aDefault)
        throws Exception
    {
    	Integer nValue = null;
    	try
    	{
    		nValue = Integer.parseInt(aConfig.get(sKey));
    	}
    	catch (Throwable ex)
    	{
    		nValue = aDefault;
    	}
    	return nValue;
    }

    //-------------------------------------------------------------------------
    public static Integer readInt (final String  sValue  ,
    							   final Integer aDefault)
        throws Exception
    {
    	Integer nValue = null;
    	try
    	{
    		nValue = Integer.parseInt(sValue);
    	}
    	catch (Throwable ex)
    	{
    		nValue = aDefault;
    	}
    	return nValue;
    }

    //-------------------------------------------------------------------------
    public static Boolean readBoolean (final String  sValue  ,
    							       final Boolean aDefault)
        throws Exception
    {
    	Boolean bValue = null;
    	try
    	{
    		bValue = Boolean.parseBoolean(sValue);
    	}
    	catch (Throwable ex)
    	{
    		bValue = aDefault;
    	}
    	return bValue;
    }

    //-------------------------------------------------------------------------
    public static List< Integer>  readIntList (final Map< String, String > aConfig  ,
    								           final String                sKey     ,
    								           final Integer...            lDefaults)
        throws Exception
    {
    	final String          sValue = aConfig.get (sKey);
    	final List< Integer > lList  = readIntList (sValue, lDefaults);
    	return lList;
    }

    //-------------------------------------------------------------------------
    public static List< Integer>  readIntList (final String     sConfigValue,
    								           final Integer... lDefaults   )
        throws Exception
    {
    	final List< Integer > lList = new ArrayList< Integer > ();
    	try
    	{
    		final String[] lListItems = StringUtils.splitPreserveAllTokens(sConfigValue, ",");
    		
    		for (final String sListItem : lListItems)
    		{
    			if (StringUtils.isEmpty(sListItem))
    				continue;

    			final Integer nValue = Integer.parseInt(sListItem);
    			lList.add (nValue);
    		}
    	}
    	catch (Throwable ex)
    	{
    		lList.clear ();

    		if (lDefaults != null)
    		{
    			for (final Integer nDefault : lDefaults)
    				lList.add (nDefault);
    		}
    	}
    	return lList;
    }
    //-------------------------------------------------------------------------
    public static Pair< Integer, Integer >  readIntRange (final Map< String, String > aConfig,
    								                      final String                sKey   )
        throws Exception
    {
    	final String   sValue  = aConfig.get (sKey);
    	final String[] lValues = StringUtils.split(sValue, "-");
    	
    	if (lValues == null)
    		return null;
    	
    	if (lValues.length != 2)
    		throw new IllegalArgumentException ("Illegal format of configuration item '"+sKey+"' with value '"+sValue+"'.");
    	
    	final String                   sStart = lValues[0];
    	final String                   sEnd   = lValues[1];
    	final Integer                  nStart = Integer.parseInt(sStart);
    	final Integer                  nEnd   = Integer.parseInt(sEnd  );
    	final Pair< Integer, Integer > aRange = new ImmutablePair< Integer, Integer > (nStart, nEnd);
    	
    	return aRange;
    }
}
