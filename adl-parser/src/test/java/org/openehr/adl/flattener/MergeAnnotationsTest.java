/*
 * Copyright (C) 2014 Marand
 */

package org.openehr.adl.flattener;

import org.openehr.jaxb.am.FlatArchetype;
import org.openehr.jaxb.rm.Annotation;
import org.openehr.jaxb.rm.AnnotationSet;
import org.testng.annotations.Test;

import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;
import static org.fest.assertions.MapAssert.entry;

/**
 * @author markopi
 */
public class MergeAnnotationsTest extends FlattenerTestBase {
    @Test
    public void testMergeAnnotations() {
        FlatArchetype archetype = parseAndFlattenArchetype("adl15/annotations/openEHR-EHR-EVALUATION.annotations_1st_child.v1.adls");

        assertThat(archetype.getAnnotations()).hasSize(1);

        AnnotationSet en = archetype.getAnnotations().get(0);
        assertThat(en.getLanguage()).isEqualTo("en");
        assertThat(en.getItems()).hasSize(4);

        Map<String, Annotation> enMap = annotationToMap(en.getItems());

        assertThat(enMap.get("/data[at0001]/items[at0.8]").getItems()).hasSize(3);
        Map<String, String> at08 = dictToMap(enMap.get("/data[at0001]/items[at0.8]").getItems());
        assertThat(at08).includes(
                entry("design note", "this is a design note on allergic reaction"),
                entry("requirements note", "this is a requirements note on allergic reaction"),
                entry("medline ref", "this is a medline ref on allergic reaction"));

        Map<String, String> at01 = dictToMap(enMap.get("/data[at0001]").getItems());
        assertThat(at01).includes(entry("ui", "passthrough"));


    }
}
