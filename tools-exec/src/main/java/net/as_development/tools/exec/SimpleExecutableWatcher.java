/**
 * Copyright 2012 Andreas Schlüns - as-development.net
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.as_development.tools.exec;

import java.util.concurrent.CountDownLatch;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

//==============================================================================
public class SimpleExecutableWatcher implements IExecutableWatcher
{
    //--------------------------------------------------------------------------
	private SimpleExecutableWatcher()
	{}

    //--------------------------------------------------------------------------
	public static SimpleExecutableWatcher create (final CountDownLatch aSync   ,
												  final String...      lWatches)
	    throws Exception
	{
		Validate.notNull  (aSync   , "Invalid argument 'sync'."              );
		Validate.notNull  (lWatches, "Invalid argument 'watches' (is null)." );
		Validate.notEmpty (lWatches, "Invalid argument 'watches' (is empty).");

		SimpleExecutableWatcher aWatcher = new SimpleExecutableWatcher ();
		aWatcher.m_aSync    = aSync   ;
		aWatcher.m_lWatches = lWatches;
		return aWatcher;
	}

    //--------------------------------------------------------------------------
	@Override
    public synchronized String[] getWatchPoints ()
	    throws Exception
	{
		return m_lWatches;
	}

    //--------------------------------------------------------------------------
	@Override
    public synchronized void watchPointDetected (final String sWatchPoint)
	    throws Exception
	{
		for (String sWatch : m_lWatches)
		{
			if (StringUtils.equals(sWatchPoint, sWatch))
				m_aSync.countDown();
		}
	}

	//--------------------------------------------------------------------------
	private String[] m_lWatches = null;

	//--------------------------------------------------------------------------
	private CountDownLatch m_aSync = null;
}
