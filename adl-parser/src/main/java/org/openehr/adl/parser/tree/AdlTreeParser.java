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

    public Archetype parseAdl(adlParser.AdlContext context) {
        Archetype result = new Archetype();
        parseArchetypeHeader(result, context.header());
        result.setIsDifferential(true);
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

        if (context.terminology() != null) {
            result.setTerminology(parseTerminology(context.terminology()));
        }
        if (context.annotations()!=null) {
            result.setAnnotations(parseAnnotations(context.annotations()));
        }

        return result;
    }

    private ResourceAnnotations parseAnnotations(adlParser.AnnotationsContext context) {
        ResourceAnnotations result = new ResourceAnnotations();
        OdinObject dAnnotations = OdinObject.parse(context.odinObjectValue());
        
        adlParser.OdinValueContext cLanguages = skipItems(dAnnotations.tryGet("items"));
        if (cLanguages==null || cLanguages.odinMapValue()==null) return result;
        for (adlParser.OdinMapValueEntryContext cLanguageEntry : cLanguages.odinMapValue().odinMapValueEntry()) {
            ResourceAnnotationNodes nodes = new ResourceAnnotationNodes();
            nodes.setLanguage(collectText(cLanguageEntry.key));
            adlParser.OdinValueContext cItems = skipItems(cLanguageEntry.value);
            parseResourceAnnotationNodeItems(nodes.getItems(), cItems);
            result.getItems().add(nodes);
        }
        return result;
    }

    private void parseResourceAnnotationNodeItems(List<ResourceAnnotationNodeItems> target, adlParser.OdinValueContext context) {
        if (context==null || context.odinMapValue()==null) return;
        for (adlParser.OdinMapValueEntryContext cEntry : context.odinMapValue().odinMapValueEntry()) {
            ResourceAnnotationNodeItems item = new ResourceAnnotationNodeItems();
            item.setPath(collectText(cEntry.key));

            parseStringDictionaryItems(item.getItems(), skipItems(cEntry.value));
            target.add(item);
        }
    }

    private ArchetypeTerminology parseTerminology(adlParser.TerminologyContext context) {
        ArchetypeTerminology result = new ArchetypeTerminology();

        List<adlParser.OdinObjectPropertyContext> tProperties = context.odinObjectValue().odinObjectProperty();
        for (adlParser.OdinObjectPropertyContext tProperty : tProperties) {
            String name = collectNonNullText(tProperty.identifier());
            switch (name) {
                // Each term can be singular or plural. Yes.
                case "term_definitions":
                case "term_definition":
                    parseCodeDefinitionSets(result.getTermDefinitions(), skipItems(tProperty.odinValue()).odinMapValue());
                    break;
                case "constraint_definitions":
                case "constraint_definition":
                    parseCodeDefinitionSets(result.getConstraintDefinitions(), skipItems(tProperty.odinValue()).odinMapValue());
                    break;
                case "constraint_bindings":
                case "constraint_binding":
                    parseConstraintBindings(result.getConstraintBindings(), skipItems(tProperty.odinValue()).odinMapValue());
                    break;
                case "term_bindings":
                case "term_binding":
                    parseTermBindings(result.getTermBindings(), skipItems(tProperty.odinValue()).odinMapValue());
                    break;
                case "terminology_extracts":
                case "terminology_extract":
                    parseCodeDefinitionSets(result.getTerminologyExtracts(), skipItems(tProperty.odinValue()).odinMapValue());
                    break;
                case "value_sets":
                case "value_set":
                    parseValueSetSets(result.getValueSets(), skipItems(tProperty.odinValue()).odinMapValue());
                    break;
            }
        }
        return result;
    }

    private void parseValueSetSets(List<ValueSetItem> target, adlParser.OdinMapValueContext tContext) {
        for (adlParser.OdinMapValueEntryContext cEntry : tContext.odinMapValueEntry()) {
            ValueSetItem vsi = new ValueSetItem();
            vsi.setId(collectText(cEntry.STRING()));
            adlParser.OdinValueContext adlValue = skipItems(cEntry.odinValue());
            OdinObject dValue = OdinObject.parse(adlValue.odinObjectValue());
            adlParser.OdinValueContext cMembers = dValue.get("members");
            vsi.getMembers().addAll(collectStringList(cMembers.openStringList()));
            target.add(vsi);
        }
    }

    private void parseTermBindings(List<TermBindingSet> target, adlParser.OdinMapValueContext tContext) {
        for (adlParser.OdinMapValueEntryContext cEntry : tContext.odinMapValueEntry()) {
            TermBindingSet tbs = new TermBindingSet();
            tbs.setTerminology(collectText(cEntry.STRING()));
            adlParser.OdinValueContext value = skipItems(cEntry.odinValue());
            parseTermBindingItems(tbs.getItems(), value);
            target.add(tbs);
        }
    }

    private void parseTermBindingItems(List<TermBindingItem> target, adlParser.OdinValueContext mapContext) {
        if (mapContext == null || mapContext.odinMapValue() == null) return;
        for (adlParser.OdinMapValueEntryContext cEntry : mapContext.odinMapValue().odinMapValueEntry()) {
            TermBindingItem tbi = new TermBindingItem();
            tbi.setCode(collectText(cEntry.STRING()));
            if (cEntry.odinValue().odinCodePhraseValueList() != null) {
                tbi.setValue(Joiner.on(", ").join(parseCodePhraseListString(cEntry.odinValue().odinCodePhraseValueList())));
            } else if (cEntry.odinValue().openStringList() != null) {
                tbi.setValue(collectString(cEntry.odinValue().openStringList()));
            } else if (cEntry.odinValue().url() != null) {
                tbi.setValue(collectText(cEntry.odinValue().url()));
            }
            target.add(tbi);
        }
    }

    private String parseCodePhraseString(adlParser.OdinCodePhraseValueContext context) {
        return collectNonNullText(context.tid) + "::" + collectNonNullText(context.code);
    }

    private String parseCodePhraseString(adlParser.OdinCodePhraseValueListContext tCodePhraseList) {
        return parseCodePhraseListString(tCodePhraseList).get(0);
    }

    private List<String> parseCodePhraseListString(adlParser.OdinCodePhraseValueListContext tCodePhraseList) {
        List<String> result = new ArrayList<>();
        if (tCodePhraseList.odinCodePhraseValue() != null) {
            for (adlParser.OdinCodePhraseValueContext adlCodePhraseValueContext : tCodePhraseList.odinCodePhraseValue()) {
                result.add(parseCodePhraseString(adlCodePhraseValueContext));
            }
        }
        return result;

    }


    private adlParser.OdinValueContext skipItems(adlParser.OdinValueContext context) {
        if (context==null || context.odinObjectValue() == null) return context;
        OdinObject odinObject = OdinObject.parse(context.odinObjectValue());
        adlParser.OdinValueContext result = odinObject.tryGet("items");
        if (result != null) return result;
        return context;

    }

    private void parseConstraintBindings(List<ConstraintBindingSet> target, adlParser.OdinMapValueContext tContext) {
        for (adlParser.OdinMapValueEntryContext cEntry : tContext.odinMapValueEntry()) {
            ConstraintBindingSet cbs = new ConstraintBindingSet();
            cbs.setTerminology(collectText(cEntry.STRING()));
            adlParser.OdinValueContext value = skipItems(cEntry.odinValue());
            parseConstraintBindingItems(cbs.getItems(), value);
            target.add(cbs);

        }
    }

    private void parseConstraintBindingItems(List<ConstraintBindingItem> target, adlParser.OdinValueContext context) {
        if (context == null || context.odinMapValue() == null) return;
        for (adlParser.OdinMapValueEntryContext cEntry : context.odinMapValue().odinMapValueEntry()) {
            ConstraintBindingItem cbi = new ConstraintBindingItem();
            cbi.setCode(collectText(cEntry.STRING()));

            cbi.setValue(collectText(cEntry.odinValue()));
            target.add(cbi);
        }
    }


    private void parseCodeDefinitionSets(List<CodeDefinitionSet> target, adlParser.OdinMapValueContext tContext) {
        if (tContext == null) return;
        List<adlParser.OdinMapValueEntryContext> entries = tContext.odinMapValueEntry();
        for (adlParser.OdinMapValueEntryContext entry : entries) {

            CodeDefinitionSet cds = new CodeDefinitionSet();
            cds.setLanguage(collectNonNullText(entry.STRING()));
            parseArchetypeTerms(cds.getItems(), skipItems(entry.odinValue()).odinMapValue());
            target.add(cds);
        }

    }

    private void parseArchetypeTerms(List<ArchetypeTerm> target, adlParser.OdinMapValueContext tContext) {
        if (tContext == null) return;
        List<adlParser.OdinMapValueEntryContext> entries = tContext.odinMapValueEntry();
        for (adlParser.OdinMapValueEntryContext entry : entries) {
            ArchetypeTerm at = new ArchetypeTerm();
            at.setCode(collectNonNullText(entry.STRING()));
            parseStringDictionaryItems(at.getItems(), skipItems(entry.odinValue()).odinObjectValue());
            parseStringDictionaryItems(at.getItems(), skipItems(entry.odinValue()).odinMapValue());
            target.add(at);
        }
    }

    private ResourceDescription parseDescription(adlParser.DescriptionContext context) {
        ResourceDescription result = new ResourceDescription();
        adlParser.OdinObjectValueContext description = context.odinObjectValue();
        result.setCopyright(collectString(getAdlPropertyOrNull(description, "copyright")));
        result.setLifecycleState(collectString(getAdlPropertyOrNull(description, "lifecycle_state")));
        adlParser.OdinValueContext property = getAdlPropertyOrNull(description, "original_author");
        if (property != null) {
            parseStringDictionaryItems(result.getOriginalAuthor(), property.odinMapValue());
        }
        property = getAdlPropertyOrNull(description, "details");
        if (property != null) {
            parseResourceDescriptionItems(result.getDetails(), property.odinMapValue());
        }

        adlParser.OdinValueContext otherContributors = getAdlPropertyOrNull(description, "other_contributors");
        if (otherContributors != null) {
            result.getOtherContributors().addAll(collectStringList(otherContributors.openStringList()));
        }

        return result;
    }

    private void parseResourceDescriptionItems(List<ResourceDescriptionItem> target, adlParser.OdinMapValueContext context) {
        List<adlParser.OdinMapValueEntryContext> entries = context.odinMapValueEntry();

        for (adlParser.OdinMapValueEntryContext entry : entries) {
            adlParser.OdinObjectValueContext itemContext = entry.odinValue().odinObjectValue();
            adlParser.OdinValueContext property;
            ResourceDescriptionItem item = new ResourceDescriptionItem();
            //item.setCopyright(collectString(objItem.tryGet("copyright")));
            for (adlParser.OdinObjectPropertyContext propertyContext : itemContext.odinObjectProperty()) {
                String propertyName = collectNonNullText(propertyContext.identifier());
                adlParser.OdinValueContext adlValue = propertyContext.odinValue();

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
                        item.setLanguage(parseCodePhraseListSingleItem(adlValue.odinCodePhraseValueList()));
                        break;
                    case "keywords":
                        item.getKeywords().addAll(collectStringList(adlValue.openStringList()));
                        break;
                    case "original_resource_uri":
                        parseStringDictionaryItems(item.getOriginalResourceUri(), adlValue.odinMapValue());
                        break;
                }
            }


            target.add(item);

        }
    }

    private void parseStringDictionaryItems(List<StringDictionaryItem> target, @Nullable adlParser.OdinValueContext context) {
        if (context==null) return;
        parseStringDictionaryItems(target, context.odinMapValue());
        parseStringDictionaryItems(target, context.odinObjectValue());
    }
    private void parseStringDictionaryItems(List<StringDictionaryItem> target, @Nullable adlParser.OdinMapValueContext context) {
        if (context == null) return;
        List<adlParser.OdinMapValueEntryContext> entries = context.odinMapValueEntry();
        for (adlParser.OdinMapValueEntryContext entry : entries) {
            target.add(newStringDictionaryItem(collectText(entry.STRING()), collectString(entry.odinValue())));
        }
    }

    private void parseStringDictionaryItems(List<StringDictionaryItem> target, @Nullable adlParser.OdinObjectValueContext context) {
        if (context == null) return;
        List<adlParser.OdinObjectPropertyContext> entries = context.odinObjectProperty();
        for (adlParser.OdinObjectPropertyContext entry : entries) {
            target.add(newStringDictionaryItem(collectText(entry.identifier()), collectString(entry.odinValue())));
        }
    }


    private void parseLanguage(Archetype target, adlParser.LanguageContext context) {
        OdinObject cLanguage = OdinObject.parse(context.odinObjectValue());

        target.setOriginalLanguage(parseCodePhraseListSingleItem(cLanguage.get("original_language").odinCodePhraseValueList()));
        adlParser.OdinValueContext cTranslations = cLanguage.tryGet("translations");
        if (cTranslations != null && cTranslations.odinMapValue() != null) {
            for (adlParser.OdinMapValueEntryContext cEntry : cTranslations.odinMapValue().odinMapValueEntry()) {
                target.getTranslations().add(parseTranslation(cEntry.odinValue()));
            }
        }
    }

    private TranslationDetails parseTranslation(adlParser.OdinValueContext context) {
        OdinObject dTranslation = OdinObject.parse(context.odinObjectValue());
        TranslationDetails result = new TranslationDetails();
        result.setLanguage(parseCodePhraseListSingleItem(dTranslation.get("language").odinCodePhraseValueList()));
        result.setAccreditation(dTranslation.tryGetString("accreditation"));
        for (Map.Entry<String, String> cAuthor : toStringMap(dTranslation.tryGet("author")).entrySet()) {
            result.getAuthor().add(newStringDictionaryItem(cAuthor.getKey(), cAuthor.getValue()));
        }

        for (Map.Entry<String, String> cAuthor : toStringMap(dTranslation.tryGet("other_details")).entrySet()) {
            result.getOtherDetails().add(newStringDictionaryItem(cAuthor.getKey(), cAuthor.getValue()));
        }

        return result;
    }

    private Map<String, String> toStringMap(adlParser.OdinValueContext context) {
        if (context==null) return ImmutableMap.of();
        return toStringMap(context.odinMapValue());
    }
    private Map<String, String> toStringMap(adlParser.OdinMapValueContext context) {
        if (context==null) return ImmutableMap.of();
        Map<String, String> result = new LinkedHashMap<>();
        for (adlParser.OdinMapValueEntryContext cEntry : context.odinMapValueEntry()) {
            String key = collectText(cEntry.STRING());
            String value=null;
            if (cEntry.odinValue().openStringList()!=null) {
                value = collectString(cEntry.odinValue().openStringList());
            }
            if (cEntry.odinValue().url()!=null) {
                value = collectText(cEntry.odinValue().url());
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
