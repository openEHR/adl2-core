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
import org.antlr.v4.runtime.tree.TerminalNode;
import org.openehr.adl.antlr4.generated.adlParser;
import org.openehr.jaxb.am.CPrimitiveObject;
import org.openehr.jaxb.am.CString;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author markopi
 */
public class AdlTreePrimitiveConstraintsParser extends AbstractAdlTreeParser {
    public CPrimitiveObject parsePrimitiveValue(adlParser.PrimitiveValueConstraintContext context) {
        if (context.stringConstraint()!=null) {
            return parseString(context.stringConstraint(), context.STRING());
        }
        throw new AssertionError();
    }

    private CString parseString(adlParser.StringConstraintContext context, TerminalNode assumedValue) {
        CString result = new CString();
        if (context.stringList()!=null) {
            result.getList().addAll(collectStringList(context.stringList()));
        }
        return result;
    }

    private List<String> collectStringList(adlParser.StringListContext context) {
        if (context==null) return ImmutableList.of();
        List<String> result = new ArrayList<>();
        for (TerminalNode node : context.STRING()) {
            result.add(unescapeString(node.getText()));
        }
        return result;
    }


}
