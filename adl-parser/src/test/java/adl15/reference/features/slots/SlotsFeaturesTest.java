/*
 * Copyright (C) 2014 Marand
 */

package adl15.reference.features.slots;

import com.marand.thinkehr.adl.util.TestAdlParser;
import org.openehr.jaxb.am.ArchetypeSlot;
import org.openehr.jaxb.am.Assertion;
import org.openehr.jaxb.am.DifferentialArchetype;
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
        DifferentialArchetype sourceArchetype = TestAdlParser.parseAdl(
                "adl15/reference/features/slots/openEHR-EHR-SECTION.slot_include_any_exclude_empty.v1.adls");

        ArchetypeSlot slot = (ArchetypeSlot) sourceArchetype.getDefinition().getAttributes().get(0).getChildren().get(0);
        checkAssertions(slot.getIncludes(), "archetype_id/value matches {/.*/}");
        checkAssertions(slot.getExcludes());
    }

    @Test
    public void testIncludeAnyExcludeNonAny() {
        DifferentialArchetype sourceArchetype = TestAdlParser.parseAdl(
                "adl15/reference/features/slots/openEHR-EHR-SECTION.slot_include_any_exclude_non_any.v1.adls");

        ArchetypeSlot slot = (ArchetypeSlot) sourceArchetype.getDefinition().getAttributes().get(0).getChildren().get(0);
        checkAssertions(slot.getIncludes(), "archetype_id/value matches {/.*/}");
        checkAssertions(slot.getExcludes(), "archetype_id/value matches {/openEHR-EHR-OBSERVATION\\.blood_pressure(-a-zA-Z0-9_]+)*\\.v1/}");
    }

    @Test
    public void testIncludeEmptyExcludeAny() {
        DifferentialArchetype sourceArchetype = TestAdlParser.parseAdl(
                "adl15/reference/features/slots/openEHR-EHR-SECTION.slot_include_empty_exclude_any.v1.adls");

        ArchetypeSlot slot = (ArchetypeSlot) sourceArchetype.getDefinition().getAttributes().get(0).getChildren().get(0);
        checkAssertions(slot.getIncludes());
        checkAssertions(slot.getExcludes(), "archetype_id/value matches {/.*/}");
    }

    @Test
    public void testIncludeEmptyExcludeEmpty() {
        DifferentialArchetype sourceArchetype = TestAdlParser.parseAdl(
                "adl15/reference/features/slots/openEHR-EHR-SECTION.slot_include_empty_exclude_empty.v1.adls");

        ArchetypeSlot slot = (ArchetypeSlot) sourceArchetype.getDefinition().getAttributes().get(0).getChildren().get(0);
        checkAssertions(slot.getIncludes());
        checkAssertions(slot.getExcludes());
    }

    @Test
    public void testIncludeEmptyExcludeNonAny() {
        DifferentialArchetype sourceArchetype = TestAdlParser.parseAdl(
                "adl15/reference/features/slots/openEHR-EHR-SECTION.slot_include_empty_exclude_non_any.v1.adls");

        ArchetypeSlot slot = (ArchetypeSlot) sourceArchetype.getDefinition().getAttributes().get(0).getChildren().get(0);
        checkAssertions(slot.getIncludes());
        checkAssertions(slot.getExcludes(), "archetype_id/value matches {/openEHR-EHR-OBSERVATION\\.blood_pressure(-a-zA-Z0-9_]+)*\\.v1/}");
    }
    @Test
    public void testIncludeNonAnyExcludeAny() {
        DifferentialArchetype sourceArchetype = TestAdlParser.parseAdl(
                "adl15/reference/features/slots/openEHR-EHR-SECTION.slot_include_empty_exclude_non_any.v1.adls");

        ArchetypeSlot slot = (ArchetypeSlot) sourceArchetype.getDefinition().getAttributes().get(0).getChildren().get(0);
        checkAssertions(slot.getIncludes());
        checkAssertions(slot.getExcludes(), "archetype_id/value matches {/openEHR-EHR-OBSERVATION\\.blood_pressure(-a-zA-Z0-9_]+)*\\.v1/}");
    }
    @Test
    public void testIncludeNonAnyExcludeEmpty() {
        DifferentialArchetype sourceArchetype = TestAdlParser.parseAdl(
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
