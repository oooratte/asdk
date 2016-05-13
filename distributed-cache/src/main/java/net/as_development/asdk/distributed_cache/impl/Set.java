package net.as_development.asdk.distributed_cache.impl;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

//=============================================================================
public class Set extends Message
{
	//-------------------------------------------------------------------------
	private Set ()
	    throws Exception
	{
		super.setAction(Message.ACTION_SET);
	}
	
	//-------------------------------------------------------------------------
	public static Set newSet (final String sKey  ,
							  final String sValue)
		throws Exception
	{
		final Set aSet = new Set ();
		aSet.set(sKey, sValue);
		return aSet;
	}

	//-------------------------------------------------------------------------
	public static Set fromMessage (final Message aMsg)
		throws Exception
	{
		final String   sData  = aMsg.getData();
		final String[] lParts = StringUtils.splitPreserveAllTokens(sData, '=');
		final Set      aSet   = new Set ();
		aSet.m_sKey   = lParts[0];
		aSet.m_sValue = lParts[1];
		return aSet;
	}

	//-------------------------------------------------------------------------
	public void set (final String sKey  ,
					 final String sValue)
	    throws Exception
	{
		m_sKey   = sKey  ;
		m_sValue = sValue;
		
		final StringBuffer sData = new StringBuffer (256);
		sData.append(sKey  );
		sData.append("="   );
		sData.append(sValue);
		setData (sData.toString ());
	}

	//-------------------------------------------------------------------------
	public String getKey ()
		throws Exception
	{
		return m_sKey;
	}

	//-------------------------------------------------------------------------
	public String getValue ()
		throws Exception
	{
		return m_sValue;
	}

	//-------------------------------------------------------------------------
	private String m_sKey = null;

	//-------------------------------------------------------------------------
	private String m_sValue = null;
}
