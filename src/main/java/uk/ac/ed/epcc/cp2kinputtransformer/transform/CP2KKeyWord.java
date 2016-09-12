/*
 * Copyright (c) The University of Edinburgh, 2016.
 *
 * LICENCE-START
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * LICENCE-END
 */

package uk.ac.ed.epcc.cp2kinputtransformer.transform;

import org.apache.ws.commons.schema.XmlSchemaAnnotationItem;
import org.apache.ws.commons.schema.XmlSchemaAppInfo;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Class representing a CP2K keyword. A keyword has a name,
 * and possibly aliases, as defined in the CP2K schema. It has
 * a sanitised name which is used to name the keyword in an XML
 * compliant way in schema and XML representations of an input
 * file.
 *
 * @author Jeremy Nowell
 */
public class CP2KKeyWord {

    /** The XML Schema element object. */
    private XmlSchemaElement m_schemaElement;

    /** The sanitised name of the section as used in schema and XML. */
    private String m_sanitisedName;

    /** The true name as used in CP2K input files. */
    private String m_cp2kName;

    /** The list of aliases of this keyword. */
    private List<String> m_aliases;

    /** The name of the alias node in the schema. */
    private static final String ALIAS_LOCALNAME = "alias";

    /**
     * Instantiates a new CP2K keyword.
     *
     * @param schemaElement the schema element to generate this keyword from.
     */
    public CP2KKeyWord(XmlSchemaElement schemaElement) {
        m_schemaElement = schemaElement;
        // Sanitised name is the name of the element in the schema.
        m_sanitisedName = m_schemaElement.getName();

        // Get the CP2K name from the trueName attribute.
        Map<Object, Object> metaInfoMap = schemaElement.getMetaInfoMap();
        if (metaInfoMap != null) {
            LibhpcTrueNameCustomAttribute customAttrib =
                    (LibhpcTrueNameCustomAttribute) metaInfoMap.get(
                            LibhpcTrueNameCustomAttribute.LIBHPC_TRUE_NAME_CUSTOM_ATTRIBUTE_QNAME);
            if (customAttrib != null) {
                m_cp2kName = customAttrib.getTrueName();
            }
        } else {
            m_cp2kName = m_sanitisedName;
        }

        // Populate the list of aliases
        populateAliases();
    }

    /**
     * Gets the sanitised name for this section as used in schema and XML.
     *
     * @return the sanitised name.
     */
    public String getSanitisedName() {
        return m_sanitisedName;
    }

    /**
     * Gets the true name for this section as used by CP2K.
     *
     * @return the true CP2K name.
     */
    public String getCp2kName() {
        return m_cp2kName;
    }

    /**
     * Gets the schema element.
     *
     * @return the schema element.
     */
    public XmlSchemaElement getSchemaElement() {
        return m_schemaElement;
    }

    /**
     * Gets the list of aliases.
     *
     * @return the List of aliases.
     */
    public List<String> getAliases() {
        return m_aliases;
    }

    /**
     * Returns the aliases for this keyword.
     */
    private void populateAliases() {

        List<String> aliases = new ArrayList<String>();

        // Get the schema annotation items
        List<XmlSchemaAnnotationItem> items = m_schemaElement.getAnnotation().getItems();
        for (XmlSchemaAnnotationItem item : items) {
            // Assume that annotation just contains appinfo child.
            XmlSchemaAppInfo appInfo = (XmlSchemaAppInfo) item;
            // Get list of child nodes
            NodeList markup = appInfo.getMarkup();
            // Iterate list of nodes, only consider elements
            for (int i = 0; i < markup.getLength(); i++) {
                Node node = markup.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    if (ALIAS_LOCALNAME.equals(element.getLocalName())) {
                        aliases.add(element.getTextContent());
                    }
                }
            }
        }

        m_aliases = aliases;
    }
}
