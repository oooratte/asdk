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
