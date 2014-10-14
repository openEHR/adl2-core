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

package org.openehr.adl.flattener;

import org.openehr.jaxb.am.Archetype;
import org.openehr.jaxb.am.CAttribute;
import org.openehr.jaxb.am.CComplexObject;
import org.testng.annotations.Test;

/**
 * @author markopi
 */
public class NodeOrderingTest extends FlattenerTestBase {
    @Test
    public void testNodeOrdering() {

        Archetype flattened = parseAndFlattenArchetype("adl15/ordering/openehr-ehr-OBSERVATION.ordered-specialized.v1.adls");

        CAttribute attr = ((CComplexObject) flattened.getDefinition().getAttributes().get(0).getChildren().get(0)).getAttributes().get(0);

        assertEquals(attr.getChildren().get(0).getNodeId(), "at0.7");
        assertEquals(attr.getChildren().get(1).getNodeId(), "at0002");
        assertEquals(attr.getChildren().get(2).getNodeId(), "at0.1");
        assertEquals(attr.getChildren().get(3).getNodeId(), "at0.5");
        assertEquals(attr.getChildren().get(4).getNodeId(), "at0003");
        assertEquals(attr.getChildren().get(5).getNodeId(), "at0.2");
        assertEquals(attr.getChildren().get(6).getNodeId(), "at0.3");
        assertEquals(attr.getChildren().get(7).getNodeId(), "at0.4");
        assertEquals(attr.getChildren().get(8).getNodeId(), "at0004");
        assertEquals(attr.getChildren().get(9).getNodeId(), "at0.6");

    }

}
