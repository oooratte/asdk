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
public class TaskDirectSDTFunction extends TaskBase
{
    //--------------------------------------------------------------------------
	public TaskDirectSDTFunction ()
		throws Exception
	{}

    //--------------------------------------------------------------------------
	public static TaskDirectSDTFunction create (final String    sFunction ,
			  							        final String... lArguments)
		throws Exception
	{
		final TaskDirectSDTFunction aTask = new TaskDirectSDTFunction ();
		aTask.setFunction(sFunction, lArguments);
		return aTask;
	}
	
    //--------------------------------------------------------------------------
	public void setFunction (final String    sFunction ,
							 final String... lArguments)
	    throws Exception
	{
		m_sFunction  = sFunction ;
		m_lArguments = lArguments;
	}
	
    //--------------------------------------------------------------------------
	@Override
	public void execute(final Node aNode)
		throws Exception
	{
		System.out.println("execute direct SDT function '"+m_sFunction+"' ...");

		final SDT      aSDT      = aNode.accessSDT();
		final boolean  bDebug    = aSDT.isDebug();
		final String   sSDT_HOME = SDTConst.DEFAULT_SDT_HOME;
		final String   sSdtSh    = SDT.defineSDTResource(sSDT_HOME, SDTConst.SDT_DIR_BIN, SDTConst.SDT_SH_STD);
              String[] lShArgs   = new String[4];
		final String[] lFuncArgs = impl_quoteArgs (m_lArguments);
              
  		lShArgs[0] = "--debug";
		lShArgs[1] = Boolean.toString(bDebug);
		lShArgs[2] = "--run-function";
		lShArgs[3] = m_sFunction;
		lShArgs    = ArrayUtils.addAll(lShArgs, lFuncArgs);
		
		final SSHServer aSSH = aNode.accessSSH();
		SSHMacros.execScript(aSSH, sSdtSh, lShArgs);

		System.out.println("ok");
	}

    //--------------------------------------------------------------------------
	private String[] impl_quoteArgs (final String[] lArgs)
	    throws Exception
	{
        final int      c           = lArgs.length;
        final String[] lQuotedArgs = new String[c];
              int      i           = 0;

        for (i=0; i<c; ++i)
        	lQuotedArgs[i] = "\\\"" + lArgs[i] + "\\\"";
        
        return lQuotedArgs;
	}
	
    //--------------------------------------------------------------------------
	protected String m_sFunction = null;

	//--------------------------------------------------------------------------
	protected String[] m_lArguments = null;
}
