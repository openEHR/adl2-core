/*
 * Copyright (C) 2014 Marand
 */

package com.marand.thinkehr.adl.parser.adl14;

import com.marand.thinkehr.adl.ParserTestBase;
import com.marand.thinkehr.adl.am.AmQuery;
import org.openehr.jaxb.am.Archetype;
import org.openehr.jaxb.am.CAttribute;
import org.openehr.jaxb.am.CComplexObject;
import org.openehr.jaxb.am.CDvOrdinal;
import org.testng.annotations.Test;

import static org.fest.assertions.Assertions.assertThat;

public class MixedNodeTypesTest extends ParserTestBase {

    @Test
    public void testMixedNodeTypes() throws Exception {

        Archetype archetype = parseArchetype("adl14/adl-test-entry.mixed_node_types.draft.adl");
        CComplexObject items2 = AmQuery.get(archetype, "/items2");

        assertThat(items2.getAttributes()).hasSize(1);

        CAttribute attr = items2.getAttributes().get(0);
        assertThat(attr.getChildren()).hasSize(2);

        CComplexObject first = (CComplexObject) attr.getChildren().get(0);
        assertCComplexObject(first, "DV_CODED_TEXT", null, MANDATORY, 1);
        assertThat(first.getAttributes().get(0).getRmAttributeName()).isEqualTo("defining_code");

        CDvOrdinal second = (CDvOrdinal) attr.getChildren().get(1);
        assertCObject(second, "DV_ORDINAL", null, null);
        assertThat(second.getList().get(0).getSymbol().getValue()).isEqualTo("at0002");
        assertThat(second.getList().get(1).getSymbol().getValue()).isEqualTo("at0003");

    }
}
