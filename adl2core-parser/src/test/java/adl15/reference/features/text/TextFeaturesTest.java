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

package adl15.reference.features.text;

import com.google.common.collect.ImmutableMap;
import org.openehr.adl.am.AmQuery;
import org.openehr.adl.util.TestAdlParser;
import org.openehr.jaxb.am.Archetype;
import org.openehr.jaxb.am.CString;
import org.openehr.jaxb.am.CodeDefinitionSet;
import org.openehr.jaxb.rm.ResourceDescriptionItem;
import org.testng.annotations.Test;

import static org.openehr.adl.util.TestUtils.*;
import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Marko Pipan
 */
public class TextFeaturesTest {

    @Test
    public void testQuotedStrings() {
        Archetype archetype = TestAdlParser.parseAdl("adl15/reference/features/text/openEHR-EHR-CLUSTER.quoted_strings.v1.adls");
        CodeDefinitionSet cds = archetype.getTerminology().getTermDefinitions().get(0);

        assertArchetypeTerm(cds.getItems().get(0), "id1", "unknown", "unknown");
        assertArchetypeTerm(cds.getItems().get(1), "id2", "a\\b", "a\"b");
        assertArchetypeTerm(cds.getItems().get(2), "id3", "\\a\\", "\"a\"");
        assertArchetypeTerm(cds.getItems().get(3), "at11", "\\", "xxx");
        assertArchetypeTerm(cds.getItems().get(4), "at12", "\\\\", "xxx");
        assertArchetypeTerm(cds.getItems().get(5), "ac1", "a\\b", "a\"b");

        CString cstring = AmQuery.get(archetype, "items[id3]/value/value");
        assertThat(cstring.getConstraint()).containsExactly("No quoting should happen here");
    }

    @Test
    public void testUnicodeFarsi() {
        Archetype archetype = TestAdlParser.parseAdl("adl15/reference/features/text/openEHR-EHR-EVALUATION.unicode_farsi.v1.adls");

        ResourceDescriptionItem en = archetype.getDescription().getDetails().get(0);
        assertThat(en.getLanguage().getCodeString()).isEqualTo("en");
        assertThat(en.getPurpose()).isEqualTo("Test unicode language archetype with Farsi translation");

        ResourceDescriptionItem fa = archetype.getDescription().getDetails().get(1);
        assertThat(fa.getLanguage().getCodeString()).isEqualTo("fa");
        assertThat(fa.getPurpose()).isEqualTo("ثبت احتمال خطرداشتن و یا ایجاد شرایط ارزیابی شده به دلیل بروز در اقوام ");
        assertThat(fa.getUse()).isEqualTo("برای ثبت اطلاعات درباره بروز شرایط در اقوام و احتمال خطر برای موضوع مراقبت ");
    }

    @Test
    public void testMaxDescription() {
        Archetype archetype = TestAdlParser.parseAdl("adl15/reference/features/text/openEHR-EHR-OBSERVATION.max_description.v1.adls");

        ResourceDescriptionItem fr = archetype.getDescription().getDetails().get(0);
        assertCodePhrase(fr.getLanguage(), "ISO_639-1", "fr");
        assertThat(fr.getPurpose()).isEqualTo("a quoi ça sert");
        assertThat(fr.getUse()).isEqualTo("ceçi explique la fonction de l'archetype");
        assertThat(fr.getKeywords()).containsExactly("ceçi", "est", "une", "liste", "de", "mots", "clés");
        assertThat(fr.getMisuse()).isEqualTo("les interdictions de cet archetype");
//        assertThat(fr.getCopyright()).isEqualTo("c'est à moi tout ce travail!");

        assertThat(stringDictionaryItemsToMap(fr.getOriginalResourceUri())).isEqualTo(ImmutableMap.of(
                "ligne guide", "http://guidelines.are.us/wherever/fr",
                "medline", "http://some%20medline%20ref"));

    }

    @Test
    public void testLongLines() {
        Archetype archetype = TestAdlParser.parseAdl("adl15/reference/features/text/openEHR-TEST_PKG-ENTRY.long_lines.v1.adls");
        ResourceDescriptionItem en = archetype.getDescription().getDetails().get(0);
        assertThat(en.getPurpose()).isEqualTo("what this is for");
        assertThat(normalizeLines(en.getUse())).isEqualTo(
                "this is what it is used for, this could be a really long line or even\n" +
                "multiple lines, just like \n" +
                "what you are reading now");
        assertThat(en.getKeywords()).containsExactly("this", "is", "a", "list", "keywords");
//        assertThat(normalizeLines(en.getCopyright())).isEqualTo(
//                "this is mine, mine, mine I tell you! Since 2003. \n" +
//                "And another thing.\n" +
//                "And another");

    }

    public String normalizeLines(String str) {
        if (str == null) return null;
        return str.replaceAll("\r\n", "\n").replaceAll("\n\r", "\n");
    }


}
