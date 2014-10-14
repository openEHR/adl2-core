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
import org.openehr.jaxb.am.Archetype;
import org.openehr.jaxb.am.ArchetypeTerm;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * This test case verifies the unicode support of the parser.
 * <p/>
 * It parses an ADL file which contains Chinese and Swedish text
 * encoded in UTF-8, then compare these text with pre-defined unicode
 * escaped text strings in the code to check if they get parsed correctly.
 *
 * @author Rong Chen
 * @version 1.0
 */
public class UnicodeSupportTest extends ParserTestBase {


    @BeforeClass
    public void setUp() throws Exception {
        archetype = parseArchetype("adl14/adl-test-entry.unicode_support.test.adl");
    }

    @AfterTest
    public void cleanup() {
        archetype = null;
    }

    @Test
    public void testParse() {
        assertNotNull("parsing failed", archetype);
    }

    /**
     * Tests parsing of Chinese text in the ADL file
     *
     * @throws Exception
     */
    @Test
    public void testParsingWithChineseText() throws Exception {
        ArchetypeTerm term = termToMap(codeToMap(archetype.getOntology().getTermDefinitions()).get("zh").getItems()).get("at0000");

        //ArchetypeTerm term = archetype.getOntology().termDefinition("zh", "at0000");
        assertNotNull("definition in zh not found", term);

        // "\u6982\u5ff5" is "concept" in Chinese in escaped unicode format
        assertEquals("concept text wrong", "\u6982\u5ff5", dictToMap(term.getItems()).get("text"));

        // "\u63cf\u8ff0" is "description" in Chinese in escaped unicode format
        assertEquals("concept description wrong", "\u63cf\u8ff0",
                dictToMap(term.getItems()).get("description"));
    }

    private Map<String, ArchetypeTerm> termToMap(List<ArchetypeTerm> terms) {
        Map<String, ArchetypeTerm> result = new LinkedHashMap<>();
        for (ArchetypeTerm term : terms) {
            result.put(term.getCode(), term);
        }
        return result;
    }

    /**
     * Tests parsing of Swedish text in the ADL file
     *
     * @throws Exception
     */
    @Test
    public void testParsingWithSwedishText() throws Exception {
        ArchetypeTerm term = termToMap(codeToMap(archetype.getOntology().getTermDefinitions()).get("sv").getItems()).get("at0000");

        assertNotNull("definition in sv not found", term);

        // "spr\u00e5k" is "language" in Swedish in escaped unicode format
        assertEquals("concept text wrong", "spr\u00e5k", dictToMap(term.getItems()).get("text"));

        // "Hj\u00e4lp" is "help" in Swedish in escaped unicode format
        assertEquals("concept description wrong", "Hj\u00e4lp",
                dictToMap(term.getItems()).get("description"));
    }

    private Archetype archetype;

}
