/*
 * Copyright (C) 2014 Marand
 */

package adl15.reference.features.identification;

import org.openehr.adl.util.TestAdlParser;
import org.openehr.jaxb.am.Archetype;
import org.testng.annotations.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Marko Pipan
 */
public class IdentificationFeaturesTest {
    @Test
    public void testFullId() {
        Archetype archetype = TestAdlParser.parseAdl(
                "adl15/reference/features/identification/openEHR-EHR-OBSERVATION.full_id_1.v1.adls");
        assertThat(archetype.getArchetypeId().getValue()).isEqualTo("org.openehr::openEHR-EHR-OBSERVATION.full_id_1.v1.0.4");
    }
}
