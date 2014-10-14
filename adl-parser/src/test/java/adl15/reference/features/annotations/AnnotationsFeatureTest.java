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

package adl15.reference.features.annotations;

import org.openehr.adl.util.TestAdlParser;
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
