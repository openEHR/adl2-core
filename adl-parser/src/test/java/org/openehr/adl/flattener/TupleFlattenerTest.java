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
import org.openehr.jaxb.am.CComplexObject;
import org.testng.annotations.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Marko Pipan
 */
public class TupleFlattenerTest extends FlattenerTestBase {

    @Test
    public void testFlattenerKeepsParentTuples() {
        Archetype flattened = parseAndFlattenArchetype("adl15/flattener/openEHR-EHR-OBSERVATION.body_temperature_override_tuple.v1.0.0.adls");

        CComplexObject value = AmQuery.get(flattened, "/data[id3]/events[id4]/data[id2]/items[id5]/value");
        assertThat(value.getAttributeTuples()).hasSize(1);
        assertThat(value.getAttributeTuples().get(0).getChildren()).hasSize(2);
    }
}
