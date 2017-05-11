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
package net.as_development.asdk.tools.common.concurrent;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

//=============================================================================
/**
 */
public class SimpleCondition
{
	//-------------------------------------------------------------------------
	public SimpleCondition ()
		/* no throws Exception */
	{}

	//-------------------------------------------------------------------------
	public synchronized void reset()
		/* no throws Exception */
	{
		set ();
		mem_Core ().reset();
    }

	//-------------------------------------------------------------------------
    public synchronized void set()
    	/* no throws Exception */
    {
    	mem_Core ().countDown();
    }

	//-------------------------------------------------------------------------
    public /* no synchronized */ void await()
    	throws InterruptedException
    {
    	mem_Core ().await();
    }

	//-------------------------------------------------------------------------
    public /* no synchronized */ boolean await(final long     nTimeOut ,
    										   final TimeUnit aTimeUnit)
    	throws InterruptedException
    {
    	boolean bOK = mem_Core ().await(nTimeOut, aTimeUnit);
    	return bOK;
    }

	//-------------------------------------------------------------------------
	private synchronized ResetableCountDownLatch mem_Core ()
		// no throws
	{
		if (m_aCore == null)
			m_aCore = new ResetableCountDownLatch (1);
		return m_aCore;
	}

	//-------------------------------------------------------------------------
	private ResetableCountDownLatch m_aCore = null; 
}
