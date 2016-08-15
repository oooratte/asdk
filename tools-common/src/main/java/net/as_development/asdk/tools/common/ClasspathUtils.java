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

import java.net.URL;
import java.net.URLClassLoader;

import org.apache.commons.lang3.Validate;

//=============================================================================
/** collection of helper functions around class path ...
 */
public class ClasspathUtils
{
	//-------------------------------------------------------------------------
	// static helper only - instances of this class not needed
	private ClasspathUtils ()
	{}

	//-------------------------------------------------------------------------
	/** dump current class path and return it.
	 * 
	 *  Use the system class loader to retrieve the class path.
	 *  If this is not suitable for you - use dump(ClassLoader) instead.
	 * 
	 *  @param	sSeparator [IN]
	 *  		separate each class path entry (can be '\n' for new lines or any
	 *          separator you like)
	 * 
	 *  @return the class path as string
	 */
	public static String dump (final String sSeparator)
	{
		final ClassLoader aClassLoader = ClassLoader.getSystemClassLoader();
		final String      sDump        = dump (aClassLoader, sSeparator);
		return sDump;
	}

	//-------------------------------------------------------------------------
	/** dump current class path and return it.
	 * 
	 *  It use the given class loader to retrieve the class path.
	 * 
	 *  @param	aClassLoader [IN]
	 *  		the class loader where the class path has to be retrieved from.
	 * 
	 *  @param	sSeparator [IN]
	 *  		separate each class path entry (can be '\n' for new lines or any
	 *          separator you like)
	 * 
	 *  @return the class path as string
	 */
	public static String dump (final ClassLoader aClassLoader,
							   final String      sSeparator  )
	{
		Validate.notNull (aClassLoader                                                  , "Invalid argument 'classloader'."                              );
		Validate.notEmpty(sSeparator                                                    , "Invalid argument 'separator'."                                );
		Validate.isTrue  (URLClassLoader.class.isAssignableFrom(aClassLoader.getClass()), "Given class loader is not an URLClassLoader and cant be used.");
		
		final StringBuffer sDump = new StringBuffer (256);
        final URL[]        lURLs = ((URLClassLoader)aClassLoader).getURLs();

        for(final URL aURL: lURLs)
        {
        	final String sURL = aURL.getFile ();
        	sDump.append(sURL      );
        	sDump.append(sSeparator);
        }

        return sDump.toString ();
	}
}
