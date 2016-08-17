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
import org.openehr.jaxb.am.CAttribute;
import org.testng.annotations.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author markopi
 */
public class DifferentialPathTest extends ParserTestBase {
    @Test
    public void testReadDifferentialPath() {
        Archetype archetype = parseArchetype("adl15/openEHR-EHR-EVALUATION.alert-zn.v1.adls");
        CAttribute diff = archetype.getDefinition().getAttributes().get(0);

        assertThat(diff.getRmAttributeName()).isEqualTo("items");
        assertThat(diff.getDifferentialPath()).isEqualTo("/data[at0001]/items");
    }

    @Test
    public void testNonDifferentialAttribute() {
        Archetype archetype = parseArchetype("adl15/openEHR-EHR-EVALUATION.alert.v1.adls");
        CAttribute diff = archetype.getDefinition().getAttributes().get(0);

        assertThat(diff.getRmAttributeName()).isEqualTo("data");
        assertThat(diff.getDifferentialPath()).isNull();
    }
}
