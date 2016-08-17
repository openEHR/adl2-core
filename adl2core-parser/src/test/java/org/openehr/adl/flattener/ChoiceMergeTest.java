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

package org.openehr.adl.flattener;

import org.openehr.adl.am.AmQuery;
import org.openehr.jaxb.am.Archetype;
import org.openehr.jaxb.am.CAttribute;
import org.openehr.jaxb.am.CComplexObject;
import org.testng.annotations.Test;

import static org.fest.assertions.Assertions.assertThat;
import static org.openehr.adl.rm.RmObjectFactory.newIntervalOfInteger;
import static org.openehr.adl.rm.RmObjectFactory.newIntervalOfReal;

/**
 * @author Marko Pipan
 */
public class ChoiceMergeTest extends FlattenerTestBase {

    @Test
    public void testChoiceOverride() {
        Archetype archetype = parseAndFlattenArchetype("adl14/openEHR-EHR-CLUSTER.amount-range.v1.adl");
        CAttribute choiceAttribute = ((CComplexObject) AmQuery.get(archetype, "items[at0001.1]")).getAttributes().get(0);
        assertThat(choiceAttribute.getRmAttributeName()).isEqualTo("value");
        assertThat(choiceAttribute.isIsMultiple()).isFalse();
        assertThat(choiceAttribute.getChildren()).hasSize(2);

        // specialized dvCount
        CComplexObject dvCount = (CComplexObject) choiceAttribute.getChildren().get(0);
        assertCComplexObject(dvCount, "DV_COUNT", null, null, 1);
        assertCInteger(AmQuery.get(dvCount, "magnitude"), newIntervalOfInteger(2, null), null, null);

        // specialized dvProportion
        CComplexObject dvProportion = (CComplexObject) choiceAttribute.getChildren().get(1);
        assertCComplexObject(dvProportion, "DV_PROPORTION", null, null, 2);
        assertCReal(AmQuery.get(dvProportion, "numerator"), newIntervalOfReal(2.0, null, true, false), null, null);
        assertCReal(AmQuery.get(dvProportion, "denominator"), newIntervalOfReal(2.0, null, false, false), null, null);
    }
}
