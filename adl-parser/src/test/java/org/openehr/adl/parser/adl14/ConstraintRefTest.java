/*
 * Copyright (C) 2014 Marand
 */

package org.openehr.adl.parser.adl14;

import org.openehr.adl.ParserTestBase;
import org.openehr.adl.am.AmQuery;
import org.openehr.jaxb.am.Archetype;
import org.openehr.jaxb.am.ArchetypeConstraint;
import org.openehr.jaxb.am.CTerminologyCode;
import org.testng.annotations.Test;

public class ConstraintRefTest extends ParserTestBase {

    @Test
    public void testParseConstraintRef() throws Exception {
        Archetype archetype = parseArchetype("adl14/adl-test-entry.constraint_ref.test.adl");

        String path = "/content[at0001]/items[at0002]/value/defining_code";
        ArchetypeConstraint node = AmQuery.find(archetype, path);
        assertNotNull("node not found at path" + path, node);

        assertTrue("CTerminologyCode expected, instead got: " + node.getClass(),
                node instanceof CTerminologyCode);

        CTerminologyCode ref = (CTerminologyCode) node;
        assertEquals("reference wrong", "ac0001", ref.getCodeList().get(0));
    }
}
