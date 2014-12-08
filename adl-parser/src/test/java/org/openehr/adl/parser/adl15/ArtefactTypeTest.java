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
        assertThat(template.isIsTemplate()).isFalse();
    }

    @Test
    public void testRecognizeArchetype() {
        Archetype template = parseArchetype("adl15/openEHR-EHR-EVALUATION.alert-zn.v1.adls");

        assertThat(template.isIsOverlay()).isFalse();
        assertThat(template.isIsTemplate()).isFalse();
    }
}
