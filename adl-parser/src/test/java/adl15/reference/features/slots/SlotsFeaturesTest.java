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

package adl15.reference.features.slots;

import org.openehr.adl.util.TestAdlParser;
import org.openehr.jaxb.am.Archetype;
import org.openehr.jaxb.am.ArchetypeSlot;
import org.openehr.jaxb.am.Assertion;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Marko Pipan
 */
public class SlotsFeaturesTest {
    @Test
    public void testIncludeAnyExcludeEmpty() {
        Archetype sourceArchetype = TestAdlParser.parseAdl(
                "adl15/reference/features/slots/openEHR-EHR-SECTION.slot_include_any_exclude_empty.v1.adls");

        ArchetypeSlot slot = (ArchetypeSlot) sourceArchetype.getDefinition().getAttributes().get(0).getChildren().get(0);
        checkAssertions(slot.getIncludes(), "archetype_id/value matches {/.*/}");
        checkAssertions(slot.getExcludes());
    }

    @Test
    public void testIncludeAnyExcludeNonAny() {
        Archetype sourceArchetype = TestAdlParser.parseAdl(
                "adl15/reference/features/slots/openEHR-EHR-SECTION.slot_include_any_exclude_non_any.v1.adls");

        ArchetypeSlot slot = (ArchetypeSlot) sourceArchetype.getDefinition().getAttributes().get(0).getChildren().get(0);
        checkAssertions(slot.getIncludes(), "archetype_id/value matches {/.*/}");
        checkAssertions(slot.getExcludes(), "archetype_id/value matches {/openEHR-EHR-OBSERVATION\\.blood_pressure(-a-zA-Z0-9_]+)*\\.v1/}");
    }

    @Test
    public void testIncludeEmptyExcludeAny() {
        Archetype sourceArchetype = TestAdlParser.parseAdl(
                "adl15/reference/features/slots/openEHR-EHR-SECTION.slot_include_empty_exclude_any.v1.adls");

        ArchetypeSlot slot = (ArchetypeSlot) sourceArchetype.getDefinition().getAttributes().get(0).getChildren().get(0);
        checkAssertions(slot.getIncludes());
        checkAssertions(slot.getExcludes(), "archetype_id/value matches {/.*/}");
    }

    @Test
    public void testIncludeEmptyExcludeEmpty() {
        Archetype sourceArchetype = TestAdlParser.parseAdl(
                "adl15/reference/features/slots/openEHR-EHR-SECTION.slot_include_empty_exclude_empty.v1.adls");

        ArchetypeSlot slot = (ArchetypeSlot) sourceArchetype.getDefinition().getAttributes().get(0).getChildren().get(0);
        checkAssertions(slot.getIncludes());
        checkAssertions(slot.getExcludes());
    }

    @Test
    public void testIncludeEmptyExcludeNonAny() {
        Archetype sourceArchetype = TestAdlParser.parseAdl(
                "adl15/reference/features/slots/openEHR-EHR-SECTION.slot_include_empty_exclude_non_any.v1.adls");

        ArchetypeSlot slot = (ArchetypeSlot) sourceArchetype.getDefinition().getAttributes().get(0).getChildren().get(0);
        checkAssertions(slot.getIncludes());
        checkAssertions(slot.getExcludes(), "archetype_id/value matches {/openEHR-EHR-OBSERVATION\\.blood_pressure(-a-zA-Z0-9_]+)*\\.v1/}");
    }

    @Test
    public void testIncludeNonAnyExcludeAny() {
        Archetype sourceArchetype = TestAdlParser.parseAdl(
                "adl15/reference/features/slots/openEHR-EHR-SECTION.slot_include_empty_exclude_non_any.v1.adls");

        ArchetypeSlot slot = (ArchetypeSlot) sourceArchetype.getDefinition().getAttributes().get(0).getChildren().get(0);
        checkAssertions(slot.getIncludes());
        checkAssertions(slot.getExcludes(), "archetype_id/value matches {/openEHR-EHR-OBSERVATION\\.blood_pressure(-a-zA-Z0-9_]+)*\\.v1/}");
    }

    @Test
    public void testIncludeNonAnyExcludeEmpty() {
        Archetype sourceArchetype = TestAdlParser.parseAdl(
                "adl15/reference/features/slots/openEHR-EHR-SECTION.slot_include_non_any_exclude_empty.v1.adls");

        ArchetypeSlot slot = (ArchetypeSlot) sourceArchetype.getDefinition().getAttributes().get(0).getChildren().get(0);
        checkAssertions(slot.getIncludes(), "archetype_id/value matches {/openEHR-EHR-OBSERVATION\\.blood_pressure(-a-zA-Z0-9_]+)*\\.v1|openEHR-EHR-OBSERVATION\\.body_temperature(-a-zA-Z0-9_]+)*\\.v1|openEHR-EHR-OBSERVATION\\.heart_rate(-a-zA-Z0-9_]+)*\\.v1|openEHR-EHR-OBSERVATION\\.indirect_oximetry(-a-zA-Z0-9_]+)*\\.v1|openEHR-EHR-OBSERVATION\\.respiration(-a-zA-Z0-9_]+)*\\.v1/}");
        checkAssertions(slot.getExcludes());
    }


    private void checkAssertions(List<Assertion> actual, String... expected) {
        List<String> actualExpressions = new ArrayList<>();
        if (actual != null) {
            for (Assertion assertion : actual) {
                actualExpressions.add(assertion.getStringExpression());
            }
        }
        assertThat(actualExpressions).isEqualTo(Arrays.asList(expected));
    }

}
