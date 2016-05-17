package net.as_development.asdk.distributed_cache.impl.channel;

import net.as_development.asdk.distributed_cache.DistributedCacheConfig;
import net.as_development.asdk.distributed_cache.impl.Message;

//=============================================================================
public interface IChannel
{
	//-------------------------------------------------------------------------
	public void setSenderId (final String sId)
	    throws Exception;
	
	//-------------------------------------------------------------------------
	public void configure (final DistributedCacheConfig aConfig)
		throws Exception;

	//-------------------------------------------------------------------------
	public void connect ()
	    throws Exception;

	//-------------------------------------------------------------------------
	public void disconnect ()
	    throws Exception;

	//-------------------------------------------------------------------------
	public void send (final Message aMsg)
		throws Exception;
}
