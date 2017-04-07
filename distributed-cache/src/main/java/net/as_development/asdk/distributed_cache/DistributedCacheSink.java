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

import org.apache.commons.lang3.StringUtils;

import net.as_development.asdk.distributed_cache.impl.Set;
import net.as_development.asdk.tools.common.pattern.observation.ObservableBase;
import net.as_development.asdk.tools.common.pattern.observation.Observer;
import net.as_development.asdk.tools.logging.ELogLevel;
import net.as_development.asdk.tools.logging.LoggerFactory;
import net.as_development.asdk.tools.logging.impl.Logger;

//=============================================================================
public class DistributedCacheSink extends ObservableBase< DistributedCacheItem >
{
	//-------------------------------------------------------------------------
	private static Logger LOG = LoggerFactory.newLogger(DistributedCacheSink.class);

	//-------------------------------------------------------------------------
	public DistributedCacheSink ()
	    throws Exception
	{}

	//-------------------------------------------------------------------------
	protected synchronized void setCacheID (final String sCacheID)
	    throws Exception
	{
		m_sCacheID = sCacheID;
	}
	
	//-------------------------------------------------------------------------
	/** enable/disable caching of values within this instance.
	 *  Notifications about new / changed values wont be influenced by this setting !
	 */
	public synchronized void setCachingEnabled (final boolean bEnabled)
	    throws Exception
	{
		m_bCachingEnabled = bEnabled;
	}

	//-------------------------------------------------------------------------
	/** has to be called by DistributedCache only.
	 *  Calling it from outside will change the cache (of course) ...
	 *  but do not broadcast those new value within the distributed cache environment !
	 */
	protected synchronized void set (final Set aSet)
	    throws Exception
	{
		if (m_bCachingEnabled)
		{
			LOG	.forLevel	(ELogLevel.E_DEBUG)
				.withMessage("["+m_sCacheID+"] set on cache ("+aSet+") ...")
				.log 		();

			final String                sKey      = aSet.getKey   ();
			final String                sValue    = aSet.getValue ();
			final Map< String, String > aCache    = mem_Cache ();
			final String                sOldValue = aCache.get(sKey);
			final boolean               bChanged  = ! StringUtils.equals(sValue, sOldValue);
			
			if ( ! bChanged)
				return;
	
			aCache.put(sKey, sValue);
		}

		impl_notifyObserver (aSet);
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
	private void impl_notifyObserver (final Set aSet)
		throws Exception
    {
		try
		{
			LOG	.forLevel	(ELogLevel.E_DEBUG)
				.withMessage("["+m_sCacheID+"] notify observer ("+aSet+") ...")
				.log 		();

			final String sKey   = aSet.getKey   ();
			final String sValue = aSet.getValue ();

			fire(DistributedCacheItem.newItem(sKey, sValue));
		}
		catch (Throwable exIgnore)
		{
			LOG	.forLevel	(ELogLevel.E_ERROR)
				.withError	(exIgnore)
				.log 		();
		}
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
	private String m_sCacheID = null;
	
	//-------------------------------------------------------------------------
	private boolean m_bCachingEnabled = true;
	
	//-------------------------------------------------------------------------
	private Map< String, String > m_aCache = null;

	//-------------------------------------------------------------------------
	private Observer< DistributedCacheItem > m_aObserver = null;
}
