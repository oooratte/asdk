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
package net.as_development.tools.reflection;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

//=============================================================================
/** Collect helper functions around Java packages ...
 */
public class PackageUtils
{
    //-------------------------------------------------------------------------
    public static final String PACKAGE_SEPARATOR = ".";
    
    //-------------------------------------------------------------------------
    public static final String RESOURCE_SEPARATOR = "/";
    
    //-------------------------------------------------------------------------
    public static final String URLPROTOCOL_FILE = "file";
    
    //-------------------------------------------------------------------------
    public static final String URLPROTOCOL_JAR = "jar";
    
    //-------------------------------------------------------------------------
    /** @return a path created from the given package.
     *  Will be null for null or empty packages.
     * 
     *  Those path can't be used as system path directly.
     *  It can be used by Class.getResource() only further.
     *  
     *  E.g. you can convert a package 'example.package'
     *  to it's resource path '/example/package'.
     * 
     *  @param  sPackage [IN]
     *          the package.
     *          Can be null or empty ... but we return null also then .-)
     */
    public static String getResourcePathForPackage (String sPackage)
        throws Exception
    {
        String sTrimmed = StringUtils.trimToEmpty(sPackage);
        if (StringUtils.isEmpty (sTrimmed))
            return null;
        
        String sPath      = StringUtils.replace(sTrimmed, PackageUtils.PACKAGE_SEPARATOR, PackageUtils.RESOURCE_SEPARATOR);
        String sFinalPath = sPath; 
        
        if ( ! StringUtils.startsWith(sPath, PackageUtils.RESOURCE_SEPARATOR))
            sFinalPath = PackageUtils.RESOURCE_SEPARATOR + sPath;
        
        return sFinalPath;
    }
    
    //-------------------------------------------------------------------------
    /** @return a list of entries part of the specified package.
     *
     *  The returned list wont be null - but can be empty.
     *  All entries within those list will be relative
     *  to the given package. If you wish to get full qualified
     *  entry paths concatenate the results with the package by yourself .-)
     * 
     *  @param  sPackage [IN]
     *          specify the Java package to be scanned for entries.
     */
    public static List< String > listPackageEntries (String sPackage)
        throws Exception
    {
        List< String > lEntries  = new Vector< String > ();
        if (StringUtils.isEmpty (sPackage))
            return lEntries;

        String sPath = PackageUtils.getResourcePathForPackage(sPackage);
        URL    aURL  = PackageUtils.class.getResource(sPath);
        if (aURL == null)
            return lEntries;

        String sProtocol = aURL.getProtocol();
        
        if (StringUtils.equalsIgnoreCase(sProtocol, PackageUtils.URLPROTOCOL_FILE))
            PackageUtils.impl_listFileSystemEntries(aURL, sPath, lEntries);
        else
        if (StringUtils.equalsIgnoreCase(sProtocol, PackageUtils.URLPROTOCOL_JAR))
            PackageUtils.impl_listJarEntries (aURL, sPath, lEntries);
        else
            throw new IOException ("Could not list package entries for package '"+sPackage+"'. Support for protocol '"+sProtocol+"' not implemented yet.");
        
        return lEntries;
    }
    
    //-------------------------------------------------------------------------
    private static void impl_listJarEntries (URL            aURL ,
                                             String         sBase,
                                             List< String > lEntries)
        throws Exception
    {
        JarURLConnection        aConnection = (JarURLConnection)aURL.openConnection();
        JarFile                 aJar        = aConnection.getJarFile();
        Enumeration< JarEntry > lJarEntries = aJar.entries();
        
        while (lJarEntries.hasMoreElements())
        {
            JarEntry aEntry    = lJarEntries.nextElement();
            String   sEntry    = aEntry.getName ();

            // arrrrgggggg: sometimes packages start with / and sometimes not
            // Make sure WE START WITH / always ... otherwise following code will fail .-(
            if ( ! StringUtils.startsWith(sEntry, PackageUtils.RESOURCE_SEPARATOR))
                sEntry = PackageUtils.RESOURCE_SEPARATOR + sEntry;
            
            // ignore all entries not part of the requested package !
            if ( ! StringUtils.startsWith(sEntry, sBase))
                continue;
            
            String   sRelative = PackageUtils.impl_makeRelativePath(sEntry, sBase);
                     
            lEntries.add (sRelative);
        }
    }
    
    //-------------------------------------------------------------------------
    private static void impl_listFileSystemEntries (URL            aURL    ,
                                                    String         sBasea   ,
                                                    List< String > lEntries)
        throws Exception
    {
        File             aRootPath = FileUtils.toFile(aURL);
        String           sBase     = FilenameUtils.separatorsToUnix(aRootPath.getAbsolutePath());
        Iterator< File > lFiles    = FileUtils.listFiles(aRootPath, null, true).iterator();
        
        while (lFiles.hasNext())
        {
            File aFile = lFiles.next();
            if (aFile.isDirectory())
                continue;
            
            String sFile     = FilenameUtils.separatorsToUnix(aFile.getAbsolutePath());
            String sRelative = PackageUtils.impl_makeRelativePath(sFile, sBase);
                     
            lEntries.add (sRelative);
        }
    }
    
    //-------------------------------------------------------------------------
    private static String impl_makeRelativePath (String sPath,
                                                 String sRoot)
        throws Exception
    {
        // strip base path from original path.
        String sRelative = StringUtils.replace(sPath, sRoot, "");
        
        // make sure there is no leading '/' for relative paths
        // so concatenation at outside code works without problems.
        sRelative = StringUtils.removeStart(sRelative, PackageUtils.RESOURCE_SEPARATOR);

        return sRelative;
    }
}
