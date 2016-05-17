package net.as_development.asdk.distributed_cache;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import net.as_development.asdk.distributed_cache.impl.ERunMode;

//=============================================================================
public class DistributedCacheConfig
{
	//-------------------------------------------------------------------------
	public static final String DEFAULT_UNICAST_ADDRESS   = "127.0.0.1";
	public static final int    DEFAULT_UNICAST_PORT      = 19876;

	public static final String DEFAULT_MULTICAST_ADDRESS = "224.0.0.3";
	public static final int    DEFAULT_MULTICAST_PORT    = 9876;
	
	//-------------------------------------------------------------------------
	public DistributedCacheConfig ()
	    throws Exception
	{}

	//-------------------------------------------------------------------------
	public synchronized void enableMulticast (final boolean bState)
		throws Exception
	{
		m_bMulticast = bState;
	}
	
	//-------------------------------------------------------------------------
	public synchronized boolean isMulticast ()
		throws Exception
	{
		return m_bMulticast;
	}

	//-------------------------------------------------------------------------
	public synchronized void setRunMode (final ERunMode eMode)
	    throws Exception
	{
		m_eRunMode = eMode;
	}

	//-------------------------------------------------------------------------
	public synchronized ERunMode getRunMode ()
	    throws Exception
	{
		return m_eRunMode;
	}

	//-------------------------------------------------------------------------
	public synchronized void setAddress (final String sAddress)
		throws Exception
	{
		Validate.notEmpty(sAddress, "Invalid argument 'address'.");
		m_sAddress = sAddress;
	}

	//-------------------------------------------------------------------------
	public synchronized String getAddress ()
		throws Exception
	{
		if (StringUtils.isEmpty(m_sAddress))
		{
			if (m_bMulticast)
				m_sAddress = DEFAULT_MULTICAST_ADDRESS;
			else
				m_sAddress = DEFAULT_UNICAST_ADDRESS;
		}
		return m_sAddress;
	}

	//-------------------------------------------------------------------------
	public synchronized void setPort (final int nPort)
		throws Exception
	{
		m_nPort = nPort;
	}

	//-------------------------------------------------------------------------
	public synchronized int getPort ()
		throws Exception
	{
		if (m_nPort == null)
		{
			if (m_bMulticast)
				m_nPort = DEFAULT_MULTICAST_PORT;
			else
				m_nPort = DEFAULT_UNICAST_PORT;
		}
		return m_nPort;
	}

	//-------------------------------------------------------------------------
	private boolean m_bMulticast = false;

	//-------------------------------------------------------------------------
	private ERunMode m_eRunMode = null;

	//-------------------------------------------------------------------------
	private String m_sAddress = null;

	//-------------------------------------------------------------------------
	private Integer m_nPort = null;
}
