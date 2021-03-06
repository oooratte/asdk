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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import net.as_development.asdk.tools.reflection.AnnotationSearch;
import test.net.as_development.asdk.tools.reflection.testdata.TestClassA;
import test.net.as_development.asdk.tools.reflection.testdata.TestClassB;
import test.net.as_development.asdk.tools.reflection.testdata.TestFieldAnnotation;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

//==============================================================================
/** unit tests for class AnnotationSearch.
 */
public class AnnotationSearchTest
{
    //--------------------------------------------------------------------------
    private static final boolean INCLUDE_SUPER_CLASSES = true;
    
    //--------------------------------------------------------------------------
    private static final boolean DONT_INCLUDE_SUPER_CLASSES = false;

    //--------------------------------------------------------------------------
    public AnnotationSearchTest()
    {
    }

    //--------------------------------------------------------------------------
    @BeforeClass
    public static void setUpClass()
        throws Exception
    {
    }

    //--------------------------------------------------------------------------
    @AfterClass
    public static void tearDownClass()
        throws Exception
    {
    }

    //--------------------------------------------------------------------------
    @Before
    public void setUp()
    {
    }

    //--------------------------------------------------------------------------
    @After
    public void tearDown()
    {
    }

    //--------------------------------------------------------------------------
    /**
     * Test of findAnnotatedFields method, of class AnnotationSearch.
     */
    @Test
    public void testFindAnnotatedFields()
        throws Exception
    {
        List< Field > lResults = new ArrayList< Field >(0);
        Field         aFieldA2 = TestClassA.class.getDeclaredField(TestClassA.NAME_OF_FIELD_A2);
        Field         aFieldB2 = TestClassB.class.getDeclaredField(TestClassB.NAME_OF_FIELD_B2);

        lResults.clear();
        AnnotationSearch.findAnnotatedFields(TestClassA.class, TestFieldAnnotation.class, DONT_INCLUDE_SUPER_CLASSES, lResults);
        Assert.assertEquals("[testFindAnnotatedFields_[01] : class A; non recursive search; count check"        , 1, lResults.size());
        Assert.assertTrue  ("[testFindAnnotatedFields_[02] : class A; non recursive search; field check for A2.", lResults.contains(aFieldA2));

        lResults.clear();
        AnnotationSearch.findAnnotatedFields(TestClassA.class, TestFieldAnnotation.class, INCLUDE_SUPER_CLASSES, lResults);
        Assert.assertEquals("[testFindAnnotatedFields_[03] : class A; recursive search; count check"        , 1, lResults.size());
        Assert.assertTrue  ("[testFindAnnotatedFields_[04] : class A; recursive search; field check for A2.", lResults.contains(aFieldA2));

        lResults.clear();
        AnnotationSearch.findAnnotatedFields(TestClassB.class, TestFieldAnnotation.class, DONT_INCLUDE_SUPER_CLASSES, lResults);
        Assert.assertEquals("[testFindAnnotatedFields_[05] : class B; non recursive search; count check"       , 1, lResults.size());
        Assert.assertTrue  ("[testFindAnnotatedFields_[06] : class B; non recursive search; field check for B2", lResults.contains(aFieldB2));

        lResults.clear();
        AnnotationSearch.findAnnotatedFields(TestClassB.class, TestFieldAnnotation.class, INCLUDE_SUPER_CLASSES, lResults);
        Assert.assertEquals("[testFindAnnotatedFields_[07] : class B; recursive search; count check"       , 2, lResults.size());
        Assert.assertTrue  ("[testFindAnnotatedFields_[08] : class B; recursive search; field check for A2", lResults.contains(aFieldA2));
        Assert.assertTrue  ("[testFindAnnotatedFields_[09] : class B; recursive search; field check for B2", lResults.contains(aFieldB2));
    }
}