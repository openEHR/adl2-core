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

package org.openehr.adl.util;

import com.google.common.collect.Lists;
import org.openehr.jaxb.am.*;
import org.openehr.jaxb.rm.CodePhrase;
import org.openehr.jaxb.rm.StringDictionaryItem;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Marko Pipan
 */
public class ArchetypeWrapper {
    private final Archetype archetype;
    // terminology, code -> value
    private final Map<String, Map<String, CodePhrase>> terminologyBindings;
    // language, code, id(text/description) -> value
    private final Map<String, Map<String, Map<String, String>>> terminologyDefinitions;
    private final Map<String, Map<String, Map<String, String>>> constraintDefinitions;
    private final Map<String, List<String>> valueSets;

    public ArchetypeWrapper(Archetype archetype) {
        this.archetype = archetype;
        terminologyBindings = parseTerminologyBindings(archetype.getOntology());
        terminologyDefinitions = parseCodeDefinitionSet(archetype.getOntology().getTermDefinitions());
        constraintDefinitions = parseCodeDefinitionSet(archetype.getOntology().getConstraintDefinitions());
        valueSets = parseValueSets(archetype.getOntology().getValueSets());

    }

    public Archetype getArchetype() {
        return archetype;
    }

    private Map<String, List<String>> parseValueSets(List<ValueSetItem> valueSets) {
        Map<String, List<String>> result = new LinkedHashMap<>();
        for (ValueSetItem valueSet : valueSets) {
            result.put(valueSet.getId(), Lists.newArrayList(valueSet.getMembers()));
        }
        return result;
    }

    private Map<String, Map<String, CodePhrase>> parseTerminologyBindings(ArchetypeOntology ontology) {
        Map<String, Map<String, CodePhrase>> terminologyBindings = new LinkedHashMap<>();

        for (TermBindingSet tbs : ontology.getTermBindings()) {
            Map<String, CodePhrase> codePhrases = new LinkedHashMap<>();
            for (TermBindingItem tbi : tbs.getItems()) {
                codePhrases.put(tbi.getCode(), tbi.getValue());
            }
            terminologyBindings.put(tbs.getTerminology(), codePhrases);
        }
        return terminologyBindings;
    }

    private Map<String, Map<String, Map<String, String>>> parseCodeDefinitionSet(List<CodeDefinitionSet> codeDefinitionList) {
        Map<String, Map<String, Map<String, String>>> codeDefinitionSet = new LinkedHashMap<>();
        for (CodeDefinitionSet cds : codeDefinitionList) {
            Map<String, Map<String, String>> archetypeTerms = new LinkedHashMap<>();
            for (ArchetypeTerm at : cds.getItems()) {
                Map<String, String> dictionary = new LinkedHashMap<>();
                for (StringDictionaryItem sdi : at.getItems()) {
                    dictionary.put(sdi.getId(), sdi.getValue());
                }
                archetypeTerms.put(at.getCode(), dictionary);
            }
            codeDefinitionSet.put(cds.getLanguage(), archetypeTerms);
        }
        return codeDefinitionSet;
    }

    @Nullable
    public Map<String, String> getTerm(String language, String concept) {
        Map<String, Map<String, String>> termMap = terminologyDefinitions.get(language);
        if (termMap == null) return null;
        Map<String, String> descMap = termMap.get(concept);
        return descMap;
    }

    @Nullable
    public String getTermText(String language, String code) {
        Map<String, String> descMap = getTerm(language, code);
        if (descMap == null) return null;
        return descMap.get("text");
    }

    @Nullable
    public String getTermText(String code) {
        return getTermText(archetype.getOriginalLanguage().getCodeString(), code);
    }


    @Nullable
    public Map<String, String> getConstraintDefinition(String language, String concept) {
        Map<String, Map<String, String>> cmap = constraintDefinitions.get(language);
        if (cmap == null) return null;
        Map<String, String> descMap = cmap.get(concept);
        return descMap;
    }

    public String getConstraintDefinitionText(String language, String code) {
        Map<String, String> descMap = getConstraintDefinition(language, code);
        if (descMap == null) return null;
        return descMap.get("text");
    }

    @Nullable
    public String getConstraintDefinitionText(String code) {
        return getConstraintDefinitionText(archetype.getOriginalLanguage().getCodeString(), code);
    }

    public List<String> getValueSet(String code) {
        return valueSets.get(code);
    }

    @Nullable
    public CodePhrase getTerminologyBinding(String terminology, String code) {
        Map<String, CodePhrase> singleTerminologyBinding = terminologyBindings.get(terminology);
        if (singleTerminologyBinding == null) return null;
        return singleTerminologyBinding.get(code);
    }


}
