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

import static org.openehr.adl.am.AmObjectFactory.newCardinality;

/**
 * @author markopi
 */
public class SpecializedAttributeMergeTest extends FlattenerTestBase {

    @Test
    public void testAttributeMerge() throws Exception {

        Archetype flattened = parseAndFlattenArchetype("adl15/openEHR-EHR-EVALUATION.alert-zn.v1.adls");
        assertCComplexObject(flattened.getDefinition(), "EVALUATION", "at0000.1", null, 1);

        CComplexObject itemList = (CComplexObject) flattened.getDefinition().getAttributes().get(0).getChildren().get(0);
        CAttribute items = itemList.getAttributes().get(0);
        assertCAttribute(items, "items", null, newCardinality(false, false, MANDATORY_UNBOUNDED), 7);
    }


}
