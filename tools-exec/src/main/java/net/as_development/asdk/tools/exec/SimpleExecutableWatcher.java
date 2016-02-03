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
package net.as_development.asdk.tools.exec;

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
