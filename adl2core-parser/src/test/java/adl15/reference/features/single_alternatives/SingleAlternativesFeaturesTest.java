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

package adl15.reference.features.single_alternatives;

import org.openehr.adl.util.TestAdlParser;
import org.openehr.jaxb.am.Archetype;
import org.openehr.jaxb.am.CObject;
import org.testng.annotations.Test;

import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Marko Pipan
 */
public class SingleAlternativesFeaturesTest {
    @Test
    public void testMultipleAlternatives() {
        Archetype sourceArchetype = TestAdlParser.parseAdl(
                "adl15/reference/features/single_alternatives/openEHR-EHR-OBSERVATION.multiple_alternatives.v1.adls");

        List<CObject> alternatives = sourceArchetype.getDefinition().getAttributes().get(0).getChildren();
        assertThat(alternatives.get(0).getRmTypeName()).isEqualTo("ITEM_TREE");
        assertThat(alternatives.get(0).getNodeId()).isEqualTo("id2");
        assertThat(alternatives.get(1).getRmTypeName()).isEqualTo("ITEM_TREE");
        assertThat(alternatives.get(1).getNodeId()).isEqualTo("id3");

    }
}
