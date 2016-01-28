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

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.lang3.StringUtils;

import net.as_development.asdk.api.db.IPersistenceUnit;

//==============================================================================
/** knows all informations regarding a persistence unit.
 */
public class PersistenceUnit implements IPersistenceUnit
{
    //--------------------------------------------------------------------------
    /// default ctor doing nothing special.
    public PersistenceUnit ()
    {}

    //--------------------------------------------------------------------------
    public PersistenceUnit (IPersistenceUnit iSource)
    	throws Exception
    {
    	// ????
    	if (iSource == null)
    		return;
    	
    	if ( ! (iSource instanceof PersistenceUnit))
    		return; // @todo implement me .-)
    	
    	PersistenceUnit aSource = (PersistenceUnit)iSource;
    	m_sName          = aSource.m_sName;
    	m_sProviderClass = aSource.m_sProviderClass;
    	m_lEntities      = aSource.m_lEntities;
    	m_lProps         = aSource.m_lProps;
    	m_sMappFile      = aSource.m_sMappFile;
    }
    
    //--------------------------------------------------------------------------
    public void merge (IPersistenceUnit iPU)
    	throws Exception
    {
    	if (iPU == null)
    		return;
    	
    	for (String sProp : iPU.getPropertNames())
    	{
    		String sValue = iPU.getProperty(sProp);
    		setProperty(sProp, sValue);
    	}
    }
    
    //--------------------------------------------------------------------------
    /** load the specified persistence unit from the (hopefully existing)
     *  persistence.xml ... create a suitable persistence unit object and return
     *  those object.
     *
     *  @param	sName [IN]
     *  		name of the persistence unit to be loaded here.
     *
     *  @throws	an exception if specified persistence unit does not exists
     *          or couldn't be loaded successfully.
     */
    public static PersistenceUnit loadUnit(String sName)
    	throws Exception
    {
    	PersistenceUnit aUnit = new PersistenceUnit ();
    	aUnit.setName(sName);
    	PersistenceXml.readXml(aUnit);
    	return aUnit;
    }

    //--------------------------------------------------------------------------
    /** @return a list of all persistence units retrieved from a hopefully found
     *          /META-INF/persistence.xml file.
     *          
     *  Such list wont be null - but can be empty.
     */
    public static List< String > listUnits ()
        throws Exception
    {
        return PersistenceXml.getListOfUnits();
    }
    
    //--------------------------------------------------------------------------
    /** set name for these persistence unit.
     *
     *  @param  sName [IN]
     *          new name for these unit.
     *          Must not be empty or null !
     *
     *  @throws an exception in case unit name is invalid.
     */
    public  void setName (String sName)
        throws Exception
    {
        if (StringUtils.isEmpty (sName))
            throw new IllegalArgumentException ("Empty name not allowed as new persistence unit name.");

        m_sName = sName;
    }

    //--------------------------------------------------------------------------
    /** @return current name of these persistence unit.
     */
    public  String getName ()
        throws Exception
    {
        return m_sName;
    }

    //--------------------------------------------------------------------------
    /** set class name of provider implementation.
     *
     *  @param  sProvider
     *          class name of provider impl.
     */
    public  void setProvider (String sProvider)
        throws Exception
    {
        m_sProviderClass = sProvider;
    }

    //--------------------------------------------------------------------------
    /** @return class name of provider implementation.
     */
    public  String getProvider ()
        throws Exception
    {
        return m_sProviderClass;
    }

    //--------------------------------------------------------------------------
    public void setMappFile (String sFile)
        throws Exception
    {
        m_sMappFile = sFile;
    }

    //--------------------------------------------------------------------------
    public String getMappFile ()
        throws Exception
    {
        return m_sMappFile;
    }

    //--------------------------------------------------------------------------
    public void setUser (String sUser)
        throws Exception
    {
        mem_Props ().put(PersistenceUnitConst.DB_USER, sUser);
    }
    
    //--------------------------------------------------------------------------
    public void setPassword (String sPassword)
        throws Exception
    {
        mem_Props ().put(PersistenceUnitConst.DB_PASSWORD, sPassword);
    }
    
    //--------------------------------------------------------------------------
    public String getUser ()
        throws Exception
    {
        return mem_Props ().get(PersistenceUnitConst.DB_USER);
    }
    
    //--------------------------------------------------------------------------
    public String getPassword ()
        throws Exception
    {
        return mem_Props ().get(PersistenceUnitConst.DB_PASSWORD);
    }
    
    //--------------------------------------------------------------------------
    /** add class name of an entity to these unit.
     *
     *  @note   Duplicate entries will be filtered out.
     *          Further invalid entities (empty name) will
     *          be silently ignored.
     *
     *  @param  sEntity
     *          class name of a new entity.
     */
    public void addEntity (String sEntity)
        throws Exception
    {
        if (StringUtils.isEmpty(sEntity))
            return;

        List< String > lEntities = mem_Entities ();
        if ( ! lEntities.contains(sEntity))
            lEntities.add(sEntity);
    }

    //--------------------------------------------------------------------------
    /** @return list of all entities registered for these unit.
     *
     *  @note   returned list wont be NULL ... but it can be empty.
     */
    public List< String > getEntities ()
        throws Exception
    {
        return mem_Entities ();
    }

    //--------------------------------------------------------------------------
    /** set new property for these unit.
     *
     *  @param  sProperty
     *
     *  @param  sValue
     */
    public void setProperty (String sProperty,
                             String sValue   )
        throws Exception
    {
        if (StringUtils.isEmpty(sProperty))
            return;

        HashMap< String, String > lProps = mem_Props ();
        lProps.put(sProperty, sValue);
    }

    //--------------------------------------------------------------------------
    /** @return value of requested property.
     *
     *  @note   if property is unknown an empty string will be returned.
     */
    public String getProperty (String sProperty)
        throws Exception
    {
        if (StringUtils.isEmpty(sProperty))
            return "";

        return mem_Props ().get(sProperty);
    }

    //--------------------------------------------------------------------------
    public boolean getBooleanProperty (String sProperty)
    	throws Exception
    {
        String sValue = getProperty (sProperty);
        return Boolean.parseBoolean(sValue);
    }

    //--------------------------------------------------------------------------
    public boolean getBooleanProperty (String  sProperty,
    								   boolean bDefault )
    	throws Exception
    {
        try
        {
            String sValue = getProperty (sProperty);
        	return Boolean.parseBoolean(sValue);
        }
        catch (Throwable exIgnore)
        {}
        
        return bDefault;
    }
    
    //--------------------------------------------------------------------------
    /** @return set of all property names.
     *
     *  @note   set wont be null ... but can be empty.
     */
    public Set< String > getPropertNames ()
        throws Exception
    {
        return mem_Props ().keySet();
    }

    //--------------------------------------------------------------------------
    @Override
    public  String toString()
    {
        try
        {
            return PersistenceXml.generateXml(this);
        }
        catch(Throwable ex)
        {}

        return super.toString();
    }

    //--------------------------------------------------------------------------
    private String m_sName = null;

    //--------------------------------------------------------------------------
    private String m_sProviderClass = null;

    //--------------------------------------------------------------------------
    private String m_sMappFile = null;

    //--------------------------------------------------------------------------
    private List< String > m_lEntities = null;
    private List< String > mem_Entities ()
    {
        if (m_lEntities == null)
            m_lEntities = new Vector< String >(10);
        return m_lEntities;
    }

    //--------------------------------------------------------------------------
    private HashMap< String, String > m_lProps = null;
    private HashMap< String, String > mem_Props ()
    {
        if (m_lProps == null)
            m_lProps = new HashMap< String, String >(10);
        return m_lProps;
    }
}
