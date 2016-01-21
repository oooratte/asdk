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
package test.net.as_development.asdk.tools.reflection.testdata;

import org.junit.Ignore;

//==============================================================================
/** Additional to class TestClassA it brings base/super class dependencies
 *  into the game ... .-)
 */
@Ignore // not an unit test .-)
public class TestClassB extends TestClassA
{
	//-------------------------------------------------------------------------
    /// Name of the field B1 (must correspond to real field name used within this test class !)
    public static final String NAME_OF_FIELD_B1 = "m_nFieldB1";

	//-------------------------------------------------------------------------
    /// test field not bound to any annotation !
    @SuppressWarnings("unused")
	private int m_nFieldB1;

	//-------------------------------------------------------------------------
    /// Name of the field B2 (must correspond to real field name used within this test class !)
    public static final String NAME_OF_FIELD_B2 = "m_nFieldB2";

	//-------------------------------------------------------------------------
    /// test field bound to our test annotation ... so it must be found
    @SuppressWarnings("unused")
	@TestFieldAnnotation
    private int m_nFieldB2;
}