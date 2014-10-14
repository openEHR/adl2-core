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
import org.openehr.adl.am.AmQuery;
import org.openehr.jaxb.am.Archetype;
import org.openehr.jaxb.am.ArchetypeConstraint;
import org.openehr.jaxb.am.CTerminologyCode;
import org.testng.annotations.Test;

public class ConstraintRefTest extends ParserTestBase {

    @Test
    public void testParseConstraintRef() throws Exception {
        Archetype archetype = parseArchetype("adl14/adl-test-entry.constraint_ref.test.adl");

        String path = "/content[at0001]/items[at0002]/value/defining_code";
        ArchetypeConstraint node = AmQuery.find(archetype, path);
        assertNotNull("node not found at path" + path, node);

        assertTrue("CTerminologyCode expected, instead got: " + node.getClass(),
                node instanceof CTerminologyCode);

        CTerminologyCode ref = (CTerminologyCode) node;
        assertEquals("reference wrong", "ac0001", ref.getCodeList().get(0));
    }
}
