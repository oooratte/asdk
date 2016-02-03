/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
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
		System.out.println("execute direct command '"+m_sCommand+"' ...");

		final boolean  bDebug    = aNode.accessSDT().isDebug();
		final String   sSDT_HOME = SDTConst.DEFAULT_SDT_HOME;
		final String   sSdtSh    = SDT.defineSDTResource(sSDT_HOME, SDTConst.SDT_DIR_BIN, SDTConst.SDT_SH_STD);
		final String   sTempSh   = SDT.defineSDTResource(sSDT_HOME, SDTConst.SDT_DIR_TEMP, "direct-command.sh");
		final String   sContent  = impl_defineTempScriptContent ();
              String[] lArgs     = new String[4];
		
		final SSHServer aSSH = aNode.accessSSH();
		SSHMacros.dumpToFile(aSSH, sTempSh, sContent);
		SSHMacros.chmod     (aSSH, sTempSh, "755"   );
		      
		lArgs[0] = "--run-command";
		lArgs[1] = sTempSh;
		lArgs[2] = "--debug";
		lArgs[3] = Boolean.toString(bDebug);
		lArgs    = ArrayUtils.addAll(lArgs, m_lArguments);
		
		SSHMacros.execScript(aSSH, sSdtSh, lArgs);

		System.out.println("ok");
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
