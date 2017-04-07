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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;

import net.as_development.asdk.tools.logging.ELogLevel;

//=============================================================================
public class LogContext
{
    //-------------------------------------------------------------------------
    private LogContext ()
    {}

    //-------------------------------------------------------------------------
    /** factory method for new log context objects.
     * 
     *  Protected to be called from inside the logger environment only.
     *  Not thought to be called from outside ...
     * 
     *  @param	aLogger [IN]
     *  		the logger where a new context has to be created for.
     *  
     *  @return the new created log context.
     */
    protected static LogContext create (final LoggerBase aLogger)
    {
    	final LogContext aContext = new LogContext ();
    	aContext.m_aLog = new WeakReference< LoggerBase > (aLogger);
    	return aContext;
    }
    
    //-------------------------------------------------------------------------
    public LogContext clone ()
    {
    	final LogContext aClone = new LogContext ();
    	aClone.m_aParent        = m_aParent ;
    	aClone.m_aLog           = m_aLog    ;
    	aClone.m_eLevel         = m_eLevel  ;
    	aClone.m_sMessage       = m_sMessage;
    	aClone.m_aError         = m_aError  ;
    	aClone.m_lIndirectVars  = m_lIndirectVars;
    	aClone.m_lCategories    = new ArrayList< String >             (mem_Categories());
    	aClone.m_lDirectVars    = new HashMap< String, Object >       (mem_DirectVars());
    	aClone.m_lLevelItems    = new HashMap< ELogLevel, LogContext >(mem_LevelItems());
    	return aClone;
    }
    
    //-------------------------------------------------------------------------
    /** define the log message.
     *  One message can be defined only ...
     *  even if this method is called more then one ...
     *  last message will win.
     *  
     *  @param	sMessage [IN]
     *			the log message.
     *
     *  @return the current log item for adding further details.
     */
    public LogContext withMessage (final String sMessage)
    {
    	m_sMessage = sMessage;
    	return this;
    }
    
    //-------------------------------------------------------------------------
    /** add new (sub) category/ies to this log item.
     * 
     *  Can be called as often as you want.
     *  Every new category will be added at the end of the list of
     *  existing categories. Duplicate categories wont be filtered out.
     * 
     *  @param	lSubCategories [IN]
     *  		the list of new (sub) categories added to the list of existing ones.
     *
     *  @return the current log item for adding further details.
     */
    public LogContext inCategory (final String... lSubCategories)
    {
    	if (lSubCategories == null)
    		return this;
    	
    	final List< String > lCategories = mem_Categories ();
    	for (final String sCategory : lSubCategories)
    		lCategories.add(sCategory);

    	return this;
    }

    //-------------------------------------------------------------------------
    /** spawn a new log level.
     * 
     *  For those level additional informations as e.g. variables or errors
     *  can be defined. They will be merged together with other defined level informations.
     * 
     *	@param	eLevel [IN]
     *			the new level.
     *
     *  @return new level context for adding further details.
     */
    public LogContext forLevel (final ELogLevel eLevel)
    {
    	LogContext aParent = null;
    	if (m_aParent != null)
    		aParent = m_aParent.get ();
    	
    	if (aParent == null)
    	{
    		final LogContext aNewContext = new LogContext ();
    		aNewContext.m_eLevel  = eLevel;
    		aNewContext.m_aParent = new WeakReference< LogContext > (this);
    		mem_LevelItems ().put(eLevel, aNewContext);
    		return aNewContext;
    	}
    	
    	return aParent.forLevel(eLevel);
    }

    //-------------------------------------------------------------------------
    /** add a set of variables to a new spawned log level
     *  (or to the parent context to be merged with all variables of
     *   all spawned log level ...)
     * 
     *  @param	lVars [IN]
     *  		the set of variables.
     *
     *  @return the current log item for adding further details.
     */
    public LogContext withVars (final LogVars lVars)
    {
    	m_lIndirectVars = lVars;
    	return this;
    }

    //-------------------------------------------------------------------------
    public LogContext setVar (final String sVar  ,
    						  final Object aValue)
    {
    	mem_DirectVars ().put (sVar, aValue);
    	return this;
    }
    
    //-------------------------------------------------------------------------
    public LogContext unsetVar (final String sVar)
    {
    	mem_DirectVars ().remove(sVar);
    	return this;
    }

    //-------------------------------------------------------------------------
    /** define an error (exception) for a new spawned log level.
     * 
     *  Can be called as often as you want ...
     *  but the last error will be used only for logging.
     * 
     *  @param	aError [IN]
     *  		the error.
     *
     *  @return the current log item for adding further details.
     */
    public LogContext withError (final Throwable aError)
    {
    	Validate.isTrue(m_aParent != null, "Error can be defined only, in case you called forLevel() before.");
    	
    	m_aError = aError;
    	return this;
    }

    //-------------------------------------------------------------------------
    protected String getMessage ()
    {
    	return StringUtils.trimToEmpty(m_sMessage);
    }

    //-------------------------------------------------------------------------
    /** collect all variables defined for a dedicated log level.
     * 
     *  Caused by the fact retrieving of those variables can produce errors
     *  (in case methods are called to get them) we catch all those possible
     *  errors/exceptions and ignore them. We try to collect as much variables
     *  we can ...
     * 
     *  Internally used only.
     * 
     *  @return the list of variables defined outside. Wont be null - but can be empty.
     */
    protected Map< String, Object > getVars ()
    {
    	final Map< String, Object > lVars = new HashMap< String, Object > ();

    	lVars.putAll(mem_DirectVars ());
    	
    	if (m_lIndirectVars != null)
    	{
    		try
    		{
    			m_lIndirectVars.defineVars(lVars);
    		}
    		catch (final Throwable ex)
    		{
    			// ignored by intention !
    			// we are not interested on errors within errors within ...
    		}
    	}
    	return lVars;
    }

    //-------------------------------------------------------------------------
    protected Throwable getError ()
    {
    	return m_aError;
    }

    //-------------------------------------------------------------------------
    public String log ()
    {
    	LogContext aParent = null;
    	if (m_aParent != null)
    		aParent = m_aParent.get ();

    	if (aParent != null)
    		return aParent.log();
    	
    	if (m_aLog == null)
    		return "";
    	
    	final LoggerBase aLog = m_aLog.get();
    	if (aLog == null)
    		return "";

    	final StringBuffer sLogItem = new StringBuffer (256);
    	
    	final List< String > lCategories = mem_Categories ();
    	for (final String sCategory : lCategories)
    	{
    		final String sNormalized = StringUtils.upperCase(sCategory);
    		sLogItem.append ("::"       );
    		sLogItem.append (sNormalized);
    	}
    	
    	final Mutable                      eLowestLevel     = new MutableObject(ELogLevel.E_TRACE);
    	final Mutable                      aHighestError    = new MutableObject(null);
    	final StringBuffer                 sCombinedMessage = new StringBuffer (256);
    	final Map< String   , Object     > lVars            = new HashMap< String, Object >();
    	final Map< ELogLevel, LogContext > lLogItems        = mem_LevelItems ();
    	
    	// reset all derived log level so they are not written out next time the same log context is used !
    	
    	impl_resetLevelItems ();

    	// start collecting all messages ... with parent message
    	
    	sCombinedMessage.append (getMessage ());

    	// merge together all vars/messages/infos of all level which are active (active against the configured log level)
    	
    	lVars.putAll(getVars());
    	
    	impl_processLogItem (aLog, ELogLevel.E_FATAL  , lLogItems.get(ELogLevel.E_FATAL  ), sCombinedMessage, lVars, eLowestLevel, aHighestError);
    	impl_processLogItem (aLog, ELogLevel.E_ERROR  , lLogItems.get(ELogLevel.E_ERROR  ), sCombinedMessage, lVars, eLowestLevel, aHighestError);
    	impl_processLogItem (aLog, ELogLevel.E_WARNING, lLogItems.get(ELogLevel.E_WARNING), sCombinedMessage, lVars, eLowestLevel, aHighestError);
    	impl_processLogItem (aLog, ELogLevel.E_INFO   , lLogItems.get(ELogLevel.E_INFO   ), sCombinedMessage, lVars, eLowestLevel, aHighestError);
    	impl_processLogItem (aLog, ELogLevel.E_DEBUG  , lLogItems.get(ELogLevel.E_DEBUG  ), sCombinedMessage, lVars, eLowestLevel, aHighestError);
    	impl_processLogItem (aLog, ELogLevel.E_TRACE  , lLogItems.get(ELogLevel.E_TRACE  ), sCombinedMessage, lVars, eLowestLevel, aHighestError);

    	// format the message itself
    	
    	sLogItem.append (" '"                        );
    	sLogItem.append (sCombinedMessage.toString ());
    	sLogItem.append ("'"                         );
    	
    	// layout the set of merged variables

    	impl_formatVars (sLogItem, lVars);

    	// log in lowest mode (e.g. ERROR) even if DEBUG is active and DEBUG variables are shown.
    	// ERROR with DEBUG variables means : "detailed error" ;-)

    	final Throwable aError = (Throwable) aHighestError.getValue();
    	
    	// bring all together
    	
    	final String sLog4Output = sLogItem.toString ();
    	      String sLog4Return = sLog4Output;
    	
    	if (aError != null)
    	{
    		sLogItem.append (aError.getClass()  );
    		sLogItem.append (" : "              );
    		sLogItem.append (aError.getMessage());
    		
    		sLog4Return = sLogItem.toString ();
    	}
    	
    	aLog.log((ELogLevel)eLowestLevel.getValue(), sLog4Output, aError);
    	
    	return sLog4Return;
    }
    
    //-------------------------------------------------------------------------
    protected Map< ELogLevel, LogContext > getLogItems ()
    {
    	return mem_LevelItems ();
    }

    //-------------------------------------------------------------------------
    private void impl_processLogItem (final LoggerBase            aLog            ,
    							      final ELogLevel             eLogLevel       ,
    								  final LogContext            aLogItem        ,
    								  final StringBuffer          sCombinedMessage,
    								  final Map< String, Object > lMergedLogVars  ,
    								  final Mutable               eLowestLogLevel ,
    								  final Mutable               aHighestError   )
    {
    	if (aLogItem == null)
    		return;

    	// ignore log items where it's level is not active
    	
    	if ( ! aLog.isActive(aLogItem.m_eLevel))
    		return;
    	
    	// add might existing message
    	
    	final String sMessage = aLogItem.getMessage();
    	if ( ! StringUtils.isEmpty(sMessage))
    	{
    		// add separator if there is any text before only
    		if (sCombinedMessage.length() > 0)
    			sCombinedMessage.append (" - ");

    		sCombinedMessage.append (sMessage);
    	}
    	
    	// merge together all variables

    	final Map< String, Object > lItemVars = aLogItem.getVars();
    	lMergedLogVars.putAll(lItemVars);

    	// find the lowest log level
    	// FATAL will win against all others

    	ELogLevel eNewLowestLevel = (ELogLevel) eLowestLogLevel.getValue();
    	if (eNewLowestLevel == null)
    		eNewLowestLevel = aLogItem.m_eLevel;
    	else
    	if (aLogItem.m_eLevel.lessThan(eNewLowestLevel))
    		eNewLowestLevel = aLogItem.m_eLevel;
    	eLowestLogLevel.setValue(eNewLowestLevel);
    	
    	// find any error defined within any level
    	
    	final Throwable aError = aLogItem.getError();
    	if (
    		(aError                   != null) &&
    		(aHighestError.getValue() == null)
    	   )
    		aHighestError.setValue (aError);
    }
    
    //-------------------------------------------------------------------------
    private void impl_formatVars (final StringBuffer          sLog ,
    							  final Map< String, Object > lVars)
    {
    	if (
    		(lVars == null  ) ||
    		(lVars.isEmpty())
    	   )
    		return;
    	
    	sLog.append ("\n[\n");
    	final Iterator< Entry< String, Object > > rVars         = lVars.entrySet().iterator();
    	      boolean                             bAddSeparator = false;
    	while (rVars.hasNext())
    	{
    		final Entry< String, Object > rVar       = rVars.next();
    		final String                  sVar       = StringUtils.lowerCase(rVar.getKey());
    		final Object                  aValue     = rVar.getValue();
    		
    		if (bAddSeparator)
    			sLog.append (",\n" );
    		else
    			bAddSeparator = true;
    		
    		sLog.append (sVar  );
    		sLog.append ("="   );
    		sLog.append (aValue);
    	}
    	sLog.append ("\n]\n");
    }

    //-------------------------------------------------------------------------
    private void impl_resetLevelItems ()
    {
    	m_lLevelItems = null;
    }
    
    //-------------------------------------------------------------------------
    private List< String > mem_Categories ()
    {
    	if (m_lCategories == null)
    		m_lCategories = new ArrayList< String > ();
    	return m_lCategories;
    }

    //-------------------------------------------------------------------------
    private Map< ELogLevel, LogContext > mem_LevelItems ()
    {
    	if (m_lLevelItems == null)
    		m_lLevelItems = new HashMap< ELogLevel, LogContext > ();
    	return m_lLevelItems;
    }

    //-------------------------------------------------------------------------
    private Map< String, Object > mem_DirectVars ()
    {
    	if (m_lDirectVars == null)
    		m_lDirectVars = new HashMap< String, Object > ();
    	return m_lDirectVars;
    }
    
    //-------------------------------------------------------------------------
    private WeakReference< LoggerBase > m_aLog = null;
    
    //-------------------------------------------------------------------------
    private WeakReference< LogContext > m_aParent = null;

    //-------------------------------------------------------------------------
    private ELogLevel m_eLevel = null;
    
    //-------------------------------------------------------------------------
    private String m_sMessage = null;
    
    //-------------------------------------------------------------------------
    private List< String > m_lCategories = null;

    //-------------------------------------------------------------------------
    private Map< ELogLevel, LogContext > m_lLevelItems = null;

    //-------------------------------------------------------------------------
    private LogVars m_lIndirectVars = null;

    //-------------------------------------------------------------------------
    private Map< String, Object > m_lDirectVars = null;
    
    //-------------------------------------------------------------------------
    private Throwable m_aError = null;
}