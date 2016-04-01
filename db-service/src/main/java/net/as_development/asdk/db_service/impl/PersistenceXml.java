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

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

//==============================================================================
/** provides read access to persistence.xml which must be part of any
 *  package dealing with JPA.
 */
public class PersistenceXml
{
    //--------------------------------------------------------------------------
    /// path to the persistence.xml file inside a Java package.
    private static final String DEFAULT_PERSISTENCE_XML = "/META-INF/persistence.xml";

    /// XML tag name of persistence unit entries.
    private static final String XML_TAG_PERSISTENCE_UNIT = "persistence-unit";
    
    /// XML tag name of provider entry
    private static final String XML_TAG_PROVIDER = "provider";

    /// XML tag name of class (entity) entry
    private static final String XML_TAG_ENTITY_CLASS = "class";

    /// XML tag name of properties entry
    private static final String XML_TAG_PROPERTIES = "properties";

    /// XML tag name of property entry
    private static final String XML_TAG_PROPERTY = "property";

    /// XML tag name of mapping-file entry
    private static final String XML_TAG_MAPPING_FILE = "mapping-file";

    /// "name" attribute of an XML tag.
    private static final String XML_ATTRIBUTE_NAME = "name";

    /// "value" attribute of an XML tag.
    private static final String XML_ATTRIBUTE_VALUE = "value";

    //--------------------------------------------------------------------------
    /// default ctor doing nothing special
    public PersistenceXml ()
    {}

    //--------------------------------------------------------------------------
    /** @return a list of all persistence units retrieved from a hopefully found
     *          /META-INF/persistence.xml file.
     *          
     *  Such list wont be null - but can be empty.
     */
    public static List< String > getListOfUnits ()
        throws Exception
    {
        List< String > lUnits          = new Vector< String >(10);
        InputStream    aPersistenceXml = null;
        
        try
        {
            aPersistenceXml = PersistenceXml.class.getResourceAsStream(PersistenceXml.PERSISTENCE_XML_LOCATION);
            if (aPersistenceXml == null)
                throw new IOException ("Could not locate '"+PersistenceXml.DEFAULT_PERSISTENCE_XML+"' resource.");
    
            DocumentBuilderFactory aDomBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder        aDomBuilder        = aDomBuilderFactory.newDocumentBuilder();
            Document               aDom               = aDomBuilder.parse(aPersistenceXml);
            NodeList               lXmlUnits          = aDom.getElementsByTagName(PersistenceXml.XML_TAG_PERSISTENCE_UNIT);
            
            int c = lXmlUnits.getLength();
            int i = 0;
            for (i=0; i<c; ++i)
            {
                Node   aXmlUnit  = lXmlUnits.item(i);
                Node   aNameAttr = aXmlUnit.getAttributes().getNamedItem(PersistenceXml.XML_ATTRIBUTE_NAME);
                String sUnitName = aNameAttr.getNodeValue();
                
                if (StringUtils.isEmpty(sUnitName))
                    throw new IOException ("Wrong formated persistenc.exml detected. Miss name of persistence unit.");
                
                lUnits.add(sUnitName);
            }
        }
        finally
        {
            if (aPersistenceXml != null)
                aPersistenceXml.close ();
        }
        
        return lUnits;
    }
    
    //--------------------------------------------------------------------------
    /** read persistence.xml into given set of properties.
     *
     *  Note   Set of given properties wont be cleaned up before.
     *          But existing properties will be overriden.
     *
     *  @param  aUnit [IN]
     *          name of persistence unit where all config settings
     *          should be read for.
     *
     *  @throws Exception in case configuration seems to be damaged
     *          but no exception if config will be empty.
     */
    public static synchronized void readXml (PersistenceUnit aUnit)
        throws Exception
    {
        InputStream aPersistenceXml = null;
        try
        {
	        aPersistenceXml = PersistenceXml.class.getResourceAsStream(PersistenceXml.PERSISTENCE_XML_LOCATION);
	        if (aPersistenceXml == null)
	            throw new IOException ("Could not locate '"+PersistenceXml.DEFAULT_PERSISTENCE_XML+"' resource.");
	
	        DocumentBuilderFactory aDomBuilderFactory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder        aDomBuilder        = aDomBuilderFactory.newDocumentBuilder();
	        Document               aDom               = aDomBuilder.parse(aPersistenceXml);
	        NodeList               lXmlUnits          = aDom.getElementsByTagName(PersistenceXml.XML_TAG_PERSISTENCE_UNIT);
	        Node                   aXmlUnit           = PersistenceXml.impl_getPersistenceUnitNodeByName (lXmlUnits, aUnit.getName());
	
	        if (aXmlUnit == null)
	            throw new IOException ("Could not locate persistence unit '"+aUnit.getName()+"' inside persistence.xml resource.");
	
	        PersistenceXml.impl_readPersistenceUnit (aXmlUnit, aUnit);
	    }
	    finally
	    {
	    	if (aPersistenceXml != null)
	    		aPersistenceXml.close ();
	    }
    }

    //--------------------------------------------------------------------------
    /** @return Xml representation of given persistence unit.
     *
     *  @param  aUnit
     *          persistence unit to be transformed to Xml.
     */
    public static String generateXml (PersistenceUnit aUnit)
        throws Exception
    {
        StringBuffer sXml = new StringBuffer (256);

        // <persistence-unit name="" ...>
        sXml.append ("<"                                        );
        sXml.append (PersistenceXml.XML_TAG_PERSISTENCE_UNIT );
        sXml.append (" "                                        );
        sXml.append (PersistenceXml.XML_ATTRIBUTE_NAME       );
        sXml.append ("=\""                                      );
        sXml.append (aUnit.getName()                            );
        sXml.append ("\">\n"                                    );

        // <provider>...</provider>
        sXml.append ("\t");
        PersistenceXml.impl_printXmlTag (sXml, PersistenceXml.XML_TAG_PROVIDER, aUnit.getProvider());
        sXml.append ("\n");

        // <class>...</class>
        Iterator< String > pEntities = aUnit.getEntities().iterator();
        while (pEntities.hasNext ())
        {
            String sEntity = pEntities.next();
            sXml.append ("\t");
            PersistenceXml.impl_printXmlTag (sXml, PersistenceXml.XML_TAG_ENTITY_CLASS, sEntity);
            sXml.append ("\n");
        }

        // <properties>
        sXml.append ("\t<"                               );
        sXml.append (PersistenceXml.XML_TAG_PROPERTIES);
        sXml.append (">\n"                               );

        // <property name="..." ... />
        Iterator< String > pProps = aUnit.getPropertNames().iterator();
        while (pProps.hasNext())
        {
            String sProp  = pProps.next();
            String sValue = aUnit.getProperty(sProp);

            sXml.append ("\t\t<"                              );
            sXml.append (PersistenceXml.XML_TAG_PROPERTY   );
            sXml.append (" "                                  );
            sXml.append (PersistenceXml.XML_ATTRIBUTE_NAME );
            sXml.append ("=\""                                );
            sXml.append (sProp                                );
            sXml.append ("\" "                                );
            sXml.append (PersistenceXml.XML_ATTRIBUTE_VALUE);
            sXml.append ("=\""                                );
            sXml.append (sValue                               );
            sXml.append ("\" />\n"                            );
        }

        // </properties>
        sXml.append ("\t</"                              );
        sXml.append (PersistenceXml.XML_TAG_PROPERTIES);
        sXml.append (">\n"                               );

        // </persistence-unit>
        sXml.append ("</"                                       );
        sXml.append (PersistenceXml.XML_TAG_PERSISTENCE_UNIT );
        sXml.append (">\n"                                      );

        return sXml.toString ();
    }

    //--------------------------------------------------------------------------
    private static void impl_printXmlTag (StringBuffer sXml  ,
                                          String       sTag  ,
                                          String       sValue)
    {
        sXml.append ("<"    );
        sXml.append (sTag   );
        sXml.append (">"    );
        sXml.append (sValue );
        sXml.append ("</"   );
        sXml.append (sTag   );
        sXml.append (">"    );
    }

    //--------------------------------------------------------------------------
    /** @return Dom node representing requested persistence unit.
     *          Return can be null in case persistence unit does not exists.
     *
     *  @param  lNodes
     *          list of all Dom nodes where each of them represent
     *          one persistence unit.
     *
     *  @param  sUnitName
     *          requested persistence unit.
     */
    private static Node impl_getPersistenceUnitNodeByName (NodeList lNodes   ,
                                                           String   sUnitName)
        throws Exception
    {
        int c = lNodes.getLength();
        int i = 0;

        for (i=0; i<c; ++i)
        {
            Node   aCheck    = lNodes.item(i);
            Node   aNameAttr = aCheck.getAttributes().getNamedItem(PersistenceXml.XML_ATTRIBUTE_NAME);
            String sCheck    = aNameAttr.getNodeValue();
            if (StringUtils.equals(sCheck, sUnitName))
                return aCheck;
        }

        return null;
    }

    //--------------------------------------------------------------------------
    /** read unit configuration and fill given unit object.
     *
     *  @param  aConfig
     *          root Dom node where the requested persistence unit
     *          exists inside Xml tree.
     *
     *  @param  aUnit
     *          the unit object to be filled with all data read
     *          from that config.
     *
     *  @throws Exception in case configuration is wrong.
     */
    private static void impl_readPersistenceUnit (Node            aConfig,
                                                  PersistenceUnit aUnit  )
        throws Exception
    {
        NodeList lChilds = aConfig.getChildNodes();
        int      c       = lChilds.getLength();
        int      i       = 0;

        for (i=0; i<c; ++i)
        {
            Node   aChild  = lChilds.item(i);
            String sXmlTag = aChild.getNodeName();

            if (sXmlTag.equals(PersistenceXml.XML_TAG_PROVIDER))
                aUnit.setProvider(aChild.getFirstChild().getNodeValue());
            else
            if (sXmlTag.equals(PersistenceXml.XML_TAG_MAPPING_FILE))
                aUnit.setMappFile(aChild.getFirstChild().getNodeValue());
            else
            if (sXmlTag.equals(PersistenceXml.XML_TAG_ENTITY_CLASS))
                aUnit.addEntity(aChild.getFirstChild().getNodeValue());
            else
            if (sXmlTag.equals(PersistenceXml.XML_TAG_PROPERTIES))
                PersistenceXml.impl_readProperties (aChild, aUnit);
        }
    }
    
    //--------------------------------------------------------------------------
    /** read set of properties of persistence unit.
     *
     *  @param  aConfig
     *          Dom node for "properties" where all properties
     *          are available.
     *
     *  @param  aUnit
     *          persistence unit object  where those properties has to be set.
     *
     *  @throws Exception if some properties couldnt be read.
     */
    private static void impl_readProperties (Node            aConfig,
                                             PersistenceUnit aUnit  )
        throws Exception
    {
        NodeList lChilds = aConfig.getChildNodes();
        int      c       = lChilds.getLength();
        int      i       = 0;

        for (i=0; i<c; ++i)
        {
            Node   aChild  = lChilds.item(i);
            String sXmlTag = aChild.getNodeName();

            if (sXmlTag.equals(PersistenceXml.XML_TAG_PROPERTY))
            {
                NamedNodeMap lAttributes = aChild.getAttributes();
                String       sProperty   = lAttributes.getNamedItem(PersistenceXml.XML_ATTRIBUTE_NAME ).getNodeValue();
                String       sValue      = lAttributes.getNamedItem(PersistenceXml.XML_ATTRIBUTE_VALUE).getNodeValue();

                aUnit.setProperty(sProperty, sValue);
            }
        }
    }

    //--------------------------------------------------------------------------
    public static final String PERSISTENCE_XML_LOCATION = PersistenceXml.DEFAULT_PERSISTENCE_XML;
}
