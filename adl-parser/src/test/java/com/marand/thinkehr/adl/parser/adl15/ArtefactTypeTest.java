/*
 * Copyright (C) 2014 Marand
 */

package com.marand.thinkehr.adl.parser.adl15;

import com.marand.thinkehr.adl.ParserTestBase;
import org.openehr.jaxb.am.Archetype;
import org.testng.annotations.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author markopi
 */
public class ArtefactTypeTest extends ParserTestBase {
    @Test
    public void testRecognizeTemplate() {
        Archetype template = parseArchetype("adl15/artefact_type/openEHR-EHR-COMPOSITION.template.v1.adls");

        assertThat(template.isIsOverlay()).isFalse();
        assertThat(template.isIsTemplate()).isTrue();
    }

    @Test
    public void testRecognizeOverlay() {
        Archetype template = parseArchetype("adl15/artefact_type/openEHR-EHR-ADMIN_ENTRY.template_overlay.v1.adls");

        assertThat(template.isIsOverlay()).isTrue();
        assertThat(template.isIsTemplate()).isTrue();
    }

    @Test
    public void testRecognizeArchetype() {
        Archetype template = parseArchetype("adl15/openEHR-EHR-EVALUATION.alert-zn.v1.adls");

        assertThat(template.isIsOverlay()).isFalse();
        assertThat(template.isIsTemplate()).isFalse();
    }
}
