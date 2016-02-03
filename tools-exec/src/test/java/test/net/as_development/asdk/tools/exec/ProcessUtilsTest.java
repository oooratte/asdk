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

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import net.as_development.asdk.tools.exec.ProcessUtils;
import net.as_development.asdk.tools.exec.impl.GlobalPidProcessHandler;

//==============================================================================
public class ProcessUtilsTest
{
    //--------------------------------------------------------------------------
	@Before
	public void setUp()
		throws Exception
	{
	}

    //--------------------------------------------------------------------------
	@After
	public void tearDown()
		throws Exception
	{
	}

    //--------------------------------------------------------------------------
	@Test
	public void testGetPidOfThis ()
		throws Exception
	{
		final Integer nPid = ProcessUtils.getPidOfThis();
		Assert.assertNotNull("testGetPidOfThis [01]", nPid    );
		Assert.assertTrue   ("testGetPidOfThis [02]", nPid > 0);
	}

	//--------------------------------------------------------------------------
	@Test
	public void testGetRSS ()
		throws Exception
	{
		final Integer nPid = ProcessUtils.getPidOfThis();
		final Long    nRSS = ProcessUtils.getProcessResidentSetSize (nPid);
		Assert.assertNotNull("testGetRSS [01]", nRSS    );
		Assert.assertTrue   ("testGetRSS [02]", nRSS > 0);
	}

	//--------------------------------------------------------------------------
	@Test
	public void testGetVSZ ()
		throws Exception
	{
		final Integer nPid = ProcessUtils.getPidOfThis();
		final Long    nVSZ = ProcessUtils.getProcessVirtualSetSize (nPid);
		Assert.assertNotNull("testGetVSZ [01]", nVSZ    );
		Assert.assertTrue   ("testGetVSZ [02]", nVSZ > 0);
	}

	//--------------------------------------------------------------------------
	@Test
	public void testRobustnessRSS ()
		throws Exception
	{
		final Integer nPid = GlobalPidProcessHandler.INVALID_PID;
		final Long    nRSS = ProcessUtils.getProcessResidentSetSize (nPid);
		Assert.assertNull("testRobustnessRSS [01]", nRSS);
	}

	//--------------------------------------------------------------------------
	@Test
	public void testRobustnessVSZ ()
		throws Exception
	{
		final Integer nPid = GlobalPidProcessHandler.INVALID_PID;
		final Long    nVSZ = ProcessUtils.getProcessVirtualSetSize (nPid);
		Assert.assertNull("testRobustnessVSZ [01]", nVSZ);
	}
}
