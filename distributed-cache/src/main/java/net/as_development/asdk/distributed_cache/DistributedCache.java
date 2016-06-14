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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import net.as_development.asdk.distributed_cache.impl.Message;
import net.as_development.asdk.distributed_cache.impl.Set;
import net.as_development.asdk.distributed_cache.impl.channel.IChannel;
import net.as_development.asdk.distributed_cache.impl.channel.MulticastChannel;
import net.as_development.asdk.distributed_cache.impl.channel.UnicastChannel;
import net.as_development.asdk.tools.common.pattern.observation.Observable;
import net.as_development.asdk.tools.common.pattern.observation.Observer;

//=============================================================================
public class DistributedCache implements Observer< Message >
{
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
	@SuppressWarnings("unchecked")
	public synchronized void connect ()
	    throws Exception
	{
		if (m_iChannel != null)
			return;

		final String                 sME          = mem_MyId   ();
		final DistributedCacheConfig aConfig      = mem_Config ();
		final boolean                bIsMulticast = aConfig.isMulticast();
		      IChannel               iChannel     = null;
		
		if (bIsMulticast)
			iChannel = new MulticastChannel ();
		else
			iChannel = new UnicastChannel ();

		iChannel.setSenderId(sME    );
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
		catch (Throwable ex)
		{}
	}

	//-------------------------------------------------------------------------
	public synchronized void set (final String sKey  ,
								  final String sValue)
	    throws Exception
	{
		final Map< String, String > aCache    = mem_Cache ();
		final String                sOldValue = aCache.get(sKey);
		final boolean               bChanged  = ! StringUtils.equals(sValue, sOldValue);
		
		if ( ! bChanged)
			return;

		aCache.put(sKey, sValue);
		
		final Message aSet = Set.newSet(sKey, sValue);
		impl_send (aSet);
	}

	//-------------------------------------------------------------------------
	public synchronized String get (final String sKey)
	    throws Exception
	{
		final Map< String, String > aCache = mem_Cache ();
		final String                sValue = aCache.get(sKey);
		return sValue;
	}

	//-------------------------------------------------------------------------
	public synchronized Map< String, String > get (final List< String > lKeys)
	    throws Exception
	{
		final Map< String, String > aCache  = mem_Cache ();
		final Map< String, String > aResult = new HashMap< String, String > ();
		
		for (final String sKey : lKeys)
		{
			final String sValue = aCache.get(sKey);
			aResult.put(sKey, sValue);
		}
		
		return aResult;
	}

	//-------------------------------------------------------------------------
	public synchronized List< String > listAll ()
	    throws Exception
	{
		final Map< String, String > aCache = mem_Cache ();
		final List< String >        lAll   = new ArrayList< String > ();
		lAll.addAll(aCache.keySet());
		return lAll;
	}

	//-------------------------------------------------------------------------
	public /* no synchronized */ List< String > listSubSet (final String sRegEx)
	    throws Exception
	{
		final List< String > lAll    = listAll ();
		final List< String > lSubSet = new ArrayList< String > ();
		
		for (final String sKey : lAll)
		{
			if (sKey.matches(sRegEx))
				lSubSet.add(sKey);
		}
		
		return lSubSet;
	}

	//-------------------------------------------------------------------------
	private void impl_send (final Message aMsg)
		throws Exception
	{
		final String sME = mem_MyId ();
		if (m_iChannel == null)
			throw new RuntimeException ("["+sME+"] Not connected.");

//		System.out.println("... ["+sME+"] send out : '"+aMsg+"'");
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
		final String                sME    = mem_MyId  ();
		final Map< String, String > aCache = mem_Cache ();
		final Set                   aSet   = Set.fromMessage(aMsg);
		final String                sKey   = aSet.getKey  ();
		final String                sValue = aSet.getValue();

//		System.out.println("["+sME+"] set : '"+sKey+"' = '"+sValue+"'");
		aCache.put(sKey, sValue);
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
	private synchronized String mem_MyId ()
	    throws Exception
	{
		if (m_sMyId == null)
			m_sMyId = UUID.randomUUID().toString();
		return m_sMyId;
	}

	//-------------------------------------------------------------------------
	private synchronized Map< String, String > mem_Cache ()
	    throws Exception
	{
		if (m_aCache == null)
			m_aCache = new HashMap< String, String > ();
		return m_aCache;
	}

	//-------------------------------------------------------------------------
	private String m_sMyId = null;
	
	//-------------------------------------------------------------------------
	private DistributedCacheConfig m_aConfig = null;
	
	//-------------------------------------------------------------------------
	private IChannel m_iChannel = null;

	//-------------------------------------------------------------------------
	private Map< String, String > m_aCache = null;
}
