/*
 * Copyright (C) 2014 Marand
 */

package org.openehr.adl.parser.tree;

import org.openehr.adl.antlr.AdlParser;
import org.openehr.adl.parser.RuntimeRecognitionException;
import org.openehr.adl.rm.RmModel;
import org.openehr.adl.util.AdlUtils;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;
import org.openehr.jaxb.am.*;
import org.openehr.jaxb.rm.*;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.openehr.adl.am.AmObjectFactory.newCInteger;
import static org.openehr.adl.am.AmObjectFactory.newCReal;
import static org.openehr.adl.rm.RmObjectFactory.*;

/**
 * @author markopi
 */
public class AdlTreePrimitiveConstraintsParser extends AbstractAdlTreeParser {
    private final AdlTreeDAdlParser dadl;

    public AdlTreePrimitiveConstraintsParser(CommonTokenStream tokenStream, CommonTree adlTree,
            AdlTreeParserState state, AdlTreeDAdlParser dadl, RmModel rmModel) {
        super(tokenStream, adlTree, state, rmModel);
        this.dadl = dadl;
    }

    CPrimitiveObject parsePrimitiveValueConstraint(Tree tConstraint) {
        assertTokenType(tConstraint, AdlParser.AST_PRIMITIVE_VALUE_CONSTRAINT);

        Tree tPrimitiveConstraint = tConstraint.getChild(0);
        final CPrimitiveObject primitive;
        switch (tPrimitiveConstraint.getType()) {
            case AdlParser.AST_STRING_LIST:
                primitive = parseCStringList(tConstraint);
                break;
            case AdlParser.AST_REGULAR_EXPRESSION:
                primitive = parseCStringRegularExpression(tConstraint);
                break;
            case AdlParser.AST_NUMBER_INTERVAL_CONSTRAINT:
                primitive = parseCNumberInterval(tConstraint);
                break;
            case AdlParser.AST_NUMBER_LIST:
                primitive = parseCNumberList(tConstraint);
                break;
            case AdlParser.AST_BOOLEAN_LIST:
                primitive = parseCBooleanList(tConstraint);
                break;
            case AdlParser.DATE_PATTERN:
                primitive = parseCDatePattern(tConstraint);
                break;
            case AdlParser.ISO_DATE:
                primitive = parseCDateIsoDate(tConstraint);
                break;
            case AdlParser.AST_DATE_INTERVAL_CONSTRAINT:
                primitive = parseCDateInterval(tConstraint);
                break;
            case AdlParser.DATE_TIME_PATTERN:
                primitive = parseCDateTimePattern(tConstraint);
                break;
            case AdlParser.ISO_DATE_TIME:
                primitive = parseCDateTimeIsoDateTime(tConstraint);
                break;
            case AdlParser.AST_DATE_TIME_INTERVAL_CONSTRAINT:
                primitive = parseCDateTimeInterval(tConstraint);
                break;
            case AdlParser.TIME_PATTERN:
                primitive = parseCTimePattern(tConstraint);
                break;
            case AdlParser.ISO_TIME:
                primitive = parseCTimeIsoTime(tConstraint);
                break;
            case AdlParser.AST_TIME_INTERVAL_CONSTRAINT:
                primitive = parseCTimeInterval(tConstraint);
                break;
            case AdlParser.AST_DURATION_CONSTRAINT:
                primitive = parseCDurationConstraint(tConstraint);
                break;
            case AdlParser.AST_CODE_LIST:
                primitive = parseTerminologyCode(tPrimitiveConstraint);
                break;
            default:
                throw new RuntimeRecognitionException(tPrimitiveConstraint);
        }

        primitive.setOccurrences(newMultiplicityInterval(1, 1));
        primitive.setRmTypeName(AdlUtils.getRmTypeName(primitive.getClass()));

        return primitive;
    }

    private CTerminologyCode parseTerminologyCode(Tree tConstraint) {
        assertTokenType(tConstraint, AdlParser.AST_CODE_LIST);

        CTerminologyCode result = new CTerminologyCode();
        int i = 0;
        while (tConstraint.getChildCount() > i && tConstraint.getChild(i).getType() == AdlParser.AST_TEXT) {
            Tree tCode = child(tConstraint, i, AdlParser.AST_TEXT);
            result.getCodeList().add(collectText(tCode));
            i++;
        }

        return result;
    }

    private CDuration parseCDurationConstraint(Tree tConstraint) {
        assertTokenType(tConstraint, AdlParser.AST_PRIMITIVE_VALUE_CONSTRAINT);
        Tree tDuration = child(tConstraint, 0, AdlParser.AST_DURATION_CONSTRAINT);

        String duration = collectText(tDuration.getChild(0));
        CDuration result = new CDuration();
        if (duration != null) {
            if (Pattern.compile("\\d").matcher(duration).find()) {
                result.setDefaultValue(duration);
            } else {
                result.setPattern(duration);
            }
        }
        if (tDuration.getChild(1).getType() != AdlParser.AST_NULL) {
            Tree tInterval = child(tDuration, 1, AdlParser.AST_DURATION_INTERVAL_CONSTRAINT);
            result.setRange(newIntervalOfDuration(
                    collectText(tInterval.getChild(0)),
                    collectText(tInterval.getChild(1)),
                    tInterval.getChild(2).getType() == AdlParser.TRUE,
                    tInterval.getChild(3).getType() == AdlParser.TRUE));
        }

        result.setAssumedValue(parseAssumedTextValue(tConstraint.getChild(1)));

        return result;
    }

    // Date
    private CDate parseCDateInterval(Tree tConstraint) {
        Tree tInterval = child(tConstraint, 0, AdlParser.AST_DATE_INTERVAL_CONSTRAINT);
        CDate result = new CDate();

        result.setRange(newIntervalOfDate(
                collectText(tInterval.getChild(0)),
                collectText(tInterval.getChild(1)),
                tInterval.getChild(2).getType() == AdlParser.TRUE,
                tInterval.getChild(3).getType() == AdlParser.TRUE));

        result.setAssumedValue(parseAssumedTextValue(tConstraint.getChild(1)));
        return result;
    }

    private CDate parseCDateIsoDate(Tree tConstraint) {
        Tree tIsoDate = child(tConstraint, 0, AdlParser.ISO_DATE);
        CDate result = new CDate();
        String value = collectText(tIsoDate);
        result.setDefaultValue(value);
        result.setAssumedValue(parseAssumedTextValue(tConstraint.getChild(1)));
        return result;
    }

    private CDate parseCDatePattern(Tree tConstraint) {
        Tree tDatePattern = child(tConstraint, 0, AdlParser.DATE_PATTERN);
        CDate result = new CDate();
        result.setPattern(collectText(tDatePattern));
        result.setAssumedValue(parseAssumedTextValue(tConstraint.getChild(1)));
        return result;
    }

    // Time
    private CTime parseCTimeInterval(Tree tConstraint) {
        Tree tInterval = child(tConstraint, 0, AdlParser.AST_TIME_INTERVAL_CONSTRAINT);
        CTime result = new CTime();

        result.setRange(newIntervalOfTime(
                collectText(tInterval.getChild(0)),
                collectText(tInterval.getChild(1)),
                tInterval.getChild(2).getType() == AdlParser.TRUE,
                tInterval.getChild(3).getType() == AdlParser.TRUE));

        result.setAssumedValue(parseAssumedTextValue(tConstraint.getChild(1)));
        return result;
    }

    private CTime parseCTimeIsoTime(Tree tConstraint) {
        Tree tIsoTime = child(tConstraint, 0, AdlParser.ISO_TIME);
        CTime result = new CTime();
        String value = collectText(tIsoTime);
        result.setDefaultValue(value);
        result.setAssumedValue(parseAssumedTextValue(tConstraint.getChild(1)));
        return result;
    }

    private CTime parseCTimePattern(Tree tConstraint) {
        Tree tTimePattern = child(tConstraint, 0, AdlParser.TIME_PATTERN);
        CTime result = new CTime();
        result.setPattern(collectText(tTimePattern));
        result.setAssumedValue(parseAssumedTextValue(tConstraint.getChild(1)));
        return result;
    }

    // DateTime
    private CDateTime parseCDateTimeInterval(Tree tConstraint) {
        Tree tInterval = child(tConstraint, 0, AdlParser.AST_DATE_TIME_INTERVAL_CONSTRAINT);
        CDateTime result = new CDateTime();

        result.setRange(newIntervalOfDateTime(
                collectText(tInterval.getChild(0)),
                collectText(tInterval.getChild(1)),
                tInterval.getChild(2).getType() == AdlParser.TRUE,
                tInterval.getChild(3).getType() == AdlParser.TRUE));

        result.setAssumedValue(parseAssumedTextValue(tConstraint.getChild(1)));
        return result;
    }

    private CDateTime parseCDateTimeIsoDateTime(Tree tConstraint) {
        Tree tIsoDateDate = child(tConstraint, 0, AdlParser.ISO_DATE_TIME);
        CDateTime result = new CDateTime();
        String value = collectText(tIsoDateDate);
        result.setDefaultValue(value);
        result.setAssumedValue(parseAssumedTextValue(tConstraint.getChild(1)));
        return result;
    }

    private CDateTime parseCDateTimePattern(Tree tConstraint) {
        Tree tDatePattern = child(tConstraint, 0, AdlParser.DATE_TIME_PATTERN);
        CDateTime result = new CDateTime();
        result.setPattern(collectText(tDatePattern));
        result.setAssumedValue(parseAssumedTextValue(tConstraint.getChild(1)));
        return result;
    }

    private CPrimitiveObject parseCNumberList(Tree tConstraint) {
        Tree tNumberList = child(tConstraint, 0, AdlParser.AST_NUMBER_LIST);

        boolean isInteger = true;
        List<Number> numbers = new ArrayList<>();
        for (Tree tNumber : children(tNumberList)) {
            try {
                numbers.add(Integer.parseInt(collectText(tNumber)));
            } catch (NumberFormatException e) {
                isInteger = false;
                numbers.add(Float.parseFloat(collectText(tNumber)));
            }
        }
        if (isInteger) {
            CInteger result = new CInteger();
            if (numbers.size() == 1) {
                result.setDefaultValue(numbers.get(0).intValue());
            }
            for (Number number : numbers) {
                result.getList().add(number.intValue());
            }
            result.setAssumedValue(parseAssumedValue(tConstraint.getChild(1), Integer.class));
            return result;
        } else {
            CReal result = new CReal();
            if (numbers.size() == 1) {
                result.setDefaultValue(numbers.get(0).floatValue());
            }
            for (Number number : numbers) {
                result.getList().add(number.floatValue());
            }
            result.setAssumedValue(parseAssumedValue(tConstraint.getChild(1), Float.class));
            return result;
        }
    }

    private CString parseCStringRegularExpression(Tree tConstraint) {
        assertTokenType(tConstraint, AdlParser.AST_PRIMITIVE_VALUE_CONSTRAINT);

        CString result = new CString();
        result.setPattern(collectRegularExpression(tConstraint.getChild(0)));
        result.setAssumedValue(parseAssumedValue(tConstraint.getChild(1), String.class));
        return result;
    }

    private CBoolean parseCBooleanList(Tree tConstraint) {
        Tree tBooleanList = tConstraint.getChild(0);
        CBoolean result = new CBoolean();
        List<Tree> booleanValues = children(tBooleanList);
        for (Tree tree : booleanValues) {
            switch (tree.getType()) {
                case AdlParser.TRUE:
                    result.setTrueValid(true);
                    break;
                case AdlParser.FALSE:
                    result.setFalseValid(true);
                    break;
                default:
                    throw new AdlTreeParserException("Unexpected value in boolean list: " + AdlParser.tokenNames[tree.getType()],
                            tokenOf(tree));
            }
        }
        if (booleanValues.size() == 1) {
            result.setDefaultValue(booleanValues.get(0).getType() == AdlParser.TRUE);
        }
        result.setAssumedValue(parseAssumedValue(tConstraint.getChild(1), Boolean.class));

        return result;
    }

    private CPrimitiveObject parseCNumberInterval(Tree tConstraint) {
        Interval interval = parseNumberInterval(tConstraint.getChild(0), false);

        if (interval instanceof IntervalOfInteger) {
            return newCInteger((IntervalOfInteger) interval, null, parseAssumedValue(tConstraint.getChild(1), Integer.class));
        } else {
            return newCReal((IntervalOfReal) interval, null, parseAssumedValue(tConstraint.getChild(1), Float.class));
        }
    }

    private String collectRegularExpression(Tree tRegularExpression) {
        assertTokenType(tRegularExpression, AdlParser.AST_REGULAR_EXPRESSION);

        String result = checkNotNull(collectText(tRegularExpression));
        result = result.substring(1, result.length() - 1);
        return result;
    }


    private Interval parseNumberInterval(Tree tNumberInterval, boolean mustBeInteger) {
        assertTokenType(tNumberInterval, AdlParser.AST_NUMBER_INTERVAL_CONSTRAINT);
        boolean lowerIncluded = tNumberInterval.getChild(2).getType() == AdlParser.TRUE;
        boolean upperIncluded = tNumberInterval.getChild(3).getType() == AdlParser.TRUE;

        Interval interval;
        String lowStr = collectText(tNumberInterval.getChild(0));
        String highStr = collectText(tNumberInterval.getChild(1));

        try {
            Integer low = lowStr != null ? Integer.parseInt(lowStr) : null;
            Integer high = highStr != null ? Integer.parseInt(highStr) : null;

            interval = newIntervalOfInteger(low, high, lowerIncluded, upperIncluded);

        } catch (NumberFormatException e) {
            if (mustBeInteger) {
                throw new AdlTreeParserException("Expected interval of integer, found interval of real", tokenOf(tNumberInterval));
            }

            Float low = lowStr != null ? Float.parseFloat(lowStr) : null;
            Float high = highStr != null ? Float.parseFloat(highStr) : null;

            interval = newIntervalOfReal(low, high, lowerIncluded, upperIncluded);
        }
        return interval;
    }

    private CString parseCStringList(Tree tConstraint) {
        assertTokenType(tConstraint, AdlParser.AST_PRIMITIVE_VALUE_CONSTRAINT);

        final CString result = new CString();
        final List<String> stringList = collectStringList(tConstraint.getChild(0));
        if (stringList.size() == 1) {
            result.setDefaultValue(stringList.get(0));
        }
        result.getList().addAll(stringList);

        result.setAssumedValue(parseAssumedValue(tConstraint.getChild(1), String.class));
        return result;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    private <T> T parseAssumedValue(Tree tAssumedValue, Class<T> primitiveClass) {
        if (tAssumedValue == null) return null;
        assertTokenType(tAssumedValue, AdlParser.AST_ASSUMED_VALUE_CONSTRAINT);

        if (primitiveClass == String.class) {
            return (T) collectString(tAssumedValue.getChild(0));
        }
        if (primitiveClass == Integer.class) {
            String str = collectText(tAssumedValue.getChild(0));
            return (T) (Integer) Integer.parseInt(str);
        }
        if (primitiveClass == Float.class) {
            String str = collectText(tAssumedValue.getChild(0));
            return (T) (Float) Float.parseFloat(str);
        }
        if (primitiveClass == Boolean.class) {
            Tree t = tAssumedValue.getChild(0);
            if (t.getType() == AdlParser.TRUE) return (T) Boolean.TRUE;
            if (t.getType() == AdlParser.FALSE) return (T) Boolean.FALSE;
            throw new AdlTreeParserException("Unexpected tree node: " + t.getText(), tokenOf(t));
        }
        throw new IllegalArgumentException("Unknown primitive class: " + primitiveClass.getName());
    }

    @Nullable
    private String parseAssumedTextValue(@Nullable Tree tAssumedValue) {
        if (tAssumedValue == null) return null;
        assertTokenType(tAssumedValue, AdlParser.AST_ASSUMED_VALUE_CONSTRAINT);

        return collectText(tAssumedValue.getChild(0));
    }

    // returns a CTerminologyCode if adl 1.5, else returns CCodePhrase
    CObject parseCodeConstraint(Tree tConstraint) {
        assertTokenType(tConstraint, AdlParser.AST_CODE_PHRASE_CONSTRAINT);
        if (isAdlV15()) {
            return parseTerminologyCodeConstraint(tConstraint);
        } else {
            return parseCodePhraseConstraint(tConstraint);
        }
    }


    private CCodePhrase parseCodePhraseConstraint(Tree tConstraint) {

        CCodePhrase result = new CCodePhrase();
        result.setRmTypeName(rmModel.getRmTypeName(CodePhrase.class));

        result.setTerminologyId(newTerminologyId(checkNotNull(collectText(tConstraint.getChild(0)))));
        Tree tCodeList = child(tConstraint, 1, AdlParser.AST_CODE_LIST);
        for (Tree tCode : children(tCodeList)) {
            result.getCodeList().add(collectText(tCode));
        }

        String assumedCode = parseAssumedTextValue(tConstraint.getChild(2));
        if (assumedCode != null) {
            result.setAssumedValue(newCodePhrase(result.getTerminologyId(), assumedCode));
        }
        return result;
    }

    private CTerminologyCode parseTerminologyCodeConstraint(Tree tConstraint) {
        CTerminologyCode result = new CTerminologyCode();
        result.setRmTypeName(rmModel.getRmTypeName(CodePhrase.class));

        result.setTerminologyId(checkNotNull(collectText(tConstraint.getChild(0))));
        Tree tCodeList = child(tConstraint, 1, AdlParser.AST_CODE_LIST);
        for (Tree tCode : children(tCodeList)) {
            result.getCodeList().add(collectText(tCode));
        }

        String assumedCode = parseAssumedTextValue(tConstraint.getChild(2));
        if (assumedCode != null) {
            result.setAssumedValue(toTerminologyCode(result.getTerminologyId(), assumedCode));
        }
        return result;
    }

    private String toTerminologyCode(String terminologyId, String codeString) {
        return terminologyId + "::" + (codeString != null ? codeString : "");
    }

    CDvOrdinal parseCDvOrdinalConstraint(Tree tConstraint) {
        assertTokenType(tConstraint, AdlParser.AST_ORDINAL_CONSTRAINT);

        CDvOrdinal result = new CDvOrdinal();
        result.setRmTypeName(rmModel.getRmTypeName(DvOrdinal.class));

        Map<Integer, DvOrdinal> values = new HashMap<>();

        for (Tree tItem : children(tConstraint.getChild(0))) {
            Integer code = Integer.parseInt(collectText(tItem.getChild(0)));
            CodePhrase phrase = parseCodePhrase(tItem.getChild(1));
            DvOrdinal value = newDvOrdinal(code, phrase);
            values.put(code, value);
            result.getList().add(value);
        }
        Integer assumedCode = parseAssumedValue(tConstraint.getChild(1), Integer.class);
        if (assumedCode != null) {
            result.setAssumedValue(values.get(assumedCode));
        }
        return result;
    }
}
