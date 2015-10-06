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

package adl15.reference.features.tuples;

import org.openehr.adl.am.AmQuery;
import org.openehr.adl.am.mixin.AmMixins;
import org.openehr.adl.util.TestAdlParser;
import org.openehr.jaxb.am.*;
import org.openehr.jaxb.rm.IntervalOfInteger;
import org.openehr.jaxb.rm.IntervalOfReal;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static org.openehr.adl.rm.RmObjectFactory.newIntervalOfReal;
import static org.fest.assertions.Assertions.assertThat;
import static org.testng.AssertJUnit.assertTrue;

/**
 * @author Marko Pipan
 */
public class TuplesFeaturesTest {

    @Test
    public void testDvOrdinals() {
        Archetype archetype = TestAdlParser.parseAdl("adl15/reference/features/tuples/openehr-test_pkg-SOME_TYPE.dv_ordinals.v1.adls");
        CAttribute cstandard = archetype.getDefinition().getAttributes().get(0);

        assertThat(cstandard.getChildren()).hasSize(3);
        assertStandardOrdinalAttr(((CComplexObject) cstandard.getChildren().get(0)), "id5", 0, "at2");
        assertStandardOrdinalAttr(((CComplexObject) cstandard.getChildren().get(1)), "id6", 1, "at3");
        assertStandardOrdinalAttr(((CComplexObject) cstandard.getChildren().get(2)), "id7", 2, "at4");


        CComplexObject cclinical = AmQuery.get(archetype, "clinical_ordinal_attr_1");
        CAttributeTuple cat = cclinical.getAttributeTuples().get(0);

        assertOrdinalTuple(cat.getChildren().get(0), 0, "at2");
        assertOrdinalTuple(cat.getChildren().get(1), 1, "at3");
        assertOrdinalTuple(cat.getChildren().get(2), 2, "at4");

    }

    @Test
    public void testOrdinalTuple() {
        Archetype archetype = TestAdlParser.parseAdl("adl15/reference/features/tuples/openEHR-EHR-OBSERVATION.ordinal_tuple.v1.adls");
        CComplexObject cobj = AmQuery.get(archetype, "data[id3]/events[id4]/data[id2]/items[id10]/value");
        assertThat(cobj.getAttributes()).isEmpty();
        assertThat(cobj.getAttributeTuples()).hasSize(1);
        CAttributeTuple cat = cobj.getAttributeTuples().get(0);

        assertThat(cat.getMembers()).hasSize(2);
        assertThat(cat.getMembers().get(0).getRmAttributeName()).isEqualTo("value");
        assertThat(cat.getMembers().get(1).getRmAttributeName()).isEqualTo("symbol");

        assertOrdinalTuple(cat.getChildren().get(0), 0, "at11");
        assertOrdinalTuple(cat.getChildren().get(1), 1, "at12");
        assertOrdinalTuple(cat.getChildren().get(2), 2, "at13");
    }
    @Test
    public void testQuantityNodeIds() {
        Archetype archetype = TestAdlParser.parseAdl(
                "adl15/reference/features/tuples/openehr-test_pkg-SOME_TYPE.dv_quantity_node_ids.v1.adls");
        CTerminologyCode cq1 = AmQuery.get(archetype, "clinical_quantity_attr_2[id4]/property");
        assertThat(cq1.getCodeList()).containsExactly("at4");
        CString cq2 = AmQuery.get(archetype, "clinical_quantity_attr_2[id4]/units");
        assertThat(cq2.getConstraint()).containsExactly("C", "F");

        cq1 = AmQuery.get(archetype, "clinical_quantity_attr_2[id5]/property");
        assertThat(cq1.getCodeList()).containsExactly("at5");
        cq2 = AmQuery.get(archetype, "clinical_quantity_attr_2[id5]/units");
        assertThat(cq2.getConstraint()).containsExactly("K", "F");
    }

    @Test
    public void testQuantityTuple() {
        Archetype archetype = TestAdlParser.parseAdl(
                "adl15/reference/features/tuples/openehr-test_pkg-SOME_TYPE.dv_quantity_tuple.v1.adls");
        CComplexObject cobj = AmQuery.get(archetype, "clinical_quantity_attr_1");
        assertThat(cobj.getAttributes()).hasSize(1);
        assertThat(cobj.getAttributes().get(0).getRmAttributeName()).isEqualTo("property");
        CAttributeTuple cat = cobj.getAttributeTuples().get(0);
        assertThat(cat.getMembers().get(0).getRmAttributeName()).isEqualTo("units");
        assertThat(cat.getMembers().get(1).getRmAttributeName()).isEqualTo("magnitude");

        assertQuantityTuple(cat.getChildren().get(0), "C", newIntervalOfReal(4.0, null));
        assertQuantityTuple(cat.getChildren().get(1), "F", newIntervalOfReal(40.0, null));

    }

    @Test
    public void testQuantityVariations() {
        Archetype archetype = TestAdlParser.parseAdl(
                "adl15/reference/features/tuples/openehr-test_pkg-SOME_TYPE.dv_quantity_variations_1.v1.adls");
    }

    @Test
    public void testPropertyCodePhrase() {
        Archetype archetype = TestAdlParser.parseAdl(
                "adl15/reference/features/tuples/openehr-test_pkg-SOME_TYPE.property_code_phrase.v1.adls");
    }

    @Test
    public void testTerms() {
        Archetype archetype = TestAdlParser.parseAdl(
                "adl15/reference/features/tuples/openehr-test_pkg-SOME_TYPE.terms.v1.adls");
    }



    private void assertOrdinalTuple(CObjectTuple cot, int value, String... symbols) {
        List<IntervalOfInteger> intervals = ((CInteger) (cot.getMembers().get(0))).getConstraint();
        assertThat(intervals).isNotEmpty();
        assertTrue("interval", AmMixins.of(intervals.get(0)).isSingleValued(value));

        assertTrue("value", AmMixins.of(intervals.get(0)).isSingleValued(value));
        assertThat(((CTerminologyCode) (cot.getMembers().get(1))).getCodeList()).containsExactly(symbols);
    }

    private void assertQuantityTuple(CObjectTuple cot, String units, IntervalOfReal expectedRange) {
        assertThat(((CString) (cot.getMembers().get(0))).getConstraint()).containsExactly(units);

        IntervalOfReal actualRange = ((CReal) (cot.getMembers().get(1))).getConstraint().get(0);
        assertThat(AmMixins.of(actualRange).isEqualTo(expectedRange));

    }

    private void assertStandardOrdinalAttr(CComplexObject cord, String nodeId, int value, String... symbol) {
        assertThat(cord.getNodeId()).isEqualTo(nodeId);
        CInteger c = AmQuery.get(cord, "value");

        assertTrue("value", AmMixins.of(c.getConstraint().get(0)).isSingleValued(value));
        assertThat(((CTerminologyCode) AmQuery.get(cord, "symbol/defining_code")).getCodeList()).containsExactly(symbol);
    }
}
