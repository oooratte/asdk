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
package net.as_development.asdk.db_service.impl.backend.creator;

import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.lang3.StringUtils;

//==============================================================================
/** Supports operations around command lines and can be used as base class
 *  for own implementations.
 */
public abstract class CommandLineBase
{
    //--------------------------------------------------------------------------
    public static final String OPT_INFOLEVEL = "i";
    
    //--------------------------------------------------------------------------
    public static final String OPT_HELP = "h";

    //--------------------------------------------------------------------------
    public static final boolean REQUIRED = true;

    //--------------------------------------------------------------------------
    public static final boolean NOT_REQUIRED = false;

    //--------------------------------------------------------------------------
    public static final boolean HAS_VALUE = true;

    //--------------------------------------------------------------------------
    public static final boolean HAS_NO_VALUE = false;

    //--------------------------------------------------------------------------
    public static final String LIST_SEPARATOR = ",";
    
    //-------------------------------------------------------------------------
    public CommandLineBase ()
    {}
    
    //--------------------------------------------------------------------------
    /** create a new command line instance and name it.
     * 
     *  @param  sCommandName [IN]
     *          the 'name' of the outside tool using that command line.
     *          Used e.g. for logging and showing help messages.
     */
    public CommandLineBase (String sCommandName)
    {
        m_sCommandName = sCommandName;
    }

    //--------------------------------------------------------------------------
    /** add the predefined option for showing a help to this instance.
     */
    public void addStdOpt_Help ()
        throws Exception
    {
        addOption (CommandLineBase.OPT_HELP    ,
                   "help"                      ,
                   CommandLineBase.HAS_NO_VALUE,
                   CommandLineBase.NOT_REQUIRED,
                   "show this help."           );
    }

    //--------------------------------------------------------------------------
    /** add a predefined option for logging to this instance
     */
    public void addStdOpt_InfoLevel ()
        throws Exception
    {
        addOption (CommandLineBase.OPT_INFOLEVEL,
                   "infolevel"                  ,
                   CommandLineBase.HAS_VALUE    ,
                   CommandLineBase.NOT_REQUIRED ,
                   "the level for logging."     +
                   "\n{info, warning, error}"   );
    }
    
    //--------------------------------------------------------------------------
    /** define a new option for this command line.
     * 
     *  @param  sShort [IN]
     *          the short key for that option.
     *          Used as "-x" later.
     *          
     *  @param  sLong [IN]
     *          the long key for that option.
     *          Used as "--xxxxxx" later.
     *          
     *  @param  bHasValue [IN]
     *          true if that option must have a value,
     *          false if that option is a boolean option only.
     *          
     *  @param  bRequired [IN]
     *          true if that option is mandatory,
     *          false otherwise.
     *          
     *  @param  sDescription [IN]
     *          a short description what that option mean.
     */
    public void addOption (String  sShort      ,
                           String  sLong       ,
                           boolean bHasValue   ,
                           boolean bRequired   ,
                           String  sDescription)
        throws Exception
    {
        Option aOption = new Option (sShort, sLong, bHasValue, sDescription);
        aOption.setRequired (bRequired);
        mem_Options ().addOption (aOption);
    }

    //--------------------------------------------------------------------------
    /** define an option as required (mandatory) one.
     * 
     *  @param  sShort [IN]
     *          the short key for that option.
     *          
     *  @param  bRequired [IN]
     *          the mandatory state (true=mandatory)
     */
    public void setOptionRequired (String  sShort   ,
                                   boolean bRequired)
        throws Exception
    {
        Option aOption = mem_Options ().getOption (sShort);
        aOption.setRequired (bRequired);
    }

    //--------------------------------------------------------------------------
    /** parse the list of command line options and set all found
     *  options on this instance.
     *  
     *  @param  lArgs [IN]
     *          the list of command line arguments to be parsed here.
     */
    public void parse (String... lArgs)
        throws Exception
    {
        try
        {
            CommandLineParser aParser = new PosixParser ();
            m_aCmdLine = aParser.parse(mem_Options (), lArgs);
            verify ();
        }
        catch(Throwable ex)
        {
            System.err.println(ex.getMessage ());
            m_bWasError = true;
        }
    }

    //--------------------------------------------------------------------------
    /** @return the level for logging.
     */
    public String getInfoLevel ()
    {
        return m_aCmdLine.getOptionValue (CommandLineBase.OPT_INFOLEVEL);
    }
    
    //--------------------------------------------------------------------------
    /** @return true if showing the help seems to be needed.
     * 
     *  E.g. in case there is no option given or help is requested
     *  explicit by using -h option.
     */
    public boolean needsHelp ()
        throws Exception
    {
        return (
                (m_bWasError                                    ) ||
                (m_aCmdLine.hasOption (CommandLineBase.OPT_HELP))
               );
    }

    //--------------------------------------------------------------------------
    /** print some help info to stdout.
     */
    public void printHelp ()
        throws Exception
    {
        HelpFormatter aCmdLineHelp = new HelpFormatter ();
        aCmdLineHelp.printHelp (m_sCommandName+" [options]", mem_Options ());
    }

    //--------------------------------------------------------------------------
    /** must be overwritten by a derived class to verify the command line more in detail.
     *  We can verify if required options exists or not here ...
     *  but only the derived class knows more details about possible option values.
     *  
     *  Has to throw an exception in case something is wrong on verify.
     *  We catch ALL and show the message in combination with the normal
     *  command line help then :-)
     */
    protected abstract void verify ()
        throws Exception;

    //--------------------------------------------------------------------------
    /** @return true if the given command line is empty.
     */
    protected boolean isEmpty ()
    {
        if (m_aCmdLine == null)
            return true;

        String[] lArgs = m_aCmdLine.getArgs    ();
        Option[] lOpts = m_aCmdLine.getOptions ();
        return (
                (lArgs == null || lArgs.length < 1) &&
                (lOpts == null || lOpts.length < 1)
               );
    }

    //--------------------------------------------------------------------------
    /** @return the value of the specified option.
     * 
     *  @note   Return value can be null or empty.
     *  
     *  @param  sShort [IN]
     *          the short key of the option where the value must be returned.
     */
    protected String getOptionValue (String sShort)
        throws Exception
    {
        return m_aCmdLine.getOptionValue (sShort);
    }

    //--------------------------------------------------------------------------
    /** @return the value of the specified option interpreted as a list.
     * 
     *  If a value contain comma separated values you can use this method
     *  to get a parsed list of values instead one single value.
     *  Of course it's up to you to know if the value will be a list or not .-)
     *  
     *  @param  sShort [IN]
     *          the short key of the option where the value must be parsed.
     */
    protected List< String > getOptionValueAsList (String sShort)
        throws Exception
    {
        String          sValue  = m_aCmdLine.getOptionValue (sShort);
        List< String >  lList   = new Vector< String >(10);
        
        if (StringUtils.isEmpty(sValue))
            return lList;
        
        StringTokenizer aParser = new StringTokenizer (sValue, CommandLineBase.LIST_SEPARATOR);
        while (aParser.hasMoreTokens())
            lList.add(aParser.nextToken());
        
        return lList;
    }
    
    //--------------------------------------------------------------------------
    /** @return true if the specified option exists (even if there is no value)
     * 
     *  @param  sShort [IN]
     *          the short key of the option where the state has to be checked.
     */
    protected boolean hasOption (String sShort)
        throws Exception
    {
        return m_aCmdLine.hasOption (sShort);
    }

    //--------------------------------------------------------------------------
    private Options mem_Options ()
        throws Exception
    {
        if (m_lOptions == null)
            m_lOptions = new Options ();
        return m_lOptions;
    }

    //--------------------------------------------------------------------------
    /// the 'name' of the command line tool itself .. used for branding of help or info messages .-)
    private String m_sCommandName = null;

    //--------------------------------------------------------------------------
    /// the support options.
    private Options m_lOptions = null;

    //--------------------------------------------------------------------------
    /// the command line parser itself.
    private CommandLine m_aCmdLine = null;

    //--------------------------------------------------------------------------
    /// knows if an error exists within given command line.
    private boolean m_bWasError = false;
}
