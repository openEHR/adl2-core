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
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringEscapeUtils;
import org.openehr.adl.antlr4.generated.adlLexer;
import org.openehr.adl.antlr4.generated.adlParser;
import org.openehr.jaxb.rm.CodePhrase;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static org.openehr.adl.rm.RmObjectFactory.newCodePhrase;
import static org.openehr.adl.rm.RmObjectFactory.newTerminologyId;

/**
 * @author markopi
 */
abstract class AdlTreeParserUtils {

    static Token tokenOf(ParseTree tree) {
        if (tree instanceof TerminalNode) {
            return ((TerminalNode) tree).getSymbol();
        } else if (tree instanceof ParserRuleContext) {
            return ((ParserRuleContext) tree).getStart();
        } else {
            return null;
        }

    }

    static String tokenName(ParseTree tree) {
        Token token = tokenOf(tree);
        return token == null ? "null" : adlParser.VOCABULARY.getSymbolicName(token.getType());
    }

    static String unescapeString(String string) {
        if (string.startsWith("\"") && string.endsWith("\"")) {
            string = string.substring(1, string.length() - 1);
        }
        return StringEscapeUtils.unescapeJava(string);
    }


    static void require(boolean condition, ParseTree location, String message, Object... parameters) {
        if (!condition) {
            throw new AdlTreeParserException(tokenOf(location), message, parameters);
        }
    }


    @Nullable
    static Integer parseNullableInteger(@Nullable ParseTree tNumber) {
        if (tNumber == null) return null;
        return parseInteger(tNumber);
    }

    static int parseInteger(ParseTree tNumber) {
        try {
            return Integer.parseInt(collectNonNullText(tNumber));
        } catch (NumberFormatException e) {
            throw new AdlTreeParserException(tokenOf(tNumber), "Invalid integer: ", collectNonNullText(tNumber));
        }
    }

    @Nullable
    static Double parseNullableFloat(@Nullable ParseTree tNumber) {
        if (tNumber == null) return null;
        return parseFloat(tNumber);
    }

    static double parseFloat(ParseTree tNumber) {
        try {
            return Double.parseDouble(collectNonNullText(tNumber));
        } catch (NumberFormatException e) {
            throw new AdlTreeParserException(tokenOf(tNumber), "Invalid float: ", collectNonNullText(tNumber));
        }
    }

    @Nullable
    static String collectString(@Nullable adlParser.OpenStringListContext tStringList) {
        if (tStringList == null) return null;
        // todo
        return unescapeString(collectText(tStringList));
//        if (tStringList == null) return null;
//        if (tStringList.getType() == AdlParser.STRING) {
//            return unescapeString(tStringList.getText());
//        }
//
//        List<String> stringList = collectStringList(tStringList);
//        return stringList.get(0);
    }

    static List<String> collectStringList(adlParser.OpenStringListContext tStringList) {
        if (tStringList == null) return ImmutableList.of();

        List<String> result = new ArrayList<>();
        List<TerminalNode> contexts = tStringList.STRING();
        for (TerminalNode context : contexts) {
            result.add(unescapeString(collectText(context)));
        }
        return result;
    }


    static CodePhrase parseCodePhraseListSingleItem(adlParser.AdlCodePhraseValueListContext context) {
        List<adlParser.AdlCodePhraseValueContext> phrases = context.adlCodePhraseValue();
        if (phrases.size() != 1)
            throw new AdlTreeParserException(tokenOf(context), "Expected exactly one code phrase in list");
        return parseCodePhrase(phrases.get(0));
    }


    static CodePhrase parseCodePhrase(adlParser.AdlCodePhraseValueContext context) {
        return newCodePhrase(
                newTerminologyId(collectNonNullText(context.tid)),
                collectNonNullText(context.code));
    }


    static String collectNonNullText(ParseTree context) {
        String result = collectText(context);
        if (result == null) {
            throw new AdlTreeParserException(tokenOf(context), "Text must not be null");
        }
        return result;
    }

    @Nullable
    static String collectText(Token token) {
        if (token==null) return null;
        if (token.getType()==adlLexer.STRING) {
            return unescapeString(token.getText());
        }
        return token.getText();
    }

    static String collectTextWithSpaces(ParserRuleContext context) {
        int start = context.start.getStartIndex();
        int stop = context.stop.getStopIndex();
        return context.start.getInputStream().getText(new Interval(start, stop));

    }

    static String collectText(adlParser.AdlValueContext context) {
        if (context==null) return null;
        if (context.url()!=null) {
            return collectText(context.url());
        }
        if (context.openStringList()!=null) {
            return collectString(context.openStringList());
        }
        return collectText(context);
    }

    @Nullable
    static String collectText(ParseTree context) {
        if (context == null) return null;
        if (context instanceof TerminalNode) {
            TerminalNode tn = (TerminalNode) context;
            if (tn.getSymbol().getType()== adlLexer.STRING) {
                return unescapeString(tn.getText());
            }
        }
        return context.getText();
    }

    static adlParser.AdlValueContext getAdlProperty(adlParser.AdlObjectValueContext adlContext, String name) {
        adlParser.AdlValueContext value = getAdlPropertyOrNull(adlContext, name);
        if (value == null) {
            throw new AdlTreeParserException(tokenOf(adlContext), "Missing required property: %s", name);
        }
        return value;
    }

    static String collectString( adlParser.AdlValueContext adlValue) {
        if (adlValue==null) {
            return null;
        }
        return collectString(adlValue.openStringList());
    }

    static String parseAtCode(adlParser.AtCodeContext context) {
        if (context == null) return null;
        return context.AT_CODE_VALUE().getText();
    }


    static adlParser.AdlValueContext getAdlPropertyOrNull(adlParser.AdlObjectValueContext adlContext, String name) {
        List<adlParser.AdlObjectPropertyContext> properties = adlContext.adlObjectProperty();
        for (adlParser.AdlObjectPropertyContext propertyContext : properties) {
            String identifier = collectNonNullText(propertyContext.identifier());
            if (identifier.equals(name)) return propertyContext.adlValue();
        }
        return null;
    }

    static adlParser.AdlValueContext getAdlPropertyOrNull(adlParser.AdlMapValueContext adlContext, String name) {
        List<adlParser.AdlMapValueEntryContext> properties = adlContext.adlMapValueEntry();
        for (adlParser.AdlMapValueEntryContext propertyContext : properties) {
            String identifier = collectNonNullText(propertyContext.STRING());
            if (identifier.equals(name)) return propertyContext.adlValue();
        }
        return null;
    }


}
