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

package adl15.reference.features.basic;

import org.openehr.adl.serializer.ArchetypeSerializer;
import org.openehr.adl.util.TestAdlParser;
import org.openehr.jaxb.am.Archetype;
import org.testng.annotations.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author markopi
 */
public class ExistenceOutputTest {
    @Test
    public void outputExistence() {
        Archetype archetype = TestAdlParser.parseAdl(
                "adl15/reference/features/basic/CIMI-CORE-PARTY.party.v1.0.0.adls");

        String serialized = ArchetypeSerializer.serialize(archetype);

        assertThat(serialized).contains("ELEMENT[id3] occurrences matches {1}");
        assertThat(serialized).contains("null_flavor existence matches {0}");
    }
}
