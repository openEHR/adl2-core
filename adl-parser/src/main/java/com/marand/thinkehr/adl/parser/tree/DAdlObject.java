/*
 * Copyright (C) 2014 Marand
 */

package com.marand.thinkehr.adl.parser.tree;

import org.antlr.runtime.Token;
import org.antlr.runtime.tree.Tree;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * @author markopi
 */
class DAdlObject {
    private final Token startToken;
    private final Map<String, Tree> properties;

    DAdlObject(Token startToken, Map<String, Tree> properties) {
        this.startToken = startToken;
        this.properties = properties;
    }

    Tree get(String property) {
        Tree result = properties.get(property);
        if (result == null) {
            throw new AdlTreeParserException("Adl object does not contain required property: " + property, startToken);
        }
        return result;
    }

    @Nullable
    Tree tryGet(String property) {
        return properties.get(property);
    }

    boolean contains(String property) {
        return properties.containsKey(property);
    }

    Map<String, Tree> getProperties() {
        return properties;
    }
}
