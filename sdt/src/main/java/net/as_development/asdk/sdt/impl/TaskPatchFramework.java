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
package net.as_development.asdk.sdt.impl;

import net.as_development.asdk.sdt.Node;
import net.as_development.asdk.ssh.SSHServer;

//==============================================================================
public class TaskPatchFramework extends TaskDeployFrameworkCore
{
    //--------------------------------------------------------------------------
	public TaskPatchFramework ()
		throws Exception
	{}

    //--------------------------------------------------------------------------
	public static TaskPatchFramework create (final String sResPackage)
		throws Exception
	{
		final TaskPatchFramework aTask = new TaskPatchFramework ();
		aTask.setPatchResourcePackage(sResPackage);
		return aTask;
	}
	
    //--------------------------------------------------------------------------
	public void setPatchResourcePackage (final String sResPackage)
	    throws Exception
	{
		m_sPatchResourcePackage = sResPackage;
	}
	
    //--------------------------------------------------------------------------
	@Override
	public void execute(final Node aNode)
		throws Exception
	{
		log(aNode, "patch SDT framework ...");

		final SSHServer aSSH = aNode.accessSSH();
		uploadFwkParts(aSSH, m_sPatchResourcePackage, DEFAULT_SDT_HOME);

		log(aNode, "ok");
	}

    //--------------------------------------------------------------------------
	private String m_sPatchResourcePackage = null;
}
