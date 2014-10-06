/*
 * Copyright (C) 2014 Marand
 */

package adl15.reference.features.rm_checking;

import com.marand.thinkehr.adl.flattener.ArchetypeFlattener;
import com.marand.thinkehr.adl.rm.OpenEhrRmModel;
import com.marand.thinkehr.adl.util.TestAdlParser;
import com.marand.thinkehr.adl.validator.ArchetypeValidator;
import org.openehr.jaxb.am.DifferentialArchetype;
import org.openehr.jaxb.am.FlatArchetype;
import org.testng.annotations.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Marko Pipan
 */
public class RmCheckingFeaturesTest {
    @Test
    public void testRmConformingSubtype() {
        DifferentialArchetype sourceArchetype = TestAdlParser.parseAdl(
                "adl15/reference/features/rm_checking/openEHR-EHR-OBSERVATION.rm_conforming_rm_subtype.v1.adls");
        final OpenEhrRmModel rmModel = new OpenEhrRmModel();
        FlatArchetype flatArchetype = new ArchetypeFlattener(rmModel).flatten(null, sourceArchetype);

        ArchetypeValidator validator = new ArchetypeValidator(rmModel, flatArchetype);
        validator.validate();
    }

    @Test
    public void testCorrectNonGeneric() {
        DifferentialArchetype sourceArchetype = TestAdlParser.parseAdl(
                "adl15/reference/features/rm_checking/openEHR-EHR-OBSERVATION.rm_correct_non_generic.v1.adls");
        final OpenEhrRmModel rmModel = new OpenEhrRmModel();
        FlatArchetype flatArchetype = new ArchetypeFlattener(rmModel).flatten(null, sourceArchetype);

        ArchetypeValidator validator = new ArchetypeValidator(rmModel, flatArchetype);
        validator.validate();
        assertThat(validator.getErrors()).isEmpty();
    }
}
