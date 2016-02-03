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
