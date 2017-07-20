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
package net.as_development.asdk.tools.logging.impl;

import org.slf4j.spi.LocationAwareLogger;

import net.as_development.asdk.tools.logging.ELogLevel;

//=============================================================================
public class Slf4JLogger extends LoggerBase
{
    //-------------------------------------------------------------------------
    private Slf4JLogger ()
    {}
    
    //-------------------------------------------------------------------------
    public static Slf4JLogger create (final Class< ? > aOwner)
    {
    		final String      sOwner = aOwner.getName();
    		final Slf4JLogger aLog   = create (sOwner);
	    	return aLog;
    }

    //-------------------------------------------------------------------------
    public static Slf4JLogger create (final String sOwner)
    {
	    	final Slf4JLogger aLog         = new Slf4JLogger ();
	    	                  aLog.m_aLog  = (org.slf4j.spi.LocationAwareLogger) org.slf4j.LoggerFactory.getLogger(sOwner);
	    	                  aLog.m_sFQCN = LogContext.class.getName();
	    	return aLog;
    }
    
    //-------------------------------------------------------------------------
    @Override
    public synchronized boolean isActive (final ELogLevel eLevel)
    {
	    	if (eLevel == ELogLevel.E_TRACE)
	    		return m_aLog.isTraceEnabled();
	    	if (eLevel == ELogLevel.E_DEBUG)
	    		return m_aLog.isDebugEnabled();
	    	if (eLevel == ELogLevel.E_INFO)
	    		return m_aLog.isInfoEnabled();
	    	if (eLevel == ELogLevel.E_WARNING)
	    		return m_aLog.isWarnEnabled();
	    	if (eLevel == ELogLevel.E_ERROR)
	    		return m_aLog.isErrorEnabled();
	    	if (eLevel == ELogLevel.E_FATAL)
	    		return m_aLog.isErrorEnabled();
	    	return false;
    }
    
    //-------------------------------------------------------------------------
    @Override
    protected synchronized void log (final ELogLevel eLevel    ,
	    								 final String    sLog      ,
	    								 final Throwable aException)
    {
	    	if (aException == null)
	    		impl_log (eLevel, sLog);
	    	else
	    		impl_log (eLevel, sLog, aException);
    }
    
    //-------------------------------------------------------------------------
    private void impl_log (final ELogLevel eLevel,
    						   final String    sLog  )
    {
	    	final int nLevel = impl_mapLogLevel (eLevel);
	    	m_aLog.log(null, m_sFQCN, nLevel, sLog, null, null);
    }
    
    //-------------------------------------------------------------------------
    private void impl_log (final ELogLevel eLevel    ,
	    					   final String    sLog      ,
	    					   final Throwable aException)
    {
	    	final int nLevel = impl_mapLogLevel (eLevel);
	    	m_aLog.log(null, m_sFQCN, nLevel, sLog, null, aException);
    }
    
    //-------------------------------------------------------------------------
    private static int impl_mapLogLevel (final ELogLevel eLevel)
    {
	    	if (eLevel == ELogLevel.E_FATAL)
	    		return LocationAwareLogger.ERROR_INT;
	    	else
	    	if (eLevel == ELogLevel.E_ERROR)
	    		return LocationAwareLogger.ERROR_INT;
	    	else
	    	if (eLevel == ELogLevel.E_WARNING)
	    		return LocationAwareLogger.WARN_INT;
	    	else
	    	if (eLevel == ELogLevel.E_INFO)
	    		return LocationAwareLogger.INFO_INT;
	    	else
	    	if (eLevel == ELogLevel.E_DEBUG)
	    		return LocationAwareLogger.DEBUG_INT;
	    	else
	    	if (eLevel == ELogLevel.E_TRACE)
	    		return LocationAwareLogger.TRACE_INT;
	    	else
	    		throw new UnsupportedOperationException ("No support for log level '"+eLevel+"'.");
    }
    
    //-------------------------------------------------------------------------
    private org.slf4j.spi.LocationAwareLogger m_aLog = null;

    //-------------------------------------------------------------------------
    private String m_sFQCN = null;
}
