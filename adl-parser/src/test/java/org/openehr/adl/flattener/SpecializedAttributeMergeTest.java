/*
 * Copyright (C) 2014 Marand
 */

package org.openehr.adl.flattener;

import org.openehr.jaxb.am.Archetype;
import org.openehr.jaxb.am.CAttribute;
import org.openehr.jaxb.am.CComplexObject;
import org.testng.annotations.Test;

import static org.openehr.adl.am.AmObjectFactory.newCardinality;

/**
 * @author markopi
 */
public class SpecializedAttributeMergeTest extends FlattenerTestBase {

    @Test
    public void testAttributeMerge() throws Exception {

        Archetype flattened = parseAndFlattenArchetype("adl15/openEHR-EHR-EVALUATION.alert-zn.v1.adls");
        assertCComplexObject(flattened.getDefinition(), "EVALUATION", "at0000.1", MANDATORY, 1);

        CComplexObject itemList = (CComplexObject) flattened.getDefinition().getAttributes().get(0).getChildren().get(0);
        CAttribute items = itemList.getAttributes().get(0);
        assertCAttribute(items, "items", MANDATORY, newCardinality(false, false, MANDATORY_UNBOUNDED), 7);
    }


}
