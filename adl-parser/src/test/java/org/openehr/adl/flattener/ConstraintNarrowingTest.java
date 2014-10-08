/*
 * Copyright (C) 2014 Marand
 */

package org.openehr.adl.flattener;

import org.openehr.adl.am.AmQuery;
import org.openehr.jaxb.am.Archetype;
import org.openehr.jaxb.am.CAttribute;
import org.openehr.jaxb.am.CComplexObject;
import org.testng.annotations.Test;

import static org.openehr.adl.am.AmObjectFactory.newCardinality;

/**
 * @author markopi
 */
public class ConstraintNarrowingTest extends FlattenerTestBase {

    @Test
    public void testCardinalitySpecialization() {
        Archetype flattened = parseAndFlattenArchetype("adl15/openEHR-EHR-EVALUATION.alert-zn.v1.adls");


        CAttribute items = ((CComplexObject) AmQuery.get(flattened, "/data")).getAttributes().get(0);

        assertCAttribute(items, "items", MANDATORY, newCardinality(false, false, MANDATORY_UNBOUNDED), 7);
    }

    @Test(expectedExceptions = AdlFlattenerException.class)
    public void testTypeConstraintFails() {
        // fails, because DV_DATE_TIME is not a subclass of DV_TEXT
        Archetype flattened = parseAndFlattenArchetype("adl15/openEHR-EHR-EVALUATION.alert-zn-typeconstraint-fail.v1.adls");
    }


}
