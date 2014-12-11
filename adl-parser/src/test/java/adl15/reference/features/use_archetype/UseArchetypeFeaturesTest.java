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

package adl15.reference.features.use_archetype;

import org.openehr.adl.am.mixin.AmMixins;
import org.openehr.adl.util.TestAdlParser;
import org.openehr.jaxb.am.Archetype;
import org.openehr.jaxb.am.CArchetypeRoot;
import org.openehr.jaxb.am.CAttribute;
import org.openehr.jaxb.rm.MultiplicityInterval;
import org.testng.annotations.Test;

import static org.openehr.adl.rm.RmObjectFactory.newMultiplicityInterval;
import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Marko Pipan
 */
public class UseArchetypeFeaturesTest {
    @Test
    public void testUseArchetype() {
        Archetype archetype = TestAdlParser.parseAdl(
                "adl15/reference/features/use_archetype/openEHR-EHR-COMPOSITION.ext_ref.v1.adls");

        CAttribute aContent = archetype.getDefinition().getAttributes().get(0);
        assertArchetypeRoot((CArchetypeRoot) aContent.getChildren().get(0),
                "openEHR-EHR-SECTION.section_parent.v1", newMultiplicityInterval(0, 1));
        assertArchetypeRoot((CArchetypeRoot) aContent.getChildren().get(1),
                "openEHR-EHR-OBSERVATION.spec_test_obs.v1", newMultiplicityInterval(1, 1));
    }

    private void assertArchetypeRoot(CArchetypeRoot car, String archetypeId, MultiplicityInterval expectedOccurrences) {
        assertThat(car.getArchetypeRef()).isEqualTo(archetypeId);
        assertThat(AmMixins.of(car.getOccurrences()).isEqualTo(expectedOccurrences));
    }
}
