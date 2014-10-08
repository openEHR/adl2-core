/*
 * Copyright (C) 2014 Marand
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

