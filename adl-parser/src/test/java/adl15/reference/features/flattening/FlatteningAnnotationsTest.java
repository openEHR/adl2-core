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

package adl15.reference.features.flattening;

import com.google.common.collect.ImmutableMap;
import org.openehr.adl.flattener.ArchetypeFlattener;
import org.openehr.adl.rm.OpenEhrRmModel;
import org.openehr.adl.util.TestAdlParser;
import org.openehr.jaxb.am.DifferentialArchetype;
import org.openehr.jaxb.am.FlatArchetype;
import org.openehr.jaxb.rm.ResourceAnnotationNodeItems;
import org.openehr.jaxb.rm.ResourceAnnotationNodes;
import org.openehr.jaxb.rm.StringDictionaryItem;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Marko Pipan
 */
public class FlatteningAnnotationsTest {
    private ArchetypeFlattener flattener;

    @BeforeClass
    public void setup() {
        flattener = new ArchetypeFlattener(OpenEhrRmModel.getInstance());
    }

    @Test
    public void testOnlyChild() {
        DifferentialArchetype parent = TestAdlParser.parseAdl(
                "adl15/reference/features/annotations/openEHR-EHR-EVALUATION.annotations_parent.v1.adls");
        DifferentialArchetype specialized = TestAdlParser.parseAdl(
                "adl15/reference/features/annotations/openEHR-EHR-EVALUATION.annotations_only_child.v1.adls");

        FlatArchetype flattened = flatten(parent, specialized);

        assertThat(flattened.getAnnotations().getItems()).hasSize(1);
        ResourceAnnotationNodes annotations = flattened.getAnnotations().getItems().get(0);
        assertThat(annotations.getLanguage()).isEqualTo("en");
        assertThat(annotations.getItems()).hasSize(2);

        assertThat(getAnnotation(annotations, "/data[id2]")).isEqualTo(ImmutableMap.of(
                "ui", "passthrough"));
        assertThat(getAnnotation(annotations, "/data[id2]/items[id3]")).isEqualTo(ImmutableMap.of(
                "requirements note", "this is a requirements note on Statement",
                "medline ref", "this is a medline ref on Statement",
                "design note", "this is a SPECIALISED design note on Statement",
                "NEW TAG", "this is a SPECIALISED design note on Statement"));
    }

    @Test
    public void testFirstChild() {
        DifferentialArchetype parent = TestAdlParser.parseAdl(
                "adl15/reference/features/annotations/openEHR-EHR-EVALUATION.annotations_parent.v1.adls");
        DifferentialArchetype specialized = TestAdlParser.parseAdl(
                "adl15/reference/features/annotations/openEHR-EHR-EVALUATION.annotations_1st_child.v1.adls");

        FlatArchetype flattened = flatten(parent, specialized);

        assertThat(flattened.getAnnotations().getItems()).hasSize(1);
        ResourceAnnotationNodes annotations = flattened.getAnnotations().getItems().get(0);
        assertThat(annotations.getLanguage()).isEqualTo("en");
        assertThat(annotations.getItems()).hasSize(4);

        assertThat(getAnnotation(annotations, "/data[id2]")).isEqualTo(ImmutableMap.of(
                "ui", "passthrough"));
        assertThat(getAnnotation(annotations, "/data[id2]/items[id3]")).isEqualTo(ImmutableMap.of(
                "requirements note", "this is a requirements note on Statement",
                "medline ref", "this is a medline ref on Statement",
                "design note", "this is a SPECIALISED design note on Statement",
                "NEW TAG", "this is a SPECIALISED design note on Statement"));
        assertThat(getAnnotation(annotations, "/data[id2]/items[id0.10]")).isEqualTo(ImmutableMap.of(
                "design note", "this is a design note on intelerance",
                "requirements note", "this is a requirements note on intolerance",
                "national data dictionary", "NDD ref for intolerance"));
        assertThat(getAnnotation(annotations, "/data[id2]/items[id0.8]")).isEqualTo(ImmutableMap.of(
                "design note", "this is a design note on allergic reaction",
                "requirements note", "this is a requirements note on allergic reaction",
                "medline ref", "this is a medline ref on allergic reaction"));
    }

    private Map<String, String> getAnnotation(ResourceAnnotationNodes annotations, String path) {
        for (ResourceAnnotationNodeItems annotation : annotations.getItems()) {
            if (annotation.getPath().equals(path)) {
                Map<String, String> result = new LinkedHashMap<>();
                for (StringDictionaryItem item : annotation.getItems()) {
                    result.put(item.getId(), item.getValue());
                }
                return result;
            }
        }
        throw new IllegalArgumentException("No annotation with path: " + path);
    }

    private FlatArchetype flatten(DifferentialArchetype parent, DifferentialArchetype specialized) {
        FlatArchetype flatParent = flattener.flatten(null, parent);
        return flattener.flatten(flatParent, specialized);
    }
}
