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
package net.as_development.asdk.db_service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

//==============================================================================
/**
 * @todo document me
 */
public class EntityMetaInfoProvider
{
    //--------------------------------------------------------------------------
    /** create new instance.
     */
    public EntityMetaInfoProvider ()
    {}

    //--------------------------------------------------------------------------
    public void setPersistenceUnit (PersistenceUnit aUnit)
        throws Exception
    {
        m_aPersistenceUnit = aUnit;
    }

    //--------------------------------------------------------------------------
    /** provides access to member m_aPersistenceUnit.
     *  If those member was not initialized or "cleared" before ...
     *  we create a new one. Those instance will be created and(!) initialized.
     *  So we can make sure a valid unit will be returned where even it's
     *  configuration seems to be valid.
     *
     *  @return the current bound (and initialize) persistence unit.
     *
     *  @throws an exception in case persistence unit couldnt be created
     *          successfull ... e.g. because corresponding configuration
     *          seems to be invalid.
     */
    public PersistenceUnit getPersistenceUnit ()
        throws Exception
    {
        return m_aPersistenceUnit;
    }

    //--------------------------------------------------------------------------
    public String getPersistenceUnitName ()
        throws Exception
    {
        return m_aPersistenceUnit.getName();
    }

    //--------------------------------------------------------------------------
    public void retrieveMetaInfo ()
        throws Exception
    {
        if (m_lEntities != null)
            return;

        PersistenceUnit        aPersistenceUnit = getPersistenceUnit ();
        String                 sMappingFile     = aPersistenceUnit.getMappFile();
        List< EntityMetaInfo > lEntities        = new ArrayList< EntityMetaInfo >(10);

        boolean bParseSimple = false;
        //boolean bParseJpa    = false;
        //boolean bParseJdo    = false;

        // a) prefer mapping file
        if ( ! StringUtils.isEmpty(sMappingFile))
        {
            OrmXml aXml = new OrmXml ();
            aXml.readXml(sMappingFile, lEntities);

            bParseSimple = true;
        }
        // b) try configured set of classes inside persistence.xml then
        else
        {
            Iterator< String > pEntityClasses = aPersistenceUnit.getEntities().iterator();
            while (pEntityClasses.hasNext())
            {
                String         sClass = pEntityClasses.next();
                EntityMetaInfo aInfo  = new EntityMetaInfo ();
                aInfo.setClassName(sClass);
                lEntities.add (aInfo);
            }
        }

        if (lEntities.size() < 1)
            throw new RuntimeException ("No entites configured/defined. Do you realy think that will be usefull ?");

        Iterator< EntityMetaInfo >    pEntities  = lEntities.iterator();
        Map< String, EntityMetaInfo > lEntityMap = mem_Entities ();

        while (pEntities.hasNext())
        {
            EntityMetaInfo aEntity = pEntities.next();

            // bind generic properties to entity
            aEntity.setSchema(aPersistenceUnit.getProperty(PersistenceUnitConst.DB_SCHEMA));
            
            // bind special properties to entity
            if (bParseSimple)
                EntityClassParserSimple.parse (aEntity);
            else
                EntityClassParserAnnotations.parse (aEntity);
            
            // Throws exceptions in case entity was configured wrong.
            aEntity.verify();
            
            // check (might configured) constraints
            impl_checkPersistenceUnitConstraints (aEntity, m_aPersistenceUnit);
            
            lEntityMap.put(aEntity.getClassName(), aEntity);
        }
    }

    //--------------------------------------------------------------------------
    public EntityMetaInfo getMetaInforForEntity (Object aEntity)
        throws Exception
    {
        return getMetaInfoForEntityClass(aEntity.getClass());
    }

    //--------------------------------------------------------------------------
	public EntityMetaInfo getMetaInfoForEntityClass (Class< ? > aClass)
        throws Exception
    {
        String sClass = aClass.getName();
        return mem_Entities ().get(sClass);
    }

    //--------------------------------------------------------------------------
    @Override
    public String toString ()
    {
        StringBuffer sString = new StringBuffer (256);

        sString.append (super.toString ()+"\n");
        sString.append (m_lEntities);

        return sString.toString ();
    }

    //--------------------------------------------------------------------------
    private void impl_checkPersistenceUnitConstraints (EntityMetaInfo  aMeta,
                                                       PersistenceUnit aUnit)
        throws Exception
    {
        String sValue = null;
        
        sValue = aUnit.getProperty(PersistenceUnitConst.CONSTRAINT_MAX_IDENTIFIER_LENGTH);
        if ( ! StringUtils.isEmpty(sValue))
        {
            // we can expected it's a valid integer (if it's configured)
            int nMaxLength = Integer.parseInt(sValue);
            impl_checkMaxIdentiferLengthConstraint (aMeta, nMaxLength);
        }
        
        sValue = aUnit.getProperty(PersistenceUnitConst.CONSTRAINT_MAX_STRING_LENGTH);
        if ( ! StringUtils.isEmpty(sValue))
        {
            // we can expected it's a valid integer (if it's configured)
            int nMaxLength = Integer.parseInt(sValue);
            impl_checkMaxStringLengthConstraint (aMeta, nMaxLength);
        }
        
    }
    
    //--------------------------------------------------------------------------
    private void impl_checkMaxIdentiferLengthConstraint (EntityMetaInfo aMeta     ,
                                                         int            nMaxLength)
        throws Exception
    {
        // check table identifier
        {
            String sIdentifier = aMeta.getTable();
            int    nLength     = sIdentifier.length();
            
            if (nLength > nMaxLength)
                throw new IllegalArgumentException ("Constraint violation: max identifier length exceeded. expected max = "+nMaxLength+" but found "+nLength+" for table name '"+sIdentifier+"'.");
        }
        
        // check attribute identifier
        AttributeListMetaInfo         lAttributes = aMeta.getAttributes();
        Iterator< AttributeMetaInfo > pAttributes = lAttributes.iterator();
        while (pAttributes.hasNext())
        {
            AttributeMetaInfo aAttribute  = pAttributes.next ();
            String            sIdentifier = aAttribute.getColumnName();
            int               nLength     = sIdentifier.length();
            
            if (nLength > nMaxLength)
                throw new IllegalArgumentException ("Constraint violation: max identifier length excedded. expected max = "+nMaxLength+" but found "+nLength+" for attribute '"+sIdentifier+"'.");
        }
    }
    
    //--------------------------------------------------------------------------
    private void impl_checkMaxStringLengthConstraint (EntityMetaInfo aMeta     ,
                                                      int            nMaxLength)
        throws Exception
    {
        AttributeListMetaInfo         lAttributes = aMeta.getAttributes();
        Iterator< AttributeMetaInfo > pAttributes = lAttributes.iterator();
        while (pAttributes.hasNext())
        {
            AttributeMetaInfo aAttribute  = pAttributes.next ();
            if ( ! aAttribute.getType().equals(String.class))
                continue;
            
            int nLength = aAttribute.getLength();
            if (nLength > nMaxLength)
                throw new IllegalArgumentException ("Constraint violation: max string length excedded. expected max = "+nMaxLength+" but found "+nLength+" for attribute '"+aAttribute.getColumnName()+"'.");
        }
    }
    
    //--------------------------------------------------------------------------
    private Map< String, EntityMetaInfo > mem_Entities ()
    {
        if (m_lEntities == null)
            m_lEntities = new HashMap< String, EntityMetaInfo > (10);
        return m_lEntities;
    }
    
    //--------------------------------------------------------------------------
    /** a cached persistence unit.
     */
    private PersistenceUnit m_aPersistenceUnit = null;

    //--------------------------------------------------------------------------
    private Map< String, EntityMetaInfo > m_lEntities = null;
}
