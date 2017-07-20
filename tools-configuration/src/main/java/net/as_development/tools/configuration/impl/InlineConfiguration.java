package net.as_development.tools.configuration.impl;

import java.util.HashMap;
import java.util.Map;

import net.as_development.asdk.tools.common.type.TypeConverter;
import net.as_development.tools.configuration.IConfigurationSet;
import net.as_development.tools.configuration.IInlineConfiguration;

//=============================================================================
public class InlineConfiguration implements IInlineConfiguration
{
	//-------------------------------------------------------------------------
	public InlineConfiguration ()
		throws Exception
	{}

	//-------------------------------------------------------------------------
	@Override
	public < T > T get(final String     sKey ,
					   final Class< T > aType)
		throws Exception
	{
		final Map< String, Object > lData     = mem_Data ();
		final Object                aOrgValue = lData.get(sKey);
		final T                     aValue    = (T) TypeConverter.mapValue(aOrgValue, aType);
	    return aValue;
	}

	//-------------------------------------------------------------------------
	@Override
	public < T > T get(final String     sKey    ,
					   final Class< T > aType   ,
					   final T          aDefault)
		throws Exception
	{
		T aValue = (T) get (sKey, aType);
		if (aValue == null)
			aValue = aDefault;
		return aValue;
	}

	//-------------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	@Override
	public < T, R extends IConfigurationSet > R set(final String     sKey  ,
									   			    final Class< T > aType ,
									   			    final T          aValue)
		throws Exception
	{
		final Map< String, Object > lData = mem_Data ();
		
		if (aValue == null)
		{
			if (lData.containsKey(sKey))
				lData.remove(sKey);
		}
		else
		{
			lData.put(sKey, aValue);
		}

		return (R) this;
	}

	//-------------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	public < T, R extends IConfigurationSet > R set (final String sKey  ,
			 									     final T      aValue)
        throws Exception
    {
		Class< T > aType = (Class< T >) Object.class;
		if (aValue != null)
			aType = (Class< T >) aValue.getClass();
		return (R) set (sKey, aType, aValue);
    }

	//-------------------------------------------------------------------------
	private Map< String, Object > mem_Data ()
	   throws Exception
	{
		if (m_lData == null)
			m_lData = new HashMap< String, Object > ();
		return m_lData;
	}

	//-------------------------------------------------------------------------
	private Map< String, Object > m_lData = null;
}
