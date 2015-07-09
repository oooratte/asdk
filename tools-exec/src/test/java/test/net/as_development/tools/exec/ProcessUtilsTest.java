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

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import net.as_development.tools.exec.ProcessUtils;
import net.as_development.tools.exec.impl.GlobalPidProcessHandler;

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
