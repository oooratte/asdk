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
package test.net.as_development.asdk.tools.reflection;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import net.as_development.asdk.tools.reflection.PackageUtils;

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
        
        lEntries = PackageUtils.listPackageEntries("net.as_development.asdk.tools.reflection");
        Assert.assertNotNull ("testListPackageEntriesValid [01] must not be null" , lEntries           );
        Assert.assertTrue    ("testListPackageEntriesValid [02] must not be empty", lEntries.size() > 0);

        // check for some classes we know in that package .-)
        Assert.assertTrue ("testListPackageEntriesValid [03] miss AnnotationSearch.class"  , lEntries.contains("AnnotationSearch.class"  ));
        Assert.assertTrue ("testListPackageEntriesValid [04] miss ObjectManipulation.class", lEntries.contains("ObjectManipulation.class"));
        Assert.assertTrue ("testListPackageEntriesValid [05] miss PackagUtils.class"       , lEntries.contains("PackageUtils.class"      ));
    }
}
