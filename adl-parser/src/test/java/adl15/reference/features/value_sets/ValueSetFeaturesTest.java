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

package adl15.reference.features.value_sets;

import org.openehr.adl.util.TestAdlParser;
import org.openehr.jaxb.am.Archetype;
import org.openehr.jaxb.am.ValueSetItem;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Marko Pipan
 */
public class ValueSetFeaturesTest {
    @Test
    public void testInternalFeaturesTest() {
        Archetype archetype = TestAdlParser.parseAdl(
                "adl15/reference/features/value_sets/openEHR-EHR-OBSERVATION.internal_value_set.v1.adls");

        final List<ValueSetItem> valueSets = archetype.getTerminology().getValueSets();
        assertThat(valueSets).hasSize(6);
        assertValueSetItem(valueSets.get(0), "ac1", "at1001", "at1002", "at1003", "at1004", "at1015");
        assertValueSetItem(valueSets.get(1), "ac2", "at1045", "at1046");
        assertValueSetItem(valueSets.get(5), "ac6", "at1012", "at1013");

    }

    private void assertValueSetItem(ValueSetItem vsi, String id, String... codes) {
        assertThat(vsi.getId()).isEqualTo(id);
        assertThat(vsi.getMembers()).isEqualTo(Arrays.asList(codes));
    }
}
