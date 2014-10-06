/*
 * Copyright (C) 2014 Marand
 */

package com.marand.thinkehr.adl.validator;

import com.marand.thinkehr.adl.ParserTestBase;
import com.marand.thinkehr.adl.flattener.ArchetypeFlattener;
import com.marand.thinkehr.adl.rm.OpenEhrRmModel;
import org.openehr.jaxb.am.DifferentialArchetype;
import org.openehr.jaxb.am.FlatArchetype;
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
        rmModel = new OpenEhrRmModel();
    }

    /**
     * Tests validation logic for internal node reference.
     *
     * @throws Exception
     */
    @Test
    public void testCheckInternalReferences() throws Exception {
        DifferentialArchetype sourceArchetype = parseArchetype("adl15/validation/adl-test-car.use_node.test.adls");
        FlatArchetype useNodeArchetype = new ArchetypeFlattener(rmModel).flatten(null, sourceArchetype);

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
        DifferentialArchetype sourceArchetype = parseArchetype("adl15/validation/adl-test-ENTRY.bad_rm_model.v1.adls");
        FlatArchetype useNodeArchetype = new ArchetypeFlattener(rmModel).flatten(null, sourceArchetype);

        ArchetypeValidator validator = new ArchetypeValidator(rmModel, useNodeArchetype);
        validator.validate();

        List<AqlValidationError> expected = new ArrayList<>();
        expected.add(new AqlValidationError(AqlValidationError.Level.ERROR, "No attribute items on rm type ENTRY"));
        expected.add(new AqlValidationError(AqlValidationError.Level.ERROR, "Unknown RM type: ELEMENT_X"));
        expected.add(new AqlValidationError(AqlValidationError.Level.ERROR,
                "Existence of attribute at /items[at10001]/value/value does not conform to RM model: [1,2] is not contained in [0,1]"));


        assertThat(validator.getErrors()).containsOnly(expected.toArray());
    }

}
