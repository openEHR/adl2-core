/*
 * Copyright (C) 2014 Marand
 */

package org.openehr.adl.parser.adl14;

import org.openehr.adl.ParserTestBase;
import org.openehr.adl.am.AmQuery;
import org.openehr.jaxb.am.Archetype;
import org.openehr.jaxb.rm.IntervalOfDuration;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.openehr.adl.rm.RmObjectFactory.newIntervalOfDuration;

public class CDurationTest extends ParserTestBase {
    @BeforeClass
    public void setUp() {
        archetype = parseArchetype("adl14/adl-test-entry.durations.test.adl");

    }

    /**
     * Tests duration constraints parsing
     *
     * @throws Exception
     */
    @Test
    public void testParseCDuration() throws Exception {

        assertCDuration(AmQuery.find(archetype, "/types[at0001]/items[at1001]/value"),
                "PT0s", null);

        assertCDuration(AmQuery.find(archetype, "/types[at0001]/items[at1002]/value"),
                "P1d", null);

        assertCDuration(AmQuery.find(archetype, "/types[at0001]/items[at1003]/value"),
                "PT2h5m0s", null);

        assertCDuration(AmQuery.find(archetype, "/types[at0001]/items[at1004]/value"),
                null,
                newIntervalOfDuration("PT1h55m0s", "PT2h5m0s"));

        assertCDuration(AmQuery.find(archetype, "/types[at0001]/items[at1005]/value"),
                null,
                newIntervalOfDuration(null, "PT1h"));

        assertCDuration(AmQuery.find(archetype, "/types[at0001]/items[at1006]/value"),
                "P1DT1H2M3S", null);

        // bug fix for ISO durationg with weeks
        assertCDuration(AmQuery.find(archetype, "/types[at0001]/items[at1007]/value"),
                "P1W2DT1H2M3S", null);

        // bug fix for ISO duration with months
        assertCDuration(AmQuery.find(archetype, "/types[at0001]/items[at1008]/value"),
                "P3M1W2DT1H2M3S", null);

        // to supported newly added duration pattern
        assertCDuration(AmQuery.find(archetype, "/types[at0001]/items[at1009]/value"),
                null, null, null, "PDTH");
    }

    /**
     * Verifies the support for "|PT10M|", single duration interval
     */
    @Test
    public void testParseSingleDurationInverval() throws Exception {
        IntervalOfDuration interval = newIntervalOfDuration("PT10M", "PT10M");
        assertCDuration(AmQuery.find(archetype, "/types[at0001]/items[at1010]/value"),
                null, interval, null, null);

        // test with assumed value
        assertCDuration(AmQuery.find(archetype, "/types[at0002]/items[at1010]/value"),
                null, interval, "PT12M", null);
    }

    /**
     * Tests parsing CDurations with assumed values
     *
     * @throws Exception
     */
    @Test
    public void testParseCDurationWithAssumedValue() throws Exception {
        assertCDuration(AmQuery.find(archetype, "/types[at0002]/items[at1001]/value"),
                "PT0s", null, "P1d");

        assertCDuration(AmQuery.find(archetype, "/types[at0002]/items[at1002]/value"),
                "P1d", null, "P1d");

        assertCDuration(AmQuery.find(archetype, "/types[at0002]/items[at1003]/value"),
                "PT2h5m0s", null, "P1d");

        assertCDuration(AmQuery.find(archetype, "/types[at0002]/items[at1004]/value"),
                null,
                newIntervalOfDuration("PT1h55m0s", "PT2h5m0s"), "P1d");

        assertCDuration(AmQuery.find(archetype, "/types[at0002]/items[at1005]/value"),
                null, newIntervalOfDuration(null, "PT1h"), "P1d");
        // to supported newly added duration pattern
        assertCDuration(AmQuery.find(archetype, "/types[at0002]/items[at1006]/value"),
                null, null, "P1d", "PDTH");
    }

    /**
     * Tests parsing CDurations with assumed values
     *
     * @throws Exception
     */
    @Test
    public void testParseCDurationWithMixedPatternAndInterval() throws Exception {
        IntervalOfDuration interval = newIntervalOfDuration("PT0S", "PT120S");


        assertCDuration(AmQuery.find(archetype, "/types[at0001]/items[at1014]/value"),
                null, interval, null, "PTS");

    }

    private Archetype archetype;
}
