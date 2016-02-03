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

import java.util.Map;

import net.as_development.asdk.sdt.Node;
import net.as_development.asdk.sdt.SDT;
import net.as_development.asdk.sdt.SDTConst;
import net.as_development.asdk.sdt.TaskBase;
import net.as_development.asdk.ssh.SSHMacros;
import net.as_development.asdk.ssh.SSHServer;
import net.as_development.asdk.tools.common.CollectionUtils;

//==============================================================================
public class TaskPatchConfig extends TaskBase
{
    //--------------------------------------------------------------------------
	public TaskPatchConfig ()
		throws Exception
	{}

    //--------------------------------------------------------------------------
	public static TaskPatchConfig create (final String   sConfig    ,
			  							  final String[] aFlatConfig)
		throws Exception
	{
		final TaskPatchConfig aTask = new TaskPatchConfig ();
		aTask.setConfig(sConfig, aFlatConfig);
		return aTask;
	}
	
    //--------------------------------------------------------------------------
	public void setConfig (final String   sConfig    ,
						   final String[] aFlatConfig)
	    throws Exception
	{
		m_sConfig = sConfig;
		m_aConfig = CollectionUtils.flat2MappedArguments(aFlatConfig);
	}
	
    //--------------------------------------------------------------------------
	@Override
	public void execute(final Node aNode)
		throws Exception
	{
		System.out.println("patch config '"+m_sConfig+"' ...");

		final String    sSDT_HOME    = SDTConst.DEFAULT_SDT_HOME;
		final String    sSdtConfFile = SDT.defineSDTResource(sSDT_HOME, SDTConst.SDT_DIR_CONFIG, m_sConfig+".properties");
		final String    sContent     = CollectionUtils.formatAsProperties(m_aConfig);
		final SSHServer aSSH         = aNode.accessSSH();

		SSHMacros.dumpToFile(aSSH, sSdtConfFile, sContent);

		System.out.println("ok");
	}

    //--------------------------------------------------------------------------
	private String m_sConfig = null;

	//--------------------------------------------------------------------------
	private Map< String, String > m_aConfig = null;
}
