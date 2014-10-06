/*
 * Copyright (C) 2014 Marand
 */

package com.marand.thinkehr.adl.parser.adl15;

import com.marand.thinkehr.adl.ParserTestBase;
import com.marand.thinkehr.adl.am.AmQuery;
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
        assertThat(root.getArchetypeId().getValue()).isEqualTo("openEHR-EHR-EVALUATION.substance_use_summary.v1");
        assertThat(root.getNodeId()).isEqualTo("at0001");

    }

}
