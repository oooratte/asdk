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

import net.as_development.tools.reflection.ObjectManipulation;

import org.junit.Assert;
import org.junit.Test;

import test.net.as_development.tools.reflection.testdata.TestClassA;


//==============================================================================
public class ObjectManipulationTest
{
    //--------------------------------------------------------------------------
	@Test
	public void testSetFieldValue()
		throws Exception
	{
		TestClassA aTestObject = new TestClassA();
		int        nTestValue  = 1896;
		
		Assert.assertTrue("testSetFieldValue [01] check if default value is different", aTestObject.getFieldA1() != nTestValue);
		
		ObjectManipulation.setFieldValue(aTestObject, TestClassA.NAME_OF_FIELD_A1, nTestValue);
		Assert.assertTrue("testSetFieldValue [02] check if value was set real", aTestObject.getFieldA1() == nTestValue);
	}

    //--------------------------------------------------------------------------
	@Test
	public void testGetFieldValue()
		throws Exception
	{
		TestClassA aTestObject = new TestClassA();
		int        nTestValue  = 1896;
		
		aTestObject.setFieldA1(nTestValue);
		Integer nFoundValue = ObjectManipulation.getFieldValue(aTestObject, TestClassA.NAME_OF_FIELD_A1);
		Assert.assertTrue("testGetFieldValue [01] check if get return right value", nFoundValue == nTestValue);
	}
}
