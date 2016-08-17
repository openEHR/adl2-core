package org.openehr.adl.parser.adl15;

import org.openehr.adl.ParserTestBase;
import org.openehr.adl.am.AmQuery;
import org.openehr.jaxb.am.Archetype;
import org.openehr.jaxb.am.ArchetypeSlot;
import org.openehr.jaxb.am.Assertion;
import org.openehr.jaxb.am.ExprBinaryOperator;
import org.testng.annotations.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author markopi
 */
public class SlotExpressionTest extends ParserTestBase {
    @Test
    public void parseIncludeExpression() {
        Archetype archetype = parseArchetype("adl15/openEHR-EHR-ACTION.medication.v1.adls");

        ArchetypeSlot slot = AmQuery.find(archetype, "/description[id18]/items[id24]");
        Assertion a = slot.getIncludes().get(0);
        assertThat(a.getExpression()).isInstanceOf(ExprBinaryOperator.class);
    }
}
