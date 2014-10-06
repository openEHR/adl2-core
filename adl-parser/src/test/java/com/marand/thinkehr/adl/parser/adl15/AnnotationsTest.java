/*
 * Copyright (C) 2014 Marand
 */

package com.marand.thinkehr.adl.parser.adl15;

import com.marand.thinkehr.adl.ParserTestBase;
import org.openehr.jaxb.am.Archetype;
import org.openehr.jaxb.rm.Annotation;
import org.openehr.jaxb.rm.AnnotationSet;
import org.testng.annotations.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author markopi
 */
public class AnnotationsTest extends ParserTestBase {
    @Test
    public void testRmPath() {
        Archetype archetype = parseArchetype("adl15/annotations/openEHR-EHR-COMPOSITION.annotations_rm_path.v1.adls");

        assertThat(archetype.getAnnotations()).hasSize(1);
        AnnotationSet en = archetype.getAnnotations().get(0);

        assertThat(en.getLanguage()).isEqualTo("en");
        assertThat(en.getItems()).hasSize(3);

        Annotation facilityName = en.getItems().get(2);
        assertThat(facilityName.getPath()).isEqualTo("/context/health_care_facility/name");
        assertThat(facilityName.getItems()).hasSize(1);
        assertThat(facilityName.getItems().get(0).getId())
                .isEqualTo("design note");
        assertThat(facilityName.getItems().get(0).getValue())
                .isEqualTo("Note on use of non-archteyped context/health_care_facility/name RM element in this data");
    }
}
