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

import org.apache.commons.lang.SerializationUtils;
import org.openehr.jaxb.am.*;
import org.openehr.jaxb.rm.StringDictionaryItem;
import org.openehr.jaxb.rm.TranslationDetails;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author markopi
 */
public class OntologyFlattener {
    private final ArchetypeOntology parent;
    private final ArchetypeOntology specialized;

    public OntologyFlattener(ArchetypeOntology parent, ArchetypeOntology specialized) {
        this.parent = parent;
        this.specialized = specialized;
    }

    public void flatten() {
        flattenCodeDefinitionSets(parent.getTermDefinitions(), specialized.getTermDefinitions());
        flattenCodeDefinitionSets(parent.getConstraintDefinitions(), specialized.getConstraintDefinitions());
        flattenCodeDefinitionSets(parent.getTerminologyExtracts(), specialized.getTerminologyExtracts());
        flattenTermBindingSets(parent.getTermBindings(), specialized.getTermBindings());
        flattenConstraintBindingSets(parent.getConstraintBindings(), specialized.getConstraintBindings());

    }

    private void flattenConstraintBindingSets(List<ConstraintBindingSet> parent, List<ConstraintBindingSet> specialized) {
        Map<String, ConstraintBindingSet> existing = constraintSetToMap(specialized);
        for (ConstraintBindingSet parentTbs : parent) {
            ConstraintBindingSet specializedTbs = existing.get(parentTbs.getTerminology());
            if (specializedTbs != null) {
                flattenConstraintBindingItems(parentTbs.getItems(), specializedTbs.getItems());
            } else {
                specialized.add(clone(parentTbs));
            }
        }
    }

    private void flattenConstraintBindingItems(List<ConstraintBindingItem> parent, List<ConstraintBindingItem> specialized) {
        Map<String, ConstraintBindingItem> existing = constraintItemToMap(specialized);
        for (ConstraintBindingItem parentTbi : parent) {
            ConstraintBindingItem specializedTbi = existing.get(parentTbi.getCode());
            if (specializedTbi == null) {
                specialized.add(clone(parentTbi));
            }
        }
    }


    private void flattenTermBindingSets(List<TermBindingSet> parent, List<TermBindingSet> specialized) {
        Map<String, TermBindingSet> existing = termSetToMap(specialized);
        for (TermBindingSet parentTbs : parent) {
            TermBindingSet specializedTbs = existing.get(parentTbs.getTerminology());
            if (specializedTbs != null) {
                flattenTermBindingItems(parentTbs.getItems(), specializedTbs.getItems());
            } else {
                specialized.add(clone(parentTbs));
            }
        }
    }

    private void flattenTermBindingItems(List<TermBindingItem> parent, List<TermBindingItem> specialized) {
        Map<String, TermBindingItem> existing = termItemToMap(specialized);
        for (TermBindingItem parentTbi : parent) {
            TermBindingItem specializedTbi = existing.get(parentTbi.getCode());
            if (specializedTbi == null) {
                specialized.add(clone(parentTbi));
            }
        }
    }

    private void flattenCodeDefinitionSets(List<CodeDefinitionSet> parent, List<CodeDefinitionSet> specialized) {
        Map<String, CodeDefinitionSet> existing = codeToMap(specialized);
        for (CodeDefinitionSet parentCds : parent) {
            CodeDefinitionSet specializedCds = existing.get(parentCds.getLanguage());
            if (specializedCds != null) {
                flattenCodeDefinitionItems(parentCds.getItems(), specializedCds.getItems());
            } else {
                specialized.add(clone(parentCds));
            }
        }
    }

    private void flattenCodeDefinitionItems(List<ArchetypeTerm> parent, List<ArchetypeTerm> specialized) {
        Map<String, ArchetypeTerm> existing = termToMap(specialized);
        for (ArchetypeTerm parentTerm : parent) {
            ArchetypeTerm specializedTerm = existing.get(parentTerm.getCode());
            if (specializedTerm != null) {
                flattenStringDictionaryItems(parentTerm.getItems(), specializedTerm.getItems());
            } else {
                specialized.add(clone(parentTerm));
            }
        }
    }

    private void flattenStringDictionaryItems(List<StringDictionaryItem> parent, List<StringDictionaryItem> specialized) {
        Map<String, String> existing = dictToMap(specialized);
        for (StringDictionaryItem parentItem : parent) {
            if (!existing.containsKey(parentItem.getId())) {
                specialized.add(clone(parentItem));
            }
        }
    }

    protected Map<String, CodeDefinitionSet> codeToMap(List<CodeDefinitionSet> termDefinitions) {
        Map<String, CodeDefinitionSet> result = new HashMap<>();
        for (CodeDefinitionSet termDefinition : termDefinitions) {
            result.put(termDefinition.getLanguage(), termDefinition);
        }
        return result;
    }

    protected Map<String, String> dictToMap(List<StringDictionaryItem> items) {
        Map<String, String> result = new HashMap<>();
        for (StringDictionaryItem item : items) {
            result.put(item.getId(), item.getValue());
        }
        return result;
    }

    protected Map<String, TranslationDetails> transToMap(List<TranslationDetails> translations) {
        Map<String, TranslationDetails> result = new HashMap<>();
        for (TranslationDetails translation : translations) {
            result.put(translation.getLanguage().getCodeString(), translation);
        }
        return result;
    }


    protected Map<String, ArchetypeTerm> termToMap(List<ArchetypeTerm> termDefinitions) {
        Map<String, ArchetypeTerm> result = new HashMap<>();
        for (ArchetypeTerm termDefinition : termDefinitions) {
            result.put(termDefinition.getCode(), termDefinition);
        }
        return result;
    }

    protected Map<String, TermBindingSet> termSetToMap(List<TermBindingSet> tbs) {
        Map<String, TermBindingSet> result = new HashMap<>();
        for (TermBindingSet termDefinition : tbs) {
            result.put(termDefinition.getTerminology(), termDefinition);
        }
        return result;
    }

    protected Map<String, TermBindingItem> termItemToMap(List<TermBindingItem> tbs) {
        Map<String, TermBindingItem> result = new HashMap<>();
        for (TermBindingItem termDefinition : tbs) {
            result.put(termDefinition.getCode(), termDefinition);
        }
        return result;
    }

    protected Map<String, ConstraintBindingSet> constraintSetToMap(List<ConstraintBindingSet> cbs) {
        Map<String, ConstraintBindingSet> result = new HashMap<>();
        for (ConstraintBindingSet ConstraintDefinition : cbs) {
            result.put(ConstraintDefinition.getTerminology(), ConstraintDefinition);
        }
        return result;
    }

    protected Map<String, ConstraintBindingItem> constraintItemToMap(List<ConstraintBindingItem> cbi) {
        Map<String, ConstraintBindingItem> result = new HashMap<>();
        for (ConstraintBindingItem constraintDefinition : cbi) {
            result.put(constraintDefinition.getCode(), constraintDefinition);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private <T extends Serializable> T clone(T from) {
        return (T) SerializationUtils.clone(from);
    }


}
