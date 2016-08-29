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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import net.as_development.asdk.jms.core.EErrorCode;

//=============================================================================
public class JMSMessageBean
{
	//-------------------------------------------------------------------------
	public static final String HEADER_NAMESPACE = "ox"+JMSHeader.SEPARATOR+"msg";

	//-------------------------------------------------------------------------
	public static final String HEADER_MSGTYPE      = HEADER_NAMESPACE+JMSHeader.SEPARATOR+"type";
	public static final String HEADER_ERRORCODE    = HEADER_NAMESPACE+JMSHeader.SEPARATOR+"error"+JMSHeader.SEPARATOR+"code";

    //-------------------------------------------------------------------------
	public JMSMessageBean ()
		throws Exception
	{
		setType(getClass());
	}

    //-------------------------------------------------------------------------
	public void validate ()
		throws Exception
	{
		// DON'T validate JMSXXX values here ... they are generated/defined within the
		// queue context on demand .. and might not initialized always !!!
		
		if (StringUtils.isEmpty (m_sType))
			throw new IllegalStateException ("No 'type' defined.");
	}
	
	//-------------------------------------------------------------------------
	public String getJMSID ()
		throws Exception
	{
		return m_sJMSID;
	}
	
	//-------------------------------------------------------------------------
	public String getJMSCorrelationID ()
		throws Exception
	{
		return m_sJMSCorrelationID;
	}
	
	//-------------------------------------------------------------------------
	public void setJMSCorrelationID (final String sID)
		throws Exception
	{
		m_sJMSCorrelationID = sID;
	}

	//-------------------------------------------------------------------------
	public String getJMSReplyTo ()
		throws Exception
	{
		return m_sJMSReplyTo;
	}

	//-------------------------------------------------------------------------
	public void setJMSReplyTo (final String sReplyTo)
		throws Exception
	{
		m_sJMSReplyTo = sReplyTo;
	}

	//-------------------------------------------------------------------------
	public void setOK ()
	    throws Exception
	{
		m_eErrorCode = EErrorCode.E_OK;
	}
	
	//-------------------------------------------------------------------------
	public boolean isOK ()
	    throws Exception
	{
		if (m_eErrorCode == null)
			return true;
		if (m_eErrorCode == EErrorCode.E_OK)
			return true;
		return false;
	}

	//-------------------------------------------------------------------------
	public void setError (final EErrorCode eError  ,
						  final String     sMessage)
	    throws Exception
	{
		m_eErrorCode = eError;
		setBody(sMessage);
	}
	
	//-------------------------------------------------------------------------
	public EErrorCode getErrorCode ()
	    throws Exception
	{
		return m_eErrorCode;
	}

	//-------------------------------------------------------------------------
	public String getErrorMessage ()
	    throws Exception
	{
		return getBody ();
	}

	//-------------------------------------------------------------------------
	protected void setType (final Class< ? extends JMSMessageBean > aType)
		throws Exception
	{
		m_sType = aType.getName();
	}

	//-------------------------------------------------------------------------
	public String getType ()
		throws Exception
	{
		return m_sType;
	}

	//-------------------------------------------------------------------------
	public void setCustomHeader (final String sHeader,
								 final Object aValue )
		throws Exception
	{
		mem_CustomHeader ().put (sHeader, aValue);
	}

	//-------------------------------------------------------------------------
	public Set< String > listCustomHeader ()
		throws Exception
	{
		return mem_CustomHeader ().keySet();
	}

	//-------------------------------------------------------------------------
	public Object getCustomHeader (final String sHeader)
		throws Exception
	{
		return mem_CustomHeader ().get(sHeader);
	}
	
	//-------------------------------------------------------------------------
	public <T extends Serializable> void setBody (final T aBody)
		throws Exception
	{
		m_aBody = aBody;
	}

	//-------------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	public <T extends Serializable> T getBody ()
		throws Exception
	{
		return (T) m_aBody;
	}

	//-------------------------------------------------------------------------
	@Override
	public String toString ()
	{
		final StringBuffer sStr = new StringBuffer (256);
		sStr.append (super.toString ()    );
		sStr.append ("[JMSID="            );
		sStr.append (m_sJMSID             );
		sStr.append (" ,JMSCorrelationID=");
		sStr.append (m_sJMSCorrelationID  );
		sStr.append (" ,JMSReplyTo="      );
		sStr.append (m_sJMSReplyTo        );
		sStr.append (" ,type="            );
		sStr.append (m_sType              );
		sStr.append (" ,error-code="      );
		sStr.append (m_eErrorCode         );
		sStr.append (" ,body="            );
		sStr.append (m_aBody              );
		sStr.append ("]"                  );
		return sStr.toString ();
	}
	
	//-------------------------------------------------------------------------
	private Map< String, Object > mem_CustomHeader ()
	    throws Exception
	{
		if (m_lCustomHeader == null)
			m_lCustomHeader = new HashMap< String, Object > ();
		return m_lCustomHeader;
	}

	//-------------------------------------------------------------------------
	@JMSHeader(name=JMSHeader.JMSHEADER_MESSAGEID)
	private String m_sJMSID = null;

	//-------------------------------------------------------------------------
	@JMSHeader(name=JMSHeader.JMSHEADER_CORRELATIONID)
	private String m_sJMSCorrelationID = null;
	
	//-------------------------------------------------------------------------
	@JMSHeader(name=JMSHeader.JMSHEADER_REPLYTO)
	private String m_sJMSReplyTo = null;

	//-------------------------------------------------------------------------
	@JMSProperty(name=HEADER_MSGTYPE)
	private String m_sType = null;

	//-------------------------------------------------------------------------
	@JMSProperty(name=HEADER_ERRORCODE)
	private EErrorCode m_eErrorCode = EErrorCode.E_OK;

	//-------------------------------------------------------------------------
	private Map< String, Object > m_lCustomHeader = null;
	
	//-------------------------------------------------------------------------
	@JMSBody
	private Object m_aBody = null;
}
