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
import org.openehr.jaxb.am.CObject;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author markopi
 */
public class NodeOrderingTest extends FlattenerTestBase {
    @Test
    public void testNodeOrdering() {

        Archetype flattened = parseAndFlattenArchetype("adl15/ordering/openehr-ehr-OBSERVATION.ordered-specialized.v1.adls");

        CAttribute attr = ((CComplexObject) flattened.getDefinition().getAttributes().get(0).getChildren().get(0)).getAttributes().get(0);

        List<String> nodeids = new ArrayList<>();
        for (CObject cObject : attr.getChildren()) {
            nodeids.add(cObject.getNodeId());
        }
        assertThat(nodeids).containsExactly("at0.7", "at0002", "at0.1", "at0.5", "at0003", "at0.2", "at0.4", "at0.3", "at0004", "at0.6");


    }

}
