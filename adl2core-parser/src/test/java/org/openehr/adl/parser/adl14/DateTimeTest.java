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
import org.openehr.jaxb.am.CAttribute;
import org.openehr.jaxb.am.CComplexObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static org.openehr.adl.rm.RmObjectFactory.*;

/**
 * Test case tests parsing of datetime types in ADL files.
 *
 * @author rong.chen
 */

public class DateTimeTest extends ParserTestBase {

    @BeforeClass
    public void setUp() {
        attributeList = parseArchetype("adl14/adl-test-entry.datetime.test.adl").getDefinition().getAttributes();

    }

    private List getConstraints(int index) {
        CAttribute ca = (CAttribute) attributeList.get(index);
        return ((CComplexObject) ca.getChildren().get(0)).getAttributes();
    }

    /**
     * Tests date and partial date constraints parsing
     *
     * @throws Exception
     */
    @Test
    public void testDateConstraints() throws Exception {
        List list = getConstraints(0);

        assertCDate(list.get(0), "yyyy-mm-dd", null, null, null);

        assertCDate(list.get(1), "yyyy-??-??", null, null, null);

        assertCDate(list.get(2), "yyyy-mm-??", null, null, null);

        assertCDate(list.get(3), "yyyy-??-XX", null, null, null);

        assertCDate(list.get(4), null, null, new String[]{"1983-12-25"},
                null);

        assertCDate(list.get(5), null, null, new String[]{"2000-01-01"},
                null);

        assertCDate(list.get(6), null, newIntervalOfDate("2004-09-20", "2004-10-20"), null, null);

        assertCDate(list.get(7), null, lessThan(date("2004-09-20")), null,
                null);

        assertCDate(list.get(8), null, lessEqual(date("2004-09-20")), null,
                null);

        assertCDate(list.get(9), null, greaterThan(date("2004-09-20")), null,
                null);

        assertCDate(list.get(10), null, greaterEqual(date("2004-09-20")), null,
                null);

        // test assumed values
        assertCDate(list.get(11), "yyyy-mm-dd", null, null, "2000-01-01");

        assertCDate(list.get(12), "yyyy-??-??", null, null, "2001-01-01");

        assertCDate(list.get(13), "yyyy-mm-??", null, null, "2002-01-01");

        assertCDate(list.get(14), "yyyy-??-XX", null, null, "2003-01-01");

        assertCDate(list.get(15), null, null, new String[]{"1983-12-25"},
                "2004-01-01");

        assertCDate(list.get(16), null, null, new String[]{"2000-01-01"},
                "2005-01-01");

        assertCDate(list.get(17), null, newIntervalOfDate(
                "2004-09-20", "2004-10-20"), null,
                "2004-09-30");

        assertCDate(list.get(18), null, lessThan(date("2004-09-20")), null,
                "2004-09-01");

        assertCDate(list.get(19), null, lessEqual(date("2004-09-20")), null,
                "2003-09-20");

        assertCDate(list.get(20), null, greaterThan(date("2004-09-20")), null,
                "2005-01-02");

        assertCDate(list.get(21), null, greaterEqual(date("2004-09-20")), null,
                "2005-10-30");
    }

    /**
     * Tests time and partial time constraints parsing
     *
     * @throws Exception
     */
    @Test
    public void testTimeConstraints() throws Exception {
        List list = getConstraints(1);

        assertCTime(list.get(0), "hh:mm:ss", null, null, null);

        assertCTime(list.get(1), "hh:mm:XX", null, null, null);

        assertCTime(list.get(2), "hh:??:XX", null, null, null);

        assertCTime(list.get(3), "hh:??:??", null, null, null);

        assertCTime(list.get(4), null, null, new String[]{"22:00:05"},
                null);

        assertCTime(list.get(5), null, null, new String[]{"00:00:59"},
                null);

        assertCTime(list.get(6), null, null, new String[]{"12:35"},
                null);

        assertCTime(list.get(7), null, null, new String[]{"12:35:45,666"},
                null);

        assertCTime(list.get(8), null, null, new String[]{"12:35:45-0700"},
                null);

        assertCTime(list.get(9), null, null, new String[]{"12:35:45+0800"},
                null);

        assertCTime(list.get(10), null, null,
                new String[]{"12:35:45,999-0700"}, null);

        assertCTime(list.get(11), null, null,
                new String[]{"12:35:45,000+0800"}, null);

        assertCTime(list.get(12), null, null,
                new String[]{"12:35:45,000Z"}, null);

        assertCTime(list.get(13), null, null,
                new String[]{"12:35:45,995-0700"}, null);

        assertCTime(list.get(14), null, null,
                new String[]{"12:35:45,001+0800"}, null);

        assertCTime(list.get(15), null, newIntervalOfTime("12:35",
                "16:35"), null, null);

        assertCTime(list.get(16), null, lessThan(time("12:35")), null, null);

        assertCTime(list.get(17), null, lessEqual(time("12:35")), null, null);

        assertCTime(list.get(18), null, greaterThan(time("12:35")), null, null);

        assertCTime(list.get(19), null, greaterEqual(time("12:35")), null, null);

    }

    /**
     * Tests time and partial time constraints parsing
     *
     * @throws Exception
     */
    @Test
    public void testTimeConstraintsWithAssumedValues() throws Exception {
        List list = getConstraints(1);

        assertCTime(list.get(20), "hh:mm:ss", null, null, "10:00:00");

        assertCTime(list.get(21), "hh:mm:XX", null, null, "10:00:00");

        assertCTime(list.get(22), "hh:??:XX", null, null, "10:00:00");

        assertCTime(list.get(23), "hh:??:??", null, null, "10:00:00");

        assertCTime(list.get(24), null, null, new String[]{"22:00:05"},
                "10:00:00");

        assertCTime(list.get(25), null, null, new String[]{"00:00:59"},
                "10:00:00");

        assertCTime(list.get(26), null, null, new String[]{"12:35"},
                "10:00:00");

        assertCTime(list.get(27), null, null, new String[]{"12:35:45,666"},
                "10:00:00");

        assertCTime(list.get(28), null, null, new String[]{"12:35:45-0700"},
                "10:00:00");

        assertCTime(list.get(29), null, null, new String[]{"12:35:45+0800"},
                "10:00:00");

        assertCTime(list.get(30), null, null,
                new String[]{"12:35:45,999-0700"}, "10:00:00");

        assertCTime(list.get(31), null, null,
                new String[]{"12:35:45,000+0800"}, "10:00:00");

        assertCTime(list.get(32), null, null,
                new String[]{"12:35:45,000Z"}, "10:00:00");

        assertCTime(list.get(33), null, null,
                new String[]{"12:35:45,995-0700"}, "10:00:00");

        assertCTime(list.get(34), null, null,
                new String[]{"12:35:45,001+0800"}, "10:00:00");

        assertCTime(list.get(35), null, newIntervalOfTime("12:35",
                "16:35"), null, "10:00:00");

        assertCTime(list.get(36), null, lessThan(time("12:35")), null,
                "10:00:00");

        assertCTime(list.get(37), null, lessEqual(time("12:35")), null,
                "10:00:00");

        assertCTime(list.get(38), null, greaterThan(time("12:35")), null,
                "10:00:00");

        assertCTime(list.get(39), null, greaterEqual(time("12:35")), null,
                "10:00:00");
    }

    /**
     * Tests datetime and partial datetime constraints parsing
     *
     * @throws Exception
     */
    @Test
    public void testDateTimeConstraints() throws Exception {
        List list = getConstraints(2);

        assertCDateTime(list.get(0), "yyyy-mm-ddThh:mm:ss", null, null, null);

        assertCDateTime(list.get(1), "yyyy-mm-ddThh:mm:??", null, null, null);

        assertCDateTime(list.get(2), "yyyy-mm-ddThh:mm:XX", null, null, null);

        assertCDateTime(list.get(3), "yyyy-mm-ddThh:??:XX", null, null, null);

        assertCDateTime(list.get(4), "yyyy-??-??T??:??:??", null, null, null);

        assertCDateTime(list.get(5), null, null,
                new String[]{"1983-12-25T22:00:05"}, null);

        assertCDateTime(list.get(6), null, null,
                new String[]{"2000-01-01T00:00:59"}, null);

        assertCDateTime(list.get(7), null, null,
                new String[]{"2000-01-01T00:00:59"}, null);

        assertCDateTime(list.get(8), null, null,
                new String[]{"2000-01-01T00:00:59,105"}, null);

        assertCDateTime(list.get(9), null, null,
                new String[]{"2000-01-01T00:00:59Z"}, null);

        assertCDateTime(list.get(10), null, null,
                new String[]{"2000-01-01T00:00:59+1200"}, null);

        assertCDateTime(list.get(11), null, null,
                new String[]{"2000-01-01T00:00:59,500Z"}, null);

        assertCDateTime(list.get(12), null, null,
                new String[]{"2000-01-01T00:00:59,500+1200"}, null);

        assertCDateTime(list.get(13), null, null,
                new String[]{"2000-01-01T00:00:59,000Z"}, null);

        assertCDateTime(list.get(14), null, null,
                new String[]{"2000-01-01T00:00:59,000+1200"}, null);

        assertCDateTime(list.get(15), null, newIntervalOfDateTime(
                "2000-01-01T00:00:00", "2000-01-02T00:00:00"),
                null, null);

        assertCDateTime(list.get(16), null,
                lessThan(dateTime("2000-01-01T00:00:00")), null, null);

        assertCDateTime(list.get(17), null,
                lessEqual(dateTime("2000-01-01T00:00:00")), null, null);

        assertCDateTime(list.get(18), null,
                greaterThan(dateTime("2000-01-01T00:00:00")), null, null);

        assertCDateTime(list.get(19), null,
                greaterEqual(dateTime("2000-01-01T00:00:00")), null, null);

        assertCDateTime(list.get(40), "yyyy-??-??T??:??:??", null, null, null);
    }

    /**
     * Tests datetime and partial datetime constraints parsing
     *
     * @throws Exception
     */
    @Test
    public void testDateTimeConstraintsWithAssumedValues() throws Exception {
        List list = getConstraints(2);

        assertCDateTime(list.get(20), "yyyy-mm-ddThh:mm:ss", null, null,
                "2006-03-31T01:12:00");

        assertCDateTime(list.get(21), "yyyy-mm-ddThh:mm:??", null, null,
                "2006-03-31T01:12:00");

        assertCDateTime(list.get(22), "yyyy-mm-ddThh:mm:XX", null, null,
                "2006-03-31T01:12:00");

        assertCDateTime(list.get(23), "yyyy-mm-ddThh:??:XX", null, null,
                "2006-03-31T01:12:00");

        assertCDateTime(list.get(24), "yyyy-??-??T??:??:??", null, null,
                "2006-03-31T01:12:00");

        assertCDateTime(list.get(25), null, null,
                new String[]{"1983-12-25T22:00:05"},
                "2006-03-31T01:12:00");

        assertCDateTime(list.get(26), null, null,
                new String[]{"2000-01-01T00:00:59"},
                "2006-03-31T01:12:00");

        assertCDateTime(list.get(27), null, null,
                new String[]{"2000-01-01T00:00:59,000"},
                "2006-03-31T01:12:00");

        assertCDateTime(list.get(28), null, null,
                new String[]{"2000-01-01T00:00:59,105"},
                "2006-03-31T01:12:00");

        assertCDateTime(list.get(29), null, null,
                new String[]{"2000-01-01T00:00:59Z"},
                "2006-03-31T01:12:00");

        assertCDateTime(list.get(30), null, null,
                new String[]{"2000-01-01T00:00:59+1200"},
                "2006-03-31T01:12:00");

        assertCDateTime(list.get(31), null, null,
                new String[]{"2000-01-01T00:00:59,500Z"},
                "2006-03-31T01:12:00");

        assertCDateTime(list.get(32), null, null,
                new String[]{"2000-01-01T00:00:59,500+1200"},
                "2006-03-31T01:12:00");

        assertCDateTime(list.get(33), null, null,
                new String[]{"2000-01-01T00:00:59,000Z"},
                "2006-03-31T01:12:00");

        assertCDateTime(list.get(34), null, null,
                new String[]{"2000-01-01T00:00:59,000+1200"},
                "2006-03-31T01:12:00");

        assertCDateTime(list.get(35), null, newIntervalOfDateTime(
                ("2000-01-01T00:00:00"),
                ("2000-01-02T00:00:00")), null,
                "2006-03-31T01:12:00");

        assertCDateTime(list.get(36), null,
                lessThan(dateTime("2000-01-01T00:00:00")), null,
                "2006-03-31T01:12:00");

        assertCDateTime(list.get(37), null,
                lessEqual(dateTime("2000-01-01T00:00:00")), null,
                "2006-03-31T01:12:00");

        assertCDateTime(list.get(38), null,
                greaterThan(dateTime("2000-01-01T00:00:00")), null,
                "2006-03-31T01:12:00");

        assertCDateTime(list.get(39), null,
                greaterEqual(dateTime("2000-01-01T00:00:00")), null,
                "2006-03-31T01:12:00");
    }

    private List attributeList;
}
