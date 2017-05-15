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
package net.as_development.asdk.tools.common;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

//=============================================================================
public class UriParser
{
    //-------------------------------------------------------------------------
    private UriParser()
    	throws Exception
    {}

    //-------------------------------------------------------------------------
    public static UriParser parse(final String sUri)
    	throws Exception
    {
    	final UriParser aParser = new UriParser ();
    	aParser.m_sUri = sUri;
    	return aParser;
    }

    //-------------------------------------------------------------------------
    public String getScheme ()
    	throws Exception
    {
    	return mem_Uri ().getScheme();
    }
    
    //-------------------------------------------------------------------------
    public String getUser ()
    	throws Exception
    {
    	return mem_Uri ().getUserInfo();
    }

    //-------------------------------------------------------------------------
    public String getPassword ()
    	throws Exception
    {
    	throw new UnsupportedOperationException ("please implement");
    }

    //-------------------------------------------------------------------------
    public String getHost ()
    	throws Exception
    {
    	return mem_Uri ().getHost();
    }

    //-------------------------------------------------------------------------
    public Integer getPort ()
    	throws Exception
    {
    	return mem_Uri ().getPort();
    }

    //-------------------------------------------------------------------------
    public String getPath ()
    	throws Exception
    {
    	return mem_Uri ().getPath();
    }

    //-------------------------------------------------------------------------
    public String getQuery ()
    	throws Exception
    {
    	return mem_Uri ().getQuery();
    }

    //-------------------------------------------------------------------------
    public String getQueryParameter (final String sParameter)
    	throws Exception
    {
    	return mem_QueryParams ().get(sParameter);
    }

    //-------------------------------------------------------------------------
    private URI mem_Uri ()
        throws Exception
    {
    	if (m_aUri == null)
    		m_aUri = new URI (m_sUri);
    	return m_aUri;
    }

    //-------------------------------------------------------------------------
    private void impl_parseQuery (final String                sQuery ,
    							  final Map< String, String > lParams)
        throws Exception
    {
    	final String[] lQueryParams = StringUtils.splitByWholeSeparator(sQuery, "&");

    	if (lQueryParams == null)
    		return;

    	for (final String sQueryParam : lQueryParams)
    	{
    		final String[] lParamParts = StringUtils.splitByWholeSeparator(sQueryParam, "=");
    		final String   sKey        = lParamParts [0];
    		final String   sValue      = lParamParts [1];
    		lParams.put(sKey, sValue);
    	}
    }
    
    //-------------------------------------------------------------------------
    private Map< String, String > mem_QueryParams ()
        throws Exception
    {
    	if (m_lQueryParams == null)
    	{
    		final Map< String, String > lParams = new HashMap< String, String > ();
    		impl_parseQuery (getQuery (), lParams);
    		m_lQueryParams = lParams;
    	}
    	return m_lQueryParams;
    }

    //-------------------------------------------------------------------------
    private String m_sUri = null;
    
    //-------------------------------------------------------------------------
    private URI m_aUri = null;

    //-------------------------------------------------------------------------
    private Map< String, String > m_lQueryParams = null;
}