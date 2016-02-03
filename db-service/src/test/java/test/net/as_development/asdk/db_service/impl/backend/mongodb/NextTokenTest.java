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
package test.net.as_development.asdk.db_service.impl.backend.mongodb;

import org.junit.Assert;
import org.junit.Test;

import net.as_development.asdk.db_service.impl.NextToken;

//==============================================================================
public class NextTokenTest
{
    //--------------------------------------------------------------------------
    @Test
    public void testDefaults()
        throws Exception
    {
        NextToken aToken = new NextToken ();
        
        Assert.assertEquals ("testDefaults [01] check offset"  , NextToken.DEFAULT_OFFSET  , aToken.getOffset  ());
        Assert.assertEquals ("testDefaults [02] check pagesize", NextToken.DEFAULT_PAGESIZE, aToken.getPageSize());
    }
    
    //--------------------------------------------------------------------------
    @Test
    public void testParsingEmptyToken()
        throws Exception
    {
        NextToken aToken = new NextToken ();
        
        aToken.parse(null);
        Assert.assertEquals ("testParsingEmptyToken [01] parsing an null token (check offset)"  , NextToken.DEFAULT_OFFSET  , aToken.getOffset  ());
        Assert.assertEquals ("testParsingEmptyToken [02] parsing an null token (check pagesize)", NextToken.DEFAULT_PAGESIZE, aToken.getPageSize());
        
        aToken.parse("");
        Assert.assertEquals ("testParsingEmptyToken [03] parsing an empty token (check offset)"  , NextToken.DEFAULT_OFFSET  , aToken.getOffset  ());
        Assert.assertEquals ("testParsingEmptyToken [04] parsing an empty token (check pagesize)", NextToken.DEFAULT_PAGESIZE, aToken.getPageSize());
    }
    
    //--------------------------------------------------------------------------
    @Test
    public void testParsingValidToken()
        throws Exception
    {
        int nOffset   = 0;
        int nPageSize = 0;
        
        NextToken aToken    = new NextToken ();
        
        // parse "offset=...;pagesize=..."
        nOffset   = 197;
        nPageSize = 1584;
        
        aToken.parse(NextToken.PROP_OFFSET+"="+nOffset+NextToken.SEPARATOR+NextToken.PROP_PAGESIZE+"="+nPageSize);
        
        Assert.assertEquals ("testParsingValidToken [01] check offset"  , nOffset  , aToken.getOffset  ());
        Assert.assertEquals ("testParsingValidToken [02] check pagesize", nPageSize, aToken.getPageSize());
        
        // parse "pagesize=...;offset=..."
        nOffset   = 18;
        nPageSize = 7777;
        
        aToken.parse(NextToken.PROP_PAGESIZE+"="+nPageSize+NextToken.SEPARATOR+NextToken.PROP_OFFSET+"="+nOffset);
        
        Assert.assertEquals ("testParsingValidToken [03] check offset"  , nOffset  , aToken.getOffset  ());
        Assert.assertEquals ("testParsingValidToken [04] check pagesize", nPageSize, aToken.getPageSize());
        
        // parse "offset=..." only !
        nOffset = 796;
        
        aToken.parse(NextToken.PROP_OFFSET+"="+nOffset);
        
        Assert.assertEquals ("testParsingValidToken [05] check offset"  , nOffset                   , aToken.getOffset  ());
        Assert.assertEquals ("testParsingValidToken [06] check pagesize", NextToken.DEFAULT_PAGESIZE, aToken.getPageSize());
        
        // parse "pagesize=..." only !
        nPageSize = 1234;
        
        aToken.parse(NextToken.PROP_PAGESIZE+"="+nPageSize);
        
        Assert.assertEquals ("testParsingValidToken [07] check offset"  , NextToken.DEFAULT_OFFSET, aToken.getOffset  ());
        Assert.assertEquals ("testParsingValidToken [08] check pagesize", nPageSize               , aToken.getPageSize());
    }
    
    //--------------------------------------------------------------------------
    @Test
    public void testAssemblingToken()
        throws Exception
    {
        int nOffset   = 19;
        int nPageSize = 1357;
        
        NextToken aToken = new NextToken ();
        
        aToken.setOffset  (nOffset  );
        aToken.setPageSize(nPageSize);
        
        String sToken         = aToken.asString();
        String sExpectedToken = NextToken.PROP_OFFSET+"="+nOffset+NextToken.SEPARATOR+NextToken.PROP_PAGESIZE+"="+nPageSize;
        
        Assert.assertEquals ("testAssemblingToken [01]", sExpectedToken, sToken);
    }
}
