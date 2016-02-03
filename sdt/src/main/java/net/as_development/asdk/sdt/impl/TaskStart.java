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

//==============================================================================
public class TaskStart extends TaskScriptlet
{
    //--------------------------------------------------------------------------
	public TaskStart ()
		throws Exception
	{}

    //--------------------------------------------------------------------------
	public static TaskStart create (final int      nRunLevel ,
								    final String   sScriptlet,
			  						final String[] lArguments)
		throws Exception
	{
		final TaskStart aTask = new TaskStart ();
		aTask.setScriptlet(sScriptlet, lArguments);
		aTask.m_nRunLevel = nRunLevel;
		return aTask;
	}
	
    //--------------------------------------------------------------------------
	public int getRunLevel ()
	    throws Exception
	{
		return m_nRunLevel;
	}
	
    //--------------------------------------------------------------------------
	private int m_nRunLevel = 0;
}
