/*
 * Copyright (C) 2014 Marand
 */

package org.openehr.adl.flattener;

import org.openehr.jaxb.am.Archetype;
import org.openehr.jaxb.am.CAttribute;
import org.openehr.jaxb.am.CComplexObject;
import org.testng.annotations.Test;

/**
 * @author markopi
 */
public class NodeOrderingTest extends FlattenerTestBase {
    @Test
    public void testNodeOrdering() {

        Archetype flattened = parseAndFlattenArchetype("adl15/ordering/openehr-ehr-OBSERVATION.ordered-specialized.v1.adls");

        CAttribute attr = ((CComplexObject) flattened.getDefinition().getAttributes().get(0).getChildren().get(0)).getAttributes().get(0);

        assertEquals(attr.getChildren().get(0).getNodeId(), "at0.7");
        assertEquals(attr.getChildren().get(1).getNodeId(), "at0002");
        assertEquals(attr.getChildren().get(2).getNodeId(), "at0.1");
        assertEquals(attr.getChildren().get(3).getNodeId(), "at0.5");
        assertEquals(attr.getChildren().get(4).getNodeId(), "at0003");
        assertEquals(attr.getChildren().get(5).getNodeId(), "at0.2");
        assertEquals(attr.getChildren().get(6).getNodeId(), "at0.3");
        assertEquals(attr.getChildren().get(7).getNodeId(), "at0.4");
        assertEquals(attr.getChildren().get(8).getNodeId(), "at0004");
        assertEquals(attr.getChildren().get(9).getNodeId(), "at0.6");

    }

}
