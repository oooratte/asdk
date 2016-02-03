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
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

//==============================================================================
/**
 * @todo document me
 */
public class OrmXml
{
    //--------------------------------------------------------------------------
    /// path to the persistence.xml file inside a Java package.
    private static final String DEFAULT_ORM_XML = "/META-INF/orm.xml";

    /// XML tag name of an entity
    private static final String XML_TAG_ENTITY = "entity";

    /// XML tag name of a table mapping
    private static final String XML_TAG_TABLE = "table";

    /// XML tag name of a set of attribute mappings
    private static final String XML_TAG_ATTRIBUTES = "attributes";

    /// XML tag name of property "id"
    private static final String XML_TAG_ID = "id";

    /// XML tag name of property "generated-value"
    //private static final String XML_TAG_GENERATED_VALUE = "generated-value";

    /// XML tag name of property "basic"
    private static final String XML_TAG_BASIC = "basic";

    /// XML tag name of property "column"
    private static final String XML_TAG_COLUMN = "column";

    /// "class" attribute of an XML tag.
    private static final String XML_ATTRIBUTE_CLASS = "class";

    /// "name" attribute of an XML tag.
    private static final String XML_ATTRIBUTE_NAME = "name";

    /// "length" attribute of an XML tag.
    private static final String XML_ATTRIBUTE_LENGTH = "length";

    /// "strategy" attribute of an XML tag
    //private static final String XML_TAG_STRATEGY = "strategy";

    //--------------------------------------------------------------------------
    /** create new instance.
     */
    public OrmXml ()
    {}

    //--------------------------------------------------------------------------
    /** read orm.xml into given descriptor.
     *
     *  @note   Such descriptor wont be cleaned before used!
     *          But existing properties will be overriden.
     *
     *  @param  aEntity [OUT]
     *          after reading of orm.xml it contains all
     *          properties from that place.
     *
     *  @throws an exception in case configuration seems to be damaged
     *          but no exception if config will be empty.
     */
    public void readXml (String                 sPathToOrmXml,
                         List< EntityMetaInfo > lMetaInfo    )
        throws Exception
    {
        InputStream aOrmXml = null;
        try
        {
	        if ( ! StringUtils.isEmpty(sPathToOrmXml))
	            aOrmXml = OrmXml.class.getResourceAsStream(sPathToOrmXml);
	        else
	            aOrmXml = OrmXml.class.getResourceAsStream(OrmXml.DEFAULT_ORM_XML);
        
	        if (aOrmXml == null)
	            throw new IOException ("Could not locate 'orm.xml' resource.");
	
	        DocumentBuilderFactory aDomBuilderFactory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder        aDomBuilder        = aDomBuilderFactory.newDocumentBuilder();
	        Document               aDom               = aDomBuilder.parse(aOrmXml);
	        NodeList               lEntityNodes       = aDom.getElementsByTagName(OrmXml.XML_TAG_ENTITY);
	
	        if (lEntityNodes == null)
	            throw new IOException ("Could not locate any entity inside 'orm.xml' resource.");
	
	        int c = lEntityNodes.getLength();
	        int i = 0;
	        for (i=0; i<c; ++i)
	        {
	            Node           aEntityNode = lEntityNodes.item(i);
	            EntityMetaInfo aEntity     = impl_readEntity (aEntityNode);
	            lMetaInfo.add(aEntity);
	        }
        }
        finally
        {
        	if (aOrmXml != null)
        		aOrmXml.close ();
        }
    }

    //--------------------------------------------------------------------------
    private EntityMetaInfo impl_readEntity (Node aEntityNode)
        throws Exception
    {
        EntityMetaInfo aEntity = new EntityMetaInfo ();
        String           sEntity = OrmXml.impl_getValueOfNodeAttribute(aEntityNode, OrmXml.XML_ATTRIBUTE_NAME );
        String           sClass  = OrmXml.impl_getValueOfNodeAttribute(aEntityNode, OrmXml.XML_ATTRIBUTE_CLASS);

        aEntity.setName      (sEntity);
        aEntity.setClassName (sClass );

        NodeList lChildNodes = aEntityNode.getChildNodes();
        int      c           = lChildNodes.getLength();
        int      i           = 0;

        for (i=0; i<c; ++i)
        {
            Node   aChildNode = lChildNodes.item(i);
            String sXmlTag    = aChildNode.getNodeName();

            if (sXmlTag.equals(OrmXml.XML_TAG_TABLE))
                aEntity.setTable(OrmXml.impl_getValueOfNodeAttribute(aChildNode, OrmXml.XML_ATTRIBUTE_NAME));
            else
            if (sXmlTag.equals(OrmXml.XML_TAG_ATTRIBUTES))
                impl_readAttributes (aChildNode, aEntity);
        }

        return aEntity;
    }

    //--------------------------------------------------------------------------
    private void impl_readAttributes (Node             aNode  ,
                                      EntityMetaInfo aEntity)
        throws Exception
    {
        AttributeListMetaInfo lAttributes = new AttributeListMetaInfo ();
        NodeList              lChildNodes = aNode.getChildNodes();
        int                   c           = lChildNodes.getLength();
        int                   i           = 0;

        aEntity.setAttributes(lAttributes);

        for (i=0; i<c; ++i)
        {
            Node   aChildNode = lChildNodes.item(i);
            String sXmlTag    = aChildNode.getNodeName();

            if (sXmlTag.equals(OrmXml.XML_TAG_ID))
                impl_readId (aChildNode, aEntity, lAttributes);
            else
            if (sXmlTag.equals(OrmXml.XML_TAG_BASIC))
                impl_readBasic (aChildNode, lAttributes);
        }
    }

    //--------------------------------------------------------------------------
    private void impl_readId (Node                aNode      ,
                              EntityMetaInfo    aEntity    ,
                              AttributeListMetaInfo lAttributes)
        throws Exception
    {
        String            sName      = OrmXml.impl_getValueOfNodeAttribute(aNode, OrmXml.XML_ATTRIBUTE_NAME);
        AttributeMetaInfo aAttribute = new AttributeMetaInfo ();

        aAttribute.setApiName (sName);
        aEntity.setIdAttribute(sName);
        lAttributes.put(sName, aAttribute);

        impl_processCommonAttributeChildNodes (aNode, aAttribute);
    }

    //--------------------------------------------------------------------------
    private void impl_readBasic (Node                aNode      ,
                                 AttributeListMetaInfo lAttributes)
        throws Exception
    {
        String            sName      = OrmXml.impl_getValueOfNodeAttribute(aNode, OrmXml.XML_ATTRIBUTE_NAME);
        AttributeMetaInfo aAttribute = new AttributeMetaInfo ();

        aAttribute.setApiName (sName);
        lAttributes.put       (sName, aAttribute);

        impl_processCommonAttributeChildNodes (aNode, aAttribute);
    }

    //--------------------------------------------------------------------------
    private void impl_processCommonAttributeChildNodes (Node            aNode     ,
                                                        AttributeMetaInfo aAttribute)
        throws Exception
    {
        NodeList lChildNodes = aNode.getChildNodes();
        int      c           = lChildNodes.getLength();
        int      i           = 0;

        for (i=0; i<c; ++i)
        {
            Node   aChildNode = lChildNodes.item(i);
            String sXmlTag    = aChildNode.getNodeName();

            if (sXmlTag.equals(OrmXml.XML_TAG_COLUMN))
                impl_readColumn (aChildNode, aAttribute);
        }
    }

    //--------------------------------------------------------------------------
    private void impl_readColumn (Node            aNode     ,
                                  AttributeMetaInfo aAttribute)
        throws Exception
    {
        // optional (attribute name is used as fallback)
        String sColumn = OrmXml.impl_getValueOfNodeAttribute(aNode, OrmXml.XML_ATTRIBUTE_NAME);
        if ( ! StringUtils.isEmpty(sColumn))
            aAttribute.setColumnName(sColumn);

        // optional ! (because it's for string types only)
        String sLength = OrmXml.impl_getValueOfNodeAttribute(aNode, OrmXml.XML_ATTRIBUTE_LENGTH);
        if ( ! StringUtils.isEmpty(sLength))
            aAttribute.setLength(Integer.valueOf(sLength));
    }

    //--------------------------------------------------------------------------
    private static String impl_getValueOfNodeAttribute (Node   aNode     ,
                                                        String sAttribute)
        throws Exception
    {
        NamedNodeMap lAttributes = aNode.getAttributes();
        Node         aAttribute  = lAttributes.getNamedItem(sAttribute);

        if (aAttribute != null)
            return aAttribute.getNodeValue();

        return null;
    }
}
