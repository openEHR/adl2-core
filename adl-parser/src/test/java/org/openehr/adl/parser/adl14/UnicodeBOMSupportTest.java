/*
 * Copyright (C) 2014 Marand
 */

package org.openehr.adl.parser.adl14;

import org.openehr.adl.ParserTestBase;
import org.testng.annotations.Test;

/**
 * This test case verifies that the parser supports the optional BOM (Byte Order Mark) of UTF-8.
 * This BOM is introduced by many Windows based Texteditors and not treated correctly by Java without extra help.
 * <p/>
 * It parses an ADL file (UTF-8 with BOM encoded)  to ascertain that it is parsable.
 *
 * @author Sebastian Garde
 * @author Rong Chen
 * @version 1.0
 */
public class UnicodeBOMSupportTest extends ParserTestBase {


    /**
     * Tests parsing of Chinese text in the ADL file
     *
     * @throws Exception
     */
    @Test
    public void testParsingWithUTF8Encoding() throws Exception {
        parseArchetype("adl14/adl-test-entry.unicode_BOM_support.test.adl");

    }

    @Test
    public void testParsingWithoutUTF8Encoding() throws Exception {
//            ADLParser parser = new ADLParser(loadFromClasspath(
//                    "adl-test-entry.unicode_BOM_support.test.adl"), "ISO-8859-1");
        parseArchetype("adl14/adl-test-entry.unicode_BOM_support.test.adl");
    }
}
