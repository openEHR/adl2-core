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
public class MostMinimalADLTest extends ParserTestBase {

    @Test
    public void testParse() throws Exception {
        Archetype archetype = parseArchetype("adl14/adl-test-entry.most_minimal.test.adl");

        assertEquals("originalLanguage wrong", "en",
                archetype.getOriginalLanguage().getCodeString());

    }
}

