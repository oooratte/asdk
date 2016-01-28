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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import au.com.bytecode.opencsv.CSVReader;
import net.as_development.asdk.api.db.IDB;
import net.as_development.asdk.api.db.IDBPool;
import net.as_development.asdk.db_service.EntityBase;
import net.as_development.asdk.db_service.impl.PersistenceUnit;
import net.as_development.asdk.service.env.ServiceEnv;
import net.as_development.asdk.tools.reflection.ObjectManipulation;

//==============================================================================
/** Can be used to import/export DB dumps from/to CSV formated files.
 * 
 *  The format of such CSV file is a 'special' one .-)
 *  First line of those file will be used as header showing the columns
 *  to be addressed for the dump. Doing so you have to the possibility to
 *  dump data base tables partial.
 *
 *  An example file:
 *  <code>
 *  column1,column2,column7
 *  value11,value12,value17
 *  value21,value22,value27
 *  ...
 *  </code>
 */
public class CSVDump
{
    //--------------------------------------------------------------------------
    public static final char SEPARATOR = ',';
    
    //--------------------------------------------------------------------------
    public static final char QUOTE = '\'';
    
    //--------------------------------------------------------------------------
    public CSVDump ()
    {}

    //--------------------------------------------------------------------------
    /** set the entity class used for import/export here.
     * 
     *  One instance of this class can be used related to ONE data base table only.
     *  So this binding will be used for all further operations on this instance.
     *  
     *  @param  aEntityClass [IN]
     *          the entity class.
     */
    public void setEntityClass (Class< ? extends EntityBase > aEntityClass)
        throws Exception
    {
        m_aEntityClass = aEntityClass;
    }
    
    //--------------------------------------------------------------------------
    /** set the corresponding persistence unit where the also given
     *  entity class is used.
     *  
     *  @param  sPersistenceUnitName [IN]
     *          the name of the corresponding persistence unit. 
     */
    public void setPersistenceUnitName (String sPersistenceUnitName)
        throws Exception
    {
        m_sPersistenceUnitName = sPersistenceUnitName;
    }
    
    //--------------------------------------------------------------------------
    /** import the given CSV directly from a local file.
     * 
     *  The given string must be a valid path and file name.
     *  
     *  @param  sCSV [IN]
     *          valid path and file name to a CSV file on disc. 
     */
    public void importFromFile (String sCSV)
        throws Exception
    {
        InputStream aCSV = new FileInputStream (new File (sCSV));
        impl_import (aCSV);
    }
    
    //--------------------------------------------------------------------------
    /** import the given CSV directly from a JAR resource.
     * 
     *  The given string must be a valid resource path inside the
     *  class path.
     *  
     *  @param  sCSV [IN]
     *          valid path to a CSV resource inside the class path. 
     */
    public void importFromResource (String sCSV)
        throws Exception
    {
        InputStream aCSV = CSVDump.class.getResourceAsStream(sCSV);
        impl_import (aCSV);
    }

    //--------------------------------------------------------------------------
    /** import the CSV content from the given stream.
     * 
     *  @param  aCSV [IN]
     *          the CSV formated stream for import.
     */
    private void impl_import (InputStream aCSV)
        throws Exception
    {
        InputStreamReader      aReader    = new InputStreamReader (aCSV);
        CSVReader              aCSVReader = new CSVReader (aReader, CSVDump.SEPARATOR, CSVDump.QUOTE);
        String[]               aHeader    = aCSVReader.readNext();
        String[]               aLine      = null;
        Map< String, Integer > aMapping   = impl_getMapping (aHeader);
        IDB                    iDB        = mem_DB ();

        while ((aLine = aCSVReader.readNext()) != null)
        {
            EntityBase aEntity = impl_newEntity ();
            impl_mapLineToEntity (aLine, aMapping, aEntity);
            iDB.storeEntities(aEntity);
        }
        
        aCSVReader.close();
        aCSV.close();
    }
    
    //--------------------------------------------------------------------------
    /** map a given line read from the CSV file to the also given entity
     *  by setting member fields directly.
     *  
     *  @param  lLine [IN]
     *          the CSV line containing values.
     *          
     *  @param  lMapping [IN]
     *          can be used to map field names to line positions.
     *          
     *  @param  aEntity [IN, OUT]
     *          used to set values there.
     */
    private void impl_mapLineToEntity (String[]               lLine   ,
                                       Map< String, Integer > lMapping,
                                       EntityBase             aEntity )
        throws Exception
    {
        Iterator< Entry < String, Integer > > pMapping = lMapping.entrySet().iterator();
        while (pMapping.hasNext())
        {
            Entry< String, Integer > aMapping = pMapping.next();
            String sAttribute      = aMapping.getKey();
            int    nValuePosInLine = aMapping.getValue();
            String sValue          = impl_normalizeValue (lLine[nValuePosInLine]);

            ObjectManipulation.setFieldValue(aEntity, sAttribute, sValue);
        }
    }

    //--------------------------------------------------------------------------
    /** @return normalize a CVS value e.g. by stripping leading/trailing white spaces.
     * 
     *  @param  sValue [IN]
     *          the value to be normalized.
     */
    private String impl_normalizeValue(String sValue)
        throws Exception
    {
        if (StringUtils.isEmpty(sValue))
            return "";

        return StringUtils.strip(sValue);
    }

    //--------------------------------------------------------------------------
    /** @return a new instance of the bound entity class.
     */
    private EntityBase impl_newEntity ()
       throws Exception
    {
        EntityBase aEntity = (EntityBase) m_aEntityClass.newInstance ();
        return aEntity;
    }

    //--------------------------------------------------------------------------
    /** @return a map where entity attributes (member fields) are mapped
     *          to position numbers within any CSV line.
     *          
     *  @param  aHeader [IN]
     *          the CSV header where field names are listed in the right order.
     */
    private Map< String, Integer > impl_getMapping (String[] aHeader)
        throws Exception
    {
        Map< String, Integer > aMapping = new HashMap< String, Integer >(10);
        int                    nPos     = 0;

        for (String sAttribute : aHeader)
            aMapping.put (sAttribute, nPos++);

        return aMapping;
    }

    //--------------------------------------------------------------------------
    private IDB mem_DB ()
        throws Exception
    {
        if (m_iDB == null)
        {
            IDBPool         iPool = ServiceEnv.get().getService(IDBPool.class);
            PersistenceUnit aUnit = PersistenceUnit.loadUnit(m_sPersistenceUnitName);
            iPool.registerPersistenceUnit(aUnit);
            IDB             iDB   = iPool.getDbForPersistenceUnit(m_sPersistenceUnitName);
            m_iDB = iDB;
        }
        return m_iDB;
    }

    //--------------------------------------------------------------------------
    private String m_sPersistenceUnitName = null;

    //--------------------------------------------------------------------------
    private IDB m_iDB = null;

    //--------------------------------------------------------------------------
    private Class< ? extends EntityBase > m_aEntityClass = null;
}
