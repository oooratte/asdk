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
import net.as_development.asdk.sdt.SDT;
import net.as_development.asdk.sdt.SDTConst;
import net.as_development.asdk.sdt.TaskBase;
import net.as_development.asdk.ssh.SSHMacros;
import net.as_development.asdk.ssh.SSHServer;

//==============================================================================
public class TaskDirectCommand extends TaskBase
{
    //--------------------------------------------------------------------------
	public TaskDirectCommand ()
		throws Exception
	{}

    //--------------------------------------------------------------------------
	public static TaskDirectCommand create (final String    sCommand  ,
			  							    final String... lArguments)
		throws Exception
	{
		final TaskDirectCommand aTask = new TaskDirectCommand ();
		aTask.setCommand(sCommand, lArguments);
		return aTask;
	}
	
    //--------------------------------------------------------------------------
	public void setCommand (final String    sCommand  ,
							final String... lArguments)
	    throws Exception
	{
		m_sCommand   = sCommand  ;
		m_lArguments = lArguments;
	}
	
    //--------------------------------------------------------------------------
	@Override
	public void execute(final Node aNode)
		throws Exception
	{
		log(aNode, "execute direct command '"+m_sCommand+"' ...");

		final boolean  bDebug    = aNode.accessSDT().isDebug();
		final String   sSDT_HOME = SDTConst.DEFAULT_SDT_HOME;
		final String   sSdtSh    = SDT.defineSDTResource(sSDT_HOME, SDTConst.SDT_DIR_BIN, SDTConst.SDT_SH_STD);
		final String   sTempSh   = SDT.defineSDTResource(sSDT_HOME, SDTConst.SDT_DIR_TEMP, "direct-command.sh");
		final String   sContent  = impl_defineTempScriptContent ();
              String[] lArgs     = new String[4];
		
		final SSHServer aSSH = aNode.accessSSH();
		SSHMacros.dumpToFile(aSSH, sTempSh, sContent);
		SSHMacros.chmod     (aSSH, sTempSh, "755"   );
		      
		lArgs[0] = "--debug";
		lArgs[1] = Boolean.toString(bDebug);
		lArgs[2] = "--run-command";
		lArgs[3] = sTempSh;
		
		SSHMacros.execScript(aSSH, sSdtSh, lArgs);

		log(aNode, "ok");
	}

    //--------------------------------------------------------------------------
	private String impl_defineTempScriptContent ()
	    throws Exception
	{
		final StringBuffer sContent = new StringBuffer (256);
		
		sContent.append("#!/bin/bash\n");
		sContent.append("set -e\n"     );
		sContent.append("lib_exec \""  );
		sContent.append(m_sCommand     );
		
		if (m_lArguments != null)
		{
			for (final String sArg : m_lArguments)
			{
				sContent.append(" " );
				sContent.append(sArg);
			}
		}
		
		sContent.append("\""           );
		
		return sContent.toString ();
	}
	
    //--------------------------------------------------------------------------
	protected String m_sCommand = null;

	//--------------------------------------------------------------------------
	protected String[] m_lArguments = null;
}
