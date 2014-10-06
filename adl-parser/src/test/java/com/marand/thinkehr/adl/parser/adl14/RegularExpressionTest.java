/*
 * Copyright (C) 2014 Marand
 */

package com.marand.thinkehr.adl.parser.adl14;

import com.marand.thinkehr.adl.ParserTestBase;
import org.openehr.jaxb.am.Archetype;
import org.testng.annotations.Test;

public class RegularExpressionTest extends ParserTestBase {


    @Test
    public void testParseRegularExpressions() throws Exception {
        Archetype archetype = parseArchetype("adl14/adl-test-entry.regular_expression.test.adl");
        assertNotNull("Missing definition", archetype.getDefinition());
    }
}
