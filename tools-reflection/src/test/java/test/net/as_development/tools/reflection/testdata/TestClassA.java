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
package test.net.as_development.tools.reflection.testdata;


import org.junit.Ignore;

//==============================================================================
/** Internal test class which provide some test fields ...
 *  some of them annotated ... some not.
 */
@Ignore // not an unit test .-)
public class TestClassA
{
	//-------------------------------------------------------------------------
    /// Name of the field A1 (must correspond to real field name used within this test class !)
    public static final String NAME_OF_FIELD_A1 = "m_nFieldA1";

	//-------------------------------------------------------------------------
    /// test field not bound to any annotation !
    @SuppressWarnings("unused")
	private int m_nFieldA1;

	//-------------------------------------------------------------------------
    // used by unit test ObjectManipulationTest
    public void setFieldA1 (int nValue)
    {
    	m_nFieldA1 = nValue;
    }
    
	//-------------------------------------------------------------------------
    // used by unit test ObjectManipulationTest
    public int getFieldA1 ()
    {
    	return m_nFieldA1;
    }
    
	//-------------------------------------------------------------------------
    /// Name of the field A2 (must correspond to real field name used within this test class !)
    public static final String NAME_OF_FIELD_A2 = "m_nFieldA2";

	//-------------------------------------------------------------------------
    /// test field bound to our test annotation ... so it must be found
    @SuppressWarnings("unused")
    @TestFieldAnnotation
    private int m_nFieldA2;
}