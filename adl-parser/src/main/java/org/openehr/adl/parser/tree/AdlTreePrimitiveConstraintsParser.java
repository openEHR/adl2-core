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

package org.openehr.adl.parser.tree;

import com.google.common.collect.ImmutableList;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.openehr.adl.antlr4.generated.adlParser;
import org.openehr.adl.rm.RmTypes;
import org.openehr.jaxb.am.*;
import org.openehr.jaxb.rm.*;

import java.util.ArrayList;
import java.util.List;

import static org.openehr.adl.parser.tree.AdlTreeParserUtils.collectText;
import static org.openehr.adl.parser.tree.AdlTreeParserUtils.unescapeString;
import static org.openehr.adl.rm.RmObjectFactory.*;

/**
 * @author markopi
 */
abstract class AdlTreePrimitiveConstraintsParser {
    static CPrimitiveObject parsePrimitiveValue(adlParser.PrimitiveValueConstraintContext context) {
        if (context.stringConstraint() != null) {
            return parseString(context.stringConstraint(), context.STRING());
        }
        if (context.booleanList() != null) {
            return parseBoolean(context.booleanList(), context.bool());
        }
        if (context.numberConstraint() != null) {
            return parseNumber(context.numberConstraint(), context.number());
        }
        if (context.dateConstraint() != null) {
            return parseDate(context.dateConstraint(), context.ISO_DATE());
        }
        if (context.timeConstraint() != null) {
            return parseTime(context.timeConstraint(), context.ISO_TIME());
        }
        if (context.dateTimeConstraint() != null) {
            return parseDateTime(context.dateTimeConstraint(), context.ISO_DATE_TIME());
        }
        if (context.durationConstraint() != null) {
            return parseDuration(context.durationConstraint(), context.DURATION());
        }
        if (context.terminologyCodeConstraint() != null) {
            return parseTerminologyCode(context.terminologyCodeConstraint());
        }
        throw new AssertionError();
    }

    private static CTerminologyCode parseTerminologyCode(adlParser.TerminologyCodeConstraintContext context) {
        CTerminologyCode result = new CTerminologyCode();
        result.setConstraint(context.constraint.getText());
        if (context.assumedValue != null) {
            result.setAssumedValue(context.assumedValue.getText());
        }
        return result;
    }


    static CDate parseDate(adlParser.DateConstraintContext context, TerminalNode assumedValue) {
        CDate result = new CDate();
        result.setRmTypeName(RmTypes.DATE);
        result.setPatternConstraint(collectText(context.DATE_PATTERN()));
        if (context.ISO_DATE() != null) {
            result.getConstraint().add(newIntervalOfDate(context.ISO_DATE().getText(), context.ISO_DATE().getText()));
        }
        for (adlParser.DateIntervalConstraintContext ctx : context.dateIntervalConstraint()) {
            result.getConstraint().add(parseDateInterval(ctx));
        }
        result.setAssumedValue(collectText(assumedValue));
        return result;
    }

    static CDateTime parseDateTime(adlParser.DateTimeConstraintContext context, TerminalNode assumedValue) {
        CDateTime result = new CDateTime();
        result.setRmTypeName(RmTypes.DATE_TIME);
        result.setPatternConstraint(collectText(context.DATE_TIME_PATTERN()));
        if (context.ISO_DATE_TIME() != null) {
            result.getConstraint().add(newIntervalOfDateTime(
                    context.ISO_DATE_TIME().getText(), context.ISO_DATE_TIME().getText()));
        }

        for (adlParser.DateTimeIntervalConstraintContext ctx : context.dateTimeIntervalConstraint()) {
            result.getConstraint().add(parseDateTimeInterval(ctx));

        }
        result.setAssumedValue(collectText(assumedValue));
        return result;
    }

    static CDuration parseDuration(adlParser.DurationConstraintContext context, TerminalNode assumedValue) {
        CDuration result = new CDuration();
        result.setRmTypeName(RmTypes.DURATION);
        if (context.pattern != null) {
            String s = context.pattern.getText();
            result.setPatternConstraint(s);
        }
        if (context.singleInterval != null) {
            String s = context.singleInterval.getText();
            result.getConstraint().add(newIntervalOfDuration(s, s));
        } else if (context.durationIntervalConstraint() != null) {
            result.getConstraint().add(parseDurationInterval(context.durationIntervalConstraint()));
        }
        result.setAssumedValue(collectText(assumedValue));
        return result;
    }

    static CTime parseTime(adlParser.TimeConstraintContext context, TerminalNode assumedValue) {
        CTime result = new CTime();
        result.setRmTypeName(RmTypes.TIME);
        result.setPatternConstraint(collectText(context.TIME_PATTERN()));
        if (context.ISO_TIME() != null) {
            result.getConstraint().add(newIntervalOfTime(context.ISO_TIME().getText(), context.ISO_TIME().getText()));
        }

        for (adlParser.TimeIntervalConstraintContext ctx : context.timeIntervalConstraint()) {
            result.getConstraint().add(parseTimeInterval(ctx));

        }
        result.setAssumedValue(collectText(assumedValue));
        return result;
    }

    static IntervalOfDate parseDateInterval(adlParser.DateIntervalConstraintContext c) {
        String lower = collectText(c.lower);
        String upper = collectText(c.upper);
        String val = collectText(c.val);
        TempInterval<String> temp = parseTempInterval(lower, upper, val, c.gt, c.gte, c.lt, c.lte);
        IntervalOfDate interval = new IntervalOfDate();
        temp.copyCommonTo(interval);
        interval.setLower(temp.lower);
        interval.setUpper(temp.upper);
        return interval;
    }

    static IntervalOfTime parseTimeInterval(adlParser.TimeIntervalConstraintContext c) {
        String lower = collectText(c.lower);
        String upper = collectText(c.upper);
        String val = collectText(c.val);
        TempInterval<String> temp = parseTempInterval(lower, upper, val, c.gt, c.gte, c.lt, c.lte);
        IntervalOfTime interval = new IntervalOfTime();
        temp.copyCommonTo(interval);
        interval.setLower(temp.lower);
        interval.setUpper(temp.upper);
        return interval;
    }

    static IntervalOfDateTime parseDateTimeInterval(adlParser.DateTimeIntervalConstraintContext c) {
        String lower = collectText(c.lower);
        String upper = collectText(c.upper);
        String val = collectText(c.val);
        TempInterval<String> temp = parseTempInterval(lower, upper, val, c.gt, c.gte, c.lt, c.lte);
        IntervalOfDateTime interval = new IntervalOfDateTime();
        temp.copyCommonTo(interval);
        interval.setLower(temp.lower);
        interval.setUpper(temp.upper);
        return interval;
    }

    static IntervalOfDuration parseDurationInterval(adlParser.DurationIntervalConstraintContext c) {
        String lower = collectText(c.lower);
        String upper = collectText(c.upper);
        String val = collectText(c.val);
        TempInterval<String> temp = parseTempInterval(lower, upper, val, c.gt, c.gte, c.lt, c.lte);
        IntervalOfDuration interval = new IntervalOfDuration();
        temp.copyCommonTo(interval);
        interval.setLower(temp.lower);
        interval.setUpper(temp.upper);
        return interval;
    }

    static Number parseNumber(adlParser.NumberContext number, boolean[] isInteger) {
        if (number == null) return null;
        try {
            return Integer.parseInt(number.getText());
        } catch (NumberFormatException e) {
            isInteger[0] = false;
            return Float.parseFloat(number.getText());
        }
    }

    static CPrimitiveObject parseNumber(adlParser.NumberConstraintContext context, adlParser.NumberContext assumedValue) {

        //  array is just to simulate OUT parameter
        boolean isInteger[] = {true};

        List<Number> numbers = new ArrayList<>();
        List<Interval> intervals = new ArrayList<>();

        if (context.numberList() != null) {
            for (adlParser.NumberContext number : context.numberList().number()) {
                numbers.add(parseNumber(number, isInteger));
            }
        }

        for (adlParser.NumberIntervalConstraintContext ctx : context.numberIntervalConstraint()) {
            intervals.add(parseNumberInterval(ctx, isInteger));

        }


        if (isInteger[0]) {
            CInteger result = new CInteger();
            result.setRmTypeName(RmTypes.INTEGER);
            for (Interval interval : intervals) {
                result.getConstraint().add((IntervalOfInteger) interval);
            }

            for (Number number : numbers) {
                result.getConstraint().add(newIntervalOfInteger(number.intValue(), number.intValue()));
            }
            if (assumedValue != null) {
                result.setAssumedValue(parseNumber(assumedValue, isInteger).intValue());
            }
            return result;
        } else {
            CReal result = new CReal();
            result.setRmTypeName(RmTypes.REAL);
            for (Interval interval : intervals) {
                result.getConstraint().add((IntervalOfReal) interval);

            }
            for (Number number : numbers) {
                result.getConstraint().add(newIntervalOfReal(number.doubleValue(), number.doubleValue()));
            }
            if (assumedValue != null) {
                result.setAssumedValue(parseNumber(assumedValue, isInteger).floatValue());
            }
            return result;

        }
    }

    static <T> TempInterval<T> parseTempInterval(T lower, T upper, T val, Token gt, Token gte, Token lt, Token lte) {
        TempInterval<T> result = new TempInterval<>();

        // map single val interval into upper/lower
        if (val != null) {
            if (gt == null && lt == null) { // fixed value
                result.lower = val;
                result.upper = val;
            } else if (gt != null) {
                result.lower = val;
                result.upper = null;
            } else {
                result.lower = null;
                result.upper = val;
            }
        } else { // directly map lower and upper
            result.lower = lower;
            result.upper = upper;
        }

        result.setLowerUnbounded(result.lower == null);
        result.setLowerIncluded(result.lower != null && (gt == null || gte != null));
        result.setUpperUnbounded(result.upper == null);
        result.setUpperIncluded(result.upper != null && (lt == null || lte != null));
        return result;
    }

    static Interval parseNumberInterval(adlParser.NumberIntervalConstraintContext context) {
        return parseNumberInterval(context, new boolean[]{true});

    }

    static Interval parseNumberInterval(adlParser.NumberIntervalConstraintContext context, boolean[] isInteger) {
        Interval result;

        Number lower = parseNumber(context.lower, isInteger);
        Number upper = parseNumber(context.upper, isInteger);
        Number val = parseNumber(context.val, isInteger);

        TempInterval<Number> temp = parseTempInterval(lower, upper, val, context.gt, context.gte, context.lt, context.lte);

        if (isInteger[0]) {
            IntervalOfInteger interval = new IntervalOfInteger();
            if (temp.lower != null) interval.setLower(temp.lower.intValue());
            if (temp.upper != null) interval.setUpper(temp.upper.intValue());
            result = interval;
        } else {
            IntervalOfReal interval = new IntervalOfReal();
            if (temp.lower != null) interval.setLower(temp.lower.floatValue());
            if (temp.upper != null) interval.setUpper(temp.upper.floatValue());
            result = interval;
        }

        temp.copyCommonTo(result);
        return result;
    }

    static CBoolean parseBoolean(adlParser.BooleanListContext context, adlParser.BoolContext assumedValue) {
        CBoolean result = new CBoolean();
        result.setRmTypeName(RmTypes.BOOLEAN);
        for (adlParser.BoolContext bool : context.bool()) {
            if (bool.TRUE() != null) {
                result.getConstraint().add(true);
            } else {
                result.getConstraint().add(false);
            }
        }
        if (assumedValue != null) {
            result.setAssumedValue(assumedValue.TRUE() != null);
        }
        return result;
    }


    static CString parseString(adlParser.StringConstraintContext context, TerminalNode assumedValue) {
        CString result = new CString();
        result.setRmTypeName(RmTypes.STRING);
        if (context.stringList() != null) {
            result.getConstraint().addAll(collectStringList(context.stringList()));
        }
        if (context.regularExpression() != null) {
            result.setPattern(collectRegularExpression(context.regularExpression()));
        }

        result.setAssumedValue(collectText(assumedValue));
        return result;
    }

    static List<String> collectStringList(adlParser.StringListContext context) {
        if (context == null) return ImmutableList.of();
        List<String> result = new ArrayList<>();
        for (TerminalNode node : context.STRING()) {
            result.add(unescapeString(node.getText()));
        }
        return result;
    }

    static String collectRegularExpression(adlParser.RegularExpressionContext tRegularExpression) {
        int start = tRegularExpression.start.getStartIndex();
        int stop = tRegularExpression.stop.getStopIndex();
        return tRegularExpression.start.getInputStream().getText(new org.antlr.v4.runtime.misc.Interval(start + 1, stop - 1));
    }

    private static class TempInterval<T> extends Interval {
        T lower;
        T upper;

        void copyCommonTo(Interval target) {
            target.setLowerUnbounded(isLowerUnbounded());
            target.setLowerIncluded(isLowerIncluded());
            target.setUpperUnbounded(isUpperUnbounded());
            target.setUpperIncluded(isUpperIncluded());
        }
    }
}
