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

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import org.openehr.adl.parser.AdlDeserializer;
import org.openehr.adl.parser.BomSupportingReader;
import org.openehr.adl.rm.OpenEhrRmModel;
import org.openehr.jaxb.am.Archetype;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Created by Bjørn on 16/01/2015.
 */
public class ParentChildWithChoiceElementTest extends FlattenerTestBase {
    private AdlDeserializer deserializer;
    private ArchetypeFlattener flattener;

    @BeforeClass
    public void init() {

        final OpenEhrRmModel rmModel = OpenEhrRmModel.getInstance();
        deserializer = new AdlDeserializer();
        flattener = new ArchetypeFlattener();
    }

    protected String readArchetype(String file) throws IOException {
        return CharStreams.toString(new BomSupportingReader(
                getClass().getClassLoader().getResourceAsStream(file),
                Charsets.UTF_8));
    }


    private final String clusterAmount = "adl14/openEHR-EHR-CLUSTER.amount.v1.adl";
    private final String clusterAmountRange = "adl14/openEHR-EHR-CLUSTER.amount-range.v1.adl";

    @Test
    public void testFlattenClusterAmountRangeWithClusterAmount() throws IOException {
        Archetype parent = deserializer.parse(readArchetype(clusterAmount));
        Archetype flatParent = flattener.flatten(null, parent);
        assertThat(flatParent).isNotNull();

        Archetype child = deserializer.parse(readArchetype(clusterAmountRange));
        Archetype flatChild = flattener.flatten(flatParent, child);
        assertThat(flatChild).isNotNull();


    }
}
