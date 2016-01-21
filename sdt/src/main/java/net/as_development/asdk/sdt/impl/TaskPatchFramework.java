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
		System.out.println("patch SDT framework ...");

		final SSHServer aSSH = aNode.accessSSH();
		uploadFwkParts(aSSH, m_sPatchResourcePackage, DEFAULT_SDT_HOME);

		System.out.println("ok");
	}

    //--------------------------------------------------------------------------
	private String m_sPatchResourcePackage = null;
}
