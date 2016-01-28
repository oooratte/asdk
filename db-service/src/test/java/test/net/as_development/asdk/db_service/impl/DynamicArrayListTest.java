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

import net.as_development.asdk.db_service.impl.DynamicArrayList;
import net.as_development.asdk.tools.test.AssertEx;

//==============================================================================
public class DynamicArrayListTest
{
    //--------------------------------------------------------------------------
    @Test
    public void testCreatingEmptyList()
        throws Exception
    {
        DynamicArrayList< String > aTesti = new DynamicArrayList< String > ();
        AssertEx.assertEquals("testCreatingEmptyList [01] check initial size", 0, aTesti.size());
    }
    
    //--------------------------------------------------------------------------
    @Test
    public void testCreatingListWithSize()
        throws Exception
    {
        int                        nSize  = 4;
        DynamicArrayList< String > aTesti = new DynamicArrayList< String > (nSize);
        AssertEx.assertEquals("testCreatingListWithSize [01] check initial size", nSize, aTesti.size());
        
        for (String sEntry : aTesti)
            AssertEx.assertNull ("testCreatingListWithSize [02] items of those list has to be null be default", sEntry);
    }
    
    //--------------------------------------------------------------------------
    @Test
    public void testResizeOnSet()
        throws Exception
    {
        String                     sItem2 = "item_2";
        String                     sItem5 = "item_5";
        DynamicArrayList< String > aTesti = new DynamicArrayList< String >();
        
        aTesti.set(2, sItem2);
        aTesti.set(5, sItem5);
        
        AssertEx.assertEquals("testResizeOnSet [01]", sItem2, aTesti.get (2));
        AssertEx.assertEquals("testResizeOnSet [02]", sItem5, aTesti.get (5));
        
        AssertEx.assertNull  ("testResizeOnSet [03]", aTesti.get (0));
        AssertEx.assertNull  ("testResizeOnSet [04]", aTesti.get (1));
        AssertEx.assertNull  ("testResizeOnSet [05]", aTesti.get (3));
        AssertEx.assertNull  ("testResizeOnSet [06]", aTesti.get (4));
    }
}
