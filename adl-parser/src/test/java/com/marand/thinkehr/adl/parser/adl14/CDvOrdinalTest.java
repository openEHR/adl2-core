/*
 * Copyright (C) 2014 Marand
 */

package com.marand.thinkehr.adl.parser.adl14;

import com.marand.thinkehr.adl.ParserTestBase;
import com.marand.thinkehr.adl.am.AmQuery;
import org.openehr.jaxb.am.Archetype;
import org.openehr.jaxb.am.ArchetypeConstraint;
import org.openehr.jaxb.am.CDvOrdinal;
import org.openehr.jaxb.rm.DvOrdinal;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static com.marand.thinkehr.adl.rm.RmObjectFactory.*;
import static com.marand.thinkehr.adl.util.TestUtils.assertDvOrdinal;

/**
 * Test case tests parsing of domain type constraints extension.
 *
 * @author Rong Chen
 * @version 1.0
 */

public class CDvOrdinalTest extends ParserTestBase {


    /**
     * The fixture set up called before every test method.
     */
    @BeforeClass
    protected void setUp() throws Exception {
        archetype = parseArchetype("adl14/adl-test-entry.c_dv_ordinal.test.adl");
    }

    /**
     * The fixture clean up called after every test method.
     */
    @AfterTest
    protected void cleanup() throws Exception {
        node = null;
    }

    @Test
    public void testCDvOrdinalWithoutAssumedValue() throws Exception {
        node = AmQuery.find(archetype, "/types[at0001]/items[at10001]/value");
        String[] codes = {
                "at0003.0", "at0003.1", "at0003.2", "at0003.3", "at0003.4"
        };
        String terminology = "local";

        assertNull("unexpected assumed value", ((CDvOrdinal) node).getAssumedValue());

        assertCDvOrdinal(node, terminology, codes, null);
    }

    @Test
    public void testCDvOrdinalWithAssumedValue() throws Exception {
        node = AmQuery.find(archetype, "/types[at0001]/items[at10002]/value");
        String[] codes = {
                "at0003.0", "at0003.1", "at0003.2", "at0003.3", "at0003.4"
        };
        String terminology = "local";
        DvOrdinal assumed = newDvOrdinal(0, newCodePhrase(newTerminologyId(terminology), codes[0]));

        assertNotNull("expected to have assumed value", ((CDvOrdinal) node).getAssumedValue());

        assertCDvOrdinal(node, terminology, codes, assumed);
    }

    @Test
    public void testEmptyCDvOrdinal() throws Exception {
        node = AmQuery.find(archetype, "/types[at0001]/items[at10003]/value");
        assertTrue("CDvOrdinal expected", node instanceof CDvOrdinal);
        CDvOrdinal cordinal = (CDvOrdinal) node;
        assertTrue("list should be empty on unconstrained node", cordinal.getList().isEmpty());
    }

    private void assertCDvOrdinal(ArchetypeConstraint node, String terminoloy,
            String[] codes, DvOrdinal assumedValue) {

        assertTrue("CDvOrdinal expected", node instanceof CDvOrdinal);
        CDvOrdinal cordinal = (CDvOrdinal) node;

        List codeList = Arrays.asList(codes);
        List<DvOrdinal> list = cordinal.getList();
        assertEquals("codes.size", codes.length, list.size());
        for (DvOrdinal ordinal : list) {
            assertEquals("terminology", "local",
                    ordinal.getSymbol().getDefiningCode().getTerminologyId().getValue());
            assertTrue("code missing",
                    codeList.contains(ordinal.getSymbol().getDefiningCode().getCodeString()));
        }
        assertDvOrdinal(assumedValue, cordinal.getAssumedValue());
    }

    private Archetype archetype;
    private ArchetypeConstraint node;
}
