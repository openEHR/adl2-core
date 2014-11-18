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

import org.openehr.adl.ParserTestBase;
import org.openehr.adl.TestingArchetypeProvider;
import org.openehr.adl.am.AmQuery;
import org.openehr.jaxb.am.*;
import org.openehr.jaxb.rm.IntervalOfInteger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.annotation.Nullable;
import java.io.IOException;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author markopi
 */
public class OperationalTemplateBuilderTest extends ParserTestBase {
    private TestingArchetypeProvider archetypeProvider;
    private OperationalTemplateBuilder operationalTemplateBuilder;

    @BeforeClass
    public void init() throws IOException {
        archetypeProvider = new TestingArchetypeProvider("adl15/repository");
        operationalTemplateBuilder = new OperationalTemplateBuilder(archetypeProvider);
    }

    @Test
    public void testUseArchetype() throws Exception {
        FlatArchetype archetype = archetypeProvider.getArchetype("openEHR-EHR-COMPOSITION.root_use_archetype.v1");

        Template template = operationalTemplateBuilder.build(archetype);

        // root node
        assertCArchetypeRoot(template.getDefinition(), "COMPOSITION", null, null, 1, "openEHR-EHR-COMPOSITION.root_use_archetype.v1");

        // inline of use_archetype node
        CArchetypeRoot evaluation = AmQuery.get(template, "content");
        assertCArchetypeRoot(evaluation, "EVALUATION", null, OPTIONAL, 1, "openEHR-EHR-EVALUATION.alert.v1");

        CDateTime value = AmQuery.get(template, "content/data/items[at0004]/value/value");
        assertCDateTime(value, "yyyy-??-??T??:??:??", null, null, null);
    }

    @Test
    public void testUseNode() throws Exception {
        FlatArchetype archetype = archetypeProvider.getArchetype("openEHR-EHR-OBSERVATION.internal_ref_binding.v1");

        Template template = operationalTemplateBuilder.build(archetype);

        // root node
        assertCArchetypeRoot(template.getDefinition(), "OBSERVATION", null, null, 1,
                "openEHR-EHR-OBSERVATION.internal_ref_binding.v1");

        CObject data1 = AmQuery.get(template, "data/events[at0002]/data");
        CObject data2 = AmQuery.get(template, "data/events[at0005]/data");

        assertThat(data1).isNotSameAs(data2);

        CObject value1 = AmQuery.get(template, "data/events[at0002]/data/items[at0004]/value/value");
        CObject value2 = AmQuery.get(template, "data/events[at0005]/data/items[at0004]/value/value");

        assertThat(value1).isNotSameAs(value2);
        assertCString(value1, null, new String[]{"day", "night"}, null);
        assertCString(value2, null, new String[]{"day", "night"}, null);

        assertEquals(json(value1), json(value2));
    }

    @Test
    public void testOntologyMerge() throws Exception {
        FlatArchetype archetype = archetypeProvider.getArchetype("openEHR-EHR-COMPOSITION.root_use_archetype.v1");

        Template template = operationalTemplateBuilder.build(archetype);

        assertThat(template.getComponentOntologies()).hasSize(2);
        assertThat(template.getComponentOntologies().get(0).getArchetypeId()).isEqualTo("openEHR-EHR-COMPOSITION.root_use_archetype.v1");
        assertThat(template.getComponentOntologies().get(1).getArchetypeId()).isEqualTo("openEHR-EHR-EVALUATION.alert.v1");

        // "de" language removed, since it is not present in root template
        FlatArchetypeOntology alertOntology = template.getComponentOntologies().get(1);
        assertThat(alertOntology.getTermDefinitions()).hasSize(1);
        assertThat(alertOntology.getTermDefinitions().get(0).getLanguage()).isEqualTo("en");


    }


    private void assertCArchetypeRoot(CComplexObject obj, String rmTypeName,
            @Nullable String nodeID, IntervalOfInteger occurrences,
            int attributes, String archetypeId) {
        assertCComplexObject(obj, rmTypeName, nodeID, occurrences, attributes);

        CArchetypeRoot root = (CArchetypeRoot) obj;
        assertThat(root.getArchetypeId().getValue()).isEqualTo(archetypeId);
    }
}
