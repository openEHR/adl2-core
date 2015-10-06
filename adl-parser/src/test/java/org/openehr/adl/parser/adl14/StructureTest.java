/*
 * ADL2-core
 * Copyright (c) 2013-2014 Marand d.o.o. (www.marand.com)
 *
 * This file is part of ADL2-core.
 *
 * ADL2-core is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
        assertCComplexObject(obj, "ENTRY", "at0000", null, 2);

        // first attribute of root object
        CAttribute attr = obj.getAttributes().get(0);
        assertCAttribute(attr, "subject_relationship", 1);

        // 2nd level object
        obj = (CComplexObject) attr.getChildren().get(0);
        assertCComplexObject(obj, "RELATED_PARTY", null, null, 1);

        // attribute of 2nd level object
        attr = (CAttribute) obj.getAttributes().get(0);
        assertCAttribute(attr, "relationship", 1);

        // leaf object
        obj = (CComplexObject) attr.getChildren().get(0);
        assertCComplexObject(obj, "TEXT", null, null, 1);

        // attribute of leaf object
        attr = (CAttribute) obj.getAttributes().get(0);
        assertCAttribute(attr, "value", 1);

        // primitive constraint of leaf object
        CString str = (CString) attr.getChildren().get(0);
        assertEquals("pattern", null, str.getPattern());
        assertEquals("set.size", 1, str.getConstraint().size());
        assertTrue("set has", str.getConstraint().contains("self"));
    }
}
