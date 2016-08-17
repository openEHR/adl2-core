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

package org.openehr.adl.parser.adl15;

import org.openehr.adl.ParserTestBase;
import org.openehr.jaxb.am.Archetype;
import org.openehr.jaxb.am.CObject;
import org.testng.annotations.Test;

import java.util.List;

import static org.openehr.adl.am.AmObjectFactory.newSiblingOrder;
import static org.fest.assertions.Assertions.assertThat;

/**
 * @author markopi
 */
public class SpecializedOrderingTest extends ParserTestBase {


    @Test
    public void testOrder() throws Exception {
        Archetype archetype = parseArchetype("adl15/ordering/openehr-ehr-OBSERVATION.ordered-specialized.v1.adls");

        List<CObject> objects = archetype.getDefinition().getAttributes().get(0).getChildren();

        assertThat(objects).hasSize(7);
        assertCObject(objects.get(0), "POINT_EVENT", "at0.1", null, newSiblingOrder(false, "at0002"));
        assertCObject(objects.get(1), "POINT_EVENT", "at0.2", null, newSiblingOrder(false, "at0003"));
        assertCObject(objects.get(2), "POINT_EVENT", "at0.3", null, newSiblingOrder(true, "at0004"));
        assertCObject(objects.get(3), "POINT_EVENT", "at0.4", null, newSiblingOrder(false, "at0003"));
        assertCObject(objects.get(4), "POINT_EVENT", "at0.5", null, newSiblingOrder(true, "at0003"));
        assertCObject(objects.get(5), "POINT_EVENT", "at0.6", null, newSiblingOrder(false, "at0004"));
        assertCObject(objects.get(6), "POINT_EVENT", "at0.7", null, newSiblingOrder(true, "at0002"));
    }

}
