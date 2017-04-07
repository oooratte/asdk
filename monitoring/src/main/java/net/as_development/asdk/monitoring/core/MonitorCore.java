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
package net.as_development.asdk.monitoring.core;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import net.as_development.asdk.api.monitoring.IMonitorRecordProcessor;

//==============================================================================
/**
 */
public class MonitorCore
{
	//--------------------------------------------------------------------------
	private MonitorCore ()
	{}
	
	//--------------------------------------------------------------------------
	public static synchronized MonitorCore get()
		throws Exception
	{
		if (m_gSingleton == null)
		{
			m_gSingleton = new MonitorCore ();
			m_gSingleton.impl_startAsyncHandler ();
		}
		return m_gSingleton;
	}
	
	//--------------------------------------------------------------------------
	public synchronized void setProcessor (final IMonitorRecordProcessor iProcessor)
		throws Exception
	{
		mem_Processor ().set (iProcessor);
	}

	//--------------------------------------------------------------------------
	public /* no synchronized */ void enqueue (final MonitorRecord... lRecords)
		throws Exception
	{
		final Queue< MonitorRecord > aQueue = mem_Queue ();
		for (final MonitorRecord aRecord : lRecords)
		{
			if (aRecord == null)
				continue;
			aQueue.offer(aRecord);
		}
		
		synchronized (aQueue)
		{
			aQueue.notifyAll();
		}
	}
	
	//--------------------------------------------------------------------------
	private synchronized void impl_startAsyncHandler ()
		throws Exception
	{
		if (m_aAsyncHandler != null)
			return;
		
		m_aAsyncHandler = Executors.newSingleThreadExecutor();
		m_aAsyncHandler.submit(new Runnable ()
		{
			@Override
			public void run()
			{
				try
				{
					impl_asyncHandlerLoop ();
				}
				catch (final Throwable exIgnore)
				{
					System.err.println(exIgnore.getMessage ());
					exIgnore.printStackTrace(System.err);
				}
			}
		});
	}
	
	//--------------------------------------------------------------------------
	private /* no synchronized */ void impl_asyncHandlerLoop ()
	    throws Exception
	{
		final long                   MAX_TIME_SLICE_IN_MS = 1000;
		final Thread                 aTHIS                = Thread.currentThread();
		final Queue< MonitorRecord > aQueue               = mem_Queue ();

		while (true)
		{
			if (aTHIS.isInterrupted())
				break;
			
			final MonitorRecord aRecord = aQueue.poll();
			if (aRecord == null)
			{
				synchronized (aQueue)
				{
					aQueue.wait(MAX_TIME_SLICE_IN_MS);
				}
				continue;
			}
			
			impl_handleRecord (aRecord);
		}
	}
	
	//--------------------------------------------------------------------------
	private /* no synchronized */ void impl_handleRecord (final MonitorRecord aRecord)
		throws Exception
	{
		final IMonitorRecordProcessor iProcessor = mem_Processor ().get ();
		
		if (iProcessor == null)
			return;

		iProcessor.processRecord(aRecord);
	}
	
	//--------------------------------------------------------------------------
	private synchronized AtomicReference< IMonitorRecordProcessor > mem_Processor ()
	    throws Exception
	{
		if (m_rProcessor == null)
			m_rProcessor = new AtomicReference< IMonitorRecordProcessor > ();
		return m_rProcessor;
	}

	//--------------------------------------------------------------------------
	private synchronized Queue< MonitorRecord > mem_Queue ()
	    throws Exception
	{
		if (m_lQueue == null)
			m_lQueue = new ConcurrentLinkedQueue< MonitorRecord > ();
		return m_lQueue;
	}

	//--------------------------------------------------------------------------
	private static MonitorCore m_gSingleton = null;
	
	//--------------------------------------------------------------------------
	private AtomicReference< IMonitorRecordProcessor > m_rProcessor = null;
	
	//--------------------------------------------------------------------------
	private Queue< MonitorRecord > m_lQueue = null;

	//--------------------------------------------------------------------------
	private ExecutorService m_aAsyncHandler = null;
}
