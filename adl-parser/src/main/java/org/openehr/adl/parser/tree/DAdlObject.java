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
