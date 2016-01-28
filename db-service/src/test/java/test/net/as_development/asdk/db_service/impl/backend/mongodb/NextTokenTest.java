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
