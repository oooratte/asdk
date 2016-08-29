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

import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.Validate;

//=============================================================================
public class JMSMessageUtils
{
	//-------------------------------------------------------------------------
	private JMSMessageUtils ()
	{}

	//-------------------------------------------------------------------------
	public static String getMessageType (final javax.jms.Message aMessage)
		throws Exception
	{
		final String sType = aMessage.getStringProperty(JMSMessageBean.HEADER_MSGTYPE);
		return sType;
	}

	//-------------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	public static < T extends JMSMessageBean > T emptyBeanFitsToMessage (final javax.jms.Message aMessage)
		throws Exception
	{
		final String sType = getMessageType (aMessage);
		Validate.notEmpty(sType, "No 'type' header defined in message. Cant create suitable message ...");
		return (T) JMSMessageUtils.newBeanForType (sType);
	}

	//-------------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	public static < T extends JMSMessageBean > T newBeanForType (final String sType)
		throws Exception
	{
		final Class< T > aType = (Class< T >) Class.forName(sType);
		final Object     aBean = aType.newInstance();
		return (T) aBean;
	}

	//-------------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	public static < T extends JMSMessageBean > T constructResponse4Request (final T aRequest)
		throws Exception
	{
		final String sType     = aRequest.getType();
		final T      aResponse = (T) newBeanForType (sType);
		
		aResponse.setJMSCorrelationID(aRequest.getJMSID());
		
		final Set< String > lCustomHeader = aRequest.listCustomHeader();
		for (final String sHeader : lCustomHeader)
		{
			if ( ! JMSBeanMapper.isCustomHeader (sHeader))
				continue;

			final Object aValue = aRequest.getCustomHeader(sHeader);
			aResponse.setCustomHeader(sHeader, aValue);
		}
		
		return aResponse;
	}

	//-------------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	public static <T extends JMSMessageBean> T mapMessageToBean (final javax.jms.Message aMessage)
	    throws Exception
	{
		final JMSMessageBean aBean = emptyBeanFitsToMessage(aMessage);
		mapMessageToBean (aMessage, aBean);
		return (T) aBean;
	}

	//-------------------------------------------------------------------------
	public static <T extends JMSMessageBean> void mapMessageToBean (final javax.jms.Message aMessage,
																	final T                 aBean   )
	    throws Exception
	{
		final JMSBeanMapper aMapper = JMSBeanMapper.get();
		aMapper.mapMessageToBean(aMessage, aBean);
	}

	//-------------------------------------------------------------------------
	public static <T extends JMSMessageBean> void mapBeanToMessage (final T                 aBean   ,
																    final javax.jms.Message aMessage)
	    throws Exception
	{
		final JMSBeanMapper aMapper = JMSBeanMapper.get();
		aMapper.mapBeanToMessage(aBean, aMessage);
	}
}
