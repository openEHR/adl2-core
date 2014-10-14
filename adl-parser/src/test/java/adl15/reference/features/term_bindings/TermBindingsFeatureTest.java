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

package adl15.reference.features.term_bindings;

import org.openehr.adl.util.TestAdlParser;
import org.openehr.jaxb.am.Archetype;
import org.openehr.jaxb.am.TermBindingItem;
import org.openehr.jaxb.am.TermBindingSet;
import org.testng.annotations.Test;

import java.util.List;

import static org.openehr.adl.am.AmObjectFactory.newTermBindingItem;
import static org.openehr.adl.rm.RmObjectFactory.newCodePhrase;
import static org.openehr.adl.rm.RmObjectFactory.newTerminologyId;
import static org.openehr.adl.util.TestUtils.assertCodePhrase;
import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Marko Pipan
 */
public class TermBindingsFeatureTest {
    @Test
    public void testConstraintBindingMultiple() {
        Archetype archetype = TestAdlParser.parseAdl("adl15/reference/features/term_bindings/openEHR-EHR-OBSERVATION.constraint_binding_multiple.v1.adls");

        final List<TermBindingSet> tb = archetype.getOntology().getTermBindings();
        assertThat(tb).hasSize(2);
        assertThat(tb.get(0).getTerminology()).isEqualTo("AIR93(1.0.0)");
        assertThat(tb.get(0).getItems().get(0).getCode()).isEqualTo("ac2");
        assertCodePhrase(tb.get(0).getItems().get(0).getValue(), "http://air93.org", "MZN-SIF-Dihanje-Kašelj");
    }

    @Test
    public void testTermBindingsBasic() {
        Archetype archetype = TestAdlParser.parseAdl("adl15/reference/features/term_bindings/openEHR-EHR-OBSERVATION.term_bindings_basic.adls");
        final List<TermBindingSet> tb = archetype.getOntology().getTermBindings();
        assertThat(tb).hasSize(1);
        assertThat(tb.get(0).getTerminology()).isEqualTo("LNC205");


        assertThat(tb.get(0).getItems().get(0).getCode()).isEqualTo("id5");

        assertTermBindingItem(tb.get(0).getItems().get(0),
                newTermBindingItem("id5", newCodePhrase(newTerminologyId("http://LNC205.org/id"), "9272-6")));
        assertTermBindingItem(tb.get(0).getItems().get(1),
                newTermBindingItem("id7", newCodePhrase(newTerminologyId("http://LNC205.org/id"), "9271-8")));

    }


    @Test
    public void testTermBindingsPaths() {
        Archetype archetype = TestAdlParser.parseAdl("adl15/reference/features/term_bindings/openEHR-EHR-OBSERVATION.term_bindings_paths.adls");
        final List<TermBindingSet> tb = archetype.getOntology().getTermBindings();
        assertThat(tb).hasSize(1);

        assertTermBindingItem(tb.get(0).getItems().get(0),
                newTermBindingItem("/data[id3]/events[id4]/data[id2]/items[id5]",
                        newCodePhrase(newTerminologyId("http://LNC205.org/id"), "9272-6")));

    }

    // update when get new am model for value_sets
    @Test(enabled=false)
    public void testTermBindingsPathsUseRefs() {
        Archetype archetype = TestAdlParser.parseAdl("adl15/reference/features/term_bindings/openEHR-EHR-OBSERVATION.term_bindings_paths_use_refs.v1.adls");
        final List<TermBindingSet> tb = archetype.getOntology().getTermBindings();
        assertThat(tb).hasSize(2);

//        assertTermBindingItem(tb.get(0).getItems().get(0),
//                newTermBindingItem("/data[id3]/events[id4]/data[id2]/items[id5]",
//                        newCodePhrase(newTerminologyId("http://LNC205.org/id"), "9272-6")));

    }



    private void assertTermBindingItem(TermBindingItem actual, TermBindingItem expected) {
        assertThat(actual.getCode()).isEqualTo(expected.getCode());
        assertCodePhrase(actual.getValue(), expected.getValue().getTerminologyId().getValue(), expected.getValue().getCodeString());
    }


}
