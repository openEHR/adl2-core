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

package org.openehr.adl.parser.tree;

import org.antlr.v4.runtime.ParserRuleContext;
import org.openehr.adl.antlr4.generated.adlParser;
import org.openehr.jaxb.am.Archetype;
import org.openehr.jaxb.am.DifferentialArchetype;
import org.openehr.jaxb.rm.ArchetypeId;

import javax.annotation.Nullable;
import java.util.List;

import static org.openehr.adl.rm.RmObjectFactory.newHierObjectId;

/**
 * @author markopi
 */
public class AdlTreeParser {
    public DifferentialArchetype parseAdl(adlParser.AdlContext context) {
        DifferentialArchetype result = new DifferentialArchetype();
        parseArchetypeHeader(result, context.header());

//        for (Tree child : children(adlTree)) {
//            switch (child.getType()) {
//                case org.openehr.adl.antlr.AdlParser.ARCHETYPE:
//                case org.openehr.adl.antlr.AdlParser.TEMPLATE:
//                case org.openehr.adl.antlr.AdlParser.TEMPLATE_OVERLAY:
//                    parseArchetypeHeader(result, child);
//                    break;
//                case org.openehr.adl.antlr.AdlParser.CONCEPT:
//                    result.setConcept(child.getChild(0).getText());
//                    break;
//                case org.openehr.adl.antlr.AdlParser.SPECIALIZE:
//                case org.openehr.adl.antlr.AdlParser.SPECIALISE:
//                    parseSpecialize(result, child);
//                    break;
//                case org.openehr.adl.antlr.AdlParser.LANGUAGE:
//                    parseLanguage(result, child);
//                    break;
//                case org.openehr.adl.antlr.AdlParser.DESCRIPTION:
//                    result.setDescription(parseDescription(child.getChild(0)));
//                    break;
//                case org.openehr.adl.antlr.AdlParser.DEFINITION:
//                    result.setDefinition(constraints.parseTypeConstraint(child.getChild(0)));
//                    break;
//                case org.openehr.adl.antlr.AdlParser.ONTOLOGY:
//                case org.openehr.adl.antlr.AdlParser.TERMINOLOGY:
//                    result.setTerminology(parseOntology(child.getChild(0)));
//                    break;
//                case org.openehr.adl.antlr.AdlParser.ANNOTATIONS:
//                    result.setAnnotations(new ResourceAnnotations());
//                    parseAnnotations(result.getAnnotations().getItems(), child.getChild(0));
//                    break;
//                default:
//                    throw new RuntimeRecognitionException(child);
//            }
//        }
        return result;
    }

    private void parseArchetypeHeader(Archetype target, adlParser.HeaderContext context) {
        target.setArchetypeId(new ArchetypeId());
        String archetypeId = collectText(context.archetypeId());
        target.getArchetypeId().setValue(archetypeId);

        target.setIsTemplate(context.headerTag().TEMPLATE() != null);
        target.setIsOverlay(context.headerTag().TEMPLATE_OVERLAY() != null);
        if (context.archetypePropertyList() != null) {
            List<adlParser.ArchetypePropertyContext> propertiesContext = context.archetypePropertyList().archetypeProperty();
            for (adlParser.ArchetypePropertyContext propertyContext : propertiesContext) {
                String propertyName = collectNonNullText(propertyContext.identifier());
                String propertyValue = collectText(propertyContext.archetypePropertyValue());

                switch (propertyName) {
                    case "adl_version":
                        target.setAdlVersion(propertyValue);
                        break;
                    case "rm_release":
                        target.setRmRelease(propertyValue);
                        break;
                    case "generated":
                        target.setIsGenerated(true);
                        break;
                    case "uid":
                        target.setUid(newHierObjectId("uid"));
                        break;
                    case "controlled":
                        target.setIsControlled(true);
                        break;
                    case "uncontrolled":
                        target.setIsControlled(false);
                        break;
                }
            }
        }
    }


    protected String collectNonNullText(ParserRuleContext context) {
        String result = collectText(context);
        if (result == null) {
            throw new AdlTreeParserException(context, "Text must not be null");
        }
        return result;
    }

    @Nullable
    protected String collectText(ParserRuleContext context) {
        if (context == null) return null;
        return context.getText();
//        if (context.ge Type() == org.openehr.adl.antlr.AdlParser.AST_STRING_LIST && tree.getChildCount() == 1) {
//            return tree.getChild(0).getText();
//        }
//        if (tree.getType() == org.openehr.adl.antlr.AdlParser.AST_NULL) {
//            return null;
//        }
//        // sometimes this is required to exclude container tags, such as <> in adlValue
//        if (tree.getType() == org.openehr.adl.antlr.AdlParser.AST_TEXT_CONTAINER) {
//            return collectText(tree.getChild(0));
//        }
//
//        // walk through all contained tokens and concatenate them
//        StringBuilder result = new StringBuilder();
//        for (Token token : tokenStream.get(tree.getTokenStartIndex(), tree.getTokenStopIndex())) {
//            result.append(token.getText());
//        }
//        return result.toString();
    }


}
