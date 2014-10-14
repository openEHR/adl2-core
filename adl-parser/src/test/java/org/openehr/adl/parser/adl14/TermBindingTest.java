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

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import org.openehr.adl.ParserTestBase;
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

