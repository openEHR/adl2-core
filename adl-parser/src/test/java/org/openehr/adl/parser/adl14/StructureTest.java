/*
 * Copyright (C) 2014 Marand
 */

package org.openehr.adl.parser.adl14;


import org.openehr.adl.ParserTestBase;
import org.openehr.jaxb.am.*;
import org.openehr.jaxb.rm.IntervalOfInteger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.openehr.adl.am.AmObjectFactory.newCardinality;
import static org.openehr.adl.rm.RmObjectFactory.newIntervalOfInteger;
import static org.openehr.adl.rm.RmObjectFactory.newMultiplicityInterval;

/**
 * Test case tests parsing objet structures with archetypes.
 *
 * @author Rong Chen
 * @version 1.0
 */
public class StructureTest extends ParserTestBase {
// ------------------------------ FIELDS ------------------------------

    private CComplexObject definition;

// -------------------------- PUBLIC METHODS --------------------------

    @BeforeClass
    public void setUp() throws Exception {
        definition = parseArchetype("adl14/adl-test-entry.structure_test1.test.adl").getDefinition();
    }

    @Test
    public void testExistenceCardinalityAndOccurrences() throws Exception {
        // second attribute of root object
        CAttribute attr = definition.getAttributes().get(1);
        Cardinality card = newCardinality(false, false, newMultiplicityInterval(0, 8));
        assertCAttribute(attr, "members", newIntervalOfInteger(0, 1), card, 2);

        // 1st PERSON
        CComplexObject obj = (CComplexObject) attr.getChildren().get(0);
        assertCComplexObject(obj, "PERSON", null, newIntervalOfInteger(1, 1), 1);

        // 2nd PERSON
        obj = (CComplexObject) attr.getChildren().get(1);
        assertCComplexObject(obj, "PERSON", null,
                newIntervalOfInteger(0, null), 1);
    }

    @Test
    public void testParseCommentWithSlashChar() throws Exception {
        Archetype archetype = parseArchetype("adl14/adl-test-entry.structure_test2.test.adl");
    }

    @Test
    public void testParseCommentWithSlashCharAfterSlot() throws Exception {
        Archetype archetype = parseArchetype("adl14/openEHR-EHR-CLUSTER.auscultation.v1.adl");
    }

    @Test
    public void testStructure() throws Exception {
        // root object
        CComplexObject obj = definition;
        IntervalOfInteger occurrences = newIntervalOfInteger(1, 1);
        assertCComplexObject(obj, "ENTRY", "at0000", occurrences, 2);

        // first attribute of root object
        CAttribute attr = obj.getAttributes().get(0);
        assertCAttribute(attr, "subject_relationship", 1);

        // 2nd level object
        obj = (CComplexObject) attr.getChildren().get(0);
        assertCComplexObject(obj, "RELATED_PARTY", null, occurrences, 1);

        // attribute of 2nd level object
        attr = (CAttribute) obj.getAttributes().get(0);
        assertCAttribute(attr, "relationship", 1);

        // leaf object
        obj = (CComplexObject) attr.getChildren().get(0);
        assertCComplexObject(obj, "TEXT", null, occurrences, 1);

        // attribute of leaf object
        attr = (CAttribute) obj.getAttributes().get(0);
        assertCAttribute(attr, "value", 1);

        // primitive constraint of leaf object
        CString str = (CString) attr.getChildren().get(0);
        assertEquals("pattern", null, str.getPattern());
        assertEquals("set.size", 1, str.getList().size());
        assertTrue("set has", str.getList().contains("self"));
    }
}
