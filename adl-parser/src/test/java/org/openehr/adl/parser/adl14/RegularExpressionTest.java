/*
 * Copyright (C) 2014 Marand
 */

package org.openehr.adl.parser.adl14;

import org.openehr.adl.ParserTestBase;
import org.openehr.jaxb.am.Archetype;
import org.testng.annotations.Test;

public class RegularExpressionTest extends ParserTestBase {


    @Test
    public void testParseRegularExpressions() throws Exception {
        Archetype archetype = parseArchetype("adl14/adl-test-entry.regular_expression.test.adl");
        assertNotNull("Missing definition", archetype.getDefinition());
    }
}
