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

import org.apache.ws.commons.schema.XmlSchemaObject;
import org.apache.ws.commons.schema.extensions.ExtensionDeserializer;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;

import javax.xml.namespace.QName;

/**
 * The Class CP2KTrueNameCustomAttributeDeserializer. This de-serialises the
 * custom libhpc:trueName attribute from this schema into an object.
 *
 * @author Jeremy Nowell
 */
public class LibhpcTrueNameCustomAttributeDeserializer implements ExtensionDeserializer {

    /**
     * Deserialise the given element.
     *
     * @param schemaObject - Parent schema element,
     * @param name - the QName of the element/attribute to be deserialised.
     *               In the case where a deserialiser is used to handle
     *               multiple elements/attributes this may be useful to
     *               determine the correct deserialisation.
     * @param domNode - the raw DOM Node read from the source. This will be
     *                  the extension element itself if for an element or
     *                  the extension attribute object if it is an attribute.
     */
    @Override
    public void deserialize(XmlSchemaObject schemaObject, QName name, Node domNode) {

        if (LibhpcTrueNameCustomAttribute.LIBHPC_TRUE_NAME_CUSTOM_ATTRIBUTE_QNAME.equals(name)) {
            Attr attrib = (Attr) domNode;
            String value = attrib.getValue();
            LibhpcTrueNameCustomAttribute trueName = new LibhpcTrueNameCustomAttribute();
            trueName.setTrueName(value);

            // Put object into meta info map
            schemaObject.addMetaInfo(
                    LibhpcTrueNameCustomAttribute.LIBHPC_TRUE_NAME_CUSTOM_ATTRIBUTE_QNAME,
                    trueName);
        }
    }
}
