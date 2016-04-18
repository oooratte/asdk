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

import java.io.File;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;

import net.as_development.asdk.sdt.Node;
import net.as_development.asdk.sdt.TaskBase;
import net.as_development.asdk.ssh.SSHMacros;
import net.as_development.asdk.ssh.SSHServer;

//==============================================================================
public class TaskDirectUpload extends TaskBase
{
    //--------------------------------------------------------------------------
	public TaskDirectUpload ()
		throws Exception
	{}

    //--------------------------------------------------------------------------
	public static TaskDirectUpload create (final File   aSourceFile,
			  							   final File   aTargetFile  ,
			  							   final String sTargetOwner ,
			  							   final String sTargetGroup ,
			  							   final String sTargetRights)
		throws Exception
	{
		final InputStream      aStream = FileUtils.openInputStream(aSourceFile);
		final TaskDirectUpload aTask   = TaskDirectUpload.create(aStream, aTargetFile, sTargetOwner, sTargetGroup, sTargetRights);
		return aTask;
	}
	
    //--------------------------------------------------------------------------
	public static TaskDirectUpload create (final InputStream aSourceStream,
			  							   final File        aTargetFile  ,
			  							   final String      sTargetOwner ,
			  							   final String      sTargetGroup ,
			  							   final String      sTargetRights)
		throws Exception
	{
		final TaskDirectUpload aTask = new TaskDirectUpload ();
		aTask.m_aSourceStream = aSourceStream;
		aTask.m_aTargetFile   = aTargetFile  ;
		aTask.m_sTargetOwner  = sTargetOwner ;
		aTask.m_sTargetGroup  = sTargetGroup ;
		aTask.m_sTargetRights = sTargetRights;
		return aTask;
	}

	//--------------------------------------------------------------------------
	@Override
	public void execute(final Node aNode)
		throws Exception
	{
		System.out.println("execute direct upload of file '"+m_aTargetFile+"' ...");

		final SSHServer aSSH        = aNode.accessSSH();
		final String    sRemoteFile = m_aTargetFile.getAbsolutePath();

		SSHMacros.dumpToFile(aSSH, sRemoteFile, m_aSourceStream);
		SSHMacros.chown     (aSSH, sRemoteFile, m_sTargetOwner );
		SSHMacros.chgrp     (aSSH, sRemoteFile, m_sTargetGroup );
		SSHMacros.chmod     (aSSH, sRemoteFile, m_sTargetRights);

		System.out.println("ok");
	}
	
    //--------------------------------------------------------------------------
	private InputStream m_aSourceStream = null;

	//--------------------------------------------------------------------------
	private File m_aTargetFile = null;

	//--------------------------------------------------------------------------
	private String m_sTargetOwner = null;

	//--------------------------------------------------------------------------
	private String m_sTargetGroup = null;

	//--------------------------------------------------------------------------
	private String m_sTargetRights = null;
}
