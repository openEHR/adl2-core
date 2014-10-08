/*
 * Copyright (C) 2014 Marand
 */

package org.openehr.adl.parser.adl14;

import org.openehr.adl.ParserTestBase;
import org.openehr.adl.am.AmQuery;
import org.openehr.jaxb.am.Archetype;
import org.openehr.jaxb.am.ArchetypeConstraint;
import org.openehr.jaxb.am.CCodePhrase;
import org.testng.annotations.Test;

public class DvCodedTextTest extends ParserTestBase {

    @Test
    public void testParse() throws Exception {
        Archetype archetype = parseArchetype("adl14/adl-test-composition.dv_coded_text.test.adl");

        ArchetypeConstraint node = AmQuery.find(archetype, "/category/defining_code");
        assertTrue("CCodePhrase expected, but got " + node.getClass(),
                node instanceof CCodePhrase);
        CCodePhrase ccp = (CCodePhrase) node;
        assertEquals("terminologyId wrong", "openehr",
                ccp.getTerminologyId().getValue());
        assertEquals("codeString wrong", "431", ccp.getCodeList().get(0));
    }
}
