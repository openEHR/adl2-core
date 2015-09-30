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

package adl15.reference.features.flattening;

import org.openehr.adl.am.AmQuery;
import org.openehr.adl.flattener.ArchetypeFlattener;
import org.openehr.adl.rm.OpenEhrRmModel;
import org.openehr.adl.util.TestAdlParser;
import org.openehr.jaxb.am.Archetype;
import org.openehr.jaxb.am.CComplexObject;
import org.openehr.jaxb.am.CTerminologyCode;
import org.testng.annotations.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Marko Pipan
 */
public class FlatteningFeaturesTest {
    @Test
    public void testFlattening() {
        Archetype parent = TestAdlParser.parseAdl("adl15/reference/features/flattening/openEHR-EHR-OBSERVATION.flattening_parent_1.v1.adls");
        Archetype specialized = TestAdlParser.parseAdl("adl15/reference/features/flattening/openEHR-EHR-OBSERVATION.override_to_multiple.v1.adls");

        ArchetypeFlattener flattener = new ArchetypeFlattener(OpenEhrRmModel.getInstance());

        Archetype flatParent = flattener.flatten(null, parent);
        Archetype flattened = flattener.flatten(flatParent, specialized);

        CTerminologyCode cr = AmQuery.get(flattened, "/data[id2]/events[id3]/data[id4]/items[id5]/value/defining_code");
        assertThat(cr.getCodeList().get(0)).isEqualTo("at0.2");
    }

    @Test
    public void testOccurrences() {
        Archetype specialized = TestAdlParser.parseAdl("adl15/reference/features/flattening/openEHR-EHR-OBSERVATION.demo.v1.adls");

        ArchetypeFlattener flattener = new ArchetypeFlattener(OpenEhrRmModel.getInstance());

        Archetype flat = flattener.flatten(null, specialized);

        CComplexObject cHeading = AmQuery.get(flat.getDefinition(), "/data[id2]/events[id3]/data[id4]/items[id5]");
    }
}
