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

package org.openehr.adl.parser.adl14;

import org.openehr.adl.ParserTestBase;
import org.openehr.jaxb.am.Archetype;
import org.testng.annotations.Test;

/**
 * MostMinimalADLTest
 *
 * @author Rong Chen
 * @version 1.0
 */
public class ArchetypeIdentificationTest extends ParserTestBase {

    @Test
    public void testParse() throws Exception {
        Archetype archetype = parseArchetype("adl14/adl-test-entry.archetype_identification.v1.adl");

        assertEquals("adl_version wrong", "1.4", archetype.getAdlVersion());
        assertEquals("uid wrong", null, archetype.getUid());
    }

    @Test
    public void testWithUid() throws Exception {
        Archetype archetype = parseArchetype("adl14/openEHR-EHR-ELEMENT.uid_test.v1.adl");

        assertEquals("adl_version wrong", "1.4", archetype.getAdlVersion());
        assertEquals("uid wrong", "1.2.456", archetype.getUid().getValue());
    }

    @Test
    public void testWithUidAndControlledFlag() throws Exception {
        Archetype archetype = parseArchetype("adl14/openEHR-EHR-ELEMENT.uid_test.v2.adl");

        assertEquals("adl_version wrong", "1.4", archetype.getAdlVersion());
        assertEquals("uid wrong", "1.2.456::22", archetype.getUid().getValue());
        assertEquals("controlled flag wrong", false, archetype.isIsControlled());
    }

}

