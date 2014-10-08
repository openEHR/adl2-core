/*
 * Copyright (C) 2014 Marand
 */

package org.openehr.adl.parser.adl14;

import org.openehr.adl.ParserTestBase;
import org.openehr.adl.am.AmQuery;
import org.openehr.adl.rm.RmTypes;
import org.openehr.jaxb.am.*;
import org.testng.annotations.Test;

import static org.openehr.adl.rm.RmObjectFactory.newIntervalOfInteger;

public class ArchetypeSlotTest extends ParserTestBase {

    @Test
    public void testParseIncludesExcludes() throws Exception {
        Archetype archetype = parseArchetype("adl14/adl-test-entry.archetype_slot.test.adl");

        ArchetypeConstraint node = AmQuery.find(archetype, "/content[at0001]");
        assertTrue("ArchetypeSlot expected", node instanceof ArchetypeSlot);

        ArchetypeSlot slot = (ArchetypeSlot) node;
        assertEquals("nodeId wrong", "at0001", slot.getNodeId());
        assertEquals("rmTypeName wrong", "SECTION", slot.getRmTypeName());
        assertEquals("occurrences wrong", json(newIntervalOfInteger(0, 1)),
                json(slot.getOccurrences()));


        assertEquals("includes total wrong", 1, slot.getIncludes().size());
        assertEquals("Excludes total wrong", 2, slot.getExcludes().size());

        Assertion assertion = slot.getIncludes().iterator().next();

        ExprItem item = assertion.getExpression();

        assertTrue("expressionItem type wrong",
                item instanceof ExprBinaryOperator);
        ExprBinaryOperator bo = (ExprBinaryOperator) item;
        ExprItem leftOp = bo.getLeftOperand();
        ExprItem rightOp = bo.getRightOperand();

        assertTrue("left operator type wrong",
                leftOp instanceof ExprLeaf);
        ExprLeaf left = (ExprLeaf) leftOp;
        assertEquals("left value wrong", "domain_concept", left.getItem());

        assertTrue("right operator type wrong",
                rightOp instanceof ExprLeaf);
        ExprLeaf right = (ExprLeaf) rightOp;
        assertTrue("right item type wrong", right.getItem() instanceof CString);
        CString cstring = (CString) right.getItem();
        assertEquals("right value wrong", "blood_pressure.v1",
                cstring.getPattern());
    }

    @Test
    public void testParseSingleInclude() throws Exception {
        Archetype archetype = parseArchetype("adl14/adl-test-entry.archetype_slot.test2.adl");

        ArchetypeConstraint node = AmQuery.find(archetype, "/content[at0001]");
        assertTrue("ArchetypeSlot expected", node instanceof ArchetypeSlot);

        ArchetypeSlot slot = (ArchetypeSlot) node;
        assertEquals("nodeId wrong", "at0001", slot.getNodeId());
        assertEquals("rmTypeName wrong", "SECTION", slot.getRmTypeName());
        assertEquals("occurrences wrong", json(newIntervalOfInteger(0, 1)),
                json(slot.getOccurrences()));


        assertEquals("includes total wrong", 1, slot.getIncludes().size());

        Assertion assertion = slot.getIncludes().iterator().next();

        ExprItem item = assertion.getExpression();

        assertTrue("expressionItem type wrong",
                item instanceof ExprBinaryOperator);
        ExprBinaryOperator bo = (ExprBinaryOperator) item;
        ExprItem leftOp = bo.getLeftOperand();
        ExprItem rightOp = bo.getRightOperand();

        assertTrue("left operator type wrong",
                leftOp instanceof ExprLeaf);
        ExprLeaf left = (ExprLeaf) leftOp;
        assertEquals("left value wrong", "archetype_id/value", left.getItem());

        assertTrue("right operator type wrong",
                rightOp instanceof ExprLeaf);
        ExprLeaf right = (ExprLeaf) rightOp;
        assertTrue("right item type wrong", right.getItem() instanceof CString);
        CString cstring = (CString) right.getItem();
        assertEquals("right value wrong", "openEHR-EHR-CLUSTER\\.device\\.v1",
                cstring.getPattern());

        assertNotNull("stringExpression missing", assertion.getStringExpression());
        String expectedStringExpression =
                "archetype_id/value matches {/openEHR-EHR-CLUSTER\\.device\\.v1/}";
        assertEquals("stringExpression wrong, got: " + assertion.getStringExpression(),
                expectedStringExpression, assertion.getStringExpression());

        // "archetype_id/value" refers to a string attribute
        assertEquals("Left type inside this archetype slot is wrong", "String", left.getType());
        assertEquals("Left reference type inside this archetype slot is wrong", RmTypes.ReferenceType.ATTRIBUTE.toString(),
                left.getReferenceType());

        // Constraint on a C_STRING - I don't think it is really specified if it needs to be C_STRING or CString, but because this is used e.g. for xml-serialisation, it should be consistent across all programming languages, and hence probably not use the Java style here.
        assertEquals("Right type inside this archetype slot is wrong", "C_STRING", right.getType());
        assertEquals("Right reference type inside this archetype slot is wrong.", RmTypes.ReferenceType.CONSTRAINT.toString(),
                right.getReferenceType());

    }
}
