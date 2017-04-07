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

import org.apache.commons.lang3.StringUtils;

//=============================================================================
public enum ELogLevel
{
    //-------------------------------------------------------------------------
    E_FATAL,
    E_ERROR,
    E_WARNING,
    E_INFO,
    E_DEBUG,
    E_TRACE;

    //-------------------------------------------------------------------------
    public static final int INT_LEVEL_FATAL   = 0;
    public static final int INT_LEVEL_ERROR   = 1;
    public static final int INT_LEVEL_WARNING = 2;
    public static final int INT_LEVEL_INFO    = 3;
    public static final int INT_LEVEL_DEBUG   = 4;
    public static final int INT_LEVEL_TRACE   = 5;

    //-------------------------------------------------------------------------
    public static final String STR_LEVEL_FATAL   = "fatal"  ;
    public static final String STR_LEVEL_ERROR   = "error"  ;
    public static final String STR_LEVEL_WARNING = "warning";
    public static final String STR_LEVEL_INFO    = "info"   ;
    public static final String STR_LEVEL_DEBUG   = "debug"  ;
    public static final String STR_LEVEL_TRACE   = "trace"  ;

    //-------------------------------------------------------------------------
    public static int toInt (final ELogLevel eLevel)
    {
    	if (eLevel == ELogLevel.E_FATAL)
    		return INT_LEVEL_FATAL;
    	else
    	if (eLevel == ELogLevel.E_ERROR)
    		return INT_LEVEL_ERROR;
    	else
    	if (eLevel == ELogLevel.E_WARNING)
    		return INT_LEVEL_WARNING;
    	else
    	if (eLevel == ELogLevel.E_INFO)
    		return INT_LEVEL_INFO;
    	else
    	if (eLevel == ELogLevel.E_DEBUG)
    		return INT_LEVEL_DEBUG;
    	else
    	if (eLevel == ELogLevel.E_TRACE)
    		return INT_LEVEL_TRACE;
    	else
    		throw new UnsupportedOperationException ("No support for log level '"+eLevel+"' implemented yet.");
    }

    //-------------------------------------------------------------------------
    public static ELogLevel fromString (final String    sString ,
    		                            final ELogLevel eDefault)
    {
    	if (StringUtils.isEmpty(sString))
    		return eDefault;
    	
    	if (StringUtils.equalsIgnoreCase(sString, STR_LEVEL_FATAL))
    		return ELogLevel.E_FATAL;
    	else
    	if (StringUtils.equalsIgnoreCase(sString, STR_LEVEL_ERROR))
    		return ELogLevel.E_ERROR;
    	else
    	if (StringUtils.equalsIgnoreCase(sString, STR_LEVEL_WARNING))
    		return ELogLevel.E_WARNING;
    	else
    	if (StringUtils.equalsIgnoreCase(sString, STR_LEVEL_INFO))
    		return ELogLevel.E_INFO;
    	else
    	if (StringUtils.equalsIgnoreCase(sString, STR_LEVEL_DEBUG))
    		return ELogLevel.E_DEBUG;
    	else
    	if (StringUtils.equalsIgnoreCase(sString, STR_LEVEL_TRACE))
    		return ELogLevel.E_TRACE;
    	else
    		throw new UnsupportedOperationException ("No support for log level '"+sString+"' implemented yet.");
    }

    //-------------------------------------------------------------------------
    public static String toString (final ELogLevel eLevel)
    {
    	if (eLevel == ELogLevel.E_FATAL)
    		return STR_LEVEL_FATAL;
    	else
    	if (eLevel == ELogLevel.E_ERROR)
    		return STR_LEVEL_ERROR;
    	else
    	if (eLevel == ELogLevel.E_WARNING)
    		return STR_LEVEL_WARNING;
    	else
    	if (eLevel == ELogLevel.E_INFO)
    		return STR_LEVEL_INFO;
    	else
    	if (eLevel == ELogLevel.E_DEBUG)
    		return STR_LEVEL_DEBUG;
    	else
    	if (eLevel == ELogLevel.E_TRACE)
    		return STR_LEVEL_TRACE;
    	else
    		throw new UnsupportedOperationException ("No support for log level '"+eLevel+"' implemented yet.");
    }

    //-------------------------------------------------------------------------
    public static boolean isActive (final ELogLevel eWorkLevel ,
    								final ELogLevel eCheckLevel)
    {
    	final int     nWorkLevel  = ELogLevel.toInt(eWorkLevel );
    	final int     nCheckLevel = ELogLevel.toInt(eCheckLevel);
    	final boolean bActive     = nCheckLevel <= nWorkLevel;
    	return bActive;
    }

    //-------------------------------------------------------------------------
    public boolean greaterThan (ELogLevel eCheck)
    {
    	final int     nThisLevel   = ELogLevel.toInt (this  );
    	final int     nCheckLevel  = ELogLevel.toInt (eCheck);
    	final boolean bGreaterThan = (nCheckLevel < nThisLevel);
    	return bGreaterThan;
    }
    
    //-------------------------------------------------------------------------
    public boolean lessThan (ELogLevel eCheck)
    {
    	final int     nThisLevel  = ELogLevel.toInt (this  );
    	final int     nCheckLevel = ELogLevel.toInt (eCheck);
    	final boolean bLessThan   = (nCheckLevel > nThisLevel);
    	return bLessThan;
    }
}
