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

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.apache.activemq.command.ActiveMQMessage;
import org.apache.activemq.util.ByteSequence;
import org.apache.commons.lang3.StringUtils;

import net.as_development.asdk.tools.common.type.TypeConverter;

//=============================================================================
public class JMSBeanMapper
{
	//-------------------------------------------------------------------------
	private JMSBeanMapper ()
		throws Exception
	{}

	//-------------------------------------------------------------------------
	public static JMSBeanMapper get ()
	    throws Exception
	{
		if (m_gSingleton == null)
			m_gSingleton = new JMSBeanMapper ();
		return m_gSingleton;
	}

	//-------------------------------------------------------------------------
	public < T extends JMSMessageBean > void mapBeanToMessage (final T                 aBean   ,
								                               final javax.jms.Message aMessage)
	    throws Exception
	{
		aBean.validate ();
		
		final JMSBeanMapping< T > aMapping = impl_getOrCreateBeanMapping (aBean);
		final Set< String >       lHeader  = aMapping.listHeader      ();
		final Set< String >       lProps   = aMapping.listProperties  ();
		final Set< String >       lCustom  = aBean   .listCustomHeader();
		
		for (final String sHeader : lHeader)
		{
			final Field  aField = aMapping.accessHeader(sHeader);
			final String sValue = impl_toString(aBean, aField);
			
			if (sValue == null) // TODO check for empty strings to ?
				continue;
			
			if (StringUtils.startsWith(sHeader, "JMS"))
			{
				if (StringUtils.equals(sHeader, JMSHeader.JMSHEADER_MESSAGEID))
					aMessage.setJMSMessageID(sValue);
				else
				if (StringUtils.equals(sHeader, JMSHeader.JMSHEADER_CORRELATIONID))
					aMessage.setJMSCorrelationID(sValue);
				else
				if (StringUtils.equals(sHeader, JMSHeader.JMSHEADER_REPLYTO))
					// we can't create a real Destination ...
					// there is no JMS Session available ...
					// but header will be converted to string anyway by the underlying frameworks ...
					// so WE do it here already ;-)
					aMessage.setStringProperty("JMSReplyTo", sValue);
				else
					throw new UnsupportedOperationException ("No support for JMS Header '"+sHeader+"' implemented yet.");
			}
			else
			{
				aMessage.setStringProperty(sHeader, sValue);
			}
		}

		for (final String sProp : lProps)
		{
			final Field  aField = aMapping.accessProperty(sProp);
			final String sValue = impl_toString(aBean, aField);
			
			if (sValue == null) // TODO check for empty strings to ?
				continue;
			
			aMessage.setStringProperty(sProp, sValue);
		}

		for (final String sHeader : lCustom)
		{
			final Object aValue = aBean.getCustomHeader(sHeader);
			aMessage.setObjectProperty(sHeader, aValue);
		}
		
		final Object aBody = aBean.getBody();
		if (aBody != null)
		{
			if (aMessage instanceof TextMessage)
				((TextMessage)aMessage).setText(TypeConverter.toString(aBody, aBody.getClass()));
			else
			if (aMessage instanceof ObjectMessage)
				((ObjectMessage)aMessage).setObject((Serializable)aBody);
			else
				throw new UnsupportedOperationException ("no support for message type '"+aMessage.getClass()+"' implemented yet :-)");
		}
	}

	//-------------------------------------------------------------------------
	public static boolean isCustomHeader (final String sHeader)
		throws Exception
	{
		final boolean bIsCustom = StringUtils.startsWithIgnoreCase(sHeader, JMSHeader.JMSHEADER_PREFIX_CUSTOM);
		return bIsCustom;
	}
	
	//-------------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	public < T extends JMSMessageBean > void mapMessageToBean (final javax.jms.Message aMessage,
															   final T                 aBean   )
	    throws Exception
	{
		final JMSBeanMapping< T >   aMapping = impl_getOrCreateBeanMapping (aBean);
		final Enumeration< String > lHeader  = aMessage.getPropertyNames();
		
		// handle custom header
		
		while (lHeader.hasMoreElements())
		{
			final String sHeader = lHeader.nextElement();
			final String sValue  = aMessage.getStringProperty(sHeader);
			final Field  aField  = aMapping.accessHeader(sHeader);

			if (aField != null)
			{
				final Object aValue = impl_fromString (sValue, aField);
				aField.set(aBean, aValue);
			}
			else
			{
				if (isCustomHeader (sHeader))
					aBean.setCustomHeader(sHeader, sValue);
			}
		}
		
		// handle special JMS header
		
		      Field  aField            = null;
		final String sJMSMessageID     = aMessage.getJMSMessageID    ();
		final String sJMSCorrelationID = aMessage.getJMSCorrelationID();
		      Object aJMSReplyTo       = aMessage.getJMSReplyTo      ();
		
		aField = aMapping.accessHeader(JMSHeader.JMSHEADER_MESSAGEID);
		if (aField != null && ! StringUtils.isEmpty(sJMSMessageID))
			aField.set(aBean, sJMSMessageID);

		aField = aMapping.accessHeader(JMSHeader.JMSHEADER_CORRELATIONID);
		if (aField != null && ! StringUtils.isEmpty(sJMSCorrelationID))
			aField.set(aBean, sJMSCorrelationID);

		aField = aMapping.accessHeader(JMSHeader.JMSHEADER_REPLYTO);
		if (aField != null)
		{
			// JMSReplyTo is not a "simple string" ... it's a Destination object.
			// But it can be transported as string to (dont't know who convert it where - but it works).
			// So check and convert those Destinations explicit.
			
			// The other way : creating a Destination on the fly is not an option ... 
			// because that require a valid JMS Session ... we dot have in this context ;-)
			
			if (aJMSReplyTo == null)
				aJMSReplyTo = null;
			else
			if (aJMSReplyTo instanceof Queue)
				aJMSReplyTo = ((Queue)aJMSReplyTo).getQueueName();
			else
			if (aJMSReplyTo instanceof Topic)
				aJMSReplyTo = ((Topic)aJMSReplyTo).getTopicName();
			else
			if (aJMSReplyTo instanceof String)
				aJMSReplyTo = (String)aJMSReplyTo;
			else
				throw new RuntimeException ("Unexpected type '"+aJMSReplyTo.getClass ()+"' of JMSReplyTo !");

			aField.set(aBean, aJMSReplyTo);
		}

		// handle body

		if (aMessage instanceof TextMessage)
		{
			final String sBody = ((TextMessage)aMessage).getText();
			if ( ! StringUtils.isEmpty(sBody))
				aBean.setBody(sBody);
		}
		else
		if (aMessage instanceof ObjectMessage)
		{
			final Object aBody = ((ObjectMessage)aMessage).getObject();
			if (aBody != null)
				aBean.setBody((Serializable)aBody);
		}
		else
		if (aMessage instanceof ActiveMQMessage)
		{
			final ByteSequence aBody = ((ActiveMQMessage)aMessage).getContent();
			if (aBody != null)
			{
				final String sContent = new String(aBody.getData(), "utf-8");
				aBean.setBody(sContent);
			}
		}
		else
			throw new UnsupportedOperationException ("No support for message type '"+aMessage.getClass ()+"' implemented yet.");
	}

	//-------------------------------------------------------------------------
	private String impl_toString (final Object aBean ,
								  final Field  aField)
	    throws Exception
	{
		final Class< ? > aType   = aField.getType();
		final Object     aValue  = aField.get(aBean);
		final String     sString = TypeConverter.toString(aValue, aType);
		return sString;
	}
	
	//-------------------------------------------------------------------------
	private Object impl_fromString (final Object aValue,
								    final Field  aField)
	    throws Exception
	{
		if (aValue == null)
			return null;
		
		if ( ! aValue.getClass().isAssignableFrom(String.class))
			return aValue;
		
		final Class< ? > aType      = aField.getType();
		final Object     aRealValue = TypeConverter.fromString((String)aValue, aType);
		return aRealValue;
	}

	//-------------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	private < T extends JMSMessageBean > JMSBeanMapping< T > impl_getOrCreateBeanMapping (final T aBean)
	    throws Exception
	{
		final Map< String, JMSBeanMapping< ? extends JMSMessageBean > > aRegistry  = mem_MappingRegistry ();
		final Class< T >                                                aBeanClass = (Class< T >)aBean.getClass ();
		final String                                                    sBeanClass = aBeanClass.getName ();
		      JMSBeanMapping< ? extends JMSMessageBean >                aMapping   = aRegistry.get(sBeanClass);
		
		if (aMapping == null)
		{
			aMapping = JMSBeanMapping.create(aBeanClass);
			aRegistry.put(sBeanClass, aMapping);
		}
		
		return (JMSBeanMapping< T >) aMapping;
	}
	
	//-------------------------------------------------------------------------
	private Map< String, JMSBeanMapping< ? extends JMSMessageBean > > mem_MappingRegistry ()
	    throws Exception
	{
		if (m_aMappingRegistry == null)
			m_aMappingRegistry = new HashMap< String, JMSBeanMapping< ? > >();
		return m_aMappingRegistry;
	}
	
	//-------------------------------------------------------------------------
	private static JMSBeanMapper m_gSingleton = null;
	
	//-------------------------------------------------------------------------
	private Map< String, JMSBeanMapping< ? extends JMSMessageBean > > m_aMappingRegistry = null;
}
