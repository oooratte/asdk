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
    		return; // TODO implement me .-)
    	
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
    		String sValue = iPU.getStringProperty(sProp);
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
     *  @throws	Exception if specified persistence unit does not exists
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
     *  @throws Exception in case unit name is invalid.
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
        return getStringProperty(PersistenceUnitConst.DB_USER);
    }
    
    //--------------------------------------------------------------------------
    public String getPassword ()
        throws Exception
    {
        return getStringProperty(PersistenceUnitConst.DB_PASSWORD);
    }

    //--------------------------------------------------------------------------
    public void setSchema (final String sSchema)
        throws Exception
    {
    	mem_Props ().put(PersistenceUnitConst.DB_SCHEMA, sSchema);
    }
    
    //--------------------------------------------------------------------------
    public String getSchema ()
        throws Exception
    {
    	return getStringProperty(PersistenceUnitConst.DB_SCHEMA);
    }

    //--------------------------------------------------------------------------
    public void setAdministrative (final boolean bAdministrative)
        throws Exception
    {
        mem_Props ().put(PersistenceUnitConst.FLAG_IS_ADMINISTRATIVE, Boolean.toString(bAdministrative));
    }

    //--------------------------------------------------------------------------
    public boolean isAdministrative ()
        throws Exception
    {
        try
        {
            final String  sValue = getStringProperty(PersistenceUnitConst.FLAG_IS_ADMINISTRATIVE);
        	final boolean bIs    = Boolean.parseBoolean(sValue);
        	return bIs;
        }
        catch (Throwable ex)
        {}
        
        return false;
    }

    //--------------------------------------------------------------------------
    /** add class name of an entity to these unit.
     *
     *  Note   Duplicate entries will be filtered out.
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
     *  Note   returned list wont be NULL ... but it can be empty.
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

        HashMap< String, Object > lProps = mem_Props ();
        lProps.put(sProperty, sValue);
    }

    //--------------------------------------------------------------------------
    public < T > void setProperty (String sProperty,
                                   T      aValue   )
        throws Exception
    {
        if (StringUtils.isEmpty(sProperty))
            return;

        HashMap< String, Object > lProps = mem_Props ();
        lProps.put(sProperty, aValue);
    }

    //--------------------------------------------------------------------------
    /** @return value of requested property.
     *
     *  Note   if property is unknown an empty string will be returned.
     */
    public String getStringProperty (String sProperty)
        throws Exception
    {
        if (StringUtils.isEmpty(sProperty))
            return "";

        return (String) mem_Props ().get(sProperty);
    }

    //--------------------------------------------------------------------------
    public boolean getBooleanProperty (String sProperty)
    	throws Exception
    {
        String sValue = getStringProperty (sProperty);
        return Boolean.parseBoolean(sValue);
    }

    //--------------------------------------------------------------------------
    public boolean getBooleanProperty (String  sProperty,
    								   boolean bDefault )
    	throws Exception
    {
        try
        {
            String sValue = getStringProperty (sProperty);
        	return Boolean.parseBoolean(sValue);
        }
        catch (Throwable exIgnore)
        {}
        
        return bDefault;
    }
    
    //--------------------------------------------------------------------------
    @SuppressWarnings("unchecked")
	@Override
    public < T > T getObjectProperty (String sProperty)
    	throws Exception
    {
    	Object aValue = mem_Props ().get(sProperty);
    	return (T) aValue;
    }

    //--------------------------------------------------------------------------
	@Override
    public boolean hasProperty (String sProperty)
    	throws Exception
    {
    	return mem_Props().containsKey(sProperty);
    }

    //--------------------------------------------------------------------------
    /** @return set of all property names.
     *
     *  Note   set wont be null ... but can be empty.
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
    private HashMap< String, Object > m_lProps = null;
    private HashMap< String, Object > mem_Props ()
    {
        if (m_lProps == null)
            m_lProps = new HashMap< String, Object >(10);
        return m_lProps;
    }
}
