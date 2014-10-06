/*
 * Copyright (C) 2014 Marand
 */

package adl15.reference.features.annotations;

import com.marand.thinkehr.adl.util.TestAdlParser;
import org.openehr.jaxb.am.Archetype;
import org.openehr.jaxb.rm.Annotation;
import org.openehr.jaxb.rm.AnnotationSet;
import org.testng.annotations.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Marko Pipan
 */
public class AnnotationsFeatureTest {
    @Test
    public void rmPath() {
        Archetype archetype = TestAdlParser.parseAdl("adl15/reference/features/annotations/openEHR-EHR-COMPOSITION.annotations_rm_path.v1.adls");

        AnnotationSet as = archetype.getAnnotations().get(0);
        assertThat(as.getLanguage()).isEqualTo("en");
        Annotation a = as.getItems().get(0);
        assertThat(a.getPath()).isEqualTo("/context/start_time");
    }

    @Test
    public void testOnlyChild() {
        Archetype archetype = TestAdlParser.parseAdl("adl15/reference/features/annotations/openEHR-EHR-EVALUATION.annotations_only_child.v1.adls");

        AnnotationSet as = archetype.getAnnotations().get(0);
        assertThat(as.getLanguage()).isEqualTo("en");
        Annotation a = as.getItems().get(0);
        assertThat(a.getPath()).isEqualTo("/data[id2]/items[id3]");
        assertThat(a.getItems().get(0).getId()).isEqualTo("design note");

    }

    @Test
    public void testParent() {
        Archetype archetype = TestAdlParser.parseAdl("adl15/reference/features/annotations/openEHR-EHR-EVALUATION.annotations_parent.v1.adls");

        AnnotationSet as = archetype.getAnnotations().get(0);
        assertThat(as.getLanguage()).isEqualTo("en");
        Annotation a = as.getItems().get(0);
        assertThat(a.getPath()).isEqualTo("/data[id2]");
        assertThat(a.getItems().get(0).getId()).isEqualTo("ui");
        assertThat(a.getItems().get(0).getValue()).isEqualTo("passthrough");
    }
}
