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
package net.as_development.asdk.ssh;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

public class SSHMacros
{
	//-------------------------------------------------------------------------
	private SSHMacros ()
	{}
	
	//-------------------------------------------------------------------------
	public static String getEnvHOME (final SSHServer aServer)
	    throws Exception
	{
		final SSHShellExecute    aShell  = aServer.accessShell();
		final SSHStdOutErrSinkV2 aStdOut = aServer.accessStdOutErr();
		final String             sCmd    = "echo -n $HOME";
			  
	    aShell.execute(sCmd);

	    final String sHOME = aStdOut.getStdOutAndClear();
	    return sHOME;
	}

	//-------------------------------------------------------------------------
	public static String getEnvTEMP (final SSHServer aServer)
	    throws Exception
	{
	    final String sTEMP = readFromExec(aServer, "dirname $(mktemp -u)");
	    return sTEMP;
	}

	//-------------------------------------------------------------------------
	public static String readFromExec (final SSHServer aServer,
									   final String    sExec  )
	    throws Exception
	{
		final SSHShellExecute    aShell  = aServer.accessShell();
		final SSHStdOutErrSinkV2 aStdOut = aServer.accessStdOutErr();
		final String             sCmd    = "echo -n $("+sExec+")";
			  
	    aShell.execute(sCmd);

	    final String sResult = aStdOut.getStdOutAndClear();
	    return sResult;
	}

	//-------------------------------------------------------------------------
	public static boolean existsDir (final SSHServer aServer    ,
							         final String    sRemotePath)
	    throws Exception
	{
		final String sResult = SSHMacros.readFromExec(aServer, "if [[ -d "+sRemotePath+" ]]; then echo 1; else echo 0; fi;");

		if (StringUtils.equals(sResult, "1"))
			return true;
		else
			return false;
	}

	//-------------------------------------------------------------------------
	public static int execScript (final SSHServer aServer      ,
							      final String    sRemoteScript,
							      final String... lArguments   )
	    throws Exception
	{
		final SSHShellExecute aShell = aServer.accessShell();
		final StringBuffer    sCmd   = new StringBuffer (256);
		
		sCmd.append(sRemoteScript);
		
		if (lArguments != null)
		{
			for (final String sArg : lArguments)
			{
				sCmd.append(" " );
				sCmd.append(sArg);
			}
		}
			  
	    final int nState = aShell.execute(sCmd.toString ());
	    return nState;
	}

	//-------------------------------------------------------------------------
	public static int mkdir (final SSHServer aServer    ,
							 final String    sRemotePath)
	    throws Exception
	{
		final SSHShellExecute aShell = aServer.accessShell();
		final String          sCmd   = "mkdir -p "+sRemotePath;
			  int             nState = 0;
			  
	    nState = aShell.execute(sCmd);
	    return nState;
	}

	//-------------------------------------------------------------------------
	public static int touch (final SSHServer aServer    ,
							 final String    sRemotePath,
							 final String    sRemoteFile)
	    throws Exception
	{
		final SSHShellExecute aShell = aServer.accessShell();
		final String          sCmd   = "touch "+sRemotePath+"/"+sRemoteFile;
			  int             nState = 0;
			  
	    nState = aShell.execute(sCmd);
	    return nState;
	}

	//-------------------------------------------------------------------------
	public static int chmod (final SSHServer aServer          ,
							 final String    sRemotePathOrFile,
							 final String    sMod             )
	    throws Exception
	{
		final SSHShellExecute aShell = aServer.accessShell();
		final String          sCmd   = "chmod "+sMod+" "+sRemotePathOrFile;
			  int             nState = 0;
			  
	    nState = aShell.execute(sCmd);
	    return nState;
	}

	//-------------------------------------------------------------------------
	public static int chmodRecursive (final SSHServer aServer    ,
							          final String    sRemotePath,
							          final boolean   bFiles     ,
							          final String    sPattern   ,
							          final String    sMod       )
	    throws Exception
	{
		final SSHShellExecute aShell = aServer.accessShell();
		final StringBuffer    sCmd   = new StringBuffer (256);
		
		sCmd.append("find "    );
		sCmd.append(sRemotePath);
		
		if (bFiles)
			sCmd.append(" -type f");
		else
			sCmd.append(" -type d");
		
		if ( ! StringUtils.isEmpty(sPattern))
		{
			sCmd.append(" -name \"");
			sCmd.append(sPattern   );
			sCmd.append("\""       );
		}
		
		sCmd.append(" -print0"          );
		sCmd.append(" | xargs -0 chmod ");
		sCmd.append(sMod                );
		
	    final int nState = aShell.execute(sCmd.toString ());
	    return nState;
	}

	//-------------------------------------------------------------------------
	public static int moveFile (final SSHServer aServer ,
						        final String    sSrcPath,
						        final String    sSrcFile,
						        final String    sDstPath,
						        final String    sDstFile)
	    throws Exception
	{
		final SSHShellExecute aShell = aServer.accessShell();
		final StringBuffer    sCmd   = new StringBuffer (256);
		final String          sSrc   = FilenameUtils.concat(sSrcPath, sSrcFile);
		final String          sDst   = FilenameUtils.concat(sDstPath, sDstFile);
		
		sCmd.append("mv ");
		sCmd.append(sSrc );
		sCmd.append(" "  );
		sCmd.append(sDst );
		
	    final int nState = aShell.execute(sCmd.toString ());
	    return nState;
	}

	//-------------------------------------------------------------------------
	/**
	 */
	public static int dumpToFile (final SSHServer aServer    ,
								  final String    sRemoteFile,
								  final String    sContent   )
	    throws Exception
	{
		final String sRemotePath     = FilenameUtils.getFullPathNoEndSeparator(sRemoteFile);
		final String sRemoteFileName = FilenameUtils.getName                  (sRemoteFile);
              int    nState          = 0;
		
		nState = SSHMacros.mkdir (aServer, sRemotePath);
		if (nState != 0)
			return nState;
		
		final SSHSFtp     aUpload = aServer.accessSFTP ();
	          InputStream aStream = null;
	    
	    try
	    {
			aStream = IOUtils.toInputStream(sContent);
			nState  = aUpload.uploadStream(aStream, sRemotePath, sRemoteFileName);
	    }
	    finally
	    {
	    	IOUtils.closeQuietly(aStream);
	    }

	    return nState;
	}
	
	//-------------------------------------------------------------------------
	/**
	 * @param	sResourceFile [IN]
	 * 			the absolute path and file name of a local resource within class path.
	 * 
	 * @param	sRemoteFile [IN]
	 * 			the absolute path and file name of the target file at remote site.
	 */
	public static int uploadResourceFile (final SSHServer aServer      ,
										  final String    sResourceFile,
										  final String    sRemoteFile  )
	    throws Exception
	{
		final String sRemotePath     = FilenameUtils.getFullPathNoEndSeparator(sRemoteFile);
		final String sRemoteFileName = FilenameUtils.getName                  (sRemoteFile);
              int    nState          = 0;
		
		nState = SSHMacros.mkdir (aServer, sRemotePath);
		if (nState != 0)
			return nState;
		
		final SSHSFtp     aUpload   = aServer.accessSFTP ();
	    final Class< ? >  aLoader   = SSHMacros.class;
	    final URL         aResource = aLoader.getResource(sResourceFile);
	    final InputStream aStream   = aResource.openStream();

	    nState = aUpload.uploadStream(aStream, sRemotePath, sRemoteFileName);
	    return nState;
	}

	//-------------------------------------------------------------------------
	/**
	 */
	public static int uploadResourceTree (final SSHServer aServer      ,
										  final String    sResourceTree,
										  final String    sRemoteTree  )
	    throws Exception
	{
		
		final ByteArrayOutputStream aTarOut = new ByteArrayOutputStream ();

		impl_tarResourceTree (sResourceTree, aTarOut);
		
		final ByteArrayInputStream aTarIn = new ByteArrayInputStream (aTarOut.toByteArray());
		
		int nState  = 0;
		
		final SSHSFtp aUpload = aServer.accessSFTP ();
	    nState = aUpload.uploadStream(aTarIn, sRemoteTree, "_temp.tar");
	    
	    final SSHShellExecute aShell = aServer.accessShell();
	    nState = aShell.execute("tar --extract --verbose --directory "+sRemoteTree+" --file "+sRemoteTree+"/_temp.tar");
	    nState = aShell.execute("rm "+sRemoteTree+"/_temp.tar");

	    return nState;
	}

	//-------------------------------------------------------------------------
	public static void createPropertiesFile (final SSHServer  aServer    ,
										     final String     sRemotePath,
										     final String     sRemoteFile,
										     final Properties lProps     )
	    throws Exception
	{
		final SSHPropertiesFile aProps = aServer.accessProperties();
		aProps.setProperties(sRemotePath, sRemoteFile, lProps);
	}
	
	//-------------------------------------------------------------------------
	private static void impl_tarResourceTree (final String       sResourceTree,
											  final OutputStream aTarStream   )
	    throws Exception
	{
		final String                 sResPackage   = sResourceTree;
		final String                 sResPath      = StringUtils.replace(sResPackage , "."   , "/");
		final Reflections            aScanner 	   = new Reflections(new ConfigurationBuilder()
							         			 				    .setUrls       (ClasspathHelper.forPackage(sResPackage))
							         			 				    .setScanners   (new ResourcesScanner())
							         			 				    .filterInputsBy(new FilterBuilder().includePackage(sResPackage))
							          							    );
		final Set< String >          lAllResources = aScanner.getResources(Pattern.compile(".*"));
		final Class< ? >             aResLoader    = SSHMacros.class;
		final TarArchiveOutputStream aTar          = new TarArchiveOutputStream (aTarStream);
		
		for (final String sResource : lAllResources)
		{
		    final URL             aResource = aResLoader.getResource("/"+sResource);
		    final InputStream     aStream   = aResource.openStream();
		    final byte[]          aContent  = IOUtils.toByteArray(aStream);
		    final int             nSize     = aContent.length;
		    final String          sName     = StringUtils.removeStart(sResource, sResPath);
			final TarArchiveEntry aResEntry = new TarArchiveEntry (sName);

			aResEntry.setSize          (nSize    );
			aTar     .putArchiveEntry  (aResEntry);
			aTar     .write            (aContent );
			aTar     .closeArchiveEntry(         );
		}

		Validate.isTrue(aTar.getBytesWritten() > 0, "Tar has no content !");
		
		aTar.close();
	}
}
