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

package org.openehr.adl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openehr.adl.util.TestAdlParser;
import org.openehr.jaxb.am.*;
import org.openehr.jaxb.rm.*;
import org.testng.Assert;

import javax.annotation.Nullable;
import java.util.*;

import static org.openehr.adl.rm.RmObjectFactory.*;

/**
 * @author markopi
 */
public class ParserTestBase {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    public static final MultiplicityInterval MANDATORY = newMultiplicityInterval(1, 1);
    public static final MultiplicityInterval OPTIONAL = newMultiplicityInterval(0, 1);
    public static final MultiplicityInterval UNBOUNDED = newMultiplicityInterval(0, null);
    public static final MultiplicityInterval MANDATORY_UNBOUNDED = newMultiplicityInterval(1, null);


    protected Archetype parseArchetype(String classpathResource) {
        return TestAdlParser.parseAdl(classpathResource);
    }

    protected Map<String, String> dictToMap(List<StringDictionaryItem> items) {
        Map<String, String> result = new HashMap<>();
        for (StringDictionaryItem item : items) {
            result.put(item.getId(), item.getValue());
        }
        return result;
    }

    protected Map<String, TranslationDetails> transToMap(List<TranslationDetails> translations) {
        Map<String, TranslationDetails> result = new HashMap<>();
        for (TranslationDetails translation : translations) {
            result.put(translation.getLanguage().getCodeString(), translation);
        }
        return result;
    }

    protected ArchetypeTerm getTermDefinition(ArchetypeTerminology terminology, String lang, String atCode) {
        for (CodeDefinitionSet cds : terminology.getTermDefinitions()) {
            if (lang == null || cds.getLanguage().equals(lang)) {
                for (ArchetypeTerm at : cds.getItems()) {
                    if (at.getCode().equals(atCode)) return at;
                }
            }
        }
        return null;
    }

    protected Map<String, CodeDefinitionSet> codeToMap(List<CodeDefinitionSet> termDefinitions) {
        Map<String, CodeDefinitionSet> result = new HashMap<>();
        for (CodeDefinitionSet termDefinition : termDefinitions) {
            result.put(termDefinition.getLanguage(), termDefinition);
        }
        return result;
    }

    protected void assertNull(String message, @Nullable Object nullValue) {
        Assert.assertNull(nullValue, message);
    }

    protected void assertNotNull(String message, @Nullable Object value) {
        Assert.assertNotNull(value, message);
    }

    protected void assertEquals(@Nullable Object expected, @Nullable Object actual) {
        assertEquals(null, expected, actual);
    }

    protected void assertEquals(String message, @Nullable Object expected, @Nullable Object actual) {
        Assert.assertEquals(actual, expected, message);
    }

    protected void assertTrue(String message, boolean test) {
        Assert.assertTrue(test, message);
    }

    @Nullable
    protected String json(@Nullable Object object) {
        if (object == null) return null;

        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    // assert CObject has expected values
    protected void assertCObject(CObject obj, String rmTypeName, @Nullable String nodeID,
                                 @Nullable IntervalOfInteger occurrences, @Nullable SiblingOrder siblingOrder) {
        assertEquals("rmTypeName", rmTypeName, obj.getRmTypeName());
        assertEquals("nodeID", nodeID, obj.getNodeId());
        assertEquals("occurrences", json(occurrences), json(obj.getOccurrences()));
        assertEquals("siblingOrder", json(siblingOrder), json(obj.getSiblingOrder()));
    }

    protected void assertCObject(CObject obj, String rmTypeName, @Nullable String nodeID,
                                 @Nullable IntervalOfInteger occurrences) {
        assertCObject(obj, rmTypeName, nodeID, occurrences, null);
    }

    // assert CComplexObject object has expected values
    protected void assertCComplexObject(CComplexObject obj, String rmTypeName,
                                        @Nullable String nodeID, IntervalOfInteger occurrences,
                                        int attributes) {
        assertCObject(obj, rmTypeName, nodeID, occurrences);
        assertEquals("attributes.size", attributes, obj.getAttributes().size());
    }

    // assert CAttribute has expected values
    protected void assertCAttribute(CAttribute attr, String rmAttributeName,
                                    IntervalOfInteger existence,
                                    @Nullable Cardinality cardinality, int children) {
        assertEquals("rmAttributeName", rmAttributeName,
                attr.getRmAttributeName());
        assertEquals("existence", json(existence), json(attr.getExistence()));
        assertEquals("cardinality", json(cardinality), json(attr.getCardinality()));
        assertEquals("children.size", children, attr.getChildren().size());
    }

    // assert CAttribute has expected values
    protected void assertCAttribute(CAttribute attr, String rmAttributeName,
                                    int children) {
        assertCAttribute(attr, rmAttributeName, null,
                null, children);
    }

    // assertion on primitive types
    protected void assertCBoolean(Object obj, boolean trueValid, boolean falseValid,
                                  boolean assumed, boolean hasAssumed) {
        CBoolean b = (CBoolean) fetchFirst(obj);
        assertEquals("trueValid", trueValid, b.isTrueValid());
        assertEquals("falseValid", falseValid, b.isFalseValid());
        assertEquals("assumed value", hasAssumed ? assumed : null, b.isAssumedValue());
    }

    protected void assertCInteger(Object obj, @Nullable IntervalOfInteger interval, @Nullable int[] values,
                                  @Nullable Integer assumed) {
        CInteger c = (CInteger) fetchFirst(obj);
        assertEquals("interval", json(interval), json(c.getRange()));
        assertEquals("list", intSet(values), c.getList());
        assertEquals("unexpected assumed value", assumed, c.getAssumedValue());
    }

    protected void assertCReal(Object obj, @Nullable IntervalOfReal interval, @Nullable double[] values,
                               @Nullable Double assumed) {
        CReal c;
        if (obj instanceof CAttribute) {
            c = (CReal) fetchFirst(obj);
        } else {
            c = (CReal) obj;
        }
        assertEquals("interval", json(interval), json(c.getRange()));
        assertEquals("list", floatSet(values), c.getList());
        assertEquals("unexpected assumed value", assumed != null ? assumed.floatValue() : null, c.getAssumedValue());
    }

    protected void assertCDate(Object obj, @Nullable String pattern, @Nullable IntervalOfDate interval,
                               @Nullable String[] values, @Nullable String assumed) {
        CDate c = (CDate) fetchFirst(obj);
        assertEquals("pattern", pattern, c.getPattern());
        assertEquals("interval", json(interval), json(c.getRange()));
        //assertEquals("list", dateSet(values), c.getList());
        assertEquals("assumed value", assumed, c.getAssumedValue());
    }

    protected void assertCDateTime(Object obj, @Nullable String pattern, @Nullable IntervalOfDateTime interval,
                                   @Nullable String[] values, @Nullable String assumed) {

        CDateTime c = (CDateTime) fetchFirst(obj);
        assertEquals("pattern", pattern, c.getPattern());
        assertEquals("interval", json(interval), json(c.getRange()));
        //assertEquals("list", dateTimeSet(values), c.getList());
        assertEquals("assumed value", assumed, c.getAssumedValue());
    }

    // without assumed value
    protected void assertCDateTime(Object obj, String pattern, IntervalOfDateTime interval,
                                   String[] values) {
        assertCDateTime(obj, pattern, interval, values, null);
    }


    protected void assertCTime(Object obj, @Nullable String pattern, @Nullable IntervalOfTime interval,
                               @Nullable String[] values, @Nullable String assumed) {

        CTime c = (CTime) fetchFirst(obj);
        assertEquals("pattern", pattern, c.getPattern());
        assertEquals("interval", json(interval), json(c.getRange()));
        //assertEquals("list", timeSet(values), c.getList());
        assertEquals("assumed value", assumed, c.getAssumedValue());
    }

    protected void assertCCodePhrase(ArchetypeConstraint actual,
                                     String terminologyId, @Nullable String[] codes, @Nullable String assumedValue) {

        // check type
        assertTrue("CCodePhrase expected, got " + actual.getClass(),
                actual instanceof CCodePhrase);
        CCodePhrase cCodePhrase = (CCodePhrase) actual;

        // check terminology
        assertEquals("terminology", terminologyId,
                cCodePhrase.getTerminologyId().getValue());

        // check code list
        if (codes == null) {
            assertTrue("codeList expected empty", cCodePhrase.getCodeList().isEmpty());
        } else {
            List<String> codeList = cCodePhrase.getCodeList();
            assertEquals("codes.size wrong", codes.length, codeList.size());
            for (int i = 0; i < codes.length; i++) {
                Object c = codeList.get(i);
                assertEquals("code wrong, got: " + c, codes[i], c);
            }
        }

        // check assumed value
        assertEquals("assumed value wrong", assumedValue,
                cCodePhrase.getAssumedValue() != null ? cCodePhrase.getAssumedValue().getCodeString() : null);
    }

    protected void assertCTerminologyCode(ArchetypeConstraint actual,
                                          String terminologyId, @Nullable String[] codes, @Nullable String assumedValue) {

        // check type
        assertTrue("CTerminologyCode expected, got " + actual.getClass(),
                actual instanceof CTerminologyCode);
        CTerminologyCode cTerminologyCode = (CTerminologyCode) actual;

        // check terminology
        assertEquals("terminology", terminologyId,
                cTerminologyCode.getTerminologyId());

        // check code list
        if (codes == null) {
            assertTrue("codeList expected empty", cTerminologyCode.getCodeList().isEmpty());
        } else {
            List<String> codeList = cTerminologyCode.getCodeList();
            assertEquals("codes.size wrong", codes.length, codeList.size());
            for (int i = 0; i < codes.length; i++) {
                Object c = codeList.get(i);
                assertEquals("code wrong, got: " + c, codes[i], c);
            }
        }

        // check assumed value
        assertEquals("assumed value wrong", assumedValue,
                cTerminologyCode.getAssumedValue() != null ? cTerminologyCode.getAssumedValue() : null);
    }


    // without assumed value
    protected void assertCTime(Object obj, String pattern, IntervalOfTime interval,
                               String[] values) {
        assertCTime(obj, pattern, interval, values, null);
    }

    // full assertion with CDuration
    protected void assertCDuration(Object obj, @Nullable String pattern, @Nullable IntervalOfDuration interval,
                                   @Nullable String assumed) {

        CDuration c = (CDuration) obj;
//        assertEquals("list", value == null ? null : DvDuration.getInstance(value),
//                c.getValue());
        assertEquals("interval", json(interval), json(c.getRange()));
        assertEquals("assumed value", assumed, c.getAssumedValue());
        assertEquals("pattern wrong", pattern, c.getPattern());
    }


    // without assumed value
    protected void assertCDuration(@Nullable Object obj, @Nullable String pattern, @Nullable IntervalOfDuration interval) {
        assertCDuration(obj, pattern, interval, null);
    }

    // fetch the first CPrimitive from the CAttribute
    CPrimitiveObject fetchFirst(Object obj) {
        if (obj instanceof CAttribute) {
            return ((CPrimitiveObject) ((CAttribute) obj).getChildren().get(0));
        } else {
            return (CPrimitiveObject) obj;
        }
    }

    protected void assertCString(Object obj, @Nullable String pattern, @Nullable String[] values,
                                 @Nullable String assumedValue) {
        CString c;
        if (obj instanceof CAttribute) {
            c = (CString) fetchFirst(obj);
        } else {
            c = (CString) obj;
        }
        if (pattern == null) {
            assertTrue("pattern null", c.getPattern() == null);
        } else {
            assertEquals("pattern", pattern, c.getPattern());
        }
        assertEquals("list", values == null ? Collections.emptyList() : Arrays.asList(values), c.getList());
        assertEquals("unexpected CString assumed value", assumedValue, c.getAssumedValue());
    }

    @Nullable
    List<Integer> intSet(@Nullable int[] values) {
        if (values == null) {
            return Collections.emptyList();
        }
        List<Integer> result = new ArrayList<>();
        for (int value : values) {
            result.add(value);
        }
        return result;
    }

    @Nullable
    List<Float> floatSet(@Nullable double[] values) {
        if (values == null) {
            return Collections.emptyList();
        }
        List<Float> set = new ArrayList<>();
        for (double value : values) {
            set.add((float) value);
        }
        return set;
    }


    // IntervalOfInteger
    protected IntervalOfInteger greaterThan(int value) {
        return newIntervalOfInteger(value, null, false, false);
    }

    protected IntervalOfInteger lessThan(int value) {
        return newIntervalOfInteger(null, value, false, false);
    }

    protected IntervalOfInteger greaterEqual(int value) {
        return newIntervalOfInteger(value, null, true, false);
    }

    protected IntervalOfInteger lessEqual(int value) {
        return newIntervalOfInteger(null, value, false, true);
    }

    // IntervalOfReal
    protected IntervalOfReal greaterThan(double value) {
        return newIntervalOfReal(value, null, false, false);
    }

    protected IntervalOfReal lessThan(double value) {
        return newIntervalOfReal(null, value, false, false);
    }

    protected IntervalOfReal greaterEqual(double value) {
        return newIntervalOfReal(value, null, true, false);
    }

    protected IntervalOfReal lessEqual(double value) {
        return newIntervalOfReal(null, value, false, true);
    }

    // IntervalOfDateTime
    protected IntervalOfDateTime greaterThan(DvDateTime value) {
        return newIntervalOfDateTime(value, null, false, false);
    }

    protected IntervalOfDateTime lessThan(DvDateTime value) {
        return newIntervalOfDateTime(null, value, false, false);
    }

    protected IntervalOfDateTime greaterEqual(DvDateTime value) {
        return newIntervalOfDateTime(value, null, true, false);
    }

    protected IntervalOfDateTime lessEqual(DvDateTime value) {
        return newIntervalOfDateTime(null, value, false, true);
    }

    // IntervalOfDate
    protected IntervalOfDate greaterThan(DvDate value) {
        return newIntervalOfDate(value, null, false, false);
    }

    protected IntervalOfDate lessThan(DvDate value) {
        return newIntervalOfDate(null, value, false, false);
    }

    protected IntervalOfDate greaterEqual(DvDate value) {
        return newIntervalOfDate(value, null, true, false);
    }

    protected IntervalOfDate lessEqual(DvDate value) {
        return newIntervalOfDate(null, value, false, true);
    }

    // IntervalOfTime
    protected IntervalOfTime greaterThan(DvTime value) {
        return newIntervalOfTime(value, null, false, false);
    }

    protected IntervalOfTime lessThan(DvTime value) {
        return newIntervalOfTime(null, value, false, false);
    }

    protected IntervalOfTime greaterEqual(DvTime value) {
        return newIntervalOfTime(value, null, true, false);
    }

    protected IntervalOfTime lessEqual(DvTime value) {
        return newIntervalOfTime(null, value, false, true);
    }

    // IntervalOfDuration
    protected IntervalOfDuration greaterThan(DvDuration value) {
        return newIntervalOfDuration(value, null, false, false);
    }

    protected IntervalOfDuration lessThan(DvDuration value) {
        return newIntervalOfDuration(null, value, false, false);
    }

    protected IntervalOfDuration greaterEqual(DvDuration value) {
        return newIntervalOfDuration(value, null, true, false);
    }

    protected IntervalOfDuration lessEqual(DvDuration value) {
        return newIntervalOfDuration(null, value, false, true);
    }


    protected DvDate date(String value) {
        return newDvDate(value);
    }

    protected DvDateTime dateTime(String value) {
        return newDvDateTime(value);
    }

    protected DvTime time(String value) {
        return newDvTime(value);
    }

}
