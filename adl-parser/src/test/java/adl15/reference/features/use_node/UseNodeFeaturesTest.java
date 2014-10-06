/*
 * Copyright (C) 2014 Marand
 */

package adl15.reference.features.use_node;

import com.marand.thinkehr.adl.am.AmQuery;
import com.marand.thinkehr.adl.am.mixin.AmMixins;
import com.marand.thinkehr.adl.util.TestAdlParser;
import org.openehr.jaxb.am.Archetype;
import org.openehr.jaxb.am.ArchetypeInternalRef;
import org.openehr.jaxb.am.CComplexObject;
import org.testng.annotations.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Marko Pipan
 */
public class UseNodeFeaturesTest {
    @Test
    public void testUseNodeOccurrences() {
        Archetype archetype = TestAdlParser.parseAdl(
                "adl15/reference/features/use_node/openEHR-DEMOGRAPHIC-PERSON.use_node_occurrences.v1.adls");
        assertThat(AmMixins.of(AmQuery.get(archetype, "contacts[id6]/addresses[id7]").getOccurrences()).toString())
                .isEqualTo("[0,1]");
        assertThat(AmMixins.of(AmQuery.get(archetype, "contacts[id6]/addresses[id8]").getOccurrences()).toString())
                .isEqualTo("[0,2]");
        assertThat(AmMixins.of(AmQuery.get(archetype, "contacts[id6]/addresses[id9]").getOccurrences()).toString())
                .isEqualTo("[1,1]");


        assertArchetypeInternalRef((ArchetypeInternalRef)AmQuery.get(archetype, "contacts[id10]/addresses[id11]"),
                "/contacts[id6]/addresses[id7]", "[0,1]");
        assertArchetypeInternalRef((ArchetypeInternalRef)AmQuery.get(archetype, "contacts[id10]/addresses[id12]"),
                "/contacts[id6]/addresses[id8]", "[0,2]");
        assertArchetypeInternalRef((ArchetypeInternalRef)AmQuery.get(archetype, "contacts[id10]/addresses[id13]"),
                "/contacts[id6]/addresses[id9]", "[1,3]");


    }
    @Test
    public void testPathAnalysis() {
        Archetype archetype = TestAdlParser.parseAdl(
                "adl15/reference/features/use_node/openEHR-EHR-OBSERVATION.path_analysis_use_nodes.v1.adls");
        CComplexObject cluster = AmQuery.get(archetype, "data[id2]/events[id3]/data[id4]/items[id12]");
        ArchetypeInternalRef items1 = AmQuery.get(cluster, "items[id13]/items[id21]");
        assertArchetypeInternalRef(items1, "/data[id2]/events[id3]/data[id4]/items[id14]", "[1,1]");
        ArchetypeInternalRef items2 = AmQuery.get(cluster, "items[id22]");
        assertArchetypeInternalRef(items2, "/data[id2]/events[id3]/data[id4]/items[id14]", "[1,1]");

    }
    @Test
    public void testMultiple() {
        Archetype archetype = TestAdlParser.parseAdl(
                "adl15/reference/features/use_node/openEHR-EHR-OBSERVATION.use_node_multiple.v1.adls");

    }

    private void assertArchetypeInternalRef(ArchetypeInternalRef actual, String targetPath, String occurrencesString) {
        assertThat(actual.getTargetPath()).isEqualTo(targetPath);
        assertThat(AmMixins.of(actual.getOccurrences()).toString()).isEqualTo(occurrencesString);
    }
}
