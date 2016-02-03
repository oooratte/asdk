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
