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
package net.as_development.asdk.sdt.desc;

import net.as_development.asdk.ssh.SSHIdentity;

//=============================================================================
public class AuthenticationDescriptor
{
    //-------------------------------------------------------------------------
	public AuthenticationDescriptor ()
	    throws Exception
	{}

	//-------------------------------------------------------------------------
	public static AuthenticationDescriptor defineUserWidthPassword (final String sUser    ,
															        final String sPassword)
	    throws Exception
	{
		final AuthenticationDescriptor aDesc = new AuthenticationDescriptor ();
		aDesc.sUser     = sUser    ;
		aDesc.sPassword = sPassword;
		return aDesc;
	}
	
	//-------------------------------------------------------------------------
	public static AuthenticationDescriptor defineUserWidthKey (final String sUser   ,
															   final String sKeyFile)
	    throws Exception
	{
		final AuthenticationDescriptor aDesc = new AuthenticationDescriptor ();
		aDesc.sUser    = sUser    ;
		aDesc.sKeyFile = sKeyFile;
		return aDesc;
	}

	//-------------------------------------------------------------------------
	public static AuthenticationDescriptor defineUser (final String  sUser     ,
													   final String  sPassword ,
													   final String  sKeyFile  ,
													   final boolean bNeedsSudo)
	    throws Exception
	{
		final AuthenticationDescriptor aDesc = new AuthenticationDescriptor ();
		aDesc.sUser      = sUser     ;
		aDesc.sPassword  = sPassword ;
		aDesc.sKeyFile   = sKeyFile  ;
		aDesc.bNeedsSudo = bNeedsSudo;
		return aDesc;
	}

	//-------------------------------------------------------------------------
	public static SSHIdentity toSSHIdentity (final AuthenticationDescriptor aAuth)
	    throws Exception
	{
		final SSHIdentity aIdentity = new SSHIdentity ();
		aIdentity.setUser       (aAuth.sUser     );
		aIdentity.setPassword   (aAuth.sPassword );
		aIdentity.setNeedsSudo  (aAuth.bNeedsSudo);
		aIdentity.setKeyFileName(aAuth.sKeyFile  );
		return aIdentity;
	}
	
	//-------------------------------------------------------------------------
	public String sUser = null;

	//-------------------------------------------------------------------------
	public String sPassword = null;

	//-------------------------------------------------------------------------
	public String sKeyFile = null;

	//-------------------------------------------------------------------------
	public Boolean bNeedsSudo = false;
}
