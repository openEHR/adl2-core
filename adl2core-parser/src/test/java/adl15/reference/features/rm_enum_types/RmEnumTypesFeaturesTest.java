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

package adl15.reference.features.rm_enum_types;

import org.openehr.adl.am.AmQuery;
import org.openehr.adl.am.mixin.AmMixin;
import org.openehr.adl.am.mixin.AmMixins;
import org.openehr.adl.util.TestAdlParser;
import org.openehr.jaxb.am.Archetype;
import org.openehr.jaxb.am.CComplexObject;
import org.openehr.jaxb.am.CInteger;
import org.openehr.jaxb.am.CObject;
import org.testng.annotations.Test;

import static org.fest.assertions.Assertions.assertThat;
import static org.testng.Assert.assertTrue;

/**
 * @author Marko Pipan
 */
public class RmEnumTypesFeaturesTest {
    @Test
    public void testEnumType() {
        Archetype sourceArchetype = TestAdlParser.parseAdl(
                "adl15/reference/features/rm_enum_types/openEHR-EHR-OBSERVATION.enum_type_1.v1.adls");

        CComplexObject c = AmQuery.find(sourceArchetype, "data[id2]/events[id3]/data[id4]");
        CObject c1 = AmQuery.find(c, "items[id5]/value");
        CObject c2 = AmQuery.find(c, "items[id7]/value");

        CInteger ci1 = AmQuery.find(c1, "type");
        assertTrue(AmMixins.of(ci1.getConstraint().get(0)).isSingleValued(1));

        CInteger ci2 = AmQuery.find(c2, "type");
        assertTrue(AmMixins.of(ci2.getConstraint().get(0)).isSingleValued(2));
        assertTrue(AmMixins.of(ci2.getConstraint().get(1)).isSingleValued(3));
    }

}
