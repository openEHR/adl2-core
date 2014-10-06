/*
 * Copyright (C) 2014 Marand
 */

package adl15.reference.features.use_archetype;

import com.marand.thinkehr.adl.am.mixin.AmMixins;
import com.marand.thinkehr.adl.util.TestAdlParser;
import org.openehr.jaxb.am.Archetype;
import org.openehr.jaxb.am.CArchetypeRoot;
import org.openehr.jaxb.am.CAttribute;
import org.openehr.jaxb.rm.MultiplicityInterval;
import org.testng.annotations.Test;

import static com.marand.thinkehr.adl.rm.RmObjectFactory.newMultiplicityInterval;
import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Marko Pipan
 */
public class UseArchetypeFeaturesTest {
    @Test
    public void testUseArchetype() {
        Archetype archetype = TestAdlParser.parseAdl(
                "adl15/reference/features/use_archetype/openEHR-EHR-COMPOSITION.ext_ref.v1.adls");

        CAttribute aContent = archetype.getDefinition().getAttributes().get(0);
        assertArchetypeRoot((CArchetypeRoot) aContent.getChildren().get(0),
                "openEHR-EHR-SECTION.section_parent.v1", newMultiplicityInterval(0, 1));
        assertArchetypeRoot((CArchetypeRoot) aContent.getChildren().get(1),
                "openEHR-EHR-OBSERVATION.spec_test_obs.v1", newMultiplicityInterval(1, 1));
    }

    private void assertArchetypeRoot(CArchetypeRoot car, String archetypeId, MultiplicityInterval expectedOccurrences) {
        assertThat(car.getArchetypeId().getValue()).isEqualTo(archetypeId);
        assertThat(AmMixins.of(car.getOccurrences()).isEqualTo(expectedOccurrences));
    }
}
