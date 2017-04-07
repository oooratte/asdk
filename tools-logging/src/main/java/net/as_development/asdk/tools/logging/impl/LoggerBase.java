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

import net.as_development.asdk.tools.logging.ELogLevel;

//=============================================================================
public abstract class LoggerBase implements Logger
{
    //-------------------------------------------------------------------------
    public LoggerBase ()
    {}

    //-------------------------------------------------------------------------
    @Override
    public LogContext withMessage (final String sMessage)
    {
    	final LogContext aRootContext = LogContext.create(this);
    	aRootContext.withMessage(sMessage);
    	return aRootContext;
    }

    //-------------------------------------------------------------------------
    @Override
    public LogContext inCategory (final String... lCategories)
    {
    	final LogContext aRootContext = LogContext.create(this);
    	aRootContext.inCategory(lCategories);
    	return aRootContext;
    }

    //-------------------------------------------------------------------------
    @Override
    public LogContext forLevel(final ELogLevel eLevel)
    {
    	final LogContext aRootContext  = LogContext.create(this);
    	final LogContext aLevelContext = aRootContext.forLevel(eLevel);
    	return aLevelContext;
    }
    
    //-------------------------------------------------------------------------
    protected abstract void log (final ELogLevel eLevel    ,
    					         final String    sLog      ,
    					         final Throwable aException);
}
