/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
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
