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
package net.as_development.asdk.db_service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

//==============================================================================
/**
 * TODO document me
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
     *  @throws Exception in case persistence unit couldnt be created
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
            aEntity.setSchema(aPersistenceUnit.getStringProperty(PersistenceUnitConst.DB_SCHEMA));
            
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
        
        sValue = aUnit.getStringProperty(PersistenceUnitConst.CONSTRAINT_MAX_IDENTIFIER_LENGTH);
        if ( ! StringUtils.isEmpty(sValue))
        {
            // we can expected it's a valid integer (if it's configured)
            int nMaxLength = Integer.parseInt(sValue);
            impl_checkMaxIdentiferLengthConstraint (aMeta, nMaxLength);
        }
        
        sValue = aUnit.getStringProperty(PersistenceUnitConst.CONSTRAINT_MAX_STRING_LENGTH);
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
