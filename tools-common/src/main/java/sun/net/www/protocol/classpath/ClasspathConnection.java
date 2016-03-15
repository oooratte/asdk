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
package sun.net.www.protocol.classpath;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

//=============================================================================
/** provide read access to classpath:/ URL content ...
 */
public class ClasspathConnection extends URLConnection
{
	//-------------------------------------------------------------------------
	public static final String PROTOCOL = "classpath";
	
	//-------------------------------------------------------------------------
    public ClasspathConnection(final URL aURL)
    	throws Exception
    {
        super(aURL);
    }

	//-------------------------------------------------------------------------
    @Override
    public void connect() 
    	throws IOException
    {
    	throw new UnsupportedOperationException("not supported");
    }
    
	//-------------------------------------------------------------------------
    @Override
    public Object getContent() 
    	throws IOException
    {
    	throw new UnsupportedOperationException("not supported");
    }

	//-------------------------------------------------------------------------
    @Override
    public InputStream getInputStream() 
    	throws IOException
    {
    	try
    	{
	    	final URL aURL = getURL();
	    	ClasspathConnection.validateURL(aURL);

			final String sURL = aURL.toURI().toString();
			      String sRes = StringUtils.removeStartIgnoreCase(sURL, PROTOCOL);
			             sRes = StringUtils.removeStartIgnoreCase(sRes, ":");

            final InputStream aStream = ClasspathConnection.class.getResourceAsStream(sRes);
			Validate.notNull(aStream, "Could not load resource '"+sRes+"' from classpath.");
			return aStream;
		}
    	catch (final Throwable ex)
    	{
    		throw new IOException (ex);
		}
    }

    //-------------------------------------------------------------------------
    public static void validateURL (final URL aURL)
        throws Exception
    {
    	Validate.isTrue(StringUtils.startsWithIgnoreCase(aURL.getProtocol(), PROTOCOL), "Invalid protocol '%s' for this handler.", aURL.getProtocol());
    }
}
