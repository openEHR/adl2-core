/*
 * ADL2-core
 * Copyright (c) 2013-2014 Marand d.o.o. (www.marand.com)
 *
 * This file is part of ADL2-core.
 *
 * ADL2-core is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openehr.adl.parser.adl14;

import org.openehr.adl.ParserTestBase;
import org.openehr.jaxb.am.*;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;
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
        assertThat(tbi.getValue()).isEqualTo("SNOMED-CT::123456");

        List<ConstraintBindingSet> constrBindings = ontology.getConstraintBindings();
        ConstraintBindingSet constrBinding = constrBindings.get(0);

        assertEquals("binding ontology wrong", "SNOMED-CT", constrBinding.getTerminology());
        List<ConstraintBindingItem> cbis = constrBinding.getItems();
        ConstraintBindingItem qbi = cbis.get(0);
        assertEquals("query binding item wrong", "http://openEHR.org/testconstraintbinding", qbi.getValue());


    }

}
