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
package net.as_development.asdk.sdt;

//=============================================================================
/** This is the base class for all deployment tasks used within SDT.
 * 
 *  A task is a Java object ...
 *  - bound to a {link Node}
 *  - able to access the configuration of such node
 *  - implementing one part of the whole deployment
 *  
 *  The combination of simple tasks can make a complex deployment at one node.
 *  
 *  There are already some default tasks implemented.
 *  As e.g. TaskPatchFramework or TaskScriptlet
 */
public abstract class TaskBase
{
    //-------------------------------------------------------------------------
	public TaskBase ()
	    throws Exception
	{}

	//-------------------------------------------------------------------------
	/** override this method and implement your deployment operation.
	 * 
	 *  The task is bound to the node passed as parameter to this method.
	 *  He provides some environment data and access to the node itself (SSH).
	 * 
	 *  Note: Don't catch any error inside. Let errors/exceptions pass.
	 *  It's up to the framework to know and handle it.
	 * 
	 *  @param	aNode [IN]
	 *  		the node where this task should run it's deployment.
	 *  
	 *  @throws any exception you want (if it indicates a real error - of course)
	 */
	public abstract void execute (final Node aNode)
		throws Exception;
}
