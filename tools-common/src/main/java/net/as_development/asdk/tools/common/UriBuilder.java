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

import org.apache.commons.lang3.StringUtils;

//=============================================================================
public class UriBuilder
{
    //-------------------------------------------------------------------------
    private UriBuilder()
    	throws Exception
    {}

    //-------------------------------------------------------------------------
    public static UriBuilder newUri()
    	throws Exception
    {
    	return new UriBuilder ();
    }

    //-------------------------------------------------------------------------
    public UriBuilder scheme(final String sScheme)
    	throws Exception
    {
    	m_sScheme = sScheme;
    	return this;
    }

    //-------------------------------------------------------------------------
    public UriBuilder user(final String sUser)
    	throws Exception
    {
        m_sUser = sUser;
    	return this;
    }
    
    //-------------------------------------------------------------------------
    public UriBuilder password(final String sPassword)
    	throws Exception
    {
        m_sPassword = sPassword;
    	return this;
    }
    
    //-------------------------------------------------------------------------
    public UriBuilder host(final String sHost)
    	throws Exception
    {
        m_sHost = sHost;
    	return this;
    }

    //-------------------------------------------------------------------------
    public UriBuilder port(final Integer nPort)
    	throws Exception
    {
        m_nPort = nPort;
    	return this;
    }

    //-------------------------------------------------------------------------
    public UriBuilder path(final String sPath)
    	throws Exception
    {
        m_sPath.append("/"  );
        m_sPath.append(sPath);
        return this;
    }

    //-------------------------------------------------------------------------
    public UriBuilder queryParameter(final String sQuery,
    						         final String sValue)
    	throws Exception
    {
        if (m_sQueries.length() > 0)
        	m_sQueries.append("&");

        m_sQueries.append(sQuery);
        m_sQueries.append("="   );
        m_sQueries.append(sValue);
        
        return this;
    }

    //-------------------------------------------------------------------------
    public UriBuilder queryParameterOpt(final boolean bAdd  ,
    									final String  sQuery,
    						            final String  sValue)
    	throws Exception
    {
    	if ( ! bAdd)
    		return this;
    	
        if (m_sQueries.length() > 0)
        	m_sQueries.append("&");

        m_sQueries.append(sQuery);
        m_sQueries.append("="   );
        m_sQueries.append(sValue);
        
        return this;
    }

    //-------------------------------------------------------------------------
    public URI toUri ()
    	throws Exception
    {
    	final String sUri = impl_toUriString ();
    	final URI    aUri = new URI (sUri);
    	return aUri;
    }

    //-------------------------------------------------------------------------
    private String impl_toUriString ()
    	throws Exception
    {
    	String  sPath      = null ;
    	String  sQuery     = null ;
    	String  sFragment  = null ;
        boolean bAuthority = false;
    	
    	if (m_sPath.length() > 0)
    		sPath = m_sPath.toString();

    	if (m_sQueries.length() > 0)
    		sQuery = m_sQueries.toString();

    	if (m_sFragments.length() > 0)
    		sFragment = m_sFragments.toString();
    	
    	final StringBuilder sUri = new StringBuilder (256);
    	sUri.append(m_sScheme);
    	sUri.append("://"    );

    	if ( ! StringUtils.isEmpty(m_sUser))
    	{
    		sUri.append(m_sUser);
    		bAuthority = true;
    	}

    	if ( ! StringUtils.isEmpty(m_sPassword))
    	{
    		if (bAuthority)
    			sUri.append(":");
    		sUri.append(m_sPassword);
    		bAuthority = true;
    	}

		if (bAuthority)
			sUri.append("@");

		if ( ! StringUtils.isEmpty(m_sHost))
    		sUri.append(m_sHost  );

    	if (m_nPort != null)
    	{
    		sUri.append(":"    );
    		sUri.append(m_nPort);
    	}
    	
    	if ( ! StringUtils.isEmpty(sPath))
    		sUri.append(sPath);
    	
    	if ( ! StringUtils.isEmpty(sQuery))
    	{
    		sUri.append("?"   );
    		sUri.append(sQuery);
    	}

    	if ( ! StringUtils.isEmpty(sFragment))
    	{
    		sUri.append("#"      );
    		sUri.append(sFragment);
    	}

    	return sUri.toString ();
    }

    //-------------------------------------------------------------------------
    private StringBuilder m_sPath = new StringBuilder (256);

    //-------------------------------------------------------------------------
    private StringBuilder m_sQueries = new StringBuilder (256);

    //-------------------------------------------------------------------------
    private StringBuilder m_sFragments = new StringBuilder (256);

    //-------------------------------------------------------------------------
    private String m_sScheme = null;

    //-------------------------------------------------------------------------
    private String m_sUser = null;

    //-------------------------------------------------------------------------
    private String m_sPassword = null;

    //-------------------------------------------------------------------------
    private String m_sHost = null;

    //-------------------------------------------------------------------------
    private Integer m_nPort = null;
}