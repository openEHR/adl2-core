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

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringEscapeUtils;
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
abstract class AbstractAdlTreeParser {

    protected Token tokenOf(ParseTree tree) {
        if (tree instanceof TerminalNode) {
            return ((TerminalNode) tree).getSymbol();
        } else if (tree instanceof ParserRuleContext) {
            return ((ParserRuleContext) tree).getStart();
        } else {
            return null;
        }

    }

    protected String tokenName(ParseTree tree) {
        Token token = tokenOf(tree);
        return token == null ? "null" : adlParser.VOCABULARY.getSymbolicName(token.getType());
    }

    protected String unescapeString(String string) {
        if (string.startsWith("\"") && string.endsWith("\"")) {
            string = string.substring(1, string.length() - 1);
        }
        return StringEscapeUtils.unescapeJava(string);
    }


    protected void require(boolean condition, ParseTree location, String message, Object... parameters) {
        if (!condition) {
            throw new AdlTreeParserException(tokenOf(location), message, parameters);
        }
    }


    @Nullable
    protected Integer parseNullableInteger(@Nullable ParseTree tNumber) {
        if (tNumber == null) return null;
        return parseInteger(tNumber);
    }

    protected int parseInteger(ParseTree tNumber) {
        try {
            return Integer.parseInt(collectNonNullText(tNumber));
        } catch (NumberFormatException e) {
            throw new AdlTreeParserException(tokenOf(tNumber), "Invalid integer: ", collectNonNullText(tNumber));
        }
    }

    @Nullable
    protected Double parseNullableFloat(@Nullable ParseTree tNumber) {
        if (tNumber == null) return null;
        return parseFloat(tNumber);
    }

    protected double parseFloat(ParseTree tNumber) {
        try {
            return Double.parseDouble(collectNonNullText(tNumber));
        } catch (NumberFormatException e) {
            throw new AdlTreeParserException(tokenOf(tNumber), "Invalid float: ", collectNonNullText(tNumber));
        }
    }

    @Nullable
    protected String collectString(@Nullable adlParser.OpenStringListContext tStringList) {
        if (tStringList == null) return null;
        // todo
        return collectText(tStringList);
//        if (tStringList == null) return null;
//        if (tStringList.getType() == AdlParser.STRING) {
//            return unescapeString(tStringList.getText());
//        }
//
//        List<String> stringList = collectStringList(tStringList);
//        return stringList.get(0);
    }

    protected List<String> collectStringList(adlParser.OpenStringListContext tStringList) {
        if (tStringList == null) return null;

        List<String> result = new ArrayList<>();
        List<TerminalNode> contexts = tStringList.STRING();
        for (TerminalNode context : contexts) {
            result.add(collectText(context));
        }
        return result;
    }


    protected CodePhrase parseCodePhraseListSingleItem(adlParser.AdlCodePhraseValueListContext context) {
        List<adlParser.AdlCodePhraseValueContext> phrases = context.adlCodePhraseValue();
        if (phrases.size() != 1)
            throw new AdlTreeParserException(tokenOf(context), "Expected exactly one code phrase in list");
        return parseCodePhrase(phrases.get(0));
    }

    protected List<CodePhrase> parseCodePhraseList(ParseTree tCodePhraseList) {
        // todo
        throw new NotImplementedException();
//        List<CodePhrase> result = new ArrayList<>();
//
//        for (Tree tCodePhrase : children(tCodePhraseList)) {
//            result.add(parseCodePhrase(tCodePhrase));
//        }
//        return result;

    }

    protected CodePhrase parseCodePhrase(adlParser.AdlCodePhraseValueContext context) {
        return newCodePhrase(
                newTerminologyId(collectNonNullText(context.tid)),
                collectNonNullText(context.code));
    }


    protected String collectNonNullText(ParseTree context) {
        String result = collectText(context);
        if (result == null) {
            throw new AdlTreeParserException(tokenOf(context), "Text must not be null");
        }
        return result;
    }

    @Nullable
    protected String collectText(ParseTree context) {
        if (context == null) return null;
        return context.getText();
    }

    protected adlParser.AdlValueContext getAdlProperty(adlParser.AdlObjectValueContext adlContext, String name) {
        adlParser.AdlValueContext value = getAdlPropertyOrNull(adlContext, name);
        if (value == null) {
            throw new AdlTreeParserException(tokenOf(adlContext), "Missing required property: %s", name);
        }
        return value;
    }

    protected adlParser.AdlValueContext getAdlPropertyOrNull(adlParser.AdlObjectValueContext adlContext, String name) {
        List<adlParser.AdlObjectPropertyContext> properties = adlContext.adlObjectProperty();
        for (adlParser.AdlObjectPropertyContext propertyContext : properties) {
            String identifier = collectNonNullText(propertyContext.identifier());
            if (identifier.equals(name)) return propertyContext.adlValue();
        }
        return null;
    }

    protected adlParser.AdlValueContext getAdlPropertyOrNull(adlParser.AdlMapValueContext adlContext, String name) {
        List<adlParser.AdlMapValueEntryContext> properties = adlContext.adlMapValueEntry();
        for (adlParser.AdlMapValueEntryContext propertyContext : properties) {
            String identifier = collectNonNullText(propertyContext.STRING());
            if (identifier.equals(name)) return propertyContext.adlValue();
        }
        return null;
    }


}
