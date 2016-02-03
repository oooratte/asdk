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

//=============================================================================
public class NodeDescriptor
{
    //-------------------------------------------------------------------------
	public static final int DEFAULT_PORT_SSH = 22;
	
    //-------------------------------------------------------------------------
	public NodeDescriptor ()
	    throws Exception
	{}

	//-------------------------------------------------------------------------
	public static NodeDescriptor define (final String                   sID  ,
										 final String                   sIP  ,
										 final EOS                      eOS  ,
										 final AuthenticationDescriptor aAuth)
	    throws Exception
	{
		final NodeDescriptor aDesc = new NodeDescriptor ();
		aDesc.sId    = sID  ;
		aDesc.sIpDns = sIP  ;
		aDesc.eOS    = eOS  ;
		aDesc.aAuth  = aAuth;
		return aDesc;
	}

	//-------------------------------------------------------------------------
	public static NodeDescriptor defineIP (final String sIP)
	    throws Exception
	{
		final NodeDescriptor aDesc = new NodeDescriptor ();
		aDesc.sIpDns = sIP;
		return aDesc;
	}

	//-------------------------------------------------------------------------
	/// [optional] an outside unique identifier for this node
	public String sId = null;
	
	//-------------------------------------------------------------------------
	/// [optional] the OS installed on that node
	public EOS eOS = null;
	
	//-------------------------------------------------------------------------
	/// [mandatory] the IP or DNS name of this node
	public String sIpDns = null;

	//-------------------------------------------------------------------------
	/// [mandatory - if not default] the SSH port for remote connections
	public Integer nSSHPort = DEFAULT_PORT_SSH;

	//-------------------------------------------------------------------------
	/// [mandatory] the SSH authentication
	public AuthenticationDescriptor aAuth = null;
}
