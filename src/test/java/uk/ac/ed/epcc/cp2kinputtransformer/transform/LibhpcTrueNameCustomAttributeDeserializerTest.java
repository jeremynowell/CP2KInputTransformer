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
import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;
import java.util.Map;

import javax.xml.transform.stream.StreamSource;

/**
 * Tests for CP2KTrueNameCustomAttributeDeserializer.
 *
 * @author Jeremy Nowell
 */
public class LibhpcTrueNameCustomAttributeDeserializerTest extends Assert {


    /**
     * Test deserialization.
     */
    @Test
    public void testDeserialization() {
        // Set the property so custom extension registry is used
        System.setProperty(
                Constants.SystemConstants.EXTENSION_REGISTRY_KEY,
                CustomExtensionRegistry.class.getName());

        try {
            // Read the schema
            InputStream is = this.getClass().getResourceAsStream("/customAttribute.xsd");

            XmlSchemaCollection schemaCol = new XmlSchemaCollection();
            XmlSchema schema = schemaCol.read(new StreamSource(is));
            assertNotNull(schema);

            // Get elements and check annotations are populated
            for (XmlSchemaElement element : schema.getElements().values()) {
                assertNotNull(element);
                Map<Object, Object> metaInfoMap = element.getMetaInfoMap();
                assertNotNull(metaInfoMap);
                LibhpcTrueNameCustomAttribute customAttrib =
                        (LibhpcTrueNameCustomAttribute) metaInfoMap.get(
                                LibhpcTrueNameCustomAttribute
                                .LIBHPC_TRUE_NAME_CUSTOM_ATTRIBUTE_QNAME);
                assertNotNull(customAttrib);
            }
        } finally {
            System.getProperties().remove(Constants.SystemConstants.EXTENSION_REGISTRY_KEY);
        }
    }
}
