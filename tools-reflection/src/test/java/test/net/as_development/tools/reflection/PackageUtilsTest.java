/**
 * Copyright 2012 Andreas Schlüns - as-development.net
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package test.net.as_development.tools.reflection;

import java.util.List;

import net.as_development.tools.reflection.PackageUtils;

import org.junit.Assert;
import org.junit.Test;

//=============================================================================
public class PackageUtilsTest
{
    //-------------------------------------------------------------------------
    /** test if getPathForPackage () works for non existing and invalid packages.
     */
    @Test
    public void testPathForPackageInvalid ()
        throws Exception
    {
        Assert.assertNull ("testPathForPackageInvalid [01]", PackageUtils.getResourcePathForPackage(null  ));
        Assert.assertNull ("testPathForPackageInvalid [02]", PackageUtils.getResourcePathForPackage(""    ));
        Assert.assertNull ("testPathForPackageInvalid [03]", PackageUtils.getResourcePathForPackage("    "));
    }
    
    //-------------------------------------------------------------------------
    /** test if getPathForPackage () works for valid packages.
     */
    @Test
    public void testPathForPackageValid ()
        throws Exception
    {
        Assert.assertEquals ("testPathForPackageValid [01]", "/simple"           , PackageUtils.getResourcePathForPackage("simple"           ));
        Assert.assertEquals ("testPathForPackageValid [02]", "/complex/structure", PackageUtils.getResourcePathForPackage("complex.structure"));
        Assert.assertEquals ("testPathForPackageValid [03]", "/starts_with_dot"  , PackageUtils.getResourcePathForPackage(".starts_with_dot" ));
    }
    
    //-------------------------------------------------------------------------
    /** test if listPackageEntries () works for non existing and invalid packages.
     */
    @Test
    public void testListPackageEntriesInvalid ()
        throws Exception
    {
        List< String > lEntries = null;
        
        lEntries = PackageUtils.listPackageEntries(null);
        Assert.assertNotNull ("testListPackageEntriesInvalid [01] check if it can handle NULL package parameter", lEntries          );
        Assert.assertEquals  ("testListPackageEntriesInvalid [02] check if it can handle NULL package parameter", 0, lEntries.size());
        
        lEntries = PackageUtils.listPackageEntries("");
        Assert.assertNotNull ("testListPackageEntriesInvalid [03] check if it can handle empty package parameter", lEntries          );
        Assert.assertEquals  ("testListPackageEntriesInvalid [04] check if it can handle empty package parameter", 0, lEntries.size());
        
        lEntries = PackageUtils.listPackageEntries("non_existing_package");
        Assert.assertNotNull ("testListPackageEntriesInvalid [05] check if it can handle non existing package", lEntries          );
        Assert.assertEquals  ("testListPackageEntriesInvalid [06] check if it can handle non existing package", 0, lEntries.size());
    }
    
    //-------------------------------------------------------------------------
    /** test if listPackageEntries () works for valid packages
     *  (file based)
     */
    @Test
    public void testListPackageEntriesValid ()
        throws Exception
    {
        List< String > lEntries = null;
        
        lEntries = PackageUtils.listPackageEntries("net.as_development.tools.reflection");
        Assert.assertNotNull ("testListPackageEntriesValid [01] must not be null" , lEntries           );
        Assert.assertTrue    ("testListPackageEntriesValid [02] must not be empty", lEntries.size() > 0);

        // check for some classes we know in that package .-)
        Assert.assertTrue ("testListPackageEntriesValid [03] miss AnnotationSearch.class"  , lEntries.contains("AnnotationSearch.class"  ));
        Assert.assertTrue ("testListPackageEntriesValid [04] miss ObjectManipulation.class", lEntries.contains("ObjectManipulation.class"));
        Assert.assertTrue ("testListPackageEntriesValid [05] miss PackagUtils.class"       , lEntries.contains("PackageUtils.class"      ));
    }
}
