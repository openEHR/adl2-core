/*
 * Copyright (C) 2014 Marand
 */

package com.marand.thinkehr.adl.flattener;

import com.marand.thinkehr.adl.am.AmQuery;
import org.openehr.jaxb.am.Archetype;
import org.openehr.jaxb.am.CTerminologyCode;
import org.testng.annotations.Test;

/**
 * @author markopi
 */
public class NegatedMatchTest extends FlattenerTestBase {
    @Test
    public void testNegatedCodeList() {
        Archetype flattened = parseAndFlattenArchetype("adl15/negation/openehr-ehr-EVALUATION.negated-child.v1.adls");

        CTerminologyCode ccp = AmQuery.get(flattened, "/data/items[at0002]/value/defining_code");

        assertCTerminologyCode(ccp, "local", new String[]{"at0003", "at0005"}, null);
    }
}
