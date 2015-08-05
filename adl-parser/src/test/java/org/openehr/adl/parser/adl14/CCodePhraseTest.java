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
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Test case tests parsing of domain type constraints extension.
 *
 * @author Rong Chen
 * @version 1.0
 */

public class CCodePhraseTest extends ParserTestBase {

    @BeforeClass
    public void setUp() throws Exception {
        archetype = parseArchetype("adl14/adl-test-entry.c_code_phrase.test.adl");
    }

    /**
     * The fixture clean up called after every test method.
     */
    @AfterTest
    protected void cleanup() throws Exception {
        node = null;
    }

    /**
     * Verifies parsing of a simple CCodePhrase
     *
     * @throws Exception
     */
    @Test
    public void testParseExternalCodes() throws Exception {
        node = AmQuery.find(archetype, "/types[at0001]/items[at10002]/value");
        String[] codes = {"F43.00", "F43.01", "F32.02"};
        assertCTerminologyCode(node, "icd10", codes, null);
    }

    @Test
    public void testParseExternalCodesWithAssumedValue() throws Exception {
        node = AmQuery.find(archetype, "/types[at0001]/items[at10005]/value");
        String[] codes = {"F43.00", "F43.01", "F32.02"};
        assertCTerminologyCode(node, "icd10", codes, "F43.00");
    }

    /**
     * Verifies parsing of a simple CCodePhrase with codes defined locally
     *
     * @throws Exception
     */
    @Test
    public void testParseLocalCodes() throws Exception {
        node = AmQuery.find(archetype, "/types[at0001]/items[at10003]/value");
        String[] codeList = {"at1311", "at1312", "at1313", "at1314", "at1315"};
        assertCTerminologyCode(node, "local", codeList, null);
    }

    @Test
    public void testParseEmptyCodeList() throws Exception {
        node = AmQuery.find(archetype, "/types[at0001]/items[at10004]/value");
        String[] codeList = null;
        assertCTerminologyCode(node, "icd10", codeList, null);
    }


    private Archetype archetype;
    private ArchetypeConstraint node;
}
