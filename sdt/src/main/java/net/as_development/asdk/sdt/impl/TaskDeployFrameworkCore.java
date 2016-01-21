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

import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

import net.as_development.asdk.sdt.Node;
import net.as_development.asdk.sdt.TaskBase;
import net.as_development.asdk.sdt.desc.AuthenticationDescriptor;
import net.as_development.asdk.sdt.desc.NodeDescriptor;
import net.as_development.asdk.ssh.SSHIdentity;
import net.as_development.asdk.ssh.SSHMacros;
import net.as_development.asdk.ssh.SSHServer;

//=============================================================================
public class TaskDeployFrameworkCore extends TaskBase
{
    //-------------------------------------------------------------------------
	public static final String DEFAULT_SDT_HOME = "/opt/sdt";
	
    //-------------------------------------------------------------------------
	public TaskDeployFrameworkCore ()
	    throws Exception
	{}
	
	//-------------------------------------------------------------------------
	@Override
	public void execute (final Node aNode)
		throws Exception
	{
		final SSHServer aSSH      = aNode.accessSSH();
		final String    sSDT_HOME = DEFAULT_SDT_HOME;

		if (impl_isFrameworkInstalled (aSSH, sSDT_HOME))
			return;

		final String sThisPackage  = TaskDeployFrameworkCore.class.getPackage().getName();
		final String sResPackage   = StringUtils.replace(sThisPackage, ".sdt.impl", ".sdt.res");
		
		uploadFwkParts (aSSH, sResPackage, sSDT_HOME);
	}

	//-------------------------------------------------------------------------
	protected void uploadFwkParts (final SSHServer aSSH            ,
								   final String    sResourcePackage,
								   final String    sRemotePath     )
		throws Exception
	{
		SSHMacros.mkdir(aSSH, sRemotePath);
		SSHMacros.chmod(aSSH, sRemotePath, "0777");
		
		SSHMacros.uploadResourceTree(aSSH, sResourcePackage, sRemotePath);

		final Properties aConfig = new Properties ();
		aConfig.setProperty("SDT_HOME", sRemotePath);
		
		final String sUserHome = SSHMacros.getEnvHOME(aSSH);
		SSHMacros.createPropertiesFile(aSSH, sUserHome+"/.sdt", "config.properties", aConfig);

		SSHMacros.chmodRecursive(aSSH, sRemotePath, false, null  , "0644");
		SSHMacros.chmodRecursive(aSSH, sRemotePath, true , null  , "0644");
		SSHMacros.chmodRecursive(aSSH, sRemotePath, true , "*.sh", "0755");
		
		aSSH.disconnect();
	}

	//-------------------------------------------------------------------------
	private boolean impl_isFrameworkInstalled (final SSHServer aSSH       ,
											   final String    sRemotePath)
	    throws Exception
	{
		// TODO check more then 'dir exists'  ... e.g. MD5 of alle files, version file in root etcpp
		final boolean bExists = SSHMacros.existsDir (aSSH, sRemotePath);
		return bExists;
	}
}
