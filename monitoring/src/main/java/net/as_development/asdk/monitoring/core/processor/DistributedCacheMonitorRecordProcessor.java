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
package net.as_development.asdk.monitoring.core.processor;

import org.apache.commons.lang3.StringUtils;

import net.as_development.asdk.api.monitoring.IMonitorRecordProcessor;
import net.as_development.asdk.distributed_cache.DistributedCache;
import net.as_development.asdk.distributed_cache.DistributedCacheConfig;
import net.as_development.asdk.monitoring.core.MonitorRecord;

//==============================================================================
/**
 */
public class DistributedCacheMonitorRecordProcessor implements IMonitorRecordProcessor
{
	//--------------------------------------------------------------------------
	public DistributedCacheMonitorRecordProcessor ()
	{}
	
	//--------------------------------------------------------------------------
	public synchronized DistributedCacheConfig configure ()
	    throws Exception
	{
		return mem_Cache ().configure();
	}
	
	//--------------------------------------------------------------------------
	public /* no synchronized */ void connect ()
		throws Exception
	{
		mem_Cache ().connect();
	}
	
	//--------------------------------------------------------------------------
	public /* no synchronized */ void disconnect ()
		throws Exception
	{
		mem_Cache ().disconnect();
	}

	//--------------------------------------------------------------------------
	@Override
	public /* no synchronized */ void processRecord(final MonitorRecord aRecord)
	    throws Exception
	{
		final String sKey    = StringUtils.join(new String[] {aRecord.getScope(), aRecord.getID()}, ".");
		final String sRecord = aRecord.convertToString();
		mem_Cache ().set (sKey, sRecord);
	}

	//--------------------------------------------------------------------------
	private synchronized DistributedCache mem_Cache ()
	    throws Exception
	{
		if (m_aCache == null)
			m_aCache = new DistributedCache ();
		return m_aCache;
	}

	//--------------------------------------------------------------------------
	private DistributedCache m_aCache = null;
}
