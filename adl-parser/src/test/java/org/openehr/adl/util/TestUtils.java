/*
 * Copyright (C) 2014 Marand
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
