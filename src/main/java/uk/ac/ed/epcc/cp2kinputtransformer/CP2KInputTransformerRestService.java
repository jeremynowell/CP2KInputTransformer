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

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import uk.ac.ed.epcc.cp2kinputtransformer.transform.TextInputProcessor;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


/**
 * The Class CP2KInputEditorRESTService. Provides a REST service
 * for processing CP2K input files to XML for use with GUI
 * editor.
 *
 * @author Jeremy Nowell
 */
@Path("/")
public class CP2KInputTransformerRestService {

    /** Logger. */
    private static final Logger s_Log =
            Logger.getLogger(CP2KInputTransformerRestService.class.getName());


    /**
     * Transform input file.
     *
     * @param templateId ID of cp2k transform to use. This will
     *                   be transformed into a schema filename
     *                   'ID.xsd'. This file MUST exist in the
     *                   resources/schema directory.
     * @param fileDisposition file information.
     * @param fileInputStream the incoming data to transform.
     * @return the web-service response.
     */
    @POST
    @Path("{templateId}/transform")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_XML)
    public Response transformInputFile(
            @PathParam("templateId") String templateId,
            @FormDataParam("inputFile") FormDataContentDisposition fileDisposition,
            @FormDataParam("inputFile") InputStream fileInputStream) {

        // Transform input file to XML
        String cp2kInputXml = "";
        String cp2kSchemaFile = "/" + templateId + ".xsd";
        try {
            TextInputProcessor tip = new TextInputProcessor(cp2kSchemaFile);
            cp2kInputXml = tip.processInputFile(fileInputStream);
        } catch (IOException e) {
            String msg = "Error processing input file: " + e.getMessage();
            s_Log.warning(msg);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(msg).build();
        }
        // Return success response
        return Response.status(Response.Status.OK).entity(cp2kInputXml).build();
    }

    /**
     * Utility to verify rest service is running.
     *
     * @return the response
     */
    @GET
    @Path("/verify")
    @Produces(MediaType.TEXT_PLAIN)
    public Response verifyRestService() {
        String result = "CP2KInputEditorRESTService Successfully started...";

        // Return success
        return Response.status(Response.Status.OK).entity(result).build();
    }
}
