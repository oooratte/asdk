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
		log(aNode, "execute direct SDT function '"+m_sFunction+"' ...");

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

		log(aNode, "ok");
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
