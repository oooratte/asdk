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

import org.apache.commons.lang3.ArrayUtils;

import net.as_development.asdk.sdt.Node;
import net.as_development.asdk.sdt.SDT;
import net.as_development.asdk.sdt.SDTConst;
import net.as_development.asdk.sdt.TaskBase;
import net.as_development.asdk.ssh.SSHMacros;
import net.as_development.asdk.ssh.SSHServer;

//==============================================================================
public class TaskScriptlet extends TaskBase
{
    //--------------------------------------------------------------------------
	public TaskScriptlet ()
		throws Exception
	{}

    //--------------------------------------------------------------------------
	public static TaskScriptlet create (final String   sScriptlet,
			  							final String[] lArguments)
		throws Exception
	{
		final TaskScriptlet aTask = new TaskScriptlet ();
		aTask.setScriptlet(sScriptlet, lArguments);
		return aTask;
	}
	
    //--------------------------------------------------------------------------
	public void setScriptlet (final String   sScriptlet,
							  final String[] lArguments)
	    throws Exception
	{
		m_sScriptlet = sScriptlet;
		m_lArguments = lArguments;
	}
	
    //--------------------------------------------------------------------------
	@Override
	public void execute(final Node aNode)
		throws Exception
	{
		System.out.println("execute scriptlet '"+m_sScriptlet+"' ...");

		final boolean  bDebug    = aNode.accessSDT().isDebug();
		final String   sSDT_HOME = SDTConst.DEFAULT_SDT_HOME;
		final String   sSdtSh    = SDT.defineSDTResource(sSDT_HOME, SDTConst.SDT_DIR_BIN, SDTConst.SDT_SH_STD);
		      String[] lArgs     = new String[4];

		lArgs[0] = "--run-scriptlet";
		lArgs[1] = m_sScriptlet;
		lArgs[2] = "--debug";
		lArgs[3] = Boolean.toString(bDebug);
		lArgs    = ArrayUtils.addAll(lArgs, m_lArguments);
		
		final SSHServer aSSH = aNode.accessSSH();
		SSHMacros.execScript(aSSH, sSdtSh, lArgs);

		System.out.println("ok");
	}

    //--------------------------------------------------------------------------
	protected String m_sScriptlet = null;

	//--------------------------------------------------------------------------
	protected String[] m_lArguments = null;
}
