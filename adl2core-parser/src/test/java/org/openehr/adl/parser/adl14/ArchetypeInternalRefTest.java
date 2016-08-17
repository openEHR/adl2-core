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
import org.openehr.jaxb.am.ArchetypeInternalRef;
import org.openehr.jaxb.rm.IntervalOfInteger;
import org.testng.annotations.Test;

import static org.openehr.adl.rm.RmObjectFactory.newIntervalOfInteger;
import static org.fest.assertions.Assertions.assertThat;

public class ArchetypeInternalRefTest extends ParserTestBase {
    @Test
    public void testParseInternalRefWithOverwrittingOccurrences()
            throws Exception {
        Archetype archetype = parseArchetype("adl14/adl-test-entry.archetype_internal_ref.test.adl");

        ArchetypeConstraint node = AmQuery.find(archetype, "/attribute2");
        assertTrue("ArchetypeInternalRef expected, actual: " + node.getClass(),
                node instanceof ArchetypeInternalRef);

        ArchetypeInternalRef ref = (ArchetypeInternalRef) node;
        assertEquals("rmType wrong", "SECTION", ref.getRmTypeName());
        assertEquals("path wrong", "/attribute1", ref.getTargetPath());

        IntervalOfInteger occurrences = newIntervalOfInteger(1, 2);
        assertEquals("overwriting occurrences wrong", json(occurrences), json(ref.getOccurrences()));
    }


    @Test
    public void testParseInternalRefWithGenerics() throws Exception {
        Archetype archetype = parseArchetype("adl14/adl-test-SOME_TYPE.generic_type_use_node.draft.adl");

        ArchetypeConstraint node = AmQuery.find(archetype, "/interval_attr2");
        assertTrue("ArchetypeInternalRef expected, actual: " + node.getClass(),
                node instanceof ArchetypeInternalRef);

        ArchetypeInternalRef ref = (ArchetypeInternalRef) node;
        assertEquals("rmType wrong", "INTERVAL<QUANTITY>",
                ref.getRmTypeName());
        assertEquals("path wrong", "/interval_attr[at0001]",
                ref.getTargetPath());
    }

    @Test
    public void testParseInternalRefWithCommentWithSlashAfterOnlyOneSlashInTarget()
            throws Exception {
        Archetype archetype = parseArchetype("adl14/adl-test-entry.archetype_internal_ref2.test.adl");
        assertThat(archetype).isNotNull();

    }

}
