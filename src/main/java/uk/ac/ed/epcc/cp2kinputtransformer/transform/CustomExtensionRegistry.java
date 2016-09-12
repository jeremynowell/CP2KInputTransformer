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

import org.apache.ws.commons.schema.extensions.ExtensionRegistry;

/**
 * The Class CustomExtensionRegistry. This registers the de-serialiser for
 * custom attributes.
 *
 * @author Jeremy Nowell
 */
public class CustomExtensionRegistry extends ExtensionRegistry {

    /**
     * Instantiates a new custom extension registry.
     */
    public CustomExtensionRegistry() {
        // Register custom types
        registerDeserializer(
                LibhpcTrueNameCustomAttribute.LIBHPC_TRUE_NAME_CUSTOM_ATTRIBUTE_QNAME,
                new LibhpcTrueNameCustomAttributeDeserializer());
        registerDeserializer(
                LibhpcAliasCustomElement.LIBHPC_ALIAS_CUSTOM_ELEMENT_QNAME,
                new LibhpcAliasCustomElementDeserializer());
    }
}
