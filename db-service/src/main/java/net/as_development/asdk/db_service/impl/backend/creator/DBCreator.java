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
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.as_development.asdk.api.db.IDB;
import net.as_development.asdk.api.db.IDBPool;
import net.as_development.asdk.api.db.IDBSchema;
import net.as_development.asdk.db_service.EntityBase;
import net.as_development.asdk.db_service.impl.PersistenceUnit;
import net.as_development.asdk.service.env.ServiceEnv;

import org.apache.commons.lang3.StringUtils;

//==============================================================================
/** Provides more comfort around creating and updating DB schema.
 * 
 *  It uses the interface IDBSchema of a IDB instance.
 *  It's implemented there because there exists all needed meta information
 *  and the needed access to the back end. Here we implement some stuff around that
 *  as e.g. command line interface, flexible API and more.
 */
public class DBCreator
{
    //-------------------------------------------------------------------------
    public enum EOperation
    {
        E_CREATE,
        E_REMOVE,
        E_UPDATE,
        E_MIGRATE
    }
    
    //-------------------------------------------------------------------------
    public DBCreator ()
    {}
    
    //-------------------------------------------------------------------------
    /** set the credentials for an administrative DB account used here
     *  to create all needed resources.
     * 
     *  @param  sUser [IN]
     *          the name of the admin user.
     *          
     *  @param  sPassword [IN]
     *          the password of the admin user.
     */
    public void setAdminCredentials (String sUser    ,
                                     String sPassword)
        throws Exception
    {
        if (StringUtils.isEmpty(sUser))
            throw new IllegalArgumentException ("Admin user name is empty.");
        
        m_sAdminUser     = sUser;
        m_sAdminPassword = sPassword;
    }
    
    //-------------------------------------------------------------------------
    /** same as setPersistenceUnits(java.lang.String...)
     */
    public void setPersistenceUnits (List< String > lUnits)
        throws Exception
    {
        int      c          = lUnits.size ();
        int      i          = 0;
        String[] lUnitNames = new String[c];
        
        for (i=0; i<c; ++i)
            lUnitNames[i] = lUnits.get(i);
        
        setPersistenceUnits(lUnitNames);
    }
    
    //-------------------------------------------------------------------------
    /** same as setEntities(java.lang.String...)
     */
    public void setEntities (List< String > lEntities)
        throws Exception
    {
        int      c          = lEntities.size ();
        int      i          = 0;
        String[] lNames = new String[c];
        
        for (i=0; i<c; ++i)
            lNames[i] = lEntities.get(i);
        
        setEntities(lNames);
    }
    
    //-------------------------------------------------------------------------
    /** define the list of persistence unit names.
     *  Those units will be used later for all requested operations as e.g.
     *  create schema, update schema, migrate data etcpp.
     *  
     *  @note   an empty list means using of ALL available units
     *          within the classpath.
     *  
     *  @param  lUnits [IN]
     *          the list of a persistence unit names.
     */
    public void setPersistenceUnits (String... lUnits)
        throws Exception
    {
        List< String > lNames = mem_PersistenceUnitNames ();
        lNames.clear ();
        
        for (String sUnit : lUnits)
        {
            if (StringUtils.isEmpty (sUnit))
                continue;
            lNames.add(sUnit);
        }
    }
    
    //-------------------------------------------------------------------------
    /** define the list of entities.
     *  Those units will be used later for all requested operations as e.g.
     *  create schema, update schema, migrate data etcpp.
     *  
     *  @note   an empty list means using of ALL available entities
     *          within the classpath.
     *  
     *  @param  lEntities [IN]
     *          the list of a persistence unit names.
     */
    public void setEntities (String... lEntities)
        throws Exception
    {
        List< String > lNames = mem_Entities ();
        lNames.clear ();
        
        for (String sEntity : lEntities)
        {
            if (StringUtils.isEmpty (sEntity))
                continue;
            lNames.add(sEntity);
        }
    }
    
    //-------------------------------------------------------------------------
    /** do the requested operation.
     * 
     *  @see    DBCreator.EOperation for more details.
     * 
     *  @param  eOperation [IN]
     *          the requested operation.
     */
    public void doOperation (DBCreator.EOperation eOperation)
        throws Exception
    {
        impl_defineEnv ();
        
        if (eOperation == DBCreator.EOperation.E_CREATE)
            impl_createSchema ();
        else
        if (eOperation == DBCreator.EOperation.E_REMOVE)
            impl_removeSchema ();
        else
            throw new UnsupportedOperationException ("Operation '"+eOperation+"' not implemented yet.");
    }
    
    //-------------------------------------------------------------------------
    /** create schema and tables new for the specified set of persistence units
     *  and DB entities.
     *  
     *  @note   There is no option to 'recreate' an existing schema.
     *          Use operation E_REMOVE in combination with E_CREATE to
     *          recreate an existing schema.
     */
    private void impl_createSchema ()
        throws Exception
    {
        IDBPool                 iPool     = ServiceEnv.get ().getService(IDBPool.class);
        List< PersistenceUnit > lUnits    = mem_PersistenceUnits ();
        List< String >          lEntities = mem_Entities (); 
        
        for (PersistenceUnit aUnit : lUnits)
        {
            aUnit.setUser    (m_sAdminUser    );
            aUnit.setPassword(m_sAdminPassword);
            
            iPool.registerPersistenceUnit(aUnit);
            
            String         sUnit         = aUnit.getName();
            IDB            iDB           = iPool.getDbForPersistenceUnit(sUnit);
            IDBSchema     iCreator      = (IDBSchema)iDB;
            List< String > lUnitEntities = DBCreator.impl_getIntersection(lEntities, aUnit.getEntities());
            
            for (String sEntity : lUnitEntities)
            {
                try
                {
                    @SuppressWarnings("unchecked")
                    Class< ? extends EntityBase > aEntity = (Class< ? extends EntityBase >) Class.forName(sEntity);
                    iCreator.createEntitySchema(aEntity);
                }
                catch (Throwable ex)
                {
                    StringBuffer sLog = new StringBuffer (256);
                    sLog.append ("Error on creating table for entity '"+sEntity+"'.\n");
                    sLog.append ("Original message was: '"+ex.getMessage ()+"'."       );
                    impl_log (Level.SEVERE, sLog.toString ());
                }
            }
        }
    }
    
    //-------------------------------------------------------------------------
    /** remove all schema for the specified list of persistence units and DB entities.
     */
    private void impl_removeSchema ()
        throws Exception
    {
        IDBPool                 iPool     = ServiceEnv.get ().getService(IDBPool.class);
        List< PersistenceUnit > lUnits    = mem_PersistenceUnits ();
        List< String >          lEntities = mem_Entities (); 
        
        for (PersistenceUnit aUnit : lUnits)
        {
            aUnit.setUser    (m_sAdminUser    );
            aUnit.setPassword(m_sAdminPassword);
            
            iPool.registerPersistenceUnit(aUnit);
            
            String         sUnit         = aUnit.getName();
            IDB            iDB           = iPool.getDbForPersistenceUnit(sUnit);
            IDBSchema     iCreator      = (IDBSchema)iDB;
            List< String > lUnitEntities = DBCreator.impl_getIntersection(lEntities, aUnit.getEntities());
            
            for (String sEntity : lUnitEntities)
            {
                try
                {
                    @SuppressWarnings("unchecked")
                    Class< ? extends EntityBase > aEntity = (Class< ? extends EntityBase >) Class.forName(sEntity);
                    iCreator.removeEntitySchema(aEntity);
                }
                catch (Throwable ex)
                {
                    StringBuffer sLog = new StringBuffer (256);
                    sLog.append ("Error on removing table for entity '"+sEntity+"'.\n");
                    sLog.append ("Original message was: '"+ex.getMessage ()+"'."      );
                    impl_log (Level.SEVERE, sLog.toString ());
                }
            }
        }
    }
    
    //-------------------------------------------------------------------------
    private static List< String > impl_getIntersection (List< String > lList1,
                                                        List< String > lList2)
        throws Exception
    {
        List< String > lIntersection = new Vector< String > (10);
        for (String sItem : lList1)
        {
            if (lList2.contains(sItem))
                lIntersection.add (sItem);
        }
        return lIntersection;
    }
    
    //-------------------------------------------------------------------------
    private void impl_log (Level  aLevel  ,
                           String sMessage)
    {
        try
        {
            Logger.getLogger(DBCreator.class.getName ()).log(aLevel, sMessage);
        }
        catch (Throwable ex)
        {
            System.err.println ("Error during writing log message.\nException message = '"+ex.getMessage()+"'.");
            ex.printStackTrace(System.err);
        }
    }
    
    //-------------------------------------------------------------------------
    private void impl_defineEnv ()
        throws Exception
    {
        if (m_bEnvInitialized)
            return;
        
        List< String >          lUnitNames   = mem_PersistenceUnitNames ();
        List< PersistenceUnit > lUnits       = mem_PersistenceUnits (); 
        List< String >          lEntities    = mem_Entities ();
        boolean                 bAllUnits    = lUnitNames.isEmpty();
        boolean                 bAllEntities = lEntities.isEmpty();
        
        if (bAllUnits)
            lUnitNames = PersistenceUnit.listUnits();
        
        for (String sUnit : lUnitNames)
        {
            PersistenceUnit aUnit = PersistenceUnit.loadUnit(sUnit);
            lUnits.add (aUnit);
            
            // @todo think about me
            //       filter or reject duplicate entities (is it an error ?)
            if (bAllEntities)
                lEntities.addAll(aUnit.getEntities());
        }
        
        m_bEnvInitialized = true;
    }
    
    //-------------------------------------------------------------------------
    private List< String > mem_PersistenceUnitNames ()
        throws Exception
    {
        if (m_lPersistenceUnitNames == null)
            m_lPersistenceUnitNames = new Vector< String >(10);
        return m_lPersistenceUnitNames;
    }
    
    //-------------------------------------------------------------------------
    private List< PersistenceUnit > mem_PersistenceUnits ()
        throws Exception
    {
        if (m_lPersistenceUnits == null)
            m_lPersistenceUnits = new Vector< PersistenceUnit >(10);
        return m_lPersistenceUnits;
    }
    
    //-------------------------------------------------------------------------
    private List< String > mem_Entities ()
        throws Exception
    {
        if (m_lEntities == null)
            m_lEntities = new Vector< String >(10);
        return m_lEntities;
    }
    
    //-------------------------------------------------------------------------
    /// login for the DB admin user used here.
    private String m_sAdminUser = null;
    
    //-------------------------------------------------------------------------
    /// password for the DB admin user used here.
    private String m_sAdminPassword = null;
    
    //-------------------------------------------------------------------------
    private List< String > m_lPersistenceUnitNames = null;
    
    //-------------------------------------------------------------------------
    private List< PersistenceUnit > m_lPersistenceUnits = null;
    
    //-------------------------------------------------------------------------
    private List< String > m_lEntities = null;
    
    //-------------------------------------------------------------------------
    private boolean m_bEnvInitialized = false;
}