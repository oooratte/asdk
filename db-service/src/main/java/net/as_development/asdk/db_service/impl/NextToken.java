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
package net.as_development.asdk.db_service.impl;

import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;

//==============================================================================
/** Handle all around paging and next token (if such token can be implemented
 *  using offset and page size values).
 * 
 *  Queries can have to much results. At least paging can help. But then
 *  following queries has to use offset and limit ... and has to know where
 *  last result page was finished and new one has to begin.
 *  
 *  The BD layer knows the paradigm of the next-token serialized as a string.
 *  It's up to the code to know which information are coded into such next token ...
 *  the code using the DB layer transport such token to the client and back.
 *  
 *  How an instance of this class has to be used:
 *  
 *  <pre><code>{@code
 *  
 *   public String queryRows(Row             aMeta     ,
 *                           String          sNextToken,
 *                           List< Row >     lResults  ,
 *                           IDBBackendQuery iQuery    )
 *       throws Exception
 *   {
 *       ....
 *
 *       // parse the given token and retrieve its information
 *       // as e.g. offset and page size
 *       NextToken4Offset aNextToken = new NextToken4Offset   (sNextToken);
 *       int              nOffset    = aNextToken.getOffset   ();
 *       int              nPageSize  = aNextToken.getPageSize ();
 *       
 *       // use such informations to start new query.
 *       Results aResult = impl_doQuery (..., nOffset, nPageSize);
 *       
 *       // 'max results' must count ALL possible results of a query
 *       // independent how many items was used from that results set ! 
 *       int nMaxResults = aResult.getCount ();
 *
 *       // pass max count of results to the token instance and
 *       // let it calculate the next possible token ...
 *       // The return value of those call will be the next sNextToken
 *       // passed to this query method then.
 *       // If method return an empty string no further results exists and
 *       // no further query makes sense.
 *       return aNextToken.stepNext (nMaxResults);
 *   }
 *  
 *  }</code></pre>
 */
public class NextToken
{
    //--------------------------------------------------------------------------
    public static final int DEFAULT_OFFSET = 0;
    
    //--------------------------------------------------------------------------
    public static       int DEFAULT_PAGESIZE = 100;
    
    //--------------------------------------------------------------------------
    public static final String PROP_OFFSET   = "offset";
    
    //--------------------------------------------------------------------------
    public static final String PROP_PAGESIZE = "pagesize";
    
    //--------------------------------------------------------------------------
    public static final String SEPARATOR = ";";
    
    //--------------------------------------------------------------------------
    /// an "empty" token is defined as "no further data exists"
    public static final String NO_NEXT_TOKEN = "";
            
    //--------------------------------------------------------------------------
    public NextToken ()
    {}
    
    //--------------------------------------------------------------------------
    public NextToken (String sToken)
        throws Exception
    {
        parse (sToken);
    }
    
    //--------------------------------------------------------------------------
    /** parse the given token and fill this instance.
     * 
     *  @param  sToken [IN]
     *          the last token to be parsed here.
     *          Can be empty or null.
     */
    public void parse (String sToken)
        throws Exception
    {
        // Even an empty token will be a valid token and has
        // to reset our member .-)
        impl_reset ();
        
        // don't parse empty token real.
        // Member was already reset.
        if (StringUtils.isEmpty(sToken))
            return;
        
        StringTokenizer aParser = new StringTokenizer (sToken, NextToken.SEPARATOR);
        while (aParser.hasMoreTokens())
        {
            String     sTokenPart = aParser.nextToken();
            String     sValue     = null;
            int        nValue     = 0;
            
            if (StringUtils.startsWithIgnoreCase(sTokenPart, PROP_OFFSET))
            {
                sValue = sTokenPart.substring(PROP_OFFSET.length()+1);
                nValue = Integer.parseInt(sValue);
                setOffset (nValue);
            }
            else
            if (StringUtils.startsWithIgnoreCase(sTokenPart, PROP_PAGESIZE))
            {
                sValue = sTokenPart.substring(PROP_PAGESIZE.length()+1);
                nValue = Integer.parseInt(sValue);
                setPageSize (nValue);
            }
        }
    }
    
    //--------------------------------------------------------------------------
    public String stepNext (int nMaxResults)
        throws Exception
    {
        int    nNewOffset = m_nOffset + m_nPageSize;
        String sNext      = NextToken.NO_NEXT_TOKEN;
        
        if (nNewOffset < nMaxResults)
        {
            setOffset(nNewOffset);
            sNext = asString ();
        }
        
        return sNext;
    }
    
    //--------------------------------------------------------------------------
    public String stepNext ()
        throws Exception
    {
        int nNewOffset = m_nOffset + m_nPageSize;
        setOffset(nNewOffset);
        return asString ();
    }
    
    //--------------------------------------------------------------------------
    public String finish ()
        throws Exception
    {
        return NextToken.NO_NEXT_TOKEN;
    }
    
    //--------------------------------------------------------------------------
    public String asString ()
        throws Exception
    {
        StringBuffer sNext = new StringBuffer (256);

        sNext.append (NextToken.PROP_OFFSET  );
        sNext.append ("="                           ); // don't add spaces here to make parsing easy
        sNext.append (m_nOffset                     );
        
        sNext.append (NextToken.SEPARATOR    );
        
        sNext.append (NextToken.PROP_PAGESIZE);
        sNext.append ("="                           ); // don't add spaces here to make parsing easy
        sNext.append (m_nPageSize                   );
        
        return sNext.toString ();
    }
    
    //--------------------------------------------------------------------------
    /** @return the offset from the last parsed token.
     *  Has to return 0 as default if last token was empty.
     */
    public int getOffset ()
        throws Exception
    {
        return m_nOffset;
    }
    
    //--------------------------------------------------------------------------
    /** set the new offset for the next token to be generated by using this
     *  instance.
     *  
     *  @param  nOffset [IN]
     *          the new offset.
     *          Has to be &gt;= 0.
     */
    public void setOffset (int nOffset)
        throws Exception
    {
        if (nOffset < 0)
            throw new IllegalArgumentException ("New offset for next token isnt valid.");
        
        m_nOffset = nOffset;
    }
    
    //--------------------------------------------------------------------------
    /** @return the page size from the last parsed token.
     *  Has to return a default $gt; 0 if last token was empty.
     */
    public int getPageSize ()
        throws Exception
    {
        return m_nPageSize;
    }
    
    //--------------------------------------------------------------------------
    /** set the new page size for the next token to be generated by using this
     *  instance.
     *  
     *  @param  nSize [IN]
     *          the new page size.
     *          Has to be $gt;= 1.
     */
    public void setPageSize (int nSize)
        throws Exception
    {
        if (nSize < 1)
            throw new IllegalArgumentException ("New page size for next token isnt valid.");
        
        m_nPageSize = nSize;
    }
    
    //--------------------------------------------------------------------------
    private void impl_reset ()
        throws Exception
    {
        m_nOffset         = NextToken.DEFAULT_OFFSET  ;
        m_nPageSize       = NextToken.DEFAULT_PAGESIZE;
        //m_nMaxResultCount = 0;
    }
    
    //--------------------------------------------------------------------------
    private int m_nOffset = NextToken.DEFAULT_OFFSET;
    
    //--------------------------------------------------------------------------
    private int m_nPageSize = NextToken.DEFAULT_PAGESIZE;
    
    //--------------------------------------------------------------------------
    //private int m_nMaxResultCount = 0;
}
