/*
 * Copyright (C) 2014 Marand
 */

package adl15.reference.features.single_alternatives;

import com.marand.thinkehr.adl.util.TestAdlParser;
import org.openehr.jaxb.am.CObject;
import org.openehr.jaxb.am.DifferentialArchetype;
import org.testng.annotations.Test;

import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Marko Pipan
 */
public class SingleAlternativesFeaturesTest {
    @Test
    public void testMultipleAlternatives() {
        DifferentialArchetype sourceArchetype = TestAdlParser.parseAdl(
                "adl15/reference/features/single_alternatives/openEHR-EHR-OBSERVATION.multiple_alternatives.v1.adls");

        List<CObject> alternatives = sourceArchetype.getDefinition ().getAttributes().get(0).getChildren();
        assertThat(alternatives.get(0).getRmTypeName()).isEqualTo("ITEM_TREE");
        assertThat(alternatives.get(0).getNodeId()).isEqualTo("id2");
        assertThat(alternatives.get(1).getRmTypeName()).isEqualTo("ITEM_TREE");
        assertThat(alternatives.get(1).getNodeId()).isEqualTo("id3");

    }
}
