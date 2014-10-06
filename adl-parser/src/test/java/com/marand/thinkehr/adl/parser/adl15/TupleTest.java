/*
 * Copyright (C) 2014 Marand
 */

package com.marand.thinkehr.adl.parser.adl15;

import com.marand.thinkehr.adl.ParserTestBase;
import com.marand.thinkehr.adl.am.AmQuery;
import org.openehr.jaxb.am.Archetype;
import org.openehr.jaxb.am.CAttributeTuple;
import org.openehr.jaxb.am.CComplexObject;
import org.testng.annotations.Test;

import static com.marand.thinkehr.adl.rm.RmObjectFactory.newIntervalOfReal;
import static org.fest.assertions.Assertions.assertThat;

/**
 * @author markopi
 */
public class TupleTest extends ParserTestBase {
    @Test
    public void dvQuantityTupleTest() {
        Archetype archetype = parseArchetype("adl15/tuples/openEHR-EHR-OBSERVATION.quantity_tuple.v1.adls");

        CComplexObject container = AmQuery.get(archetype, "data/events[at0003]/data/items/value");
        assertThat(container.getAttributes()).hasSize(1);
        assertThat(container.getAttributes().get(0).getRmAttributeName()).isEqualTo("property");

        assertThat(container.getAttributeTuples()).hasSize(1);

        CAttributeTuple attrTuple = container.getAttributeTuples().get(0);

        // tuple attributes
        assertThat(attrTuple.getMembers()).hasSize(2);
        assertThat(attrTuple.getMembers().get(0).getRmAttributeName()).isEqualTo("units");
        assertThat(attrTuple.getMembers().get(1).getRmAttributeName()).isEqualTo("magnitude");

        // first tuple constraint
        assertThat(attrTuple.getChildren().get(0).getMembers()).hasSize(2);
        assertCString(attrTuple.getChildren().get(0).getMembers().get(0), null, new String[]{"kg"}, null);
        assertCReal(attrTuple.getChildren().get(0).getMembers().get(1), newIntervalOfReal(0.0, 1000.0), null, null);

        // second tuple constraint
        assertThat(attrTuple.getChildren().get(1).getMembers()).hasSize(2);
        assertCString(attrTuple.getChildren().get(1).getMembers().get(0), null, new String[]{"lb"}, null);
        assertCReal(attrTuple.getChildren().get(1).getMembers().get(1), newIntervalOfReal(0.0, 2000.0), null, null);
    }
}
