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

package org.openehr.adl.am;

import org.testng.annotations.Test;

import static org.fest.assertions.Assertions.assertThat;

public class ArchetypeIdInfoTest {

    @Test
    public void testParse() {
        ArchetypeIdInfo info;

        info = ArchetypeIdInfo.parse("openEHR-EHR-COMPOSITION.encounter.v1.0.0");
        assertArchetypeIdInfo(info, null, "openEHR", "EHR", "COMPOSITION", "encounter", 1, 0, 0);

        info = ArchetypeIdInfo.parse("namespace::openEHR-EHR-COMPOSITION.fancy-pants_1.v1.0");
        assertArchetypeIdInfo(info, "namespace", "openEHR", "EHR", "COMPOSITION", "fancy-pants_1", 1, 0, null);
    }

    @Test
    public void testToString() {
        ArchetypeIdInfo info;
        info = ArchetypeIdInfo.ofOpenEHR(null, "COMPOSITION", "encounter", 1, 0, null);
        assertThat(info.toString()).isEqualTo("openEHR-EHR-COMPOSITION.encounter.v1.0");

        info = ArchetypeIdInfo.ofOpenEHR("namespace_X", "COMPOSITION", "encounter", 1, 0, 1);
        assertThat(info.toString()).isEqualTo("namespace_X::openEHR-EHR-COMPOSITION.encounter.v1.0.1");

    }

    @Test
    public void testToInterfaceString() {
        ArchetypeIdInfo info;
        info = ArchetypeIdInfo.ofOpenEHR(null, "COMPOSITION", "encounter", 1, 0, null);
        assertThat(info.toInterfaceString()).isEqualTo("openEHR-EHR-COMPOSITION.encounter.v1");

        info = ArchetypeIdInfo.ofOpenEHR("namespace_X", "COMPOSITION", "encounter", 1, 0, 1);
        assertThat(info.toInterfaceString()).isEqualTo("namespace_X::openEHR-EHR-COMPOSITION.encounter.v1");

    }

    private void assertArchetypeIdInfo(ArchetypeIdInfo info, String namespace,
                                       String openEhr, String ehr, String rmType, String identifier,
                                       Integer versionMajor, Integer versionMinor, Integer versionPatch) {
        assertThat(info.getNamespace()).isEqualTo(namespace);
        assertThat(info.getOpenEHR()).isEqualTo(openEhr);
        assertThat(info.getEhr()).isEqualTo(ehr);
        assertThat(info.getRmType()).isEqualTo(rmType);
        assertThat(info.getIdentifier()).isEqualTo(identifier);
        assertThat(info.getVersionMajor()).isEqualTo(versionMajor);
        assertThat(info.getVersionMinor()).isEqualTo(versionMinor);
        assertThat(info.getVersionPatch()).isEqualTo(versionPatch);
    }
}