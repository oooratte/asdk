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

import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

import net.as_development.asdk.sdt.Node;
import net.as_development.asdk.sdt.TaskBase;
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
	public static TaskDeployFrameworkCore create ()
	    throws Exception
	{
		return new TaskDeployFrameworkCore ();
	}
	
    //-------------------------------------------------------------------------
	public static TaskDeployFrameworkCore createForceUpdate ()
	    throws Exception
	{
		final TaskDeployFrameworkCore aTask = new TaskDeployFrameworkCore ();
		aTask.m_bForceUpdate = true;
		return aTask;
	}

	//-------------------------------------------------------------------------
	@Override
	public void execute (final Node aNode)
		throws Exception
	{
		final SSHServer aSSH      = aNode.accessSSH();
		final String    sSDT_HOME = DEFAULT_SDT_HOME;

		if (
			( ! m_bForceUpdate                          ) &&
			(impl_isFrameworkInstalled (aSSH, sSDT_HOME))
		   )
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

	//-------------------------------------------------------------------------
	private boolean m_bForceUpdate = false;
}
