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


import org.openehr.adl.util.TestAdlParser;
import org.openehr.jaxb.am.Archetype;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.fest.assertions.Assertions.assertThat;
import static org.testng.Assert.assertNotNull;

public class ArchetypeSerializerTest {

    @Test
    public void testSerialize() throws Exception {
        Archetype archetypeAlert = TestAdlParser.parseAdl("adl15/repository/openEHR-EHR-EVALUATION.alert.v1.adls");
        String serialized = ArchetypeSerializer.serialize(archetypeAlert);
        write(serialized, "TestSerialize-Alert-v1.adls");
    }

    @Test
    public void testSerializeCodedText() throws Exception {
        Archetype archetype = TestAdlParser.parseAdl("adl14/adl-test-composition.dv_coded_text.test.adl");
        String serialized = ArchetypeSerializer.serialize(archetype);
        assertNotNull(serialized);
    }

    @Test
    public void testSerializeBloodPressure() {
        Archetype archetype = TestAdlParser.parseAdl("adl14/openEHR-EHR-OBSERVATION.blood_pressure.v1.adl");
        String adl = ArchetypeSerializer.serialize(archetype);
        write(adl, "openEHR-EHR-OBSERVATION.blood_pressure.v1.adl");
        Archetype result = TestAdlParser.parseAdlFromString(adl);
        assertThat(result).isNotNull();

    }

    @Test
    public void testSerializeDemoArchetype() {
        Archetype archetype = TestAdlParser.parseAdl("adl14/openEHR-EHR-OBSERVATION.demo.v1.adl");
        String serialized = ArchetypeSerializer.serialize(archetype);
        assertNotNull(serialized);
        write(serialized, "TestDemoArchetype.adl");
        Archetype result = TestAdlParser.parseAdlFromString(serialized);
        assertThat(result).isNotNull();


    }


    @Test
    public void testAssumedValueOnQuantituy() throws Exception {

        Archetype archetype = TestAdlParser.parseAdl("adl14/openEHR-EHR-OBSERVATION.test_assumed.v1.adl");
        String serialized = ArchetypeSerializer.serialize(archetype);

        assertNotNull(serialized);
        write(serialized, "TestAssumedValueOnQuantity.adl");
    }

    @Test
    public void testSerializeQuantity() throws Exception {
        Archetype archetype = TestAdlParser.parseAdl("adl14/adl-test-entry.c_dv_quantity_full.test.adl");
        String serialized = ArchetypeSerializer.serialize(archetype);
        assertNotNull(serialized);
        write(serialized, "TestSerializeQuantity.adl");
    }

    @Test
    public void testSerializeAttributes() throws Exception {
        Archetype archetype = TestAdlParser.parseAdl("adl15/openEHR-EHR-COMPOSITION.attributes_a.v1.adls");
        String serialized = ArchetypeSerializer.serialize(archetype);
        assertNotNull(serialized);
        write(serialized, "TestSerializeAttribute.adls");
    }

    /**
     * Write the ADL to target/{file) to inspect manually and try open with Archetype Editor
     *
     * @param adl  the string representation of Archetype
     * @param file the filename
     */
    public static void write(String adl, String file) {
        try {
            Files.createDirectories(Paths.get("target"));
            FileWriter writer = new FileWriter(new File("target/" + file));
            writer.write(adl);
            writer.flush();
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testQuantityTuple() throws Exception {
        Archetype archetype = TestAdlParser.parseAdl("adl15/tuples/openEHR-EHR-OBSERVATION.quantity_tuple.v1.adls");
        String serialized = ArchetypeSerializer.serialize(archetype);

//        System.out.println(serialized);
    }
}