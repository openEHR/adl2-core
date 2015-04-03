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

import com.google.common.collect.Maps;
import org.openehr.adl.antlr.AdlParser;
import org.openehr.adl.rm.RmModel;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;

import java.util.Collections;
import java.util.Map;

/**
 * @author markopi
 */
class AdlTreeDAdlParser extends AbstractAdlTreeParser {
    AdlTreeDAdlParser(CommonTokenStream tokenStream, CommonTree adlTree,
            AdlTreeParserState state) {
        super(tokenStream, adlTree, state);
    }

    DAdlObject parseAdlObject(Tree tAdlObject) {
        if (tAdlObject.getType() == AdlParser.AST_NULL) {
            return new DAdlObject(
                    tokenStream.get(tAdlObject.getParent().getTokenStartIndex()),
                    Collections.<String, Tree>emptyMap());
        }

        assertTokenType(tAdlObject, AdlParser.AST_ADL_OBJECT);

        Map<String, Tree> properties = Maps.newLinkedHashMap();
        for (Tree tAdlObjectProperty : children(tAdlObject)) {
            assertTokenType(tAdlObjectProperty, AdlParser.AST_ADL_OBJECT_PROPERTY);

            String name = tAdlObjectProperty.getChild(0).getText();
            properties.put(name, tAdlObjectProperty.getChild(1));
        }
        return new DAdlObject(tokenStream.get(tAdlObject.getParent().getTokenStartIndex()), properties);
    }

    Map<String, Tree> parseAdlMap(Tree tAdlMap) {
        if (tAdlMap.getType() == AdlParser.AST_NULL) return Collections.emptyMap();

        assertTokenType(tAdlMap, AdlParser.AST_ADL_MAP);
        Map<String, Tree> properties = Maps.newLinkedHashMap();
        for (Tree tAdlMapEntry : children(tAdlMap)) {
            assertTokenType(tAdlMapEntry, AdlParser.AST_ADL_MAP_ENTRY);

            String name = unescapeString(tAdlMapEntry.getChild(0).getText());
            properties.put(name, tAdlMapEntry.getChild(1));
        }

        return properties;
    }

}
