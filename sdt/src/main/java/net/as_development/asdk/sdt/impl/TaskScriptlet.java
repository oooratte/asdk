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

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;

import net.as_development.asdk.sdt.Node;
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

		final String   sSDT_HOME = "/opt/sdt";
		final String   sSdtSh    = FilenameUtils.concat(sSDT_HOME, "bin/sdt.sh");
		      String[] lArgs     = new String[2];

		lArgs[0] = "--run-scriptlet";
		lArgs[1] = m_sScriptlet;
		lArgs    = ArrayUtils.addAll(lArgs, m_lArguments);
		
		final SSHServer aSSH = aNode.accessSSH();
		SSHMacros.execScript(aSSH, sSdtSh, lArgs);

		System.out.println("ok");
	}

    //--------------------------------------------------------------------------
	private String m_sScriptlet = null;

	//--------------------------------------------------------------------------
	private String[] m_lArguments = null;
}
