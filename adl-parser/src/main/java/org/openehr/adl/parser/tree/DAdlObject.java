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


import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.antlr.v4.runtime.Token;
import org.openehr.adl.antlr4.generated.adlParser;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

import static org.openehr.adl.parser.tree.AdlTreeParserUtils.collectText;

/**
 * @author markopi
 */
class DAdlObject {
    private final Token startToken;
    private final Map<String, adlParser.AdlValueContext> properties;

    DAdlObject(Token startToken, Map<String, adlParser.AdlValueContext> properties) {
        this.startToken = startToken;
        this.properties = properties;
    }

    static DAdlObject parse(adlParser.AdlObjectValueContext context) {
        if (context == null) return new DAdlObject(null, ImmutableMap.<String, adlParser.AdlValueContext>of());

        Map<String, adlParser.AdlValueContext> properties = Maps.newLinkedHashMap();
        List<adlParser.AdlObjectPropertyContext> adlObjectProperties = context.adlObjectProperty();
        for (adlParser.AdlObjectPropertyContext adlObjectProperty : adlObjectProperties) {
            String name = adlObjectProperty.identifier().getText();
            properties.put(name, adlObjectProperty.adlValue());

        }
        return new DAdlObject(context.start, properties);
    }

    adlParser.AdlValueContext get(String property) {
        adlParser.AdlValueContext result = properties.get(property);
        if (result == null) {
            throw new AdlTreeParserException(startToken, "Adl object does not contain required property: " + property);
        }
        return result;
    }

    @Nullable
    adlParser.AdlValueContext tryGet(String property) {
        return properties.get(property);
    }

    @Nullable
    String tryGetString(String property) {
        adlParser.AdlValueContext prop = tryGet(property);
        if (prop == null) return null;
        return AdlTreeParserUtils.collectString(prop.openStringList());
    }

    @Nullable
    Double tryGetDouble(String property) {
        adlParser.AdlValueContext prop = tryGet(property);
        if (prop == null) return null;
        String str = collectText(prop.number());
        if (str==null) return null;
        return Double.valueOf(str);
    }
    @Nullable
    Integer tryGetInteger(String property) {
        adlParser.AdlValueContext prop = tryGet(property);
        if (prop == null) return null;
        String str = collectText(prop.number());
        if (str==null) return null;
        return Integer.valueOf(str);
    }

    boolean contains(String property) {
        return properties.containsKey(property);
    }

    Map<String, adlParser.AdlValueContext> getProperties() {
        return properties;
    }

}
