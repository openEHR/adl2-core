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

package org.openehr.adl.util.walker;

import org.openehr.am.AmObject;
import org.openehr.adl.ParserTestBase;
import org.openehr.jaxb.am.*;
import org.openehr.jaxb.rm.CodePhrase;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author markopi
 */
public class ArchetypeWalkerTest extends ParserTestBase {
    @Test
    public void testConstraintWalkOrder() {
        Archetype archetype = parseArchetype("adl15/walker/adl-test-entry.walk_order.v1.adls");

        final ConstraintListVisitor listVisitor = new ConstraintListVisitor();
        AmVisitor<AmObject, AmConstraintContext> visitor = new ConstraintAmVisitor<>()
                .add(AmObject.class, listVisitor);

        ArchetypeWalker.walkConstraints(visitor, archetype, new AmConstraintContext());

        assertThat(listVisitor.visits).containsExactly(
                "C::ENTRY", "A:/items:items", "C:/items[at10001]:ELEMENT", "A:/items[at10001]/value:value",
                "C:/items[at10001]/value:DV_ORDINAL", "C:/items[at10003]:ELEMENT", "A:/items[at10003]/value:value",
                "C:/items[at10003]/value:DV_ORDINAL");
    }

    @Test
    public void testOntologyWalkOrder() {
        Archetype archetype = parseArchetype("adl15/walker/adl-test-entry.walk_order.v1.adls");

        final OntologyListVisitor listVisitor = new OntologyListVisitor();
        ArchetypeWalker.walkOntology(listVisitor, archetype.getOntology(), new AmVisitContext());

        assertThat(listVisitor.visits).containsExactly(
                "ArchetypeOntology",
                "TermBindingSet[SNOMED_CT]", "TermBindingItem[at0000/snomed_ct::1000339]",
                "TermBindingSet[ICD10]", "TermBindingItem[at0000/icd10::1000]", "TermBindingItem[at0000/icd10::1001]",
                "CodeDefinitionSet[en]", "ArchetypeTerm[at0000]", "ArchetypeTerm[at0001]", "ArchetypeTerm[at10002]");
    }

    @Test
    public void testManipulatingWalk() {
        Archetype archetype = parseArchetype("adl15/walker/adl-test-entry.walk_order.v1.adls");

        // removes TermBindingSet[ICD10] (with children) and replaces ArchetypeTerm[at0001] with ArchetypeTerm[at0999]
        final ManipulatingOntologyListVisitor manipulatingVisitor = new ManipulatingOntologyListVisitor();
        ArchetypeWalker.walkOntology(manipulatingVisitor, archetype.getOntology(), new AmVisitContext());


        final OntologyListVisitor listVisitor = new OntologyListVisitor();
        ArchetypeWalker.walkOntology(listVisitor, archetype.getOntology(), new AmVisitContext());

        assertThat(listVisitor.visits).containsExactly(
                "ArchetypeOntology",
                "TermBindingSet[SNOMED_CT]", "TermBindingItem[at0000/snomed_ct::1000339]",
                "CodeDefinitionSet[en]", "ArchetypeTerm[at0000]", "ArchetypeTerm[at0999]", "ArchetypeTerm[at10002]");

    }

    static class ConstraintListVisitor extends AbstractAmVisitor<AmObject, AmConstraintContext> {
        List<String> visits = new ArrayList<>();

        @Override
        public ArchetypeWalker.Action<? extends AmObject> preorderVisit(AmObject item, AmConstraintContext context) {
            if (item instanceof CAttribute) {
                visits.add("A:" + context.getRmPath() + ":" + ((CAttribute) item).getRmAttributeName());
            } else if (item instanceof CObject) {
                visits.add("C:" + context.getRmPath() + ":" + ((CObject) item).getRmTypeName());
            }
            return ArchetypeWalker.Action.next();
        }
    }


    static class OntologyListVisitor extends AbstractAmVisitor<AmObject, AmVisitContext> {
        List<String> visits = new ArrayList<>();

        @Override
        public ArchetypeWalker.Action<? extends AmObject> preorderVisit(AmObject item, AmVisitContext context) {
            if (item instanceof ArchetypeTerm) {
                visits.add(item.getClass().getSimpleName() + "[" + ((ArchetypeTerm) item).getCode() + "]");
            } else if (item instanceof TermBindingItem) {
                final TermBindingItem tbi = (TermBindingItem) item;
                visits.add(item.getClass().getSimpleName() + "[" + tbi.getCode() + "/" + tbi.getValue() + "]");
            } else if (item instanceof TermBindingSet) {
                visits.add(item.getClass().getSimpleName() + "[" + ((TermBindingSet) item).getTerminology() + "]");
            } else if (item instanceof CodeDefinitionSet) {
                visits.add(item.getClass().getSimpleName() + "[" + ((CodeDefinitionSet) item).getLanguage() + "]");
            } else if (item instanceof ConstraintBindingSet) {
                visits.add(item.getClass().getSimpleName() + "[" + ((ConstraintBindingSet) item).getTerminology() + "]");
            } else {
                visits.add(item.getClass().getSimpleName());
            }

            return super.preorderVisit(item, context);
        }
    }

    static class ManipulatingOntologyListVisitor extends AbstractAmVisitor<AmObject, AmVisitContext> {
        @Override
        public ArchetypeWalker.Action<? extends AmObject> preorderVisit(AmObject item, AmVisitContext context) {
            if (item instanceof TermBindingSet) {
                if (((TermBindingSet) item).getTerminology().equals("ICD10")) {
                    return ArchetypeWalker.Action.remove();
                }
            } else if (item instanceof ArchetypeTerm) {
                if (((ArchetypeTerm) item).getCode().equals("at0001")) {
                    ArchetypeTerm term = new ArchetypeTerm();
                    term.setCode("at0999");
                    return ArchetypeWalker.Action.replaceWith(term);
                }
            }
            return super.preorderVisit(item, context);
        }
    }

    private static String toCodeString(CodePhrase cs) {
        return cs.getTerminologyId().getValue() + "::" + cs.getCodeString();
    }
}
