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

package org.openehr.adl.serializer;

import org.openehr.adl.TestingArchetypeProvider;
import org.openehr.adl.util.TestAdlParser;
import org.openehr.jaxb.am.Archetype;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.*;

public class DAdlSerializerTest {
    @Test
    public void testSerializeDadl() throws IOException {
        Archetype archetype = TestAdlParser.parseAdl("adl15/repository/openEHR-EHR-EVALUATION.alert.v1.adls");

        AdlStringBuilder builder = new AdlStringBuilder();
        DAdlSerializer serializer = new DAdlSerializer(builder);
        serializer.serialize(archetype.getTranslations());
//        System.out.println("builder.toString() = " + builder.toString());
    }

}