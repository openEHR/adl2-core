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
import org.openehr.jaxb.am.Archetype;
import org.openehr.jaxb.am.ArchetypeTerm;
import org.openehr.jaxb.am.CodeDefinitionSet;
import org.testng.annotations.Test;

import java.util.Map;


/**
 * Testcase for uncommon term keys (other than text, description and comment)
 *
 * @author Sebastian Garde
 * @version 1.0
 */
public class ArchetypeUncommonTermKeysTest extends ParserTestBase {

    @Test
    public void testArchetypeUncommonTerm() throws Exception {
        Archetype archetype = parseArchetype("adl14/adl-test-entry.archetype_uncommonkeys.test.adl");
        Map<String, CodeDefinitionSet> termMap = codeToMap(archetype.getTerminology().getTermDefinitions());
        ArchetypeTerm aterm = termMap.get("en").getItems().get(0);
        Map<String, String> dict = dictToMap(aterm.getItems());

        assertEquals("key value wrong", "another key value", dict.get("anotherkey"));
        assertEquals("key value wrong", "test text", dict.get("text"));
        assertEquals("key value wrong", "test description", dict.get("description"));
    }


}

