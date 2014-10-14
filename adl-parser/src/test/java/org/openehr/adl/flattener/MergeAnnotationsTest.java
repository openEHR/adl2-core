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
