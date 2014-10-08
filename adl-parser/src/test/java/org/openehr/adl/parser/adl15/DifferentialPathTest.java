/*
 * Copyright (C) 2014 Marand
 */

package org.openehr.adl.parser.adl15;

import org.openehr.adl.ParserTestBase;
import org.openehr.jaxb.am.Archetype;
import org.openehr.jaxb.am.CAttribute;
import org.testng.annotations.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author markopi
 */
public class DifferentialPathTest extends ParserTestBase {
    @Test
    public void testReadDifferentialPath() {
        Archetype archetype = parseArchetype("adl15/openEHR-EHR-EVALUATION.alert-zn.v1.adls");
        CAttribute diff = archetype.getDefinition().getAttributes().get(0);

        assertThat(diff.getRmAttributeName()).isEqualTo("items");
        assertThat(diff.getDifferentialPath()).isEqualTo("/data[at0001]/items");
    }

    @Test
    public void testNonDifferentialAttribute() {
        Archetype archetype = parseArchetype("adl15/openEHR-EHR-EVALUATION.alert.v1.adls");
        CAttribute diff = archetype.getDefinition().getAttributes().get(0);

        assertThat(diff.getRmAttributeName()).isEqualTo("data");
        assertThat(diff.getDifferentialPath()).isNull();
    }
}
