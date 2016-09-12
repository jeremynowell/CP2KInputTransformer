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

import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.constants.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.stream.StreamSource;

/**
 * Class for processing CP2K input files in text into libhpc compatible
 * XML files.
 *
 * @author Jeremy Nowell
 */
public class TextInputProcessor {
    /**
     * Logger.
     */
    private static final Logger s_Log = Logger.getLogger(TextInputProcessor.class.getName());

    /**
     * Schema file name.
     */
    private CP2KSection m_topLevelSection;

    /**
     * Buffered string reader for input.
     */
    private BufferedReader m_inputReader;

    /**
     * XML output writer.
     */
    private XMLStreamWriter m_outputWriter;

    /**
     * The comment regexp.
     * Whitespace, then a '#', then anything.
     */
    private static final Pattern COMMENT = Pattern.compile("^\\s*#.*");

    /**
     * The START_SECTION regexp.
     * Whitespace, then an '&', then section name.
     */
    private static final Pattern START_SECTION = Pattern.compile("^\\s*&(\\S+)\\s?(.+)?");

    /**
     * The END_SECTION regexp.
     * Whitespace, then '&END'.
     */
    private static final Pattern END_SECTION = Pattern.compile("^\\s*&END.*");

    /**
     * The KEYWORD_VALUE regexp.
     * Whitespace, then keyword name, [possible unit], then keyword value, [possible unit].
     */
    private static final Pattern KEYWORD_VALUE =
            Pattern.compile("^\\s*(\\S+)\\s*(?:\\[.+\\])?([\\w\\.\\-\\+\\s]*)(?:\\[.+\\]\\s*)?$");

    /**
     * The bare keyword regexp.
     * Whitespace then keyword name, whitespace, line end.
     */
    private static final Pattern BARE_KEYWORD =
            Pattern.compile("^\\s*(\\S+)\\s*$");

    /**
     * Keyword, and unit regexp.
     * Space, followed by [unit] before value.
     */
    private static final Pattern KEYWORD_UNIT =
            Pattern.compile("^\\s*(\\S+).+\\[(\\S+)\\].*");

    /**
     * Constructor.
     *
     * @param schemaFileName relative file name of schema.
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public TextInputProcessor(String schemaFileName) throws IOException {

        s_Log.fine("Constructing processor for schema: " + schemaFileName);

        // Add custom extension registry to system properties for processor.
        System.setProperty(
                Constants.SystemConstants.EXTENSION_REGISTRY_KEY,
                CustomExtensionRegistry.class.getName());

        // TODO check schema file exists, throw exception if not.
        InputStream is = this.getClass().getResourceAsStream(schemaFileName);

        XmlSchemaCollection schemaCol = new XmlSchemaCollection();
        XmlSchema schema = schemaCol.read(new StreamSource(is));

        s_Log.fine("Got schema:\n" + schema);

        // Root schema element
        QName element = new QName("http://www.libhpc.imperial.ac.uk", "CP2K");
        XmlSchemaElement rootSchemaElement = schema.getElementByName(element);

        if (rootSchemaElement == null) {
            throw new IOException("Unable to get schema root element");
        }

        s_Log.fine("Got schema root element");

        m_topLevelSection = new CP2KSection(rootSchemaElement);

        // Remove registry from system properties.
        System.getProperties().remove(Constants.SystemConstants.EXTENSION_REGISTRY_KEY);
    }

    /**
     * Process text input file into XML.
     *
     * @param inputFileContents String containing contents of CP2K input file to be converted.
     * @return the string
     * @throws IOException when problem processing file.
     */
    public String processInputFile(String inputFileContents) throws IOException {

        s_Log.fine("processInputFile");

        // Create reader for input string
        StringReader stringReader = new StringReader(inputFileContents);
        m_inputReader = new BufferedReader(stringReader);

        // Create writer for output string
        StringWriter stringWriter = new StringWriter();
        XMLOutputFactory factory = XMLOutputFactory.newInstance();

        try {
            m_outputWriter = factory.createXMLStreamWriter(stringWriter);

            // Start XML output document
            m_outputWriter.writeStartDocument();

            // Process input
            processSection(m_topLevelSection, null);

            // End XML output document
            m_outputWriter.writeEndDocument();
            m_outputWriter.flush();
            m_outputWriter.close();
        } catch (XMLStreamException e) {
            throw new IOException("Error creating XML Document:", e);
        }

        String output = stringWriter.toString();
        stringWriter.close();

        return output;
    }

    /**
     * Process text input file into XML.
     *
     * @param inputStream InputStream containing contents of CP2K input file to be converted.
     * @return the string
     * @throws IOException when problem processing file.
     */
    public String processInputFile(InputStream inputStream) throws IOException {

        s_Log.fine("processInputFile");

        // Create reader for input string
        InputStreamReader streamReader = new InputStreamReader(inputStream);
        m_inputReader = new BufferedReader(streamReader);

        // Create writer for output string
        StringWriter stringWriter = new StringWriter();
        XMLOutputFactory factory = XMLOutputFactory.newInstance();

        try {
            m_outputWriter = factory.createXMLStreamWriter(stringWriter);

            // Start XML output document
            m_outputWriter.writeStartDocument();

            // Process input
            processSection(m_topLevelSection, null);

            // End XML output document
            m_outputWriter.writeEndDocument();
            m_outputWriter.flush();
            m_outputWriter.close();
        } catch (XMLStreamException e) {
            throw new IOException("Error creating XML Document:", e);
        }

        String output = stringWriter.toString();
        stringWriter.close();

        return output;
    }

    /**
     * Process section.
     *
     * @param currentSection the current section
     * @param sectionParameters the section parameters
     * @throws XMLStreamException the XML stream exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void processSection(CP2KSection currentSection, String sectionParameters)
            throws XMLStreamException, IOException {

        String sectionName = currentSection.getSanitisedName();
        m_outputWriter.writeStartElement(sectionName);

        if (sectionParameters != null) {
            m_outputWriter.writeStartElement("CP2K_KEYWORD_SECTION_PARAMETERS");
            m_outputWriter.writeCharacters(sectionParameters);
            m_outputWriter.writeEndElement();
        }

        String line;

        boolean defaultKeywordOpen = false;

        try {
            while ((line = m_inputReader.readLine()) != null) {

                s_Log.fine("Processing line:\n" + line);

                if (isSectionEnd(line)) {
                    s_Log.fine("Got Section END");

                    if (defaultKeywordOpen) {
                        m_outputWriter.writeEndElement();
                        defaultKeywordOpen = false;
                    }

                    m_outputWriter.writeEndElement();
                    return;
                }
                if (isComment(line)) {
                    m_outputWriter.writeComment(line);
                } else {
                    if (isSectionStart(line)) {
                        // New section starting
                        s_Log.fine("Got Section START");

                        // Close default keyword element if open.
                        if (defaultKeywordOpen) {
                            m_outputWriter.writeEndElement();
                            defaultKeywordOpen = false;
                        }

                        String newSectionName = getSectionName(line);
                        s_Log.fine("Section name: " + newSectionName);
                        if (currentSection.hasSubSection(newSectionName)) {
                            CP2KSection newSection = currentSection.getSubSection(newSectionName);
                            String newSectionParameters = getSectionParameters(line);
                            processSection(newSection, newSectionParameters);
                        }
                    } else {
                        String keyWordName = getKeywordName(line);
                        s_Log.fine("Keyword: " + keyWordName);
                        // Check if line corresponds to an allowed keyword in this section
                        if (currentSection.hasKeyWord(keyWordName)) {
                            CP2KKeyWord keyWord = currentSection.getKeyWord(keyWordName);
                            m_outputWriter.writeStartElement(keyWord.getSanitisedName());
                            // Write measurement unit as attribute if present
                            if (keywordHasUnit(line)) {
                                String unit = getKeywordUnit(line);
                                m_outputWriter.writeAttribute("UNIT", unit);
                            }
                            // Get keyword value
                            String value = getKeywordValue(line);
                            // Assume that empty keyword values should be set to T(RUE)
                            if (value == null || value.isEmpty()) {
                                value = "T";
                            }
                            s_Log.fine("Value: " + value);
                            m_outputWriter.writeCharacters(value);
                            m_outputWriter.writeEndElement();
                        } else {
                            // Not a keyword, so must be default section parameters
                            if (!(defaultKeywordOpen)) {
                                m_outputWriter.writeStartElement("CP2K_KEYWORD_DEFAULT_KEYWORD");
                                defaultKeywordOpen = true;
                            } else {
                                // Repeated section parameter, eg atoms in co-ord section.
                                // Write extra DEFAULT_KEYWORD element.
                                m_outputWriter.writeEndElement();
                                m_outputWriter.writeStartElement("CP2K_KEYWORD_DEFAULT_KEYWORD");
                            }
                            m_outputWriter.writeCharacters(line);
                        }
                    }
                }
            }

            // For CP2K top level element
            m_outputWriter.writeEndElement();

        } catch (IOException e) {
            throw new IOException("Unable to process input string", e);
        }

    }

    /**
     * Checks if line is a comment.
     *
     * @param line the line to process
     * @return true if is comment line
     */
    protected static boolean isComment(String line) {
        Matcher matcher = COMMENT.matcher(line);
        return matcher.matches();
    }

    /**
     * Checks if line is section end.
     *
     * @param line the line
     * @return true, if is section end
     */
    protected static boolean isSectionEnd(String line) {
        Matcher matcher = END_SECTION.matcher(line);
        return matcher.matches();
    }

    /**
     * Checks if line is section start.
     *
     * @param line the line
     * @return true, if is section start
     */
    protected static boolean isSectionStart(String line) {
        Matcher matcher = START_SECTION.matcher(line);
        return matcher.matches();
    }

    /**
     * Gets the section name.
     *
     * @param line the line
     * @return the section name
     */
    protected static String getSectionName(String line) {
        Matcher matcher = START_SECTION.matcher(line);
        if (matcher.matches()) {
            return matcher.group(1);
        } else {
            return null;
        }
    }

    /**
     * Gets the section parameters.
     *
     * @param line the line
     * @return the section parameters
     */
    protected static String getSectionParameters(String line) {
        Matcher matcher = START_SECTION.matcher(line);
        if (matcher.matches()) {
            return matcher.group(2);
        } else {
            return null;
        }
    }

    /**
     * Gets the keyword name.
     *
     * @param line the line
     * @return the keyword name
     */
    protected static String getKeywordName(String line) {
        Matcher matcher = BARE_KEYWORD.matcher(line);
        if (matcher.matches()) {
            return matcher.group(1);
        }
        matcher = KEYWORD_VALUE.matcher(line);
        if (matcher.matches()) {
            return matcher.group(1);
        } else {
            return null;
        }
    }

    /**
     * Gets the keyword value.
     *
     * @param line the line
     * @return the keyword value
     */
    protected static String getKeywordValue(String line) {
        Matcher matcher = BARE_KEYWORD.matcher(line);
        if (matcher.matches()) {
            return null;
        }
        matcher = KEYWORD_VALUE.matcher(line);
        if (matcher.matches()) {
            return (matcher.group(2)).trim();
        } else {
            return null;
        }
    }

    /**
     * Determines whether a keyword unit is present.
     *
     * @param line the line to process.
     * @return boolean
     */
    protected static boolean keywordHasUnit(String line) {
        Matcher matcher = KEYWORD_UNIT.matcher(line);
        return matcher.matches();
    }

    /**
     * Gets the keyword unit.
     *
     * @param line the line to process
     * @return the keyword unit or null if no unit
     */
    protected static String getKeywordUnit(String line) {
        Matcher matcher = KEYWORD_UNIT.matcher(line);
        if (matcher.matches()) {
            return matcher.group(2);
        } else {
            return null;
        }
    }
}
