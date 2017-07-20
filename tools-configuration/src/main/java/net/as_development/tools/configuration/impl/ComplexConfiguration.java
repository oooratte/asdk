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
package net.as_development.tools.configuration.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.lang3.Validate;

import net.as_development.asdk.tools.common.type.TypeConverter;
import net.as_development.tools.configuration.IComplexConfiguration;

//=============================================================================
public class ComplexConfiguration implements IComplexConfiguration
{
	//-------------------------------------------------------------------------
	public ComplexConfiguration ()
	    throws Exception
	{}

	//-------------------------------------------------------------------------
	public void bindStore4Reading (final Configuration aStore)
	    throws Exception
	{
		m_aStore4Reading = aStore;
	}
	
	//-------------------------------------------------------------------------
	@Override
	public < T > T get(final String     sKey ,
					   final Class< T > aType)
		throws Exception
	{
		final T aValue = (T) get (sKey, aType, (T)null);
		return  aValue;
	}

	//-------------------------------------------------------------------------
	@Override
	public < T > T get(final String     sKey    ,
			           final Class< T > aType   ,
					   final T          aDefault)
		throws Exception
	{
		Object aValue = m_aStore4Reading.getString(sKey); // only getString() supports variable interpolation !

		if (aValue == null)
			aValue = aDefault;
		
		final T aTypedValue = (T) impl_mapValueToType (aValue, aType);
		return aTypedValue;
	}

	//-------------------------------------------------------------------------
	@Override
	public Set< Map< String, String > > gets (final String sKey ,
            							          final String sType)
        throws Exception
	{
		Validate.notEmpty(sKey , "Invalid argument 'key'." );
		Validate.notEmpty(sType, "Invalid argument 'type'.");
		
		final Set< Map< String, String > > lValues   = new HashSet< Map< String, String > > ();
		final String                       sSelector = sKey + "." +sType;
		
		final List< HierarchicalConfiguration > aSubSet = ((HierarchicalConfiguration) m_aStore4Reading).configurationsAt(sSelector);
		for (final HierarchicalConfiguration aSub : aSubSet)
		{
			final Map< String, String > lSubData = new HashMap< String, String > ();
			final Iterator< String >    rKeys    = aSub.getKeys();

			while (rKeys.hasNext())
			{
				final String sSubKey   = rKeys.next ();
				final String sSubValue = aSub.getString(sSubKey);
				lSubData.put(sSubKey, sSubValue);
			}

			lValues.add(lSubData);
		}
		
		return lValues;
	}
	
	//-------------------------------------------------------------------------
	@Override
	public List< String > getAllRecursive (final String sKeyValueSeparator)
			throws Exception
	{
		final List< String > lAll = new Vector< String > ();
		impl_collectAll (((HierarchicalConfiguration) m_aStore4Reading).getRootNode(), "", sKeyValueSeparator, lAll);
		return lAll;
	}

	//-------------------------------------------------------------------------
	private void impl_collectAll (final ConfigurationNode aConfig   ,
							      final String            sPath     ,
							      final String            sSeparator,
							      final List< String >    lData     )
		throws Exception
	{
		if (aConfig == null)
			return;
		
		final String sName         = aConfig.getName ();
	          String sAbsolutePath = sPath;
		if (sName != null)
		{
		    sAbsolutePath += ".";
		    sAbsolutePath += sName;
			
		    final Object aValue = aConfig.getValue();
			lData.add(sAbsolutePath+sSeparator+aValue);
		}

		final List< ConfigurationNode > lAttributes = aConfig.getAttributes();
		if (lAttributes != null)
		{
			for (final ConfigurationNode aAttribute : lAttributes)
				impl_collectAll (aAttribute, sAbsolutePath, sSeparator, lData);
		}

		final List< ConfigurationNode > lChilds = aConfig.getChildren();
		if (lChilds != null)
		{
			for (final ConfigurationNode aChild : lChilds)
				impl_collectAll (aChild, sAbsolutePath, sSeparator, lData);
		}
	}
	
	//-------------------------------------------------------------------------
	public < T > T impl_mapValueToType (final Object     aValue,
										final Class< T > aType )
	    throws Exception
	{
		return (T) TypeConverter.mapValue(aValue, aType);
	}

	//-------------------------------------------------------------------------
	private Configuration m_aStore4Reading = null;
}
