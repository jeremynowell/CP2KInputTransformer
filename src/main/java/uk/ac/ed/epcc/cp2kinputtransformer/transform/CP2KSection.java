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

import org.apache.ws.commons.schema.XmlSchemaComplexType;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaParticle;
import org.apache.ws.commons.schema.XmlSchemaSequence;
import org.apache.ws.commons.schema.XmlSchemaSequenceMember;
import org.apache.ws.commons.schema.XmlSchemaType;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * The class representing a CP2K input file section.
 *
 * <p>A section has a name as defined in the CP2K schema. It has
 * a sanitised name which is used to name the keyword in an XML
 * compliant way in schema and XML representations of an input
 * file.
 *
 * <p>It may contain keywords, and sections.
 *
 * @author Jeremy Nowell
 */
public class CP2KSection {
    /** Logger. */
    private static final Logger s_Log = Logger.getLogger(CP2KSection.class.getName());

    /** The XML Schema element object. */
    private XmlSchemaElement m_schemaElement;

    /** The sanitised name of the section as used in schema and XML. */
    private String m_sanitisedName;

    /** The true name as used in CP2K input files. */
    private String m_cp2kName;

    /** The sub-sections below this section. */
    private Map<String, CP2KSection> m_subSections;

    /** The key words below this section. */
    private Map<String, CP2KKeyWord> m_keyWords;

    /**
     * Whether the contents have been populated.
     * To save unnecessary schema processing the section contents are only
     * populated on-demand when required.
     */
    private boolean m_contentsPopulated = false;

    /**
     * Instantiates a new CP2K section.
     *
     * @param schemaElement the schema element to generate this section from.
     */
    public CP2KSection(XmlSchemaElement schemaElement) {
        m_schemaElement = schemaElement;
        m_sanitisedName = m_schemaElement.getName();
        s_Log.fine("New section: " + m_sanitisedName);

        Map<Object, Object> metaInfoMap = schemaElement.getMetaInfoMap();

        // Get the CP2K defined name from the schema.
        if (metaInfoMap != null) {
            LibhpcTrueNameCustomAttribute customAttrib =
                    (LibhpcTrueNameCustomAttribute) metaInfoMap.get(
                            LibhpcTrueNameCustomAttribute.LIBHPC_TRUE_NAME_CUSTOM_ATTRIBUTE_QNAME);

            if (customAttrib != null) {
                m_cp2kName = customAttrib.getTrueName();
            } else {
                m_cp2kName = m_sanitisedName;
            }
        } else {
            m_cp2kName = m_sanitisedName;
        }

        s_Log.fine("CP2K name: " + m_cp2kName);

        // Create maps for sections and keywords contained in this section.
        m_subSections = new LinkedHashMap<String, CP2KSection>();
        m_keyWords = new LinkedHashMap<String, CP2KKeyWord>();
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
     * Checks for presence of a sub-section with given name.
     *
     * @param subSectionName the sub section name, as used by CP2K.
     * @return true, if sub-section present.
     */
    public boolean hasSubSection(String subSectionName) {
        if (!(m_contentsPopulated)) {
            populateAllowedContents();
        }

        if (m_subSections.containsKey(subSectionName)) {
            return true;
        }
        return false;
    }

    /**
     * Checks for presence of keyword in this section.
     *
     * @param keyWord the keyword to check, as used by CP2K.
     * @return true, if keyword present.
     */
    public boolean hasKeyWord(String keyWord) {
        if (!(m_contentsPopulated)) {
            populateAllowedContents();
        }

        if (m_keyWords.containsKey(keyWord)) {
            return true;
        }
        return false;
    }

    /**
     * Gets the sub-section with a given name.
     *
     * @param subSectionName the sub-section name, as used by CP2K.
     * @return the sub-section.
     */
    public CP2KSection getSubSection(String subSectionName) {
        if (!(m_contentsPopulated)) {
            populateAllowedContents();
        }

        return m_subSections.get(subSectionName);
    }

    /**
     * Gets the keyword with a given name.
     *
     * @param keyWordName the keyword, as used by CP2K.
     * @return the keyword.
     */
    public CP2KKeyWord getKeyWord(String keyWordName) {
        if (!(m_contentsPopulated)) {
            populateAllowedContents();
        }

        return m_keyWords.get(keyWordName);
    }

    /**
     * Populate allowed contents of this section - the sub-sections
     * and keywords.
     */
    private void populateAllowedContents() {
        s_Log.fine("populateAllowedContents");

        XmlSchemaParticle particle =
                ((XmlSchemaComplexType) m_schemaElement.getSchemaType()).getParticle();
        XmlSchemaSequence sequence = (XmlSchemaSequence) particle;

        List<XmlSchemaSequenceMember> subElements = sequence.getItems();

        Iterator<XmlSchemaSequenceMember> it = subElements.iterator();

        while (it.hasNext()) {
            // Get
            XmlSchemaElement childSchemaElement = (XmlSchemaElement) it.next();
            String childName = childSchemaElement.getName();
            s_Log.fine("Got child schema element: " + childName);
            XmlSchemaType childType = childSchemaElement.getSchemaType();
            s_Log.fine("Got child type: " + childType);
            // ComplexType means a section, else a keyword
            if (childType instanceof XmlSchemaComplexType) {
                s_Log.fine("Element is section");
                CP2KSection subSection = new CP2KSection(childSchemaElement);
                this.addSubSection(subSection);
            } else {
                s_Log.fine("Element is keyword");
                CP2KKeyWord keyWord = new CP2KKeyWord(childSchemaElement);
                this.addKeyWord(keyWord);
            }
        }

        m_contentsPopulated = true;

        return;
    }

    /**
     * Adds a sub-section to this section.
     *
     * @param subSection the sub-section to add.
     */
    private void addSubSection(CP2KSection subSection) {
        m_subSections.put(subSection.getCp2kName(), subSection);
    }

    /**
     * Adds a keyword to this section.
     *
     * @param keyWord the keyword to add.
     */
    private void addKeyWord(CP2KKeyWord keyWord) {
        m_keyWords.put(keyWord.getCp2kName(), keyWord);
        // Also add aliases.
        for (String alias : keyWord.getAliases()) {
            m_keyWords.put(alias, keyWord);
        }
    }
}
