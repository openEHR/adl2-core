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
