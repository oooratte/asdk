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
package net.as_development.asdk.tools.commandline;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

//=============================================================================
public class CommandLineBase
{
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
    public CommandLineBase (String sCommandName)
    	throws Exception
    {
        m_sCommandName = sCommandName;
        
        addOption (CommandLineBase.OPT_HELP    ,
                   		"help"                      ,
                   		CommandLineBase.HAS_NO_VALUE,
                   		CommandLineBase.NOT_REQUIRED,
                   		"show this help."           );
    }

    //--------------------------------------------------------------------------
    public void parse (String... lArgs)
        throws Exception
    {
        try
        {
            CommandLineParser aParser = new PosixParser ();
            m_aCmdLine = aParser.parse(mem_Options (), lArgs);
            verifyInt ();
        }
        catch(Throwable ex)
        {
            System.out.println(ex.getMessage ());
            m_bWasError = true;
        }
    }

    //--------------------------------------------------------------------------
    public boolean needsHelp ()
        throws Exception
    {
        return (
                (m_bWasError                                    ) ||
                (m_aCmdLine.hasOption (CommandLineBase.OPT_HELP))
               );
    }

    //--------------------------------------------------------------------------
    public void printHelp ()
        throws Exception
    {
        HelpFormatter aCmdLineHelp = new HelpFormatter ();
        aCmdLineHelp.printHelp (m_sCommandName+" [options]", mem_Options ());
    }

    //--------------------------------------------------------------------------
    public boolean isEmpty ()
    	throws Exception
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
    protected String getOptionValue (String sShort)
        throws Exception
    {
        return m_aCmdLine.getOptionValue (sShort);
    }

    //--------------------------------------------------------------------------
    protected boolean hasOption (String sShort)
        throws Exception
    {
        return m_aCmdLine.hasOption (sShort);
    }

    //--------------------------------------------------------------------------
    protected void addOption (String  sShort      ,
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
    protected void verifyInt ()
    	throws Exception
    {}

    //--------------------------------------------------------------------------
    private Options mem_Options ()
        throws Exception
    {
        if (m_lOptions == null)
            m_lOptions = new Options ();
        return m_lOptions;
    }

    //--------------------------------------------------------------------------
    private String m_sCommandName = null;

    //--------------------------------------------------------------------------
    private Options m_lOptions = null;

    //--------------------------------------------------------------------------
    private CommandLine m_aCmdLine = null;

    //--------------------------------------------------------------------------
    private boolean m_bWasError = false;
}
