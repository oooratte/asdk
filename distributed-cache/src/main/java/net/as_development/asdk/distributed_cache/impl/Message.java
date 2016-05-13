package net.as_development.asdk.distributed_cache.impl;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

//=============================================================================
public class Message
{
	//-------------------------------------------------------------------------
	public static final String SEPARATOR   = "%|%"  ;
	public static final String ENCODING    = "utf-8";
	public static final int    BUFSIZE     = 4096;

	//-------------------------------------------------------------------------
	public static final String ACTION_SET  = "set"     ;
	public static final String ACTION_GET  = "get"     ;
	public static final String ACTION_LIST = "list-all";

	//-------------------------------------------------------------------------
	public Message ()
	    throws Exception
	{}

	//-------------------------------------------------------------------------
	public static String serialize (final Message aMsg)
	    throws Exception
	{
		final StringBuffer sBuf = new StringBuffer (256);

		sBuf.append(aMsg.m_sSender);
		sBuf.append(SEPARATOR     );
		sBuf.append(aMsg.m_sAction);
		sBuf.append(SEPARATOR     );
		sBuf.append(aMsg.m_sData  );

		final String sRaw = sBuf.toString ();
		final String sB64 = Base64.encodeBase64String(sRaw.getBytes(ENCODING));
		
		return sB64;
	}
	
	//-------------------------------------------------------------------------
	public static Message deserialize (final String sMsg)
	    throws Exception
	{
		final String   sB64   = sMsg;
		final String   sRaw   = new String(Base64.decodeBase64(sB64), ENCODING);
		final String[] lParts = StringUtils.splitByWholeSeparatorPreserveAllTokens(sRaw, SEPARATOR);

		Validate.isTrue(lParts.length==3, "Malformed message retrieved. Unexpected count of parts.");

		final Message aMsg = new Message ();
		aMsg.m_sSender = lParts[0];
		aMsg.m_sAction = lParts[1];
		aMsg.m_sData   = lParts[2];
		
		return aMsg;
	}

	//-------------------------------------------------------------------------
	public void setSender (final String sSender)
		throws Exception
	{
		m_sSender = sSender;
	}
	
	//-------------------------------------------------------------------------
	public void setAction (final String sAction)
		throws Exception
	{
		m_sAction = sAction;
	}

	//-------------------------------------------------------------------------
	public void setData (final String sData)
		throws Exception
	{
		m_sData = sData;
	}

	//-------------------------------------------------------------------------
	public String getSender ()
		throws Exception
	{
		return m_sSender;
	}
	
	//-------------------------------------------------------------------------
	public String getAction ()
		throws Exception
	{
		return m_sAction;
	}

	//-------------------------------------------------------------------------
	public String getData ()
		throws Exception
	{
		return m_sData;
	}

	//-------------------------------------------------------------------------
	@Override
	public String toString ()
	{
		final StringBuffer sString = new StringBuffer (256);
		
		sString.append(super.toString ());
		sString.append(" : "            );
		sString.append("sender="        );
		sString.append(m_sSender        );
		sString.append(" : "            );
		sString.append("action="        );
		sString.append(m_sAction        );
		sString.append(" : "            );
		sString.append("data="          );
		sString.append(m_sData          );
		
		return sString.toString();
	}

	//-------------------------------------------------------------------------
	private String m_sSender = null;

	//-------------------------------------------------------------------------
	private String m_sAction = null;

	//-------------------------------------------------------------------------
	private String m_sData = null;
}
