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

package org.openehr.adl.serializer;

import com.google.common.collect.Iterators;
import org.openehr.adl.util.TestAdlParser;
import org.openehr.jaxb.am.*;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.fest.assertions.Assertions.assertThat;

/**
 * "Unit test" for @see CCodePhraseSerializer
 */
public class CCodePhraseSerializerTest extends CSerializer{




    @Test
    public void testSerializeCodPhrase1() {
        Archetype archetype = TestAdlParser.parseAdl("adl14/openEHR-EHR-ELEMENT.test_codephrase_1.v1.adl");
        String result = ArchetypeSerializer.serialize(archetype);
        assertThat(result).isNotEmpty();

        Archetype resultArchetype = TestAdlParser.parseAdlFromString(result);
        assertThat(resultArchetype).isNotNull();
        assertThat(resultArchetype.getDefinition().getNodeId()).isEqualToIgnoringCase("at0000");
        CObject cObject = resultArchetype.getDefinition().getAttributes().get(0).getChildren().get(0);
        assertThat(cObject.getRmTypeName()).isEqualTo("DV_CODED_TEXT");
        CComplexObject c = (CComplexObject) cObject;
        CAttribute a = c.getAttributes().get(0);
        assertThat(a).isNotNull();

        CObject child = a.getChildren().get(0);
        assertThat(child).isInstanceOf(CTerminologyCode.class);

        CTerminologyCode codePhrase = (CTerminologyCode) child;
        assertThat(codePhrase.getCodeList().get(0)).isEqualTo("at0001");
        assertThat(codePhrase.getCodeList().get(1)).isEqualTo("at0002");
        assertThat(codePhrase.getAssumedValue()).as("Should be assumed value").isNotNull();
        assertThat(codePhrase.getAssumedValue()).isEqualTo("at0002");


    }

    @Test
    public void testSerializeWithTerminologyIcd10(){
        Archetype archetype = TestAdlParser.parseAdl("adl14/openEHR-EHR-ELEMENT.test_codephrase_term.v1.adl");
        assertThat(archetype).as("No archetype named openEHR-EHR-ELEMENT.test_codephrase_term.v1.adl").isNotNull();
        String adl = ArchetypeSerializer.serialize(archetype);
        assertThat(adl).isNotNull();
        write(adl, "CodePhraseTerm.adl");
        Archetype resultArchetype = TestAdlParser.parseAdlFromString(adl);
        assertThat(resultArchetype).isNotNull();


    }



}
