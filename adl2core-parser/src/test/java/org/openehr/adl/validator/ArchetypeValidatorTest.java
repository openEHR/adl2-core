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

package org.openehr.adl.validator;

import org.openehr.adl.ParserTestBase;
import org.openehr.adl.flattener.ArchetypeFlattener;
import org.openehr.adl.rm.OpenEhrRmModel;
import org.openehr.jaxb.am.Archetype;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Test cases tests archetype validation after parsing.
 *
 * @author Rong Chen
 * @author markopi
 */
public class ArchetypeValidatorTest extends ParserTestBase {
    private OpenEhrRmModel rmModel;

    @BeforeTest
    public void setUp() throws Exception {
        rmModel = OpenEhrRmModel.getInstance();
    }

    /**
     * Tests validation logic for internal node reference.
     *
     * @throws Exception
     */
    @Test
    public void testCheckInternalReferences() throws Exception {
        Archetype sourceArchetype = parseArchetype("adl15/validation/adl-test-car.use_node.test.adls");
        Archetype useNodeArchetype = new ArchetypeFlattener().flatten(null, sourceArchetype);

        ArchetypeValidator validator = new ArchetypeValidator(rmModel, useNodeArchetype);

        List<AqlValidationError> expected = new ArrayList<>();

        // wrong target path
        expected.add(new AqlValidationError(AqlValidationError.Level.ERROR,
                "Internal reference at /wheels[at0005]/parts refers to an unknown target: /engine[at0001]/parts[at0002]"));
        // wrong rm type
        expected.add(new AqlValidationError(AqlValidationError.Level.ERROR,
                "Internal reference at /wheels[at0006]/parts has a different rmType than target /wheels[at0001]/parts[at0002]: WHEEL!=WHEEL_PART"));

        validator.checkInternalReferences();

        assertThat(validator.getErrors()).containsOnly(expected.toArray());
    }

    @Test
    public void testCheckRmModelConformance() throws Exception {
        Archetype sourceArchetype = parseArchetype("adl15/validation/adl-test-ENTRY.bad_rm_model.v1.adls");
        Archetype useNodeArchetype = new ArchetypeFlattener().flatten(null, sourceArchetype);

        ArchetypeValidator validator = new ArchetypeValidator(rmModel, useNodeArchetype);
        validator.validate();

        List<AqlValidationError> expected = new ArrayList<>();
        expected.add(new AqlValidationError(AqlValidationError.Level.ERROR, "No attribute [items] on rm type [ENTRY]"));
        expected.add(new AqlValidationError(AqlValidationError.Level.ERROR, "Unknown RM type: ELEMENT_X"));
        expected.add(new AqlValidationError(AqlValidationError.Level.ERROR,
                "Existence of attribute at /items[at10001]/value/value does not conform to RM model: 1..2 is not contained in 0..1"));


        assertThat(validator.getErrors()).containsOnly(expected.toArray());
    }

}
