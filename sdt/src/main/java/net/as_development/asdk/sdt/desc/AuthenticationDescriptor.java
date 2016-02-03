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
