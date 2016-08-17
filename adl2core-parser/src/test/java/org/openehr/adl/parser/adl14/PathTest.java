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
import org.openehr.adl.am.AmQuery;
import org.openehr.jaxb.am.Archetype;
import org.openehr.jaxb.am.CAttribute;
import org.openehr.jaxb.am.CComplexObject;
import org.openehr.jaxb.am.CObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Test case tests archetype path logic.
 *
 * @author Rong Chen
 * @version 1.0
 */
public class PathTest extends ParserTestBase {


    @BeforeClass
    public void setUp() throws Exception {
        archetype = parseArchetype("adl14/adl-test-car.paths.test.adl");
        definition = archetype.getDefinition();
    }

    @Test
    public void testNodeAtPath() throws Exception {
        String[] paths = {
                "/",
                "/wheels[at0001]",
                "/wheels[at0001]/description",
                "/wheels[at0001]/parts[at0002]",
                "/wheels[at0001]/parts[at0002]/something",
                "/wheels[at0001]/parts[at0002]/something_else",
                "/wheels[at0003]",
                "/wheels[at0003]/description",
                "/wheels[at0004]",
                "/wheels[at0004]/description",
                "/wheels[at0005]",
                "/wheels[at0005]/description"
        };

        CAttribute wheels = definition.getAttributes().get(0);
        CComplexObject wheel1 = ((CComplexObject) wheels.getChildren().get(0));
        CComplexObject wheel2 = ((CComplexObject) wheels.getChildren().get(1));
        CComplexObject wheel3 = ((CComplexObject) wheels.getChildren().get(2));
        CComplexObject wheel4 = ((CComplexObject) wheels.getChildren().get(3));
        CComplexObject parts = ((CComplexObject) wheel1.getAttributes().get(1)
                .getChildren().get(0));


        CObject[] nodes = {
                definition,
                wheel1,
                wheel1.getAttributes().get(0).getChildren().get(0),
                parts,
                parts.getAttributes().get(0).getChildren().get(0),
                parts.getAttributes().get(1).getChildren().get(0),
                wheel2,
                wheel2.getAttributes().get(0).getChildren().get(0),
                wheel3,
                wheel3.getAttributes().get(0).getChildren().get(0),
                wheel4,
                wheel4.getAttributes().get(0).getChildren().get(0),
        };

        for (int i = 0; i < paths.length; i++) {
            assertEquals("wrong at path: " + paths[i], nodes[i],
                    AmQuery.find(archetype, paths[i]));
        }
    }

    private Archetype archetype;
    private CComplexObject definition;
}
