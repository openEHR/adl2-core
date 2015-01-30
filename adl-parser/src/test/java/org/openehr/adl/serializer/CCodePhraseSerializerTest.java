package org.openehr.adl.serializer;

import org.openehr.adl.util.TestAdlParser;
import org.openehr.jaxb.am.*;
import org.testng.annotations.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Created by bna on 30.01.2015.
 */
public class CCodePhraseSerializerTest {


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
        assertThat(child).isInstanceOf(CCodePhrase.class);

        CCodePhrase codePhrase = (CCodePhrase) child;
        assertThat(codePhrase.getCodeList().get(0)).isEqualTo("at0001");
        assertThat(codePhrase.getCodeList().get(1)).isEqualTo("at0002");
        assertThat(codePhrase.getAssumedValue()).as("Should be assumed value").isNotNull();
        assertThat(codePhrase.getAssumedValue().getCodeString()).isEqualTo("at0002");


    }


}
