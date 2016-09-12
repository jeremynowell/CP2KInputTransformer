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

import javax.xml.namespace.QName;

/**
 * The Class CP2KTrueNameCustomAttribute representing the libhpc:trueName
 * attribute in the schema. This represents the name used by CP2K for sections
 * and keywords.
 *
 * @author Jeremy Nowell
 */
public class LibhpcTrueNameCustomAttribute {

    /** The Constant LIBHPC_TRUE_NAME_CUSTOM_ATTRIBUTE_QNAME. */
    public static final QName LIBHPC_TRUE_NAME_CUSTOM_ATTRIBUTE_QNAME = new QName(
            "http://www.libhpc.imperial.ac.uk/SchemaAnnotation", "trueName");

    /** The value of the trueName attribute. */
    private String m_trueName;

    /**
     * Gets the value of the trueName attribute.
     *
     * @return the trueName.
     */
    public String getTrueName() {
        return m_trueName;
    }

    /**
     * Sets the value of the trueName attribute.
     *
     * @param suffix the new trueName.
     */
    public void setTrueName(String suffix) {
        m_trueName = suffix;
    }
}
