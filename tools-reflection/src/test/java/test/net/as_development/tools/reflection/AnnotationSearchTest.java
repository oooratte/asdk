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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import net.as_development.tools.reflection.AnnotationSearch;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import test.net.as_development.tools.reflection.testdata.TestClassA;
import test.net.as_development.tools.reflection.testdata.TestClassB;
import test.net.as_development.tools.reflection.testdata.TestFieldAnnotation;

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