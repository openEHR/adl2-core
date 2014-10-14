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

package org.openehr.adl.parser.adl15;

import org.openehr.adl.ParserTestBase;
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
