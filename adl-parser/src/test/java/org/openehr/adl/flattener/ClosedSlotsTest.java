package org.openehr.adl.flattener;

import org.openehr.adl.am.AmQuery;
import org.openehr.jaxb.am.Archetype;
import org.openehr.jaxb.am.ArchetypeSlot;
import org.openehr.jaxb.am.CArchetypeRoot;
import org.openehr.jaxb.am.CObject;
import org.testng.annotations.Test;

import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author markopi
 */
public class ClosedSlotsTest extends FlattenerTestBase {

    @Test
    public void flattenClosedSlot() {
        Archetype archetype = parseAndFlattenArchetype("adl15/openEHR-EHR-COMPOSITION.slots-filled.v1.adls");
        List<CObject> content = AmQuery.getAttribute(archetype, "/content").getChildren();

        assertThat(content).hasSize(3);
        assertThat(content.get(0)).isInstanceOf(CArchetypeRoot.class);
        assertThat(((CArchetypeRoot) content.get(0)).getArchetypeRef()).isEqualTo("openEHR-EHR-EVALUATION.substance_use_summary.v1");

        assertArchetypeSlot(content.get(1), "EVALUATION", "at0001", true);
        assertArchetypeSlot(content.get(2), "ACTION", "at0002", false);

    }

    private void assertArchetypeSlot(CObject cObject, String rmType, String nodeId, boolean isClosed) {
        assertThat(cObject).isInstanceOf(ArchetypeSlot.class);
        ArchetypeSlot actual = (ArchetypeSlot) cObject;

        assertThat(actual.getRmTypeName()).as("rm_type").isEqualToIgnoringCase(rmType);
        assertThat(actual.getNodeId()).as("node_id").isEqualToIgnoringCase(nodeId);
        assertThat(actual.isIsClosed() != null && actual.isIsClosed()).as("is_closed").isEqualTo(isClosed);
    }
}
