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
package net.as_development.asdk.jms.core.beans;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

//=============================================================================
public class JMSBeanMapping< T extends JMSMessageBean >
{
	//-------------------------------------------------------------------------
	private JMSBeanMapping ()
		throws Exception
	{}

	//-------------------------------------------------------------------------
	public static < T extends JMSMessageBean > JMSBeanMapping< T > create (final Class< T > aBeanClass)
	    throws Exception
	{
		JMSBeanMapping< T > aMapping = new JMSBeanMapping< T > ();
		aMapping.m_aBeanClass = aBeanClass;
		aMapping.impl_describe (aBeanClass);
		return aMapping;
	}
	
	//-------------------------------------------------------------------------
	public Field accessBody ()
		throws Exception
	{
		return m_aBodyField;
	}

	//-------------------------------------------------------------------------
	public Set< String > listHeader ()
	    throws Exception
	{
		final Set< String > lHeader = new HashSet< String > ();
		lHeader.addAll(mem_HeaderFields ().keySet());
		return lHeader;
	}

	//-------------------------------------------------------------------------
	public Set< String > listProperties ()
	    throws Exception
	{
		final Set< String > lProps = new HashSet< String > ();
		lProps.addAll(mem_PropertyFields ().keySet());
		return lProps;
	}
	
	//-------------------------------------------------------------------------
	public Field accessHeader (final String sName)
		throws Exception
	{
		final Map< String, Field > lHeader = mem_HeaderFields ();
		return lHeader.get(sName);
	}

	//-------------------------------------------------------------------------
	public Field accessProperty (final String sName)
		throws Exception
	{
		final Map< String, Field > lHeader = mem_HeaderFields ();
		return lHeader.get(sName);
	}

	//-------------------------------------------------------------------------
	private void impl_describe (final Class< ? > aBeanClass)
		throws Exception
	{
		if (aBeanClass == null)
			return;
		
		final Map< String, Field > lHeader = mem_HeaderFields ();
		final Map< String, Field > lProps  = mem_PropertyFields ();
		final Field[]              lFields = aBeanClass.getDeclaredFields ();

		for (final Field aField : lFields)
		{
			final JMSBody     aJMSBody     = aField.getAnnotation(JMSBody    .class);
			final JMSHeader   aJMSHeader   = aField.getAnnotation(JMSHeader  .class);
			final JMSProperty aJMSProperty = aField.getAnnotation(JMSProperty.class);

			if (aJMSProperty != null)
			{
				final String sName = aJMSProperty.name();
				
				Validate.isTrue( ! lHeader.containsKey(sName)             , "Found double registration of JMSProperty field '"+sName+"' in bean '"+m_aBeanClass+"' ! Thats not supported.");
				Validate.isTrue( StringUtils.containsNone(sName, '.', '-'), "Illegal character in JMSProperty '"+sName+"' detected.");
				
				lHeader.put (sName, aField);
				aField.setAccessible(true);
			}
			else
			if (aJMSHeader != null)
			{
				final String sName = aJMSHeader.name();
				
				Validate.isTrue( ! lHeader.containsKey(sName)             , "Found double registration of JMSHeader field '"+sName+"' in bean '"+m_aBeanClass+"' ! Thats not supported.");
				Validate.isTrue( StringUtils.containsNone(sName, '.', '-'), "Illegal character in JMSHeader '"+sName+"' detected.");
				
				lHeader.put (sName, aField);
				aField.setAccessible(true);
			}
			else
			if (aJMSBody != null)
			{
				Validate.isTrue(m_aBodyField==null, "Found more then one body field in bean '"+m_aBeanClass+"' ! Thats not supported.");
				
				m_aBodyField = aField;
				aField.setAccessible(true);
			}
		}
		
		impl_describe (aBeanClass.getSuperclass());
	}
	
	//-------------------------------------------------------------------------
	private Map< String, Field > mem_HeaderFields ()
		throws Exception
	{
		if (m_lHeaderFields == null)
			m_lHeaderFields = new HashMap< String, Field > ();
		return m_lHeaderFields;
	}

	//-------------------------------------------------------------------------
	private Map< String, Field > mem_PropertyFields ()
		throws Exception
	{
		if (m_lPropertyFields == null)
			m_lPropertyFields = new HashMap< String, Field > ();
		return m_lPropertyFields;
	}

	//-------------------------------------------------------------------------
	private Class< T > m_aBeanClass;

	//-------------------------------------------------------------------------
	private Field m_aBodyField = null;
	
	//-------------------------------------------------------------------------
	private Map< String, Field > m_lHeaderFields = null;

	//-------------------------------------------------------------------------
	private Map< String, Field > m_lPropertyFields = null;
}
