/*
 * Copyright (C) 2014 Marand
 */

package org.openehr.adl.parser.tree;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.openehr.adl.antlr.AdlParser;
import org.openehr.adl.parser.RuntimeRecognitionException;
import org.openehr.adl.rm.RmModel;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;
import org.openehr.jaxb.am.*;
import org.openehr.jaxb.rm.*;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.openehr.adl.rm.RmObjectFactory.*;

/**
 * @author markopi
 */
public class AdlTreeParser extends AbstractAdlTreeParser {
    private final AdlTreeConstraintsParser constraints;
    private final AdlTreeDAdlParser dadl;

    AdlTreeParser(CommonTokenStream tokenStream, CommonTree adlTree, RmModel rmModel) {
        super(tokenStream, adlTree, new AdlTreeParserState(), rmModel);
        dadl = new AdlTreeDAdlParser(tokenStream, adlTree, getState(), rmModel);
        constraints = new AdlTreeConstraintsParser(tokenStream, adlTree, getState(), dadl, rmModel);
    }

    private DifferentialArchetype parseAdl() {
        DifferentialArchetype result = new DifferentialArchetype();
        for (Tree child : children(adlTree)) {
            switch (child.getType()) {
                case AdlParser.ARCHETYPE:
                case AdlParser.TEMPLATE:
                case AdlParser.TEMPLATE_OVERLAY:
                    parseArchetypeHeader(result, child);
                    break;
                case AdlParser.CONCEPT:
                    result.setConcept(child.getChild(0).getText());
                    break;
                case AdlParser.SPECIALIZE:
                case AdlParser.SPECIALISE:
                    parseSpecialize(result, child);
                    break;
                case AdlParser.LANGUAGE:
                    parseLanguage(result, child);
                    break;
                case AdlParser.DESCRIPTION:
                    result.setDescription(parseDescription(child.getChild(0)));
                    break;
                case AdlParser.DEFINITION:
                    result.setDefinition(constraints.parseTypeConstraint(child.getChild(0)));
                    break;
                case AdlParser.ONTOLOGY:
                case AdlParser.TERMINOLOGY:
                    result.setOntology(parseOntology(child.getChild(0)));
                    break;
                case AdlParser.ANNOTATIONS:
                    parseAnnotations(result.getAnnotations(), child.getChild(0));
                    break;
                default:
                    throw new RuntimeRecognitionException(child);
            }
        }
        return result;
    }

    private void parseAnnotations(List<AnnotationSet> target, Tree tAnnotations) {
        if (tAnnotations.getType() == AdlParser.AST_ADL_OBJECT) {
            tAnnotations = dadl.parseAdlObject(tAnnotations).get("items");
        }


        Map<String, Tree> languageMap = dadl.parseAdlMap(tAnnotations);

        for (Map.Entry<String, Tree> entry : languageMap.entrySet()) {
            String language = entry.getKey();
            AnnotationSet annotationSet = new AnnotationSet();
            annotationSet.setLanguage(language);
            parseSingleLanguageAnnotations(annotationSet.getItems(), entry.getValue());
            target.add(annotationSet);
        }

    }

    private void parseSingleLanguageAnnotations(List<Annotation> target, Tree tAnnotations) {
        if (tAnnotations.getType() == AdlParser.AST_ADL_OBJECT) {
            tAnnotations = dadl.parseAdlObject(tAnnotations).get("items");
        }

        Map<String, Tree> pathMap = dadl.parseAdlMap(tAnnotations);

        for (Map.Entry<String, Tree> entry : pathMap.entrySet()) {
            Annotation annotation = new Annotation();
            annotation.setPath(entry.getKey());
            Tree tValue = entry.getValue();
            if (tValue.getType() == AdlParser.AST_ADL_OBJECT) {
                tValue = dadl.parseAdlObject(tValue).get("items");
            }

            parseStringDictionaryItems(annotation.getItems(), tValue);
            target.add(annotation);
        }
    }

    private void parseSpecialize(Archetype target, Tree tSpecialize) {
        String archetypeId = checkNotNull(collectText(tSpecialize.getChild(0)));
        target.setParentArchetypeId(newArchetypeId(archetypeId));
    }

    private ResourceDescription parseDescription(Tree tDescription) {
        DAdlObject description = dadl.parseAdlObject(tDescription);
        ResourceDescription result = new ResourceDescription();

        result.setLifecycleState(collectString(description.tryGet("lifecycle_state")));
        parseStringDictionaryItems(result.getOriginalAuthor(), description.tryGet("original_author"));
        parseResourceDescriptionItems(result.getDetails(), description.tryGet("details"));
        Tree otherContributors = description.tryGet("other_contributors");
        if (otherContributors != null && otherContributors.getType() != AdlParser.AST_NULL) {
            result.getOtherContributors().addAll(collectStringList(otherContributors));
        }

        return result;
    }

    private void parseResourceDescriptionItems(List<ResourceDescriptionItem> target, @Nullable Tree tItems) {
        if (tItems == null) return;
        Map<String, Tree> items = dadl.parseAdlMap(tItems);
        for (Map.Entry<String, Tree> entry : items.entrySet()) {
            DAdlObject objItem = dadl.parseAdlObject(entry.getValue());

            ResourceDescriptionItem item = new ResourceDescriptionItem();
            item.setCopyright(collectString(objItem.tryGet("copyright")));
            item.setUse(collectString(objItem.tryGet("use")));
            item.setMisuse(collectString(objItem.tryGet("misuse")));
            item.setPurpose(collectString(objItem.tryGet("purpose")));
            item.setLanguage(parseCodePhraseListSingleItem(objItem.get("language")));
            if (objItem.tryGet("keywords") != null) {
                item.getKeywords().addAll(collectStringList(objItem.tryGet("keywords")));
            }
            parseDictionaryItems(item.getOriginalResourceUri(), objItem.tryGet("original_resource_uri"));
            target.add(item);
        }
    }


    private void parseArchetypeHeader(Archetype target, Tree tArchetype) {
        target.setArchetypeId(new ArchetypeId());
        String archetypeId = collectText(child(tArchetype, 0, AdlParser.AST_ARCHETYPE_ID));
        target.getArchetypeId().setValue(archetypeId);

        target.setIsTemplate(tArchetype.getType() != AdlParser.ARCHETYPE);
        target.setIsOverlay(tArchetype.getType() == AdlParser.TEMPLATE_OVERLAY);

        if (tArchetype.getChildCount() > 1) {
            Tree tArchetypeProperties = tArchetype.getChild(1);
            Map<String, String> archetypeProperties = Maps.newHashMap();
            for (Tree tArchetypeProperty : children(tArchetypeProperties)) {
                String key = collectText(tArchetypeProperty.getChild(0));
                String value = tArchetypeProperty.getChildCount() > 1 ? collectText(tArchetypeProperty.getChild(1)) : null;
                archetypeProperties.put(key, value);
            }
            String adlVersion = archetypeProperties.get("adl_version");
            if (adlVersion != null) {
                target.setAdlVersion(adlVersion);
                getState().setAdlVersion(adlVersion);
            }
            if (archetypeProperties.containsKey("uid")) {
                target.setUid(newHierObjectId(archetypeProperties.get("uid")));
            }

            target.setIsControlled(false);
            if (archetypeProperties.containsKey("controlled")) {
                target.setIsControlled(true);
            }
            if (archetypeProperties.containsKey("uncontrolled")) {
                target.setIsControlled(false);
            }
            // target.setIsControlled();
        }
    }

    private ArchetypeOntology parseOntology(Tree tOntology) {
        ArchetypeOntology result = new ArchetypeOntology();

        DAdlObject ontology = dadl.parseAdlObject(tOntology);

        // Each term can be singular or plural. Yes.
        parseCodeDefinitionSets(result.getTermDefinitions(), ontology.tryGet("term_definitions"));
        parseCodeDefinitionSets(result.getTermDefinitions(), ontology.tryGet("term_definition"));

        parseCodeDefinitionSets(result.getConstraintDefinitions(), ontology.tryGet("constraint_definitions"));
        parseCodeDefinitionSets(result.getConstraintDefinitions(), ontology.tryGet("constraint_definition"));

        parseConstraintBindings(result.getConstraintBindings(), ontology.tryGet("constraint_bindings"));
        parseConstraintBindings(result.getConstraintBindings(), ontology.tryGet("constraint_binding"));

        parseTermBindings(result.getTermBindings(), ontology.tryGet("term_bindings"));
        parseTermBindings(result.getTermBindings(), ontology.tryGet("term_binding"));

        parseCodeDefinitionSets(result.getTerminologyExtracts(), ontology.tryGet("terminology_extract"));
        parseCodeDefinitionSets(result.getTerminologyExtracts(), ontology.tryGet("terminology_extracts"));
        return result;
    }

    private void parseConstraintBindings(List<ConstraintBindingSet> target, @Nullable Tree tConstraintBindings) {
        if (tConstraintBindings == null) return;

        Map<String, Tree> constraintBindings = dadl.parseAdlMap(tConstraintBindings);
        for (Map.Entry<String, Tree> entry : constraintBindings.entrySet()) {
            ConstraintBindingSet cbs = new ConstraintBindingSet();
            cbs.setTerminology(entry.getKey());
            parseConstraintBindingItems(cbs.getItems(), entry.getValue());
            target.add(cbs);
        }
    }

    private void parseConstraintBindingItems(List<ConstraintBindingItem> target, Tree tConstraintBindingItem) {
        if (tConstraintBindingItem.getType() == AdlParser.AST_ADL_OBJECT) {
            tConstraintBindingItem = dadl.parseAdlObject(tConstraintBindingItem).get("items");
        }
        if (tConstraintBindingItem.getType() == AdlParser.AST_NULL) return;

        Map<String, Tree> constraintBindingItem = dadl.parseAdlMap(tConstraintBindingItem);
        for (Map.Entry<String, Tree> entry : constraintBindingItem.entrySet()) {
            ConstraintBindingItem cbi = new ConstraintBindingItem();
            cbi.setCode(entry.getKey());
            cbi.setValue(collectText(entry.getValue()));
            target.add(cbi);
        }
    }

    private void parseTermBindings(List<TermBindingSet> target, @Nullable Tree tTermBindings) {
        if (tTermBindings == null) return;

        Map<String, Tree> termBindings = dadl.parseAdlMap(tTermBindings);
        for (Map.Entry<String, Tree> entry : termBindings.entrySet()) {
            TermBindingSet tbs = new TermBindingSet();
            tbs.setTerminology(entry.getKey());
            parseTermBindingItems(tbs.getItems(), entry.getValue());
            target.add(tbs);
        }

    }

    private void parseTermBindingItems(List<TermBindingItem> target, Tree tTermBindingItem) {
        if (tTermBindingItem.getType() == AdlParser.AST_ADL_OBJECT) {
            tTermBindingItem = dadl.parseAdlObject(tTermBindingItem).get("items");
        }
        if (tTermBindingItem.getType() == AdlParser.AST_NULL) return;

        Map<String, Tree> termBindingItem = dadl.parseAdlMap(tTermBindingItem);
        for (Map.Entry<String, Tree> entry : termBindingItem.entrySet()) {
            List<CodePhrase> codePhraseList = parseTermBindingsValue((CommonTree) entry.getValue());
            for (CodePhrase codePhrase : codePhraseList) {
                TermBindingItem tbi = new TermBindingItem();
                tbi.setCode(entry.getKey());
                tbi.setValue(codePhrase);
                target.add(tbi);
            }
        }
    }

    private List<CodePhrase> parseTermBindingsValue(CommonTree tTermBindings) {
        if (tTermBindings.getType() == AdlParser.AST_CODE_PHRASE_LIST) {
            return parseCodePhraseList(tTermBindings);
        } else if (tTermBindings.getType() == AdlParser.AST_TEXT_CONTAINER) {
            String url = collectText(tTermBindings);
            int slash = url.lastIndexOf("/");
            CodePhrase cp = new CodePhrase();
            cp.setTerminologyId(newTerminologyId(url.substring(0, slash)));
            cp.setCodeString(url.substring(slash + 1));
            return Lists.newArrayList(cp);
        }
        throw new AdlTreeParserException("Unknown term binding value type: " + AdlParser.tokenNames[tTermBindings.getType()],
                tTermBindings.getToken());
    }

    private void parseCodeDefinitionSets(List<CodeDefinitionSet> target, @Nullable Tree tCodeDefinitions) {
        if (tCodeDefinitions == null) return;

        Map<String, Tree> codeDefinitions = dadl.parseAdlMap(tCodeDefinitions);
        for (Map.Entry<String, Tree> entry : codeDefinitions.entrySet()) {
            CodeDefinitionSet cds = new CodeDefinitionSet();
            cds.setLanguage(entry.getKey());
            parseArchetypeTerms(cds.getItems(), entry.getValue());
            target.add(cds);
        }
    }

    private void parseArchetypeTerms(List<ArchetypeTerm> target, @Nullable Tree tArchetypeTerms) {
        if (tArchetypeTerms == null) return;

        if (tArchetypeTerms.getType() == AdlParser.AST_ADL_OBJECT) {
            tArchetypeTerms = dadl.parseAdlObject(tArchetypeTerms).get("items");
        }

        Map<String, Tree> archetypeTerms = dadl.parseAdlMap(tArchetypeTerms);
        for (Map.Entry<String, Tree> entry : archetypeTerms.entrySet()) {
            ArchetypeTerm term = new ArchetypeTerm();
            term.setCode(entry.getKey());
            parseStringDictionaryItems(term.getItems(), entry.getValue());
            target.add(term);
        }
    }

    private void parseStringDictionaryItems(List<StringDictionaryItem> target, @Nullable Tree tItems) {
        if (tItems == null) return;

        final Map<String, Tree> properties;

        // check to allow parsing in both description and ontology
        if (tItems.getType() == AdlParser.AST_ADL_OBJECT) {
            properties = dadl.parseAdlObject(tItems).getProperties();
        } else {
            properties = dadl.parseAdlMap(tItems);
        }
        for (Map.Entry<String, Tree> entry : properties.entrySet()) {
            StringDictionaryItem item = new StringDictionaryItem();
            item.setId(entry.getKey());
            item.setValue(collectString(entry.getValue()));
            target.add(item);
        }
    }


    private void parseLanguage(Archetype target, Tree tLanguage) {
        DAdlObject language = dadl.parseAdlObject(tLanguage.getChild(0));

        target.setOriginalLanguage(parseCodePhraseListSingleItem(language.get("original_language")));

        Tree tTranslations = language.tryGet("translations");
        if (tTranslations != null) {
            Map<String, Tree> translations = dadl.parseAdlMap(tTranslations);
            for (Map.Entry<String, Tree> entry : translations.entrySet()) {
                target.getTranslations().add(parseTranslationDetails(entry.getValue()));
            }
        }
    }

    private TranslationDetails parseTranslationDetails(Tree tTranslationDetails) {
        DAdlObject translationDetails = dadl.parseAdlObject(tTranslationDetails);

        TranslationDetails result = new TranslationDetails();

        result.setLanguage(parseCodePhraseListSingleItem(translationDetails.get("language")));
        result.setAccreditation(collectString(translationDetails.tryGet("accreditation")));
        parseDictionaryItems(result.getAuthor(), translationDetails.tryGet("author"));
        parseDictionaryItems(result.getOtherDetails(), translationDetails.tryGet("other_details"));

        return result;
    }

    private void parseDictionaryItems(List<StringDictionaryItem> target, @Nullable Tree tDictionaryItems) {
        if (tDictionaryItems == null) return;

        Map<String, Tree> dictionaryItems = dadl.parseAdlMap(tDictionaryItems);
        for (Map.Entry<String, Tree> entry : dictionaryItems.entrySet()) {
            StringDictionaryItem item = new StringDictionaryItem();
            item.setId(entry.getKey());
            item.setValue(collectString(entry.getValue()));
            target.add(item);
        }
    }


    public static DifferentialArchetype build(CommonTokenStream tokenStream, CommonTree adlTree, RmModel rmModel) {
        AdlTreeParser builder = new AdlTreeParser(tokenStream, adlTree, rmModel);
        return builder.parseAdl();
    }
}
