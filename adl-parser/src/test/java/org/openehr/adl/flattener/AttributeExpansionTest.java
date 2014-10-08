/*
 * Copyright (C) 2014 Marand
 */

package org.openehr.adl.flattener;

import org.openehr.adl.am.AmQuery;
import org.openehr.jaxb.am.Archetype;
import org.openehr.jaxb.am.CObject;
import org.testng.annotations.Test;

/**
 * @author markopi
 */
public class AttributeExpansionTest extends FlattenerTestBase {

    @Test
    public void testAttributeExpansion() throws Exception {
        Archetype flattened = parseAndFlattenArchetype("adl15/openEHR-EHR-EVALUATION.alert-zn.v1.adls");

        CObject definingCode = AmQuery.get(flattened, "data/items[at0003]/value/defining_code");
        assertCObject(definingCode, "CODE_PHRASE", null, null);

        assertCTerminologyCode(definingCode, "local",
                new String[]{"at0.15", "at0.16", "at0.17", "at0.18", "at0.19", "at0.20", "at0.21", "at0.22"}, null);
    }

}
