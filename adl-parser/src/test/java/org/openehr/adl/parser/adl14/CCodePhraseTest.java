/*
 * Copyright (C) 2014 Marand
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
        assertCCodePhrase(node, "icd10", codes, null);
    }

    @Test
    public void testParseExternalCodesWithAssumedValue() throws Exception {
        node = AmQuery.find(archetype, "/types[at0001]/items[at10005]/value");
        String[] codes = {"F43.00", "F43.01", "F32.02"};
        assertCCodePhrase(node, "icd10", codes, "F43.00");
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
        assertCCodePhrase(node, "local", codeList, null);
    }

    @Test
    public void testParseEmptyCodeList() throws Exception {
        node = AmQuery.find(archetype, "/types[at0001]/items[at10004]/value");
        String[] codeList = null;
        assertCCodePhrase(node, "icd10", codeList, null);
    }


    private Archetype archetype;
    private ArchetypeConstraint node;
}
