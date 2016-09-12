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

import org.junit.Assert;
import org.junit.Test;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.builder.Input;
import org.xmlunit.diff.Diff;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.xml.transform.Source;

/**
 * Tests for TextInputProcessor.
 *
 * @author Jeremy Nowell
 */
public class TextInputProcessorTest extends Assert {

    /**
     * Initialisation should work.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void initialisationShouldWork() throws IOException {

        String schemaFileName = "/cp2k-3.0.xsd";

        TextInputProcessor processor = new TextInputProcessor(schemaFileName);
        assertNotNull(processor);
    }


    /**
     * Test processing.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws URISyntaxException the URI syntax exception
     */
    @Test
    public void testProcessingSimpleInputString() throws IOException, URISyntaxException {
        transformString("simple-input.inp", "simple-input.xml");
    }

    /**
     * Test processing.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws URISyntaxException the URI syntax exception
     */
    @Test
    public void testProcessingSimpleInputStream() throws IOException, URISyntaxException {
        transformStream("simple-input.inp", "simple-input.xml");
    }

    /**
     * Test processing.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws URISyntaxException the URI syntax exception
     */
    @Test
    public void testProcessingSingleSectionString() throws IOException, URISyntaxException {
        transformString("single-section.inp", "single-section.xml");
    }

    /**
     * Test processing.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws URISyntaxException the URI syntax exception
     */
    @Test
    public void testProcessingSingleSectionStream() throws IOException, URISyntaxException {
        transformStream("single-section.inp", "single-section.xml");
    }

    /**
     * Test processing.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws URISyntaxException the URI syntax exception
     */
    @Test
    public void testProcessingUnitsString() throws IOException, URISyntaxException {
        transformString("units-test.inp", "units-test.xml");
    }

    /**
     * Test processing.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws URISyntaxException the URI syntax exception
     */
    @Test
    public void testProcessingUnitsStream() throws IOException, URISyntaxException {
        transformStream("units-test.inp", "units-test.xml");
    }

    /**
     * Test processing.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws URISyntaxException the URI syntax exception
     */
    @Test
    public void testProcessingAliasString() throws IOException, URISyntaxException {
        transformString("alias.inp", "alias.xml");
    }

    /**
     * Test processing.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws URISyntaxException the URI syntax exception
     */
    @Test
    public void testProcessingAliasStream() throws IOException, URISyntaxException {
        transformStream("alias.inp", "alias.xml");
    }

    /**
     * Test processing.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws URISyntaxException the URI syntax exception
     */
    @Test
    public void testProcessingNoSectionNameEndString() throws IOException, URISyntaxException {
        transformString("end_no_section_name.inp", "end_no_section_name.xml");
    }

    /**
     * Test processing.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws URISyntaxException the URI syntax exception
     */
    @Test
    public void testProcessingNoSectionNameEndStream() throws IOException, URISyntaxException {
        transformStream("end_no_section_name.inp", "end_no_section_name.xml");
    }

    /**
     * Test processing.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws URISyntaxException the URI syntax exception
     */
    @Test
    public void testProcessingCellString() throws IOException, URISyntaxException {
        transformStream("cell_abc.inp", "cell_abc.xml");
    }

    /**
     * Test processing.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws URISyntaxException the URI syntax exception
     */
    @Test
    public void testProcessingCellStream() throws IOException, URISyntaxException {
        transformStream("cell_abc.inp", "cell_abc.xml");
    }

    /**
     * Test processing.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws URISyntaxException the URI syntax exception
     */
    @Test
    public void testProcessingRepeatsString() throws IOException, URISyntaxException {
        transformStream("repeats.inp", "repeats.xml");
    }

    /**
     * Test processing.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws URISyntaxException the URI syntax exception
     */
    @Test
    public void testProcessingRepeatsStream() throws IOException, URISyntaxException {
        transformStream("repeats.inp", "repeats.xml");
    }

    /**
     * Test processing.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws URISyntaxException the URI syntax exception
     */
    @Test
    public void testProcessingCommentString() throws IOException, URISyntaxException {
        transformStream("comment.inp", "comment.xml");
    }

    /**
     * Test processing.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws URISyntaxException the URI syntax exception
     */
    @Test
    public void testProcessingCommentStream() throws IOException, URISyntaxException {
        transformStream("comment.inp", "comment.xml");
    }

    /**
     * Test hasKeywordUnit finds unit.
     */
    @Test
    public void testHasKeywordUnitFindsUnit() {
        String line = " KEYWORD [bohr] 1.5";
        boolean hasUnit = TextInputProcessor.keywordHasUnit(line);
        assertTrue(hasUnit);

        line = "  KEYWORD 2.4 [bohr^-1*hartree]";
        hasUnit = TextInputProcessor.keywordHasUnit(line);
        assertTrue(hasUnit);
    }

    /**
     * Test hasKeywordUnit doesn't find unit.
     */
    @Test
    public void testHasKeywordUnitFindsNoUnit() {
        String line = "KEYWORD";
        boolean hasUnit = TextInputProcessor.keywordHasUnit(line);
        assertFalse(hasUnit);

        line = " KEYWORD 1.5";
        hasUnit = TextInputProcessor.keywordHasUnit(line);
        assertFalse(hasUnit);

        line = "  KEYWORD 2.4 bohr^-1*hartree";
        hasUnit = TextInputProcessor.keywordHasUnit(line);
        assertFalse(hasUnit);
    }

    /**
     * Test keywordUnit works correctly.
     */
    @Test
    public void testGetKeywordUnitGetsUnit() {
        // Usual way round
        String line = " KEYWORD [bohr] 1.5";
        String returnValue = TextInputProcessor.getKeywordUnit(line);
        assertNotNull(returnValue);
        assertEquals("bohr", returnValue);

        // Unit at end of line
        line = " KEYWORD 1.5 [bohr]";
        returnValue = TextInputProcessor.getKeywordUnit(line);
        assertNotNull(returnValue);
        assertEquals("bohr", returnValue);
    }

    /**
     * Test keywordUnit returns null for bare keyword.
     */
    @Test
    public void testGetKeywordUnitReturnsNull() {
        String line = " KEYWORD VALUE";
        String returnValue = TextInputProcessor.getKeywordUnit(line);
        assertNull(returnValue);

        line = " KEYWORD";
        returnValue = TextInputProcessor.getKeywordUnit(line);
        assertNull(returnValue);
    }

    /**
     * Test getKeyword always gets keyword.
     */
    @Test
    public void testGetKeywordName() {
        String keyWord = "ABC1";
        String line = "ABC1";
        String returnValue = TextInputProcessor.getKeywordName(line);
        assertEquals(keyWord, returnValue);

        line = "    ABC1   ";
        returnValue = TextInputProcessor.getKeywordName(line);
        assertEquals(keyWord, returnValue);

        line = "    ABC1   ";
        returnValue = TextInputProcessor.getKeywordName(line);
        assertEquals(keyWord, returnValue);

        line = "ABC1 VALUE";
        returnValue = TextInputProcessor.getKeywordName(line);
        assertEquals(keyWord, returnValue);

        line = "    ABC1   -1.07-E03   ";
        returnValue = TextInputProcessor.getKeywordName(line);
        assertEquals(keyWord, returnValue);

        line = "ABC1 VALUE1 -1.07-E03 VALUE3";
        returnValue = TextInputProcessor.getKeywordName(line);
        assertEquals(keyWord, returnValue);

        line = "       ABC1 VALUE1 -1.07-E03 VALUE3";
        returnValue = TextInputProcessor.getKeywordName(line);
        assertEquals(keyWord, returnValue);

        line = "       ABC1   [unit1]  VALUE1 -1.07-E03 VALUE3";
        returnValue = TextInputProcessor.getKeywordName(line);
        assertEquals(keyWord, returnValue);

        line = "       ABC1 VALUE1 VALUE2 VALUE3  [unit2] ";
        returnValue = TextInputProcessor.getKeywordName(line);
        assertEquals(keyWord, returnValue);
    }

    /**
     * Test getKeyword always gets keyword.
     */
    @Test
    public void testGetKeywordValue() {
        String value = null;
        String line = "ABC";
        String returnValue = TextInputProcessor.getKeywordValue(line);
        assertEquals(value, returnValue);

        line = "    ABC   ";
        returnValue = TextInputProcessor.getKeywordValue(line);
        assertEquals(value, returnValue);

        line = "    ABC   ";
        returnValue = TextInputProcessor.getKeywordValue(line);
        assertEquals(value, returnValue);

        line = "ABC VALUE";
        value = "VALUE";
        returnValue = TextInputProcessor.getKeywordValue(line);
        assertEquals(value, returnValue);

        line = "    ABC   -1.07-E03   ";
        value = "-1.07-E03";
        returnValue = TextInputProcessor.getKeywordValue(line);
        assertEquals(value, returnValue);

        line = "ABC VALUE1 -1.07-E03 VALUE3";
        value = "VALUE1 -1.07-E03 VALUE3";
        returnValue = TextInputProcessor.getKeywordValue(line);
        assertEquals(value, returnValue);

        line = "       ABC    VALUE1 -1.07-E03 VALUE3   ";
        returnValue = TextInputProcessor.getKeywordValue(line);
        assertEquals(value, returnValue);

        line = "       ABC   [unit1]  VALUE1 -1.07-E03 VALUE3";
        returnValue = TextInputProcessor.getKeywordValue(line);
        assertEquals(value, returnValue);

        line = "       ABC VALUE1 -1.07-E03 VALUE3  [unit2] ";
        returnValue = TextInputProcessor.getKeywordValue(line);
        assertEquals(value, returnValue);
    }

    /**
     * Test isComment returns true for lines starting with #.
     */
    @Test
    public void testIsComment() {
        String line = "KEYWORD VALUE";
        boolean returnValue = TextInputProcessor.isComment(line);
        assertFalse(returnValue);

        line = "   KEYWORD    VALUE   ";
        returnValue = TextInputProcessor.isComment(line);
        assertFalse(returnValue);

        line = "# I'm a comment";
        returnValue = TextInputProcessor.isComment(line);
        assertTrue(returnValue);

        line = "   #    I'm a comment  ";
        returnValue = TextInputProcessor.isComment(line);
        assertTrue(returnValue);
    }

    /**
     * Test isEndSection returns true for &END SECTION.
     */
    @Test
    public void testIsEndSectionTrueSectionName() {
        String line = " &END SECTION";
        boolean returnValue = TextInputProcessor.isSectionEnd(line);
        assertTrue(returnValue);
    }

    /**
     * Test isEndSection returns true for bare &END.
     */
    @Test
    public void testIsEndSectionTrueNoSectionName() {
        String line = " &END";
        boolean returnValue = TextInputProcessor.isSectionEnd(line);
        assertTrue(returnValue);
    }

    /**
     * Utility function for running transform from String.
     *
     * @param inputFileName name of file containing CP2K input.
     * @param expectedXmlFileName name of file containing XML to check against.
     * @throws IOException if problem.
     * @throws URISyntaxException if problem.
     */
    private void transformString(
            String inputFileName,
            String expectedXmlFileName) throws IOException, URISyntaxException {

        String schemaFileName = "/cp2k-4.0.xsd";

        TextInputProcessor processor = new TextInputProcessor(schemaFileName);

        byte[] encodedFile = Files.readAllBytes(
                Paths.get(this.getClass().getResource("/input/" + inputFileName).toURI()));
        String inputFileString = new String(encodedFile);

        String outputXmlString = processor.processInputFile(inputFileString);

        checkOutputXml(expectedXmlFileName, outputXmlString);

    }

    /**
     * Utility function for running transform from Stream.
     *
     * @param inputFileName name of file containing CP2K input.
     * @param expectedXmlFileName name of file containing XML to check against.
     * @throws IOException if problem.
     * @throws URISyntaxException if problem.
     */
    private void transformStream(
            String inputFileName,
            String expectedXmlFileName) throws IOException, URISyntaxException {

        String schemaFileName = "/cp2k-4.0.xsd";

        TextInputProcessor processor = new TextInputProcessor(schemaFileName);

        InputStream is = this.getClass().getResourceAsStream("/input/" + inputFileName);

        String outputXmlString = processor.processInputFile(is);

        checkOutputXml(expectedXmlFileName, outputXmlString);

    }

    /**
     * Check the output XML string against an expected XML file.
     *
     * @param expectedXmlFileName name of file containing XML to check against.
     * @param actualXmlString String containing XML to check against file.
     * @throws URISyntaxException if problem
     */
    private void checkOutputXml(
            String expectedXmlFileName,
            String actualXmlString) throws URISyntaxException {

        // Get expected and actual XML
        Source expectedXml = Input.fromURI(
                this.getClass().getResource("/output/" + expectedXmlFileName).toURI()).build();
        Source actualXml = Input.fromString(actualXmlString).build();

        // Compare results
        Diff xmlDiff = DiffBuilder.compare(expectedXml)
                .withTest(actualXml)
                .normalizeWhitespace()
                .checkForSimilar()
                .build();

        assertFalse("Expected XML to be similar, " + xmlDiff.toString(), xmlDiff.hasDifferences());
    }
}
