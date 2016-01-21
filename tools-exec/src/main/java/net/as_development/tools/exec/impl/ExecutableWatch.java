/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package net.as_development.tools.exec.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.as_development.tools.exec.IExecutableWatcher;

import org.apache.commons.lang3.StringUtils;

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
