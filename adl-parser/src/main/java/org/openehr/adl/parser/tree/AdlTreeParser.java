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

import org.apache.commons.lang.NotImplementedException;
import org.openehr.adl.antlr4.generated.adlParser;
import org.openehr.jaxb.am.*;
import org.openehr.jaxb.rm.ArchetypeId;
import org.openehr.jaxb.rm.ResourceDescription;
import org.openehr.jaxb.rm.ResourceDescriptionItem;
import org.openehr.jaxb.rm.StringDictionaryItem;

import javax.annotation.Nullable;
import java.util.List;

import static org.openehr.adl.rm.RmObjectFactory.*;

/**
 * @author markopi
 */
public class AdlTreeParser extends AbstractAdlTreeParser {
    private AdlTreeConstraintParser constraints = new AdlTreeConstraintParser();

    public DifferentialArchetype parseAdl(adlParser.AdlContext context) {
        DifferentialArchetype result = new DifferentialArchetype();
        parseArchetypeHeader(result, context.header());
        if (context.concept() != null) {
            result.setConcept(collectNonNullText(context.concept().atCode().AT_CODE_VALUE()));
        }
        if (context.specialize() != null) {
            result.setParentArchetypeId(newArchetypeId(collectNonNullText(context.specialize().archetypeId())));
        }
        if (context.language() != null) {
            parseLanguage(result, context.language());
        }
        if (context.description() != null) {
            result.setDescription(parseDescription(context.description()));
        }
        if (context.definition()!=null) {
            result.setDefinition(constraints.parseComplexObject(context.definition().complexObjectConstraint()));
        }

        if (context.ontology() != null) {
            result.setTerminology(parseOntology(context.ontology()));
        }

//        for (Tree child : children(adlTree)) {
//            switch (child.getType()) {
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

    private ArchetypeTerminology parseOntology(adlParser.OntologyContext context) {
        ArchetypeTerminology result = new ArchetypeTerminology();

        List<adlParser.AdlObjectPropertyContext> tProperties = context.adlObjectValue().adlObjectProperty();
        for (adlParser.AdlObjectPropertyContext tProperty : tProperties) {
            String name = collectNonNullText(tProperty.identifier());
            switch (name) {
                // Each term can be singular or plural. Yes.
                case "term_definitions":
                case "term_definition":
                    parseCodeDefinitionSets(result.getTermDefinitions(), tProperty.adlValue().adlMapValue());
                    break;
                case "constraint_definitions":
                case "constraint_definition":
                    parseCodeDefinitionSets(result.getConstraintDefinitions(), tProperty.adlValue().adlMapValue());
                    break;
                case "constraint_bindings":
                case "constraint_binding":
                    parseConstraintBindings(result.getConstraintBindings(), tProperty.adlValue().adlMapValue());
                    break;
                case "term_bindings":
                case "term_binding":
                    parseTermBindings(result.getTermBindings(), tProperty.adlValue().adlMapValue());
                    break;
                case "terminology_extracts":
                case "terminology_extract":
                    parseCodeDefinitionSets(result.getTerminologyExtracts(), tProperty.adlValue().adlMapValue());
                    break;
                case "value_sets":
                case "value_set":
                    parseValueSetSets(result.getValueSets(), tProperty.adlValue().adlMapValue());
                    break;
            }
        }
        return result;
    }

    private void parseValueSetSets(List<ValueSetItem> target, adlParser.AdlMapValueContext tContext) {
        throw new NotImplementedException();
    }

    private void parseTermBindings(List<TermBindingSet> target, adlParser.AdlMapValueContext tContext) {
        throw new NotImplementedException();
    }

    private void parseConstraintBindings(List<ConstraintBindingSet> target, adlParser.AdlMapValueContext tContext) {
        throw new NotImplementedException();
    }

    private void parseCodeDefinitionSets(List<CodeDefinitionSet> target, adlParser.AdlMapValueContext tContext) {
        if (tContext == null) return;
        List<adlParser.AdlMapValueEntryContext> entries = tContext.adlMapValueEntry();
        for (adlParser.AdlMapValueEntryContext entry : entries) {

            CodeDefinitionSet cds = new CodeDefinitionSet();
            cds.setLanguage(collectNonNullText(entry.STRING()));
            parseArchetypeTerms(cds.getItems(), entry.adlValue().adlMapValue());
            target.add(cds);
        }

    }

    private void parseArchetypeTerms(List<ArchetypeTerm> target, adlParser.AdlMapValueContext tContext) {
        if (tContext == null) return;
        List<adlParser.AdlMapValueEntryContext> entries = tContext.adlMapValueEntry();
        for (adlParser.AdlMapValueEntryContext entry : entries) {
            ArchetypeTerm at = new ArchetypeTerm();
            at.setCode(collectNonNullText(entry.STRING()));
            parseStringDictionaryItems(at.getItems(), entry.adlValue().adlObjectValue());
            parseStringDictionaryItems(at.getItems(), entry.adlValue().adlMapValue());
            target.add(at);
        }
    }

    private ResourceDescription parseDescription(adlParser.DescriptionContext context) {
        ResourceDescription result = new ResourceDescription();
        adlParser.AdlObjectValueContext description = context.adlObjectValue();
        result.setCopyright(collectString(getAdlPropertyOrNull(description, "copyright")));
        result.setLifecycleState(collectString(getAdlPropertyOrNull(description, "lifecycle_state")));
        adlParser.AdlValueContext property = getAdlPropertyOrNull(description, "original_author");
        if (property != null) {
            parseStringDictionaryItems(result.getOriginalAuthor(), property.adlMapValue());
        }
        property = getAdlPropertyOrNull(description, "details");
        if (property != null) {
            parseResourceDescriptionItems(result.getDetails(), property.adlMapValue());
        }

        adlParser.AdlValueContext otherContributors = getAdlPropertyOrNull(description, "other_contributors");
        if (otherContributors != null) {
            result.getOtherContributors().addAll(collectStringList(otherContributors.openStringList()));
        }

        return result;
    }

    private void parseResourceDescriptionItems(List<ResourceDescriptionItem> target, adlParser.AdlMapValueContext context) {
        List<adlParser.AdlMapValueEntryContext> entries = context.adlMapValueEntry();

        for (adlParser.AdlMapValueEntryContext entry : entries) {
            adlParser.AdlObjectValueContext itemContext = entry.adlValue().adlObjectValue();
            adlParser.AdlValueContext property;
            ResourceDescriptionItem item = new ResourceDescriptionItem();
            //item.setCopyright(collectString(objItem.tryGet("copyright")));
            for (adlParser.AdlObjectPropertyContext propertyContext : itemContext.adlObjectProperty()) {
                String propertyName = collectNonNullText(propertyContext.identifier());
                adlParser.AdlValueContext adlValue = propertyContext.adlValue();

                switch (propertyName) {
                    case "use":
                        item.setUse(collectString(adlValue.openStringList()));
                        break;
                    case "misuse":
                        item.setMisuse(collectString(adlValue.openStringList()));
                        break;
                    case "purpose":
                        item.setPurpose(collectString(adlValue.openStringList()));
                        break;
                    case "language":
                        item.setLanguage(parseCodePhraseListSingleItem(adlValue.adlCodePhraseValueList()));
                        break;
                    case "keywords":
                        item.getKeywords().addAll(collectStringList(adlValue.openStringList()));
                        break;
                    case "original_resource_uri":
                        parseStringDictionaryItems(item.getOriginalResourceUri(), adlValue.adlMapValue());
                        break;
                }
            }


            target.add(item);

        }
    }

    private void parseStringDictionaryItems(List<StringDictionaryItem> target, @Nullable adlParser.AdlMapValueContext context) {
        if (context == null) return;
        List<adlParser.AdlMapValueEntryContext> entries = context.adlMapValueEntry();
        for (adlParser.AdlMapValueEntryContext entry : entries) {
            target.add(newStringDictionaryItem(collectText(entry.STRING()), collectString(entry.adlValue())));
        }
    }
    private void parseStringDictionaryItems(List<StringDictionaryItem> target, @Nullable adlParser.AdlObjectValueContext context) {
        if (context == null) return;
        List<adlParser.AdlObjectPropertyContext> entries = context.adlObjectProperty();
        for (adlParser.AdlObjectPropertyContext entry : entries) {
            target.add(newStringDictionaryItem(collectText(entry.identifier()), collectString(entry.adlValue())));
        }
    }


    private void parseLanguage(DifferentialArchetype target, adlParser.LanguageContext context) {
        adlParser.AdlObjectValueContext adlContext = context.adlObjectValue();
        target.setOriginalLanguage(parseCodePhraseListSingleItem(getAdlProperty(adlContext, "original_language").adlCodePhraseValueList()));
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


}
