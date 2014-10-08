/*
 * Copyright (C) 2014 Marand
 */

package org.openehr.adl.parser.adl14;

import org.openehr.adl.ParserTestBase;
import org.openehr.jaxb.am.*;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static org.openehr.adl.util.TestUtils.assertCodePhrase;

public class ArchetypeOntologyTest extends ParserTestBase {

    @Test
    public void testParseTermDefinition() throws Exception {
        Archetype archetype = parseArchetype("adl14/adl-test-entry.archetype_ontology.test.adl");
        ArchetypeOntology ontology = archetype.getOntology();

        ArchetypeTerm term = getTermDefinition(ontology, "en", "at0000");
        Map<String, String> map = dictToMap(term.getItems());
        assertEquals("text wrong", "some text", map.get("text"));
        assertEquals("comment wrong", "some comment", map.get("comment"));
        assertEquals("description wrong", "some description",
                map.get("description"));
    }


    /**
     * Tests that term_bindings and constraint_bindings can be written with a tailing s (see http://www.openehr.org/issues/browse/SPEC-284)
     */
    @Test
    public void testBindings() throws Exception {
        Archetype archetype = parseArchetype("adl14/adl-test-entry.archetype_bindings.test.adl");

        ArchetypeOntology ontology = archetype.getOntology();
        List<TermBindingSet> termBindings = ontology.getTermBindings();
        TermBindingSet termBinding = termBindings.get(0);
        assertEquals("term bindings wrong", "SNOMED-CT", termBinding.getTerminology());
        List<TermBindingItem> tbis = termBinding.getItems();
        TermBindingItem tbi = tbis.get(0);
        assertCodePhrase(tbi.getValue(), "SNOMED-CT", "123456");

        List<ConstraintBindingSet> constrBindings = ontology.getConstraintBindings();
        ConstraintBindingSet constrBinding = constrBindings.get(0);

        assertEquals("binding ontology wrong", "SNOMED-CT", constrBinding.getTerminology());
        List<ConstraintBindingItem> cbis = constrBinding.getItems();
        ConstraintBindingItem qbi = cbis.get(0);
        assertEquals("query binding item wrong", "http://openEHR.org/testconstraintbinding", qbi.getValue());


    }

}
