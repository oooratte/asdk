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
package net.as_development.asdk.tools.logging;

import net.as_development.asdk.tools.logging.impl.Logger;
import net.as_development.asdk.tools.logging.impl.Slf4JLogger;

//=============================================================================
public class LoggerFactory
{
    //-------------------------------------------------------------------------
    public LoggerFactory ()
    {}
    
    //-------------------------------------------------------------------------
    public static synchronized void setLogFramework (final ELogFramework eFwk)
        throws Exception
    {
    	m_eLogFramework = eFwk;
    }
    
    //-------------------------------------------------------------------------
    public static synchronized Logger newLogger (final Class< ? > aClass)
    	/* no throws Exception */
    {
    	final String sClass  = aClass.getName();
    	final Logger aLogger = newLogger (sClass);
    	return aLogger;
    }

    //-------------------------------------------------------------------------
    public static synchronized Logger newLogger (final String sClass)
    	/* no throws Exception */
    {
    	Logger aLogger = null;
    	
    	if (m_eLogFramework == ELogFramework.E_SLF4J)
    		aLogger = Slf4JLogger.create(sClass);
    	else
    		throw new UnsupportedOperationException ("Support for logging framework "+m_eLogFramework+" not implemented yet.");

    	return aLogger;
    }

    //-------------------------------------------------------------------------
    private static ELogFramework m_eLogFramework = ELogFramework.E_SLF4J;
}