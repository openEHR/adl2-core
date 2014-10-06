/*
 * Copyright (C) 2014 Marand
 */

package com.marand.thinkehr.adl.parser.adl14;


import com.marand.thinkehr.adl.ParserTestBase;
import com.marand.thinkehr.adl.am.AmQuery;
import org.openehr.jaxb.am.Archetype;
import org.openehr.jaxb.am.ArchetypeConstraint;
import org.openehr.jaxb.am.CDvQuantity;
import org.openehr.jaxb.am.CQuantityItem;
import org.openehr.jaxb.rm.CodePhrase;
import org.openehr.jaxb.rm.DvQuantity;
import org.openehr.jaxb.rm.IntervalOfInteger;
import org.openehr.jaxb.rm.IntervalOfReal;
import org.testng.annotations.AfterTest;
import org.testng.annotations.Test;

import java.util.List;

import static com.marand.thinkehr.adl.rm.RmObjectFactory.*;
import static com.marand.thinkehr.adl.util.TestUtils.assertDvQuantity;
import static org.fest.assertions.Assertions.assertThat;

/**
 * Test case tests parsing of domain type constraints extension.
 *
 * @author Rong Chen
 * @version 1.0
 */

public class CDvQuantityTest extends ParserTestBase {

    /**
     * The fixture clean up called after every test method.
     */
    @AfterTest
    protected void cleanup() throws Exception {
        archetype = null;
        node = null;
    }

    @Test
    public void testParseFullCDvQuantityStartsWithProperty() throws Exception {
        archetype = parseArchetype("adl14/adl-test-entry.c_dv_quantity_full.test.adl");
        node = AmQuery.find(archetype, "/types[at0001]/items[at10005]/value");
        verifyCDvQuantityValue(node);
    }

    @Test
    public void testParseFullCDvQuantityStartsWithList() throws Exception {
        archetype = parseArchetype("adl14/adl-test-entry.c_dv_quantity_full2.test.adl");
        node = AmQuery.find(archetype, "/types[at0001]/items[at10005]/value");
        verifyCDvQuantityValue(node);
    }

    @Test
    public void testParseFullCDvQuantityStartsWithAssumedValue() throws Exception {
        archetype = parseArchetype("adl14/adl-test-entry.c_dv_quantity_full3.test.adl");
        node = AmQuery.find(archetype,
                "/types[at0001]/items[at10005]/value");
        verifyCDvQuantityValue(node);
    }

    private void verifyCDvQuantityValue(ArchetypeConstraint node) {
        assertTrue("CDvQuantity expected", node instanceof CDvQuantity);

        CDvQuantity cdvquantity = (CDvQuantity) node;

        // verify property 
        CodePhrase property = cdvquantity.getProperty();
        assertNotNull("property is null", property);
        assertEquals("openehr", property.getTerminologyId().getValue());
        assertEquals("128", property.getCodeString());

        // verify item list
        List<CQuantityItem> list = cdvquantity.getList();
        assertEquals("unexpected size of list", 2, list.size());
        assertCDvQuantityItem(list.get(0), "yr",
                newIntervalOfReal(0.0, 200.0), newIntervalOfInteger(2, 2));
        assertCDvQuantityItem(list.get(1), "mth",
                newIntervalOfReal(1.0, 36.0), newIntervalOfInteger(2, 2));

        assertDvQuantity(cdvquantity.getAssumedValue(), "yr", 8.0, 2);
    }

    private void assertCDvQuantityItem(CQuantityItem item, String units,
            IntervalOfReal magnitude, IntervalOfInteger precision) {
        assertEquals("unexpected units", units, item.getUnits());
        assertEquals("unexpected magnitude interval", json(magnitude),
                json(item.getMagnitude()));
        assertEquals("unexpected precision interval", json(precision),
                json(item.getPrecision()));
    }

    @Test
    public void testParseCDvQuantityOnlyWithProperty() throws Exception {
        archetype = parseArchetype("adl14/adl-test-entry.c_dv_quantity_property.test.adl");
    }

    @Test
    public void testParseCDvQuantityOnlyWithList() throws Exception {
        archetype = parseArchetype("adl14/adl-test-entry.c_dv_quantity_list.test.adl");
    }

    @Test
    public void testParseCDvQuantityReversed() throws Exception {
        archetype = parseArchetype("adl14/adl-test-entry.c_dv_quantity_reversed.test.adl");
    }

    @Test
    public void testParseCDvQuantityItemWithOnlyUnits() throws Exception {
        archetype = parseArchetype("adl14/adl-test-entry.c_dv_quantity_item_units_only.test.adl");
    }

    public void testParseEmptyCDvQuantity() throws Exception {
        archetype = parseArchetype("adl14/adl-test-entry.c_dv_quantity_empty.test.adl");
        node = AmQuery.find(archetype, "/types[at0001]/items[at10005]/value");
        assertTrue("CDvQuantity expected", node instanceof CDvQuantity);

        CDvQuantity cdvquantity = (CDvQuantity) node;
        assertThat(cdvquantity.getList()).isEmpty();
        assertThat(cdvquantity.getProperty()).isNull();
        assertThat(cdvquantity.getAssumedValue()).isNull();
    }

    private Archetype archetype;
    private ArchetypeConstraint node;
}
