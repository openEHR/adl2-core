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

