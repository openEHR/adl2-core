/*
 * Copyright (C) 2014 Marand
 */

package com.marand.thinkehr.adl.flattener;

import org.openehr.jaxb.am.Archetype;
import org.openehr.jaxb.am.FlatArchetype;
import org.testng.annotations.Test;

/**
 * @author markopi
 */
public class AdlWorkbenchEqualityTest extends FlattenerTestBase {
    @Test
    public void testAdlWorkbenchConstraintsEquality() {

        FlatArchetype flattened = parseAndFlattenArchetype("adl15/openEHR-EHR-EVALUATION.alert-zn.v1.adls");
        Archetype workbenchFlattened = parseArchetype("adl15/openEHR-EHR-EVALUATION.alert-zn.v1.adlf");

        assertEquals("archetype constraints differ", json(workbenchFlattened.getDefinition()), json(flattened.getDefinition()));
    }

}
