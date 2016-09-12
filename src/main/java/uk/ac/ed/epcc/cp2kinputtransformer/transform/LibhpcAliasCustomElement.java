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
 * The Class LibhpcTrueAliasCustomElement representing the libhpc:alias element
 * in the annotation of the schema. This represents alternative names used by
 * CP2K for sections and keywords.
 *
 * @author Jeremy Nowell
 */
public class LibhpcAliasCustomElement {

    /** The Constant LIBHPC_ALIAS_CUSTOM_ELEMENT_QNAME. */
    public static final QName LIBHPC_ALIAS_CUSTOM_ELEMENT_QNAME = new QName(
            "http://www.libhpc.imperial.ac.uk/SchemaAnnotation", "alias");

    /** The contents of the alias element. */
    private String m_alias;

    /**
     * Gets the contents of the alias element.
     *
     * @return the alias.
     */
    public String getAlias() {
        return m_alias;
    }

    /**
     * Sets the value of the alias element.
     *
     * @param alias the new alias.
     */
    public void setAlias(String alias) {
        m_alias = alias;
    }
}
