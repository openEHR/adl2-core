/*
 * Copyright (C) 2014 Marand
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
            AdlTreeParserState state, RmModel rmModel) {
        super(tokenStream, adlTree, state, rmModel);
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
