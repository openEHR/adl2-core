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

import org.openehr.adl.am.AmQuery;
import org.openehr.adl.rm.RmTypes;
import org.openehr.jaxb.am.Archetype;
import org.openehr.jaxb.am.CObject;
import org.testng.annotations.Test;

/**
 * @author markopi
 */
public class AttributeExpansionTest extends FlattenerTestBase {

//    @Test
//    public void testAttributeExpansion() throws Exception {
//        Archetype flattened = parseAndFlattenArchetype("adl15/openEHR-EHR-EVALUATION.alert-zn.v1.adls");
//
//        CObject definingCode = AmQuery.get(flattened, "data/items[at0003]/value/defining_code");
//        assertCObject(definingCode, RmTypes.TERMINOLOGY_CODE, null, null);
//
//        assertCTerminologyCode(definingCode, "local",
//                new String[]{"at0.15", "at0.16", "at0.17", "at0.18", "at0.19", "at0.20", "at0.21", "at0.22"}, null);
//    }

}
