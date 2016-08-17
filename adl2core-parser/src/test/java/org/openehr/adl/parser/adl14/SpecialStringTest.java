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

package org.openehr.adl.parser.adl14;

import org.openehr.adl.ParserTestBase;
import org.openehr.jaxb.am.CAttribute;
import org.openehr.jaxb.am.CComplexObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;


public class SpecialStringTest extends ParserTestBase {
// ------------------------------ FIELDS ------------------------------

    private List attributeList;
    private List list;

// -------------------------- PUBLIC METHODS --------------------------

    @BeforeClass
    public void setUp() throws Exception {
        attributeList = parseArchetype("adl14/adl-test-entry.special_string.test.adl").getDefinition().getAttributes();
    }

    @Test
    public void testParseEscapedBackslash() throws Exception {
        list = getConstraints(0);
        assertCString(list.get(1), null, new String[]{"any\\thing"}, null);
    }

    @Test
    public void testParseEscapedDoubleQuote() throws Exception {
        list = getConstraints(0);
        assertCString(list.get(0), null, new String[]{"some\"thing"}, null);
    }

// -------------------------- OTHER METHODS --------------------------

    private List getConstraints(int index) {
        CAttribute ca = (CAttribute) attributeList.get(index);
        return ((CComplexObject) ca.getChildren().get(0)).getAttributes();
    }
}
