/*
 * Copyright (C) 2014 Marand
 */

package com.marand.thinkehr.adl.parser.adl14;

import com.marand.thinkehr.adl.ParserTestBase;
import com.marand.thinkehr.adl.am.AmQuery;
import org.openehr.jaxb.am.Archetype;
import org.openehr.jaxb.am.ArchetypeConstraint;
import org.openehr.jaxb.am.ArchetypeInternalRef;
import org.openehr.jaxb.rm.IntervalOfInteger;
import org.testng.annotations.Test;

import static com.marand.thinkehr.adl.rm.RmObjectFactory.newIntervalOfInteger;
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
