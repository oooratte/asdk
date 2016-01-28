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
package test.net.as_development.asdk.db_service.impl;

import org.junit.Test;

import net.as_development.asdk.db_service.impl.AttributeListMetaInfo;
import net.as_development.asdk.db_service.impl.AttributeMetaInfo;
import net.as_development.asdk.tools.test.AssertEx;

//==============================================================================
public class AttributeListMetaInfoTest
{
    //--------------------------------------------------------------------------
	/** check if adding the same attribute second time throws right exception.
	 */
	@Test
	public void testDuplicateAttributes ()
		throws Exception
	{
		String                sAttribute = "test_attribute";
		AttributeListMetaInfo aTesti     = new AttributeListMetaInfo ();

		// first add must work without exception
		aTesti.put(sAttribute, new AttributeMetaInfo ());
		// second add must throw exception
		AssertEx.assertThrowsException("testDuplicateAttributes [01] miss exception on adding duplicate attribute.", IllegalArgumentException.class, aTesti, "put", sAttribute, new AttributeMetaInfo ());
	}
}
