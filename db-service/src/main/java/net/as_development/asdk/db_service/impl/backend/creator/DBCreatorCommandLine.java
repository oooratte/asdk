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
package net.as_development.asdk.db_service.impl.backend.creator;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

//==============================================================================
/** Knows all parameter supported by the command line for DBCreator implementation.
 */
public class DBCreatorCommandLine extends CommandLineBase
{
    //--------------------------------------------------------------------------
    public static final String OPT_USER = "u";
    
    //--------------------------------------------------------------------------
    public static final String OPT_PASSWORD = "p";
    
    //--------------------------------------------------------------------------
    public static final String OPT_PERSISTENCEUNITLIST = "l";
    
    //--------------------------------------------------------------------------
    public static final String OPT_ENTITIES = "e";
    
    //--------------------------------------------------------------------------
    public static final String OPT_OPERATION = "o";
    
    //-------------------------------------------------------------------------
    public DBCreatorCommandLine ()
        throws Exception
    {
        super ("codbcreator");
        
        addStdOpt_Help    ();
        addStdOpt_InfoLevel();
        
        addOption (DBCreatorCommandLine.OPT_USER        ,
                   "user"                               ,
                   CommandLineBase.HAS_VALUE            ,
                   CommandLineBase.REQUIRED             ,
                   "name of an administrative DB user. ");
        
        addOption (DBCreatorCommandLine.OPT_PASSWORD        ,
                   "password"                               ,
                   CommandLineBase.HAS_VALUE                ,
                   CommandLineBase.NOT_REQUIRED             ,
                   "password of an administrative DB user. ");
        
        addOption (DBCreatorCommandLine.OPT_PERSISTENCEUNITLIST     ,
                   "list-pu"                                        ,
                   CommandLineBase.HAS_VALUE                        ,
                   CommandLineBase.NOT_REQUIRED                     ,
                   "comma separated list of persistence units "     +
                   "the requested operation should rely on."        +
                   "\nIf this option is not specified ALL available"+
                   "persistence units will be used by default."     );
        
        addOption (DBCreatorCommandLine.OPT_ENTITIES                ,
                   "entities"                                       ,
                   CommandLineBase.HAS_VALUE                        ,
                   CommandLineBase.NOT_REQUIRED                     ,
                   "comma separated list of entity classes "        +
                   "the requested operation should rely on."        +
                   "\nIf this option is not specified ALL available"+
                   "entities will be used by default."              );
        
        addOption (DBCreatorCommandLine.OPT_OPERATION,
                   "operation"                       ,
                   CommandLineBase.HAS_VALUE         ,
                   CommandLineBase.REQUIRED          ,
                   "requested operation"             +
                   "\n{create, remove}"              );
    }
    
    //-------------------------------------------------------------------------
    public String getUser ()
        throws Exception
    {
        return getOptionValue(DBCreatorCommandLine.OPT_USER);
    }
    
    //-------------------------------------------------------------------------
    public String getPassword ()
        throws Exception
    {
        return getOptionValue(DBCreatorCommandLine.OPT_PASSWORD);
    }
    
    //-------------------------------------------------------------------------
    public DBCreator.EOperation getOperation ()
        throws Exception
    {
        String               sOp = getOptionValue(DBCreatorCommandLine.OPT_OPERATION);
        DBCreator.EOperation eOp = null;
        
        if (StringUtils.equalsIgnoreCase(sOp, "create"))
            eOp = DBCreator.EOperation.E_CREATE;
        else
        if (StringUtils.equalsIgnoreCase(sOp, "remove"))
            eOp = DBCreator.EOperation.E_REMOVE;
        
        return eOp;
    }
    
    //-------------------------------------------------------------------------
    public List< String > getPersistenceUnitList ()
        throws Exception
    {
        return getOptionValueAsList(DBCreatorCommandLine.OPT_PERSISTENCEUNITLIST);
    }

    //-------------------------------------------------------------------------
    public List< String > getEntities ()
        throws Exception
    {
        return getOptionValueAsList(DBCreatorCommandLine.OPT_ENTITIES);
    }
    
    //-------------------------------------------------------------------------
    /* (non-Javadoc)
     * @see net.as_development.dblayer.impl.backend.creator.CommandLineBase#verify()
     */
    @Override
    protected void verify()
        throws Exception
    {
        String sOp = getOptionValue(DBCreatorCommandLine.OPT_OPERATION);
        
        boolean bValidOperation =
        (
            (StringUtils.equalsIgnoreCase(sOp, "create" )) ||
            (StringUtils.equalsIgnoreCase(sOp, "remove" ))
        );
        
        if ( ! bValidOperation)
            throw new IllegalArgumentException ("Value for option '"+DBCreatorCommandLine.OPT_OPERATION+"' is wrong.");
    }
}
