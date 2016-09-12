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

package uk.ac.ed.epcc.cp2kinputtransformer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.builder.Input;
import org.xmlunit.diff.Diff;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.transform.Source;

/**
 * The Class CP2KInputEditorRestServiceTest.
 */
public class CP2KInputTransformerRestServiceTest extends JerseyTest {


    /**
     * @see org.glassfish.jersey.test.JerseyTest#configure()
     */
    @Override
    protected Application configure() {
        return new CP2KInputTransformer();
    }

    /**
     * Configure the client to use multipart.
     */
    @Override
    protected void configureClient(ClientConfig config) {
        config.register(MultiPartFeature.class);
    }

    /**
     * Test the verify method.
     */
    @Test
    public void testVerify() {
        final Response response = target("verify").request().get();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals("CP2KInputEditorRESTService Successfully started...",
                response.readEntity(String.class));
    }

    /**
     * Test the transform method with a simple input file.
     *
     * @throws IOException if problem
     * @throws URISyntaxException if problem
     */
    @Test
    public void testTransformSimple() throws IOException, URISyntaxException {
        submitTransform("simple-input.inp", "simple-input.xml");
    }

    /**
     * Test the transform method with an input file with a single section.
     *
     * @throws IOException if problem
     * @throws URISyntaxException if problem
     */
    @Test
    public void testTransformSingle() throws IOException, URISyntaxException {
        submitTransform("single-section.inp", "single-section.xml");
    }

    /**
     * Utility method to transform input file and compare with expected xml.
     * @throws URISyntaxException if problem
     * @throws IOException if problem
     */
    private void submitTransform(
            String inputFileName,
            String expectedXmlFileName) throws IOException, URISyntaxException {

        // Create new multi-part form
        FormDataMultiPart mp = new FormDataMultiPart();

        // Add file to form
        File filePath = Paths.get(
                this.getClass().getResource("/input/" + inputFileName).toURI()).toFile();
        FileDataBodyPart fileDataBodyPart = new FileDataBodyPart("inputFile", filePath);
        mp.bodyPart(fileDataBodyPart);

        // Transform to XML via form POST
        final Response response = target("cp2k-3.0/transform")
                .request().post(Entity.entity(mp, MediaType.MULTIPART_FORM_DATA));

        // Check status
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        // Get XML response string
        String actual = response.readEntity(String.class);

        // Get expected and actual XML
        Source expectedXml = Input.fromURI(
                this.getClass().getResource("/output/" + expectedXmlFileName).toURI()).build();
        Source actualXml = Input.fromString(actual).build();

        // Compare result with expected XML from file
        Diff xmlDiff = DiffBuilder.compare(expectedXml)
                .withTest(actualXml).ignoreComments().normalizeWhitespace().build();

        assertFalse(xmlDiff.toString(), xmlDiff.hasDifferences());
    }
}
