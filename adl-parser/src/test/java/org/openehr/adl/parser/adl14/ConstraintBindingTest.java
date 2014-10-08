/*
 * Copyright (C) 2014 Marand
 */

package org.openehr.adl.parser.adl14;

import org.openehr.adl.ParserTestBase;
import org.openehr.jaxb.am.Archetype;
import org.openehr.jaxb.am.ConstraintBindingItem;
import org.openehr.jaxb.am.ConstraintBindingSet;
import org.testng.annotations.Test;

import java.util.List;

/**
 * TermBindingTest
 *
 * @author Rong Chen
 * @version 1.0
 */
public class ConstraintBindingTest extends ParserTestBase {


    @Test
    public void testConstraintBindingWithMultiTerminologies() throws Exception {
        Archetype archetype = parseArchetype("adl14/adl-test-entry.constraint_binding.test.adl");
        List<ConstraintBindingSet> list = archetype.getOntology().getConstraintBindings();

        assertEquals("unexpected number of onotology binding", 2, list.size());

        // verify the first constraint binding
        ConstraintBindingSet binding = list.get(0);
        assertEquals("unexpected binding terminology", "SNOMED_CT", binding.getTerminology());

        ConstraintBindingItem item = binding.getItems().get(0);

        assertEquals("unexpected local code", "ac0001", item.getCode());
        assertEquals("expected query",
                "http://terminologyx.org?terminology_id=snomed_ct&&has_relation=102002;with_target=128004",
                item.getValue());

        // verify the second constraint binding
        binding = list.get(1);
        assertEquals("unexpected binding terminology", "ICD10", binding.getTerminology());

        item = (ConstraintBindingItem) binding.getItems().get(0);

        assertEquals("unexpected local code", "ac0001", item.getCode());
        assertEquals("exexpected query",
                "http://terminologyx.org?terminology_id=icd10&&has_relation=a2;with_target=b19",
                item.getValue());
    }
}

