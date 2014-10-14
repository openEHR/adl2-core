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

package org.openehr.adl.util;

import org.openehr.jaxb.am.ArchetypeTerm;
import org.openehr.jaxb.rm.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Marko Pipan
 */
public class TestUtils {
    public static void assertDvOrdinal(DvOrdinal actual, DvOrdinal expected) {
        assertThat(actual == null).isEqualTo(expected == null);
        if (actual == null) return;

        assertThat(actual.getValue()).as("value").isEqualTo(expected.getValue());
        assertDvCodedText(actual.getSymbol(), expected.getSymbol());
    }

    public static void assertDvQuantity(DvQuantity actual, String units, double magnitude, int precision) {
        assertThat(actual.getMagnitude()).isEqualTo(magnitude);
        assertThat(actual.getUnits()).isEqualTo(units);
        assertThat(actual.getPrecision()).isEqualTo(precision);
    }

    public static void assertDvCodedText(DvCodedText actual, DvCodedText expected) {
        assertThat(actual == null).isEqualTo(expected == null);
        if (actual == null) return;

        assertThat(actual.getValue()).isEqualTo(expected.getValue());
        assertCodePhrase(actual.getDefiningCode(),
                expected.getDefiningCode().getTerminologyId().getValue(),
                expected.getDefiningCode().getCodeString());

    }

    public static void assertCodePhrase(CodePhrase actual, String terminologyId, String codeString) {
        assertThat(actual.getTerminologyId().getValue()).isEqualTo(terminologyId);
        assertThat(actual.getCodeString()).isEqualTo(codeString);
    }

    public static void assertArchetypeTerm(ArchetypeTerm term, String code, String expectedText, String expectedDescription) {
        assertThat(term.getCode()).isEqualTo(code);
        for (StringDictionaryItem item : term.getItems()) {
            switch (item.getId()) {
                case "text":
                    assertThat(item.getValue()).isEqualTo(expectedText);
                    break;
                case "description":
                    assertThat(item.getValue()).isEqualTo(expectedDescription);
                    break;
            }
        }
    }

    public static Map<String, String> stringDictionaryItemsToMap(List<StringDictionaryItem> items) {
        Map<String, String> result = new LinkedHashMap<>();
        for (StringDictionaryItem item : items) {
            result.put(item.getId(), item.getValue());
        }
        return result;
    }

}
