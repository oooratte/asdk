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
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import net.as_development.asdk.api.db.IDB;
import net.as_development.asdk.api.db.IDBPool;
import net.as_development.asdk.api.db.IDBSchema;
import net.as_development.asdk.api.db.IPersistenceUnit;
import net.as_development.asdk.api.db.IPersistenceUnitRegistry;
import net.as_development.asdk.db_service.EntityBase;
import net.as_development.asdk.db_service.impl.PersistenceUnit;
import net.as_development.asdk.db_service.impl.PersistenceUnitRegistry;
import net.as_development.asdk.service.env.ServiceEnv;

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
	public void setDBPool (final IDBPool iDBPool)
	    throws Exception
	{
		m_iDBPool = iDBPool;
	}
	
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
     *  Note   an empty list means using of ALL available units
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
     *  Note   an empty list means using of ALL available entities
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
     *  see    DBCreator.EOperation for more details.
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
     *  Note   There is no option to 'recreate' an existing schema.
     *          Use operation E_REMOVE in combination with E_CREATE to
     *          recreate an existing schema.
     */
    private void impl_createSchema ()
        throws Exception
    {
    	final IDBPool                  iDBPool     = mem_DBPool               ();
        final IPersistenceUnitRegistry iPURegistry = mem_PURegistry           ();
        final List< String >           lUnits      = mem_PersistenceUnitNames ();
        final List< String >           lEntities   = mem_Entities             (); 
              int                      nErrors     = 0;
        
        for (final String sUnit : lUnits)
        {
        	final PersistenceUnit aUnit = (PersistenceUnit) iPURegistry.getPersistenceUnitByName(sUnit);
            aUnit.setUser    (m_sAdminUser    );
            aUnit.setPassword(m_sAdminPassword);
            
            final IDB            iDB           = iDBPool.getDbForPersistenceUnit(sUnit);
            final IDBSchema      iCreator      = (IDBSchema)iDB;
            final List< String > lUnitEntities = DBCreator.impl_getIntersection(lEntities, aUnit.getEntities());
            
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
                	nErrors++;
                	
                    StringBuffer sLog = new StringBuffer (256);
                    sLog.append ("Error on creating schema for entity '"+sEntity+"'.\n");
                    sLog.append ("Original message was: '"+ex.getMessage ()+"'."       );
                    impl_log (Level.SEVERE, sLog.toString ());
                }
            }
        }
        
        if (nErrors > 0)
        	throw new Exception ("There was ["+nErrors+"] errors.");
    }
    
    //-------------------------------------------------------------------------
    /** remove all schema for the specified list of persistence units and DB entities.
     */
    private void impl_removeSchema ()
        throws Exception
    {
    	final IDBPool                  iDBPool     = mem_DBPool               ();
        final IPersistenceUnitRegistry iPURegistry = mem_PURegistry           ();
        final List< String >           lUnits      = mem_PersistenceUnitNames ();
        final List< String >           lEntities   = mem_Entities             ();
        	  int                      nErrors     = 0;
        
        for (final String sUnit : lUnits)
        {
        	final PersistenceUnit aUnit = (PersistenceUnit) iPURegistry.getPersistenceUnitByName(sUnit);
            aUnit.setUser    (m_sAdminUser    );
            aUnit.setPassword(m_sAdminPassword);
            
            final IDB            iDB           = iDBPool.getDbForPersistenceUnit(sUnit);
            final IDBSchema      iCreator      = (IDBSchema)iDB;
            final List< String > lUnitEntities = DBCreator.impl_getIntersection(lEntities, aUnit.getEntities());
            
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
                	nErrors++;
                	
                    StringBuffer sLog = new StringBuffer (256);
                    sLog.append ("Error on removing schema for entity '"+sEntity+"'.\n");
                    sLog.append ("Original message was: '"+ex.getMessage ()+"'."       );
                    impl_log (Level.SEVERE, sLog.toString ());
                }
            }
        }
        
        if (nErrors > 0)
        	throw new Exception ("There was ["+nErrors+"] errors.");
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
        
        final IPersistenceUnitRegistry iPURegistry  = mem_PURegistry           ();
        final List< String >           lUnitNames   = mem_PersistenceUnitNames ();
        final List< String >           lEntities    = mem_Entities             ();
        
        for (final String sUnit : lUnitNames)
        {
        	final IPersistenceUnit iUnit = iPURegistry.getPersistenceUnitByName(sUnit);
            if (iUnit == null)
            	throw new RuntimeException ("Could not retrieve nor load persistence unit with name '"+sUnit+"'.");
            
            lEntities.addAll(iUnit.getEntities());
        }
        
        m_bEnvInitialized = true;
    }
    
    //-------------------------------------------------------------------------
	private IDBPool mem_DBPool ()
	    throws Exception
	{
		if (m_iDBPool == null)
			m_iDBPool = ServiceEnv.get ().getService (IDBPool.class);
		return m_iDBPool;
	}
	
    //-------------------------------------------------------------------------
    private IPersistenceUnitRegistry mem_PURegistry ()
        throws Exception
    {
    	if (m_iPURegistry == null)
    		m_iPURegistry = PersistenceUnitRegistry.get ();
    	return m_iPURegistry;
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
    private List< String > mem_Entities ()
        throws Exception
    {
        if (m_lEntities == null)
            m_lEntities = new Vector< String >(10);
        return m_lEntities;
    }
    
    //-------------------------------------------------------------------------
    private IDBPool m_iDBPool = null;
    
    //-------------------------------------------------------------------------
    /// login for the DB admin user used here.
    private String m_sAdminUser = null;
    
    //-------------------------------------------------------------------------
    /// password for the DB admin user used here.
    private String m_sAdminPassword = null;
    
    //-------------------------------------------------------------------------
    private IPersistenceUnitRegistry m_iPURegistry = null;
    
    //-------------------------------------------------------------------------
    private List< String > m_lPersistenceUnitNames = null;
    
    //-------------------------------------------------------------------------
    private List< String > m_lEntities = null;
    
    //-------------------------------------------------------------------------
    private boolean m_bEnvInitialized = false;
}
