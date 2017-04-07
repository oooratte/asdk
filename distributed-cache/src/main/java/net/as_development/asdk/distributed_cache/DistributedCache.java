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
package net.as_development.asdk.distributed_cache;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import net.as_development.asdk.distributed_cache.impl.Message;
import net.as_development.asdk.distributed_cache.impl.Set;
import net.as_development.asdk.distributed_cache.impl.channel.IChannel;
import net.as_development.asdk.distributed_cache.impl.channel.MulticastChannel;
import net.as_development.asdk.distributed_cache.impl.channel.UnicastChannel;
import net.as_development.asdk.tools.common.pattern.observation.Observable;
import net.as_development.asdk.tools.common.pattern.observation.Observer;
import net.as_development.asdk.tools.logging.ELogLevel;
import net.as_development.asdk.tools.logging.LoggerFactory;
import net.as_development.asdk.tools.logging.impl.Logger;

//=============================================================================
public class DistributedCache implements Observer< Message >
{
	//-------------------------------------------------------------------------
	private static Logger LOG = LoggerFactory.newLogger(DistributedCache.class);
	
	//-------------------------------------------------------------------------
	public DistributedCache ()
	    throws Exception
	{}

	//-------------------------------------------------------------------------
	public synchronized DistributedCacheConfig configure ()
		throws Exception
	{
		return mem_Config ();
	}

	//-------------------------------------------------------------------------
	public synchronized DistributedCacheSink getCacheSink ()
		throws Exception
	{
		return mem_Cache ();
	}

	//-------------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	public synchronized void connect ()
	    throws Exception
	{
		if (m_iChannel != null)
			return;

		LOG	.forLevel	(ELogLevel.E_DEBUG)
			.withMessage("["+mem_CacheID ()+"] connect ...")
			.log 		();
		
		final String                 sME          = mem_CacheID   ();
		final DistributedCacheConfig aConfig      = mem_Config ();
		final boolean                bIsMulticast = aConfig.isMulticast();
		      IChannel               iChannel     = null;
		
		if (bIsMulticast)
			iChannel = new MulticastChannel ();
		else
			iChannel = new UnicastChannel   ();

		iChannel.setCacheID(sME    );
		iChannel.configure  (aConfig);
		iChannel.connect    (       );

		((Observable< Message >)iChannel).addObserver(this);
		
		m_iChannel = iChannel;
	}
	
	//-------------------------------------------------------------------------
	public synchronized void disconnect ()
	    throws Exception
	{
		if (m_iChannel == null)
			return;
		
		LOG	.forLevel	(ELogLevel.E_DEBUG)
			.withMessage("["+mem_CacheID ()+"] disconnect ...")
			.log 		();

		final IChannel iChannel = m_iChannel;
		
		if (iChannel != null)
			iChannel.disconnect();
	}
	
	//-------------------------------------------------------------------------
	public synchronized void disconnectQuietly ()
	{
		try
		{
			disconnect ();
		}
		catch (Throwable exIgnore)
		{}
	}

	//-------------------------------------------------------------------------
	public synchronized void set (final String sKey  ,
								  final String sValue)
	    throws Exception
	{
		LOG	.forLevel	(ELogLevel.E_DEBUG)
			.withMessage("["+mem_CacheID ()+"] set ("+sKey+"="+sValue+") ...")
			.log 		();

		final DistributedCacheSink aCache    = mem_Cache ();
		final String               sOldValue = aCache.get(sKey);
		final boolean              bChanged  = ! StringUtils.equals(sValue, sOldValue);
		
		if ( ! bChanged)
			return;

		final Set aSet = Set.newSet(sKey, sValue);

		aCache.set(aSet);
		impl_send (aSet);
	}

	//-------------------------------------------------------------------------
	private void impl_send (final Message aMsg)
		throws Exception
	{
		final String sME = mem_CacheID ();
		if (m_iChannel == null)
			throw new RuntimeException ("["+mem_CacheID ()+"] cant send : not connected !");

		LOG	.forLevel	(ELogLevel.E_DEBUG)
			.withMessage("["+mem_CacheID ()+"] send : "+aMsg+" ...")
			.log 		();

		aMsg.setSender(sME);
		m_iChannel.send(aMsg);
	}
	
	//-------------------------------------------------------------------------
	@Override
	public void notify(final Message aMsg)
		throws Exception
	{
        final String sAction = aMsg.getAction();
        if (StringUtils.equals(sAction, Message.ACTION_SET))
        	impl_doSet (aMsg);
	}
	
	//-------------------------------------------------------------------------
	private synchronized void impl_doSet (final Message aMsg)
	    throws Exception
	{
		final String               sME    = mem_CacheID  ();
		final DistributedCacheSink aCache = mem_Cache ();
		final Set                  aSet   = Set.fromMessage(aMsg);

		LOG	.forLevel	(ELogLevel.E_DEBUG)
			.withMessage("["+mem_CacheID ()+"] got : "+aMsg+" ...")
			.log 		();
		aCache.set(aSet);
	}
	
	//-------------------------------------------------------------------------
	private synchronized DistributedCacheConfig mem_Config ()
	    throws Exception
	{
		if (m_aConfig == null)
			m_aConfig = new DistributedCacheConfig ();
		return m_aConfig;
	}

	//-------------------------------------------------------------------------
	private synchronized String mem_CacheID ()
	    throws Exception
	{
		if (m_sCacheID == null)
			m_sCacheID = UUID.randomUUID().toString();
		return m_sCacheID;
	}

	//-------------------------------------------------------------------------
	private synchronized DistributedCacheSink mem_Cache ()
	    throws Exception
	{
		if (m_aCache == null)
		{
			m_aCache = new DistributedCacheSink ();
			m_aCache.setCacheID(mem_CacheID ());
		}
		return m_aCache;
	}

	//-------------------------------------------------------------------------
	private String m_sCacheID = null;

	//-------------------------------------------------------------------------
	private boolean m_bNoSendWhileServerInUnicast = false;
	
	//-------------------------------------------------------------------------
	private DistributedCacheConfig m_aConfig = null;
	
	//-------------------------------------------------------------------------
	private IChannel m_iChannel = null;

	//-------------------------------------------------------------------------
	private DistributedCacheSink m_aCache = null;
}
