/*
 * Copyright (C) 2014 Marand
 */

package com.marand.thinkehr.adl.parser.adl14;

import com.marand.thinkehr.adl.ParserTestBase;
import org.openehr.jaxb.am.Archetype;
import org.testng.annotations.Test;

/**
 * Testcase verifies parsing of abnormal archetype structures
 * to ensure backwards compatibility on 'old' archetypes
 *
 * @author Rong Chen
 */
public class EmptyOtherContributorsTest extends ParserTestBase {


    @Test
    public void testParseEmptyOtherContributors() throws Exception {
        Archetype archetype = parseArchetype("adl14/adl-test-entry.empty_other_contributors.test.adl");

        assertTrue("other_contributors not empty",
                archetype.getDescription().getOtherContributors().isEmpty());
    }
}