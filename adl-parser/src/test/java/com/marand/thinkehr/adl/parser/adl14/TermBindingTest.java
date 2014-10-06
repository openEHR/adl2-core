/*
 * Copyright (C) 2014 Marand
 */

package com.marand.thinkehr.adl.parser.adl14;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.marand.thinkehr.adl.ParserTestBase;
import org.openehr.jaxb.am.Archetype;
import org.openehr.jaxb.am.TermBindingItem;
import org.openehr.jaxb.am.TermBindingSet;
import org.openehr.jaxb.rm.CodePhrase;
import org.testng.annotations.Test;

import java.util.List;

/**
 * TermBindingTest
 *
 * @author Rong Chen
 * @version 1.0
 */
public class TermBindingTest extends ParserTestBase {

    /**
     * Verifies term binding by multiple terminolgies
     *
     * @throws Exception
     */
    @Test
    public void testTermBindingWithMultiTerminologies() throws Exception {
        Archetype archetype = parseArchetype("adl14/adl-test-entry.term_binding.test.adl");

        // verify the first term binding
        TermBindingSet binding = archetype.getOntology().getTermBindings().get(0);
        assertEquals("wrong binding terminology", "SNOMED_CT", binding.getTerminology());

        TermBindingItem item = binding.getItems().get(0);

        assertEquals("wrong local code", "at0000", item.getCode());
        //assertEquals("wrong terms size", 1, item.getTerms().size());
        assertEquals("wrong term termid", "[snomed_ct::1000339]", toString(item.getValue()));

        // verify the second term binding
        binding = archetype.getOntology().getTermBindings().get(1);
        assertEquals("wrong binding terminology", "ICD10", binding.getTerminology());

        item = binding.getItems().get(0);
        ListMultimap<String, CodePhrase> items = termsToMultimap(binding.getItems());

        assertEquals("wrong local code", "at0000", item.getCode());
        assertEquals("wrong terms size", 2, items.get("at0000").size());
        assertEquals("wrong 1st term", "[icd10::1000]", toString(items.get("at0000").get(0)));
        assertEquals("wrong 2nd term", "[icd10::1001]", toString(items.get("at0000").get(1)));
    }

    private String toString(CodePhrase cp) {
        if (cp == null) return "null";
        return "[" + cp.getTerminologyId().getValue() + "::" + cp.getCodeString() + "]";
    }

    private ListMultimap<String, CodePhrase> termsToMultimap(List<TermBindingItem> items) {
        ListMultimap<String, CodePhrase> result = ArrayListMultimap.create();
        for (TermBindingItem item : items) {
            result.put(item.getCode(), item.getValue());
        }
        return result;
    }

    @Test
    public void testPathBasedBinding() throws Exception {
        Archetype archetype = parseArchetype("adl14/adl-test-entry.term_binding2.test.adl");

        TermBindingSet binding = archetype.getOntology().getTermBindings().get(0);
        assertEquals("wrong binding terminology", "LNC205", binding.getTerminology());

        //TermBindingItem item = binding.getItems().get(0);
        ListMultimap<String, CodePhrase> items = termsToMultimap(binding.getItems());
        List<CodePhrase> item = items.get("/data[at0002]/events[at0003]/data[at0001]/item[at0004]");
        assertEquals("wrong terms size", 1, item.size());
        assertEquals("wrong term", "[LNC205::8310-5]", toString(item.get(0)));

    }

    @Test
    public void testPathBasedBindingWithinInternalReference() throws Exception {
        Archetype archetype = parseArchetype("adl14/openEHR-EHR-OBSERVATION.test_internal_ref_binding.v1.adl");

        TermBindingSet binding = archetype.getOntology().getTermBindings().get(0);
        assertEquals("wrong binding terminology", "DDB00", binding.getTerminology());

        ListMultimap<String, CodePhrase> items = termsToMultimap(binding.getItems());
        List<CodePhrase> item1 = items.get("/data[at0001]/events[at0002]/data[at0003]/items[at0004]");

        assertEquals("wrong terms size", 1, item1.size());
        assertEquals("wrong term", "[DDB00::12345]", toString(item1.get(0)));

        List<CodePhrase> item2 = items.get("/data[at0001]/events[at0005]/data[at0003]/items[at0004]");
        assertEquals("wrong terms size", 1, item2.size());

        assertEquals("wrong term", "[DDB00::98765]", toString(item2.get(0)));

    }
}

