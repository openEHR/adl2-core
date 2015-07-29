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
import org.antlr.v4.runtime.tree.ParseTree;
import org.openehr.adl.antlr4.generated.adlParser;

import java.util.List;
import java.util.Map;

/**
 * @author markopi
 */
class AdlTreeDAdlParser extends AbstractAdlTreeParser {
    AdlTreeDAdlParser() {
    }

    DAdlObject parseAdlObject(adlParser.AdlObjectValueContext context) {
        if (context == null) {
            return new DAdlObject((Token) null, ImmutableMap.<String, ParseTree>of());
        }

        Map<String, ParseTree> properties = Maps.newLinkedHashMap();
        List<adlParser.AdlObjectPropertyContext> adlObjectProperties = context.adlObjectProperty();
        for (adlParser.AdlObjectPropertyContext adlObjectProperty : adlObjectProperties) {
            String name = collectNonNullText(adlObjectProperty.identifier());
            properties.put(name, adlObjectProperty.adlValue());

        }
        return new DAdlObject(tokenOf(context), properties);
    }

}
