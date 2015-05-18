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
package test.net.as_development.tools.exec;

import net.as_development.tools.exec.IExecutableWatcher;
import net.as_development.tools.exec.impl.ExecutableWatch;

import org.junit.Test;

//==============================================================================
public class ExecutableWatchTest
{
	//--------------------------------------------------------------------------
	@Test
	public void test()
		throws Exception
	{
		final ExecutableWatch aWatch = new ExecutableWatch ();
		aWatch.addWatcher(new IExecutableWatcher ()
		{
			@Override
			public String[] getWatchPoints()
				throws Exception
			{
				final String[] lWatchPoints = new String[2];
				lWatchPoints[0] = "watchpoint-01";
				lWatchPoints[1] = "watchpoint-02";
				return lWatchPoints;
			}

			@Override
			public void watchPointDetected(final String sWatchPoint)
				throws Exception
			{
				System.out.println ("got notification for '"+sWatchPoint+"'");
			}
		});
		
		final int c = 100;
		boolean bWP01 = true;
		boolean bWP02 = true;
		for (int i=0; i<c; ++i)
		{
			if (i > c/3 && bWP01)
			{
				System.err.println ("trigger wp-01 ...");
				aWatch.scanForWatches("watchpoint");
				aWatch.scanForWatches("-01"       );
				bWP01 = false;
			}
			else
			if (i > c/2 && bWP02)
			{
				System.err.println ("trigger wp-02 ...");
				aWatch.scanForWatches("watchpoint");
				aWatch.scanForWatches("-02"       );
				bWP02 = false;
			}
			
			aWatch.scanForWatches("sinnlos"+i+"\n");
		}
	}
}
