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

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import org.openehr.adl.antlr4.generated.adlParser;
import org.openehr.jaxb.am.*;
import org.openehr.jaxb.rm.*;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.openehr.adl.parser.tree.AdlTreeConstraintParser.parseComplexObject;
import static org.openehr.adl.parser.tree.AdlTreeParserUtils.*;
import static org.openehr.adl.rm.RmObjectFactory.*;

/**
 * @author markopi
 */
public class AdlTreeParser {

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
        if (context.definition() != null) {
            result.setDefinition(parseComplexObject(context.definition().complexObjectConstraint()));
        }

        if (context.ontology() != null) {
            result.setTerminology(parseOntology(context.ontology()));
        }
        if (context.annotations()!=null) {
            result.setAnnotations(parseAnnotations(context.annotations()));
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

    private ResourceAnnotations parseAnnotations(adlParser.AnnotationsContext context) {
        ResourceAnnotations result = new ResourceAnnotations();
        DAdlObject dAnnotations = DAdlObject.parse(context.adlObjectValue());
        
        adlParser.AdlValueContext cLanguages = skipItems(dAnnotations.tryGet("items"));
        if (cLanguages==null || cLanguages.adlMapValue()==null) return result;
        for (adlParser.AdlMapValueEntryContext cLanguageEntry : cLanguages.adlMapValue().adlMapValueEntry()) {
            ResourceAnnotationNodes nodes = new ResourceAnnotationNodes();
            nodes.setLanguage(collectText(cLanguageEntry.key));
            adlParser.AdlValueContext cItems = skipItems(cLanguageEntry.value);
            parseResourceAnnotationNodeItems(nodes.getItems(), cItems);
            result.getItems().add(nodes);
        }
        return result;
    }

    private void parseResourceAnnotationNodeItems(List<ResourceAnnotationNodeItems> target, adlParser.AdlValueContext context) {
        if (context==null || context.adlMapValue()==null) return;
        for (adlParser.AdlMapValueEntryContext cEntry : context.adlMapValue().adlMapValueEntry()) {
            ResourceAnnotationNodeItems item = new ResourceAnnotationNodeItems();
            item.setPath(collectText(cEntry.key));

            parseStringDictionaryItems(item.getItems(), skipItems(cEntry.value));
            target.add(item);
        }
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
                    parseCodeDefinitionSets(result.getTermDefinitions(), skipItems(tProperty.adlValue()).adlMapValue());
                    break;
                case "constraint_definitions":
                case "constraint_definition":
                    parseCodeDefinitionSets(result.getConstraintDefinitions(), skipItems(tProperty.adlValue()).adlMapValue());
                    break;
                case "constraint_bindings":
                case "constraint_binding":
                    parseConstraintBindings(result.getConstraintBindings(), skipItems(tProperty.adlValue()).adlMapValue());
                    break;
                case "term_bindings":
                case "term_binding":
                    parseTermBindings(result.getTermBindings(), skipItems(tProperty.adlValue()).adlMapValue());
                    break;
                case "terminology_extracts":
                case "terminology_extract":
                    parseCodeDefinitionSets(result.getTerminologyExtracts(), skipItems(tProperty.adlValue()).adlMapValue());
                    break;
                case "value_sets":
                case "value_set":
                    parseValueSetSets(result.getValueSets(), skipItems(tProperty.adlValue()).adlMapValue());
                    break;
            }
        }
        return result;
    }

    private void parseValueSetSets(List<ValueSetItem> target, adlParser.AdlMapValueContext tContext) {
        for (adlParser.AdlMapValueEntryContext cEntry : tContext.adlMapValueEntry()) {
            ValueSetItem vsi = new ValueSetItem();
            vsi.setId(collectText(cEntry.STRING()));
            adlParser.AdlValueContext adlValue = skipItems(cEntry.adlValue());
            DAdlObject dValue = DAdlObject.parse(adlValue.adlObjectValue());
            adlParser.AdlValueContext cMembers = dValue.get("members");
            vsi.getMembers().addAll(collectStringList(cMembers.openStringList()));
            target.add(vsi);
        }
    }

    private void parseTermBindings(List<TermBindingSet> target, adlParser.AdlMapValueContext tContext) {
        for (adlParser.AdlMapValueEntryContext cEntry : tContext.adlMapValueEntry()) {
            TermBindingSet tbs = new TermBindingSet();
            tbs.setTerminology(collectText(cEntry.STRING()));
            adlParser.AdlValueContext value = skipItems(cEntry.adlValue());
            parseTermBindingItems(tbs.getItems(), value);
            target.add(tbs);
        }
    }

    private void parseTermBindingItems(List<TermBindingItem> target, adlParser.AdlValueContext mapContext) {
        if (mapContext == null || mapContext.adlMapValue() == null) return;
        for (adlParser.AdlMapValueEntryContext cEntry : mapContext.adlMapValue().adlMapValueEntry()) {
            TermBindingItem tbi = new TermBindingItem();
            tbi.setCode(collectText(cEntry.STRING()));
            if (cEntry.adlValue().adlCodePhraseValueList() != null) {
                tbi.setValue(Joiner.on(", ").join(parseCodePhraseListString(cEntry.adlValue().adlCodePhraseValueList())));
            } else if (cEntry.adlValue().openStringList() != null) {
                tbi.setValue(collectString(cEntry.adlValue().openStringList()));
            } else if (cEntry.adlValue().url() != null) {
                tbi.setValue(collectText(cEntry.adlValue().url()));
            }
            target.add(tbi);
        }
    }

    private String parseCodePhraseString(adlParser.AdlCodePhraseValueContext context) {
        return collectNonNullText(context.tid) + "::" + collectNonNullText(context.code);
    }

    private String parseCodePhraseString(adlParser.AdlCodePhraseValueListContext tCodePhraseList) {
        return parseCodePhraseListString(tCodePhraseList).get(0);
    }

    private List<String> parseCodePhraseListString(adlParser.AdlCodePhraseValueListContext tCodePhraseList) {
        List<String> result = new ArrayList<>();
        if (tCodePhraseList.adlCodePhraseValue() != null) {
            for (adlParser.AdlCodePhraseValueContext adlCodePhraseValueContext : tCodePhraseList.adlCodePhraseValue()) {
                result.add(parseCodePhraseString(adlCodePhraseValueContext));
            }
        }
        return result;

    }


    private adlParser.AdlValueContext skipItems(adlParser.AdlValueContext context) {
        if (context==null || context.adlObjectValue() == null) return context;
        DAdlObject dAdlObject = DAdlObject.parse(context.adlObjectValue());
        adlParser.AdlValueContext result = dAdlObject.tryGet("items");
        if (result != null) return result;
        return context;

    }

    private void parseConstraintBindings(List<ConstraintBindingSet> target, adlParser.AdlMapValueContext tContext) {
        for (adlParser.AdlMapValueEntryContext cEntry : tContext.adlMapValueEntry()) {
            ConstraintBindingSet cbs = new ConstraintBindingSet();
            cbs.setTerminology(collectText(cEntry.STRING()));
            adlParser.AdlValueContext value = skipItems(cEntry.adlValue());
            parseConstraintBindingItems(cbs.getItems(), value);
            target.add(cbs);

        }
    }

    private void parseConstraintBindingItems(List<ConstraintBindingItem> target, adlParser.AdlValueContext context) {
        if (context == null || context.adlMapValue() == null) return;
        for (adlParser.AdlMapValueEntryContext cEntry : context.adlMapValue().adlMapValueEntry()) {
            ConstraintBindingItem cbi = new ConstraintBindingItem();
            cbi.setCode(collectText(cEntry.STRING()));

            cbi.setValue(collectText(cEntry.adlValue()));
            target.add(cbi);
        }
    }


    private void parseCodeDefinitionSets(List<CodeDefinitionSet> target, adlParser.AdlMapValueContext tContext) {
        if (tContext == null) return;
        List<adlParser.AdlMapValueEntryContext> entries = tContext.adlMapValueEntry();
        for (adlParser.AdlMapValueEntryContext entry : entries) {

            CodeDefinitionSet cds = new CodeDefinitionSet();
            cds.setLanguage(collectNonNullText(entry.STRING()));
            parseArchetypeTerms(cds.getItems(), skipItems(entry.adlValue()).adlMapValue());
            target.add(cds);
        }

    }

    private void parseArchetypeTerms(List<ArchetypeTerm> target, adlParser.AdlMapValueContext tContext) {
        if (tContext == null) return;
        List<adlParser.AdlMapValueEntryContext> entries = tContext.adlMapValueEntry();
        for (adlParser.AdlMapValueEntryContext entry : entries) {
            ArchetypeTerm at = new ArchetypeTerm();
            at.setCode(collectNonNullText(entry.STRING()));
            parseStringDictionaryItems(at.getItems(), skipItems(entry.adlValue()).adlObjectValue());
            parseStringDictionaryItems(at.getItems(), skipItems(entry.adlValue()).adlMapValue());
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

    private void parseStringDictionaryItems(List<StringDictionaryItem> target, @Nullable adlParser.AdlValueContext context) {
        if (context==null) return;
        parseStringDictionaryItems(target, context.adlMapValue());
        parseStringDictionaryItems(target, context.adlObjectValue());
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
        DAdlObject cLanguage = DAdlObject.parse(context.adlObjectValue());

        target.setOriginalLanguage(parseCodePhraseListSingleItem(cLanguage.get("original_language").adlCodePhraseValueList()));
        adlParser.AdlValueContext cTranslations = cLanguage.tryGet("translations");
        if (cTranslations != null && cTranslations.adlMapValue() != null) {
            for (adlParser.AdlMapValueEntryContext cEntry : cTranslations.adlMapValue().adlMapValueEntry()) {
                target.getTranslations().add(parseTranslation(cEntry.adlValue()));
            }
        }
    }

    private TranslationDetails parseTranslation(adlParser.AdlValueContext context) {
        DAdlObject dTranslation = DAdlObject.parse(context.adlObjectValue());
        TranslationDetails result = new TranslationDetails();
        result.setLanguage(parseCodePhraseListSingleItem(dTranslation.get("language").adlCodePhraseValueList()));
        result.setAccreditation(dTranslation.tryGetString("accreditation"));
        for (Map.Entry<String, String> cAuthor : toStringMap(dTranslation.tryGet("author")).entrySet()) {
            result.getAuthor().add(newStringDictionaryItem(cAuthor.getKey(), cAuthor.getValue()));
        }

        for (Map.Entry<String, String> cAuthor : toStringMap(dTranslation.tryGet("other_details")).entrySet()) {
            result.getOtherDetails().add(newStringDictionaryItem(cAuthor.getKey(), cAuthor.getValue()));
        }

        return result;
    }

    private Map<String, String> toStringMap(adlParser.AdlValueContext context) {
        if (context==null) return ImmutableMap.of();
        return toStringMap(context.adlMapValue());
    }
    private Map<String, String> toStringMap(adlParser.AdlMapValueContext context) {
        if (context==null) return ImmutableMap.of();
        Map<String, String> result = new LinkedHashMap<>();
        for (adlParser.AdlMapValueEntryContext cEntry : context.adlMapValueEntry()) {
            String key = collectText(cEntry.STRING());
            String value=null;
            if (cEntry.adlValue().openStringList()!=null) {
                value = collectString(cEntry.adlValue().openStringList());
            }
            if (cEntry.adlValue().url()!=null) {
                value = collectText(cEntry.adlValue().url());
            }
            result.put(key, value);
        }
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
                        target.setUid(newHierObjectId(propertyValue));
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
