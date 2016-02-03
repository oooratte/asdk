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
