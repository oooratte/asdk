package net.as_development.asdk.distributed_cache;

import org.apache.commons.lang3.Validate;

//=============================================================================
public class DistributedCacheConfig
{
	//-------------------------------------------------------------------------
	public static final String DEFAULT_MULTICAST_ADDRESS = "239.255.255.250";
	public static final int    DEFAULT_MULTICAST_PORT    = 9876;
	
	//-------------------------------------------------------------------------
	public DistributedCacheConfig ()
	    throws Exception
	{}

	//-------------------------------------------------------------------------
	public synchronized void setMulticastAddress (final String sAddress)
		throws Exception
	{
		Validate.notEmpty(sAddress, "Invalid argument 'address'.");
		m_sMulticastAddress = sAddress;
	}

	//-------------------------------------------------------------------------
	public synchronized String getMulticastAddress ()
		throws Exception
	{
		return m_sMulticastAddress;
	}

	//-------------------------------------------------------------------------
	public synchronized int getMulticastPort ()
		throws Exception
	{
		return m_nMulticastPort;
	}

	//-------------------------------------------------------------------------
	private String m_sMulticastAddress = DEFAULT_MULTICAST_ADDRESS;

	//-------------------------------------------------------------------------
	private int m_nMulticastPort = DEFAULT_MULTICAST_PORT;
}
