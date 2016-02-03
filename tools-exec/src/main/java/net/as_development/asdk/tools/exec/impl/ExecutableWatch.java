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
package net.as_development.asdk.tools.exec.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import net.as_development.asdk.tools.exec.IExecutableWatcher;

//==============================================================================
public class ExecutableWatch
{
    //--------------------------------------------------------------------------
	public static final int INIT_BUFFER_SIZE = 8 * 1024       ; // 8kb
	public static final int MAX_BUFFER_SIZE  = 5 * 1024 * 1024; // 5mb
//	public static final int INIT_BUFFER_SIZE = 100;
//	public static final int MAX_BUFFER_SIZE  = 100;
	
    //--------------------------------------------------------------------------
	public ExecutableWatch ()
	    throws Exception
	{}

	//--------------------------------------------------------------------------
	public synchronized void addWatcher (final IExecutableWatcher iWatcher)
	    throws Exception
	{
		final Map< String, IExecutableWatcher > aRegistry = mem_Registry ();
		final String[]                          lWatches  = iWatcher.getWatchPoints();

		if (lWatches == null)
			return;

		for (String sWatch : lWatches)
			aRegistry.put(sWatch, iWatcher);
		
		m_nWatchPointCount += lWatches.length;
	}

	//--------------------------------------------------------------------------
	public synchronized void scanForWatches (final String sContent)
	    throws Exception
	{
		// a) all watch points was detected ...
		//    no need to work further ...
		//    ignore all request for now (in the hope caller will stop calling us)

		if (allWatchPointsDetected ())
			return;
		
		// b) in case not all watch points was detected ...
		//    trim our internal buffer so we don't 'eat memory'.
		
		      StringBuffer aBuffer     = mem_Buffer ();
		final int          nBufferSize = aBuffer.length();
		if (nBufferSize > MAX_BUFFER_SIZE)
			aBuffer = impl_trimBuffer ();
		aBuffer.append (sContent);
		
		final Map< String, IExecutableWatcher > aRegistry = mem_Registry ();
		final String                            sCheck    = aBuffer.toString ();
		final Iterator< String >                pWatches  = aRegistry.keySet().iterator();

		while (pWatches.hasNext())
		{
			final String sWatch = pWatches.next();
			if ( ! StringUtils.contains(sCheck, sWatch))
				continue;

			m_nDetectedWatchPoints++;
			final IExecutableWatcher iWatcher = aRegistry.get(sWatch);

			if (iWatcher == null)
				break;

			aRegistry.remove(sWatch);
			try
			{
				iWatcher.watchPointDetected(sWatch);
			}
			catch (Exception ex)
			{
			    // no handling by intention !
				// be robust even if listener isnt robust enough ;-)
			}

			break;
		}
	}

	//--------------------------------------------------------------------------
	public synchronized boolean allWatchPointsDetected ()
		throws Exception
	{
		final boolean bAllDetected = (m_nDetectedWatchPoints >= m_nWatchPointCount);
		return bAllDetected;
	}
	
	//--------------------------------------------------------------------------
	private StringBuffer impl_trimBuffer ()
		throws Exception
	{
		final int    nTrimStart = Math.round(MAX_BUFFER_SIZE / 2);
		final String sTrimmed   = m_aBuffer.substring(nTrimStart);
System.err.println ("trimmed : ["+sTrimmed+"]");
		m_aBuffer = new StringBuffer (sTrimmed);
		return m_aBuffer;
	}
	
	//--------------------------------------------------------------------------
	private Map< String, IExecutableWatcher > mem_Registry ()
		throws Exception
	{
		if (m_lRegistry == null)
			m_lRegistry = new HashMap< String, IExecutableWatcher >();
		return m_lRegistry;
	}

	//--------------------------------------------------------------------------
	private StringBuffer mem_Buffer ()
	    throws Exception
	{
		if (m_aBuffer == null)
			m_aBuffer = new StringBuffer (INIT_BUFFER_SIZE);
		return m_aBuffer;
	}

	//--------------------------------------------------------------------------
	private Map< String, IExecutableWatcher > m_lRegistry = null;

	//--------------------------------------------------------------------------
	private int m_nWatchPointCount = 0;
	
	//--------------------------------------------------------------------------
	private int m_nDetectedWatchPoints = 0;
	
	//--------------------------------------------------------------------------
	private StringBuffer m_aBuffer = null;
}
