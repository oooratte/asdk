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

import java.util.Map;

import net.as_development.asdk.sdt.Node;
import net.as_development.asdk.sdt.SDT;
import net.as_development.asdk.sdt.SDTConst;
import net.as_development.asdk.sdt.TaskBase;
import net.as_development.asdk.ssh.SSHMacros;
import net.as_development.asdk.ssh.SSHServer;
import net.as_development.asdk.tools.common.CollectionUtils;

//==============================================================================
public class TaskPatchConfig extends TaskBase
{
    //--------------------------------------------------------------------------
	public TaskPatchConfig ()
		throws Exception
	{}

    //--------------------------------------------------------------------------
	public static TaskPatchConfig create (final String   sConfig    ,
			  							  final String[] aFlatConfig)
		throws Exception
	{
		final TaskPatchConfig aTask = new TaskPatchConfig ();
		aTask.setConfig(sConfig, aFlatConfig);
		return aTask;
	}
	
    //--------------------------------------------------------------------------
	public void setConfig (final String   sConfig    ,
						   final String[] aFlatConfig)
	    throws Exception
	{
		m_sConfig = sConfig;
		m_aConfig = CollectionUtils.flat2MappedArguments(aFlatConfig);
	}
	
    //--------------------------------------------------------------------------
	@Override
	public void execute(final Node aNode)
		throws Exception
	{
		System.out.println("patch config '"+m_sConfig+"' ...");

		final String    sSDT_HOME    = SDTConst.DEFAULT_SDT_HOME;
		final String    sSdtConfFile = SDT.defineSDTResource(sSDT_HOME, SDTConst.SDT_DIR_CONFIG, m_sConfig+".properties");
		final String    sContent     = CollectionUtils.formatAsProperties(m_aConfig);
		final SSHServer aSSH         = aNode.accessSSH();

		SSHMacros.dumpToFile(aSSH, sSdtConfFile, sContent);

		System.out.println("ok");
	}

    //--------------------------------------------------------------------------
	private String m_sConfig = null;

	//--------------------------------------------------------------------------
	private Map< String, String > m_aConfig = null;
}
