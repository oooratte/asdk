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

import org.apache.commons.configuration.Configuration;

import net.as_development.tools.configuration.ISimpleConfiguration;

//=============================================================================
public class SimpleConfiguration implements ISimpleConfiguration
{
	//-------------------------------------------------------------------------
	public SimpleConfiguration ()
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
		Object aValue = m_aStore4Reading.getProperty(sKey);

		if (aValue == null)
			aValue = aDefault;
		
		final T aTypedValue = (T) impl_mapValueToType (aValue, aType);
		return  aTypedValue;
	}

	//-------------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	public < T > T impl_mapValueToType (final Object     aValue,
										final Class< T > aType )
	    throws Exception
	{
		return (T) aValue;
	}

	//-------------------------------------------------------------------------
	private Configuration m_aStore4Reading = null;
}
