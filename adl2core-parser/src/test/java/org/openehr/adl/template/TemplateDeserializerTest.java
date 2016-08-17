package org.openehr.adl.template;

import org.openehr.adl.TestingArchetypeProvider;
import org.openehr.jaxb.am.Template;
import org.testng.annotations.Test;

import static org.fest.assertions.Assertions.assertThat;
import static org.testng.Assert.*;

/**
 * @author markopi
 */
public class TemplateDeserializerTest {

    @Test
    public void testDeserialize() throws Exception {
        Template template = TemplateDeserializer.deserialize(getClass().getClassLoader().getResourceAsStream(
                "adl15/template/openEHR-EHR-COMPOSITION.mini_vitals.v1.adlt"));

        assertThat(template.getOverlays()).hasSize(2);
        assertThat(template.getOverlays().get(0).getArchetypeId().getValue())
                .isEqualTo("openEHR-EHR-OBSERVATION.ovl-a-blood_pressure-001.v1.0.0");
        assertThat(template.getOverlays().get(1).getArchetypeId().getValue())
                .isEqualTo("openEHR-EHR-OBSERVATION.ovl-a-respiration-001.v1.0.0");
    }
}