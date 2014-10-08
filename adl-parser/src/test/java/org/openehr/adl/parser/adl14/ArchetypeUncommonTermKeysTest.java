/*
 * Copyright (C) 2014 Marand
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
        Map<String, CodeDefinitionSet> termMap = codeToMap(archetype.getOntology().getTermDefinitions());
        ArchetypeTerm aterm = termMap.get("en").getItems().get(0);
        Map<String, String> dict = dictToMap(aterm.getItems());

        assertEquals("key value wrong", "another key value", dict.get("anotherkey"));
        assertEquals("key value wrong", "test text", dict.get("text"));
        assertEquals("key value wrong", "test description", dict.get("description"));
    }


}

