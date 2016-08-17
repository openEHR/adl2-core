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

package org.openehr.adl.parser.adl15;

import org.openehr.adl.ParserTestBase;
import org.openehr.adl.am.AmQuery;
import org.openehr.jaxb.am.Archetype;
import org.openehr.jaxb.am.CAttributeTuple;
import org.openehr.jaxb.am.CComplexObject;
import org.testng.annotations.Test;

import static org.openehr.adl.rm.RmObjectFactory.newIntervalOfReal;
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
