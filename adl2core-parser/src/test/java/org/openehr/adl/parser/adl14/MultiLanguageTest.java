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
import org.openehr.jaxb.am.CodeDefinitionSet;
import org.openehr.jaxb.rm.TranslationDetails;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class MultiLanguageTest extends ParserTestBase {


    /**
     * Verifies term definitions from multiple language
     *
     * @throws Exception
     */
    @Test
    public void testMultiLanguageTermDefinitions() throws Exception {

        Archetype archetype = parseArchetype("adl14/adl-test-entry.multi_language.test.adl");
        List<CodeDefinitionSet> list =
                archetype.getTerminology().getTermDefinitions();

        assertEquals("expected number of termDefnitionsList", 2, list.size());

        CodeDefinitionSet defs = list.get(0);
        assertEquals("unexpected language", "en", defs.getLanguage());

        defs = list.get(1);
        assertEquals("unexpected language", "sv", defs.getLanguage());
    }

    /**
     * Verifies constraint definitions from multiple language
     *
     * @throws Exception
     */
    @Test
    public void testMultiLanguageConstraintDefinitions() throws Exception {

        Archetype archetype = parseArchetype("adl14/adl-test-entry.multi_language.test.adl");
        List<CodeDefinitionSet> list =
                archetype.getTerminology().getConstraintDefinitions();
        assertEquals("expected number of constraintDefinitionsList", 2, list.size());

        CodeDefinitionSet defs = list.get(0);
        assertEquals("unexpected language", "en", defs.getLanguage());

        defs = list.get(1);
        assertEquals("unexpected language", "sv", defs.getLanguage());
    }

    /**
     * Verifies that translation details are parsed correctly if not all optional elements are present.
     *
     * @throws Exception
     */
    @Test
    public void testTranslationDetails() throws Exception {

        Archetype archetype = parseArchetype("adl14/adl-test-entry.testtranslations.test.adl");
        Map<String, TranslationDetails> translations = transToMap(archetype.getTranslations());

        for (Entry<String, TranslationDetails> translation : translations.entrySet()) {
            TranslationDetails transDet = translation.getValue();
            String lang = transDet.getLanguage().getCodeString();
            if (lang.equals("de")) {
                assertEquals("wrong accreditation", "test Accreditation!", transDet.getAccreditation());
                assertEquals("wrong organisation", "test organisation", dictToMap(transDet.getAuthor()).get("organisation"));
            } else if (lang.equals("es")) {
                //  They need to be null
                assertEquals("wrong accreditation", null, transDet.getAccreditation());
                assertEquals("wrong organisation", null, dictToMap(transDet.getAuthor()).get("organisation"));
            }
        }
    }
}
