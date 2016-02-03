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
package test.net.as_development.asdk.tools.exec;

import org.junit.Test;

import net.as_development.asdk.tools.exec.IExecutableWatcher;
import net.as_development.asdk.tools.exec.impl.ExecutableWatch;

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
