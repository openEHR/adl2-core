/*
 * Copyright (C) 2014 Marand
 */

package com.marand.thinkehr.adl.parser.adl15;

import com.marand.thinkehr.adl.ParserTestBase;
import org.openehr.jaxb.am.Archetype;
import org.openehr.jaxb.am.CAttribute;
import org.testng.annotations.Test;

/**
 * @author markopi
 */
public class NegatedMatchTest extends ParserTestBase {
    @Test
    public void testNegatedMatch() throws Exception {
        Archetype archetype = parseArchetype("adl15/negation/openehr-ehr-EVALUATION.negated-child.v1.adls");

        CAttribute attr = archetype.getDefinition().getAttributes().get(0);

        assertTrue("not negated", attr.isMatchNegated());
        assertCTerminologyCode(attr.getChildren().get(0), "local", new String[]{"at0004"}, null);
    }
}
