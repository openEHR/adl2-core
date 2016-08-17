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
import org.openehr.jaxb.am.ArchetypeSlot;
import org.openehr.jaxb.am.CArchetypeRoot;
import org.testng.annotations.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author markopi
 */
public class ArchetypeReferenceTest extends ParserTestBase {
    @Test
    public void testSlotDefinition() {
        Archetype parentArchetype = parseArchetype("adl15/openehr-ehr-COMPOSITION.slots.v1.adls");
        ArchetypeSlot slot = AmQuery.get(parentArchetype, "/content[at0001]");

        assertEquals("Bad slot expression",
                "archetype_id/value matches {/openEHR-EHR-EVALUATION\\.problem\\.v1|openEHR-EHR-EVALUATION\\.problem-genetic\\.v1|openEHR-EHR-EVALUATION\\.problem-diagnosis\\.v1|openEHR-EHR-EVALUATION\\.problem-diagnosis-histological\\.v1|openEHR-EHR-EVALUATION\\.injury\\.v1/}",
                slot.getIncludes().get(0).getStringExpression());
    }

    @Test
    public void testFilledSlot() {
        Archetype archetype = parseArchetype("adl15/openEHR-EHR-COMPOSITION.slots-filled.v1.adls");
        CArchetypeRoot root = AmQuery.get(archetype, "/content[at0001]");

        assertThat(root.getRmTypeName()).isEqualTo("EVALUATION");
        assertThat(root.getArchetypeRef()).isEqualTo("openEHR-EHR-EVALUATION.substance_use_summary.v1");
        assertThat(root.getNodeId()).isEqualTo("at0001");

        ArchetypeSlot slot = (ArchetypeSlot) archetype.getDefinition().getAttributes().get(0).getChildren().get(1);
        assertThat(slot.isIsClosed()).isTrue();

    }

}
