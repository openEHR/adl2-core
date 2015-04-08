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

package adl15.reference.features.rm_checking;

import org.openehr.adl.flattener.ArchetypeFlattener;
import org.openehr.adl.rm.OpenEhrRmModel;
import org.openehr.adl.util.TestAdlParser;
import org.openehr.adl.validator.ArchetypeValidator;
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
        final OpenEhrRmModel rmModel = OpenEhrRmModel.getInstance();
        FlatArchetype flatArchetype = new ArchetypeFlattener(rmModel).flatten(null, sourceArchetype);

        ArchetypeValidator validator = new ArchetypeValidator(rmModel, flatArchetype);
        validator.validate();
    }

    @Test
    public void testCorrectNonGeneric() {
        DifferentialArchetype sourceArchetype = TestAdlParser.parseAdl(
                "adl15/reference/features/rm_checking/openEHR-EHR-OBSERVATION.rm_correct_non_generic.v1.adls");
        final OpenEhrRmModel rmModel = OpenEhrRmModel.getInstance();
        FlatArchetype flatArchetype = new ArchetypeFlattener(rmModel).flatten(null, sourceArchetype);

        ArchetypeValidator validator = new ArchetypeValidator(rmModel, flatArchetype);
        validator.validate();
        assertThat(validator.getErrors()).isEmpty();
    }
}
