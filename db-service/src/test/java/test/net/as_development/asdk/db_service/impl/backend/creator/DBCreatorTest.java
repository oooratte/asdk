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
package test.net.as_development.asdk.db_service.impl.backend.creator;

import java.util.List;
import java.util.Vector;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import junit.framework.Assert;
import net.as_development.asdk.api.db.IDB;
import net.as_development.asdk.api.db.IDBPool;
import net.as_development.asdk.api.db.IDBSchema;
import net.as_development.asdk.api.service.env.IServiceEnv;
import net.as_development.asdk.db_service.impl.PersistenceUnit;
import net.as_development.asdk.db_service.impl.backend.creator.DBCreator;
import net.as_development.asdk.service.env.ServiceEnv;
import test.net.as_development.asdk.db_service.test.entities.TestEntity;

//==============================================================================
@RunWith(PowerMockRunner.class)
@PrepareForTest({PersistenceUnit.class, ServiceEnv.class})
public class DBCreatorTest
{
    //-------------------------------------------------------------------------
    @Before
    public void setUp ()
        throws Exception
    {
        String sPersistenceUnit = "test_persistence_unit"; 
        
        List< String > lEntities = new Vector< String >(10);
        lEntities.add (TestEntity.class.getName ());
        
        m_aPersistenceUnitMock = Mockito.mock(PersistenceUnit.class);
        Mockito.when(m_aPersistenceUnitMock.getName()    ).thenReturn(sPersistenceUnit);
        Mockito.when(m_aPersistenceUnitMock.getEntities()).thenReturn(lEntities       );
        
        List< String > lPersistenceUnitNames = new Vector< String >(10);
        lPersistenceUnitNames.add (sPersistenceUnit);
        
        PowerMockito.mockStatic(PersistenceUnit.class);
        Mockito.when(PersistenceUnit.listUnits()                ).thenReturn(lPersistenceUnitNames );
        Mockito.when(PersistenceUnit.loadUnit (sPersistenceUnit)).thenReturn(m_aPersistenceUnitMock);
        
        IServiceEnv iServiceManager = Mockito.mock(IServiceEnv.class);
                    m_iDBPoolMock   = Mockito.mock(IDBPool.class);
                    m_iDBMock       = Mockito.mock(IDBSchema.class, Mockito.withSettings().extraInterfaces(IDB.class));
        
        PowerMockito.mockStatic(IServiceEnv.class);
        Mockito.when(ServiceEnv.get ()                        ).thenReturn(iServiceManager);
        Mockito.when(iServiceManager.getService(IDBPool.class)).thenReturn(m_iDBPoolMock  );
        
        Mockito.when(m_iDBPoolMock.getDbForPersistenceUnit(sPersistenceUnit)).thenReturn((IDB)m_iDBMock);
    }
    
    //-------------------------------------------------------------------------
    @After
    public void tearDown ()
        throws Exception
    {}
    
    //-------------------------------------------------------------------------
    @Ignore // TODO fix me
    public void testCredentialHandling()
        throws Exception
    {
        // prepare test env
        String sUser     = "a_user"    ;
        String sPassword = "a_password";
        
        ArgumentCaptor< String > aUserCaptor     = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor< String > aPasswordCaptor = ArgumentCaptor.forClass(String.class);
        
        // code under test
        DBCreator aCreator = new DBCreator ();
        aCreator.setAdminCredentials(sUser, sPassword);
        aCreator.doOperation(DBCreator.EOperation.E_CREATE);
        
        // check the results
        Mockito.verify(m_aPersistenceUnitMock, Mockito.times(1)).setUser(aUserCaptor.capture());
        Assert.assertEquals("testCredentialHandling [01] check if user was set on persistence unit.", sUser, aUserCaptor.getValue());
        
        Mockito.verify(m_aPersistenceUnitMock, Mockito.times(1)).setPassword(aPasswordCaptor.capture());
        Assert.assertEquals("testCredentialHandling [02] check if password was set on persistence unit.", sUser, aUserCaptor.getValue());
    }
    
    //-------------------------------------------------------------------------
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Ignore // TODO fix me
    public void testSchemaCreation()
        throws Exception
    {
        // prepare test env
        ArgumentCaptor< Class > aCaptor = ArgumentCaptor.forClass(Class.class);
        
        // code under test
        DBCreator aCreator = new DBCreator ();
        aCreator.setAdminCredentials("root", "xxx");
        aCreator.doOperation(DBCreator.EOperation.E_CREATE);
        
        // check the results
        Mockito.verify(m_iDBMock).createEntitySchema(aCaptor.capture());
        Assert.assertEquals("testSchemaCreation [01] check if schema will be created for right entity", TestEntity.class, aCaptor.getValue());
    }
    
    //-------------------------------------------------------------------------
    private IDBPool m_iDBPoolMock = null;
    
    //-------------------------------------------------------------------------
    private IDBSchema m_iDBMock = null;
    
    //-------------------------------------------------------------------------
    private PersistenceUnit m_aPersistenceUnitMock = null;
}
