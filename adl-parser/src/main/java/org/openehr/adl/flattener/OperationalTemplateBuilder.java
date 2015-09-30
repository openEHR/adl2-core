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

import com.google.common.collect.Sets;
import org.openehr.adl.FlatArchetypeProvider;
import org.openehr.adl.am.AmQuery;
import org.openehr.adl.util.walker.AbstractAmVisitor;
import org.openehr.adl.util.walker.AmConstraintContext;
import org.openehr.adl.util.walker.AmVisitContext;
import org.openehr.adl.util.walker.ArchetypeWalker;
import org.openehr.am.AmObject;
import org.openehr.jaxb.am.*;
import org.openehr.jaxb.rm.ResourceAnnotationNodeItems;
import org.openehr.jaxb.rm.ResourceAnnotationNodes;
import org.openehr.jaxb.rm.ResourceAnnotations;

import javax.annotation.Nullable;
import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;
import static org.openehr.adl.util.AdlUtils.createTemplateClone;
import static org.openehr.adl.util.AdlUtils.makeClone;

/**
 * @author markopi
 */
public class OperationalTemplateBuilder {
    private final FlatArchetypeProvider archetypeProvider;

    public OperationalTemplateBuilder(FlatArchetypeProvider archetypeProvider) {
        this.archetypeProvider = archetypeProvider;
    }

    /**
     * Builds an operational template from a flat archetype
     *
     * @param source flattened archetype representing a template
     * @return operational template
     */
    public Template build(Archetype source) {
        checkArgument(!source.isIsDifferential(), "Archetype must be flattened");
        checkArgument(source.isIsTemplate(), "Archetype must be a template");
        Template result = createTemplateClone(source);

        State state = new State();

        resolveReferences(result, source, state);
        buildOntology(result, state);
        addArchetypeAnnotations(result, source, state);

        return result;
    }

    private void addArchetypeAnnotations(Template result, Archetype source, State state) {
        ArchetypeWalker.walkConstraints(new AnnotationAddingVisitor(result), source, new AmConstraintContext());
    }

    private void buildOntology(Template target, State state) {
        for (Map.Entry<String, Archetype> entry : state.containedArchetypes.entrySet()) {
            String archetypeId = entry.getKey();
            Archetype archetype = entry.getValue();
            ArchetypeTerminology source = archetype.getTerminology();

            FlatArchetypeOntology dest = new FlatArchetypeOntology();
            dest.setArchetypeId(archetypeId);


            dest.getConstraintBindings().addAll(source.getConstraintBindings());
            dest.getTermDefinitions().addAll(source.getTermDefinitions());
            dest.getTermBindings().addAll(source.getTermBindings());
            dest.getConstraintDefinitions().addAll(source.getConstraintDefinitions());
            dest.getValueSets().addAll(source.getValueSets());

            target.getComponentOntologies().add(makeClone(dest));
        }

        // remove unneeded languages
        Set<String> existingLanguages = new HashSet<>();
        for (CodeDefinitionSet cds : target.getTerminology().getTermDefinitions()) {
            existingLanguages.add(cds.getLanguage());
        }

        LanguageRemovingVisitor visitor = new LanguageRemovingVisitor(existingLanguages);
        for (FlatArchetypeOntology archetypeOntology : target.getComponentOntologies()) {
            ArchetypeWalker.walkTerminology(visitor, archetypeOntology, new AmVisitContext());
        }

    }

    private void resolveReferences(Template target, Archetype source, State state) {
        state.archetypeRoots.push(source);
        target.setDefinition(createArchetypeRoot(source, state));

        state.archetypeRoots.pop();
    }

    // returns new object, or null if the object should be deleted
    @Nullable
    private CObject resolveCObjectReferences(CObject source, State state) {
        if (source instanceof ArchetypeInternalRef) {
            ArchetypeInternalRef t = (ArchetypeInternalRef) source;
            CObject targetNode = AmQuery.get(state.archetypeRoots.peek(), t.getTargetPath());
            return makeClone(targetNode);
        } else if (source instanceof CArchetypeRoot) {
            CArchetypeRoot t = (CArchetypeRoot) source;
            final String archetypeId = t.getArchetypeRef();
            Archetype targetArchetype = archetypeProvider.getFlatArchetype(archetypeId);
            CObject result = createArchetypeRoot(targetArchetype, state);
            result.setNodeId(source.getNodeId());
            result.setSiblingOrder(source.getSiblingOrder());
            result.setOccurrences(source.getOccurrences());
            return result;
        } else if (source instanceof ArchetypeSlot) {
            ArchetypeSlot t = (ArchetypeSlot) source;
            if (t.isIsClosed() != null && t.isIsClosed()) {
                return null; // exclude closed archetype slot
            } else {
                return t;
            }
        } else if (source instanceof CComplexObject) {
            for (CAttribute attribute : ((CComplexObject) source).getAttributes()) {
                resolveCAttributeReferences(attribute, state);
            }
            return source;
        } else {
            return source;
        }
    }

    private void resolveCAttributeReferences(CAttribute attribute, State state) {
        ListIterator<CObject> it = attribute.getChildren().listIterator();
        Set<String> filledSlots = Sets.newHashSet();

        while (it.hasNext()) {
            CObject source = it.next();
            CObject target = resolveCObjectReferences(source, state);
            if (target instanceof CArchetypeRoot) {
                filledSlots.add(target.getNodeId());
            }
            if (target != null) {
                it.set(target);
            } else {
                it.remove();
            }
        }

        // remove filled slots
        if (!filledSlots.isEmpty()) {
            ListIterator<CObject> it2 = attribute.getChildren().listIterator();
            while (it2.hasNext()) {
                CObject child = it2.next();
                if (child instanceof ArchetypeSlot && filledSlots.contains(child.getNodeId())) {
                    it2.remove();
                }
            }
        }
    }

    private CArchetypeRoot createArchetypeRoot(Archetype source, State state) {

        CArchetypeRoot result = new CArchetypeRoot();
        result.setArchetypeRef(source.getArchetypeId().getValue());
        result.setRmTypeName(source.getDefinition().getRmTypeName());
        result.setOccurrences(makeClone(source.getDefinition().getOccurrences()));
        result.setNodeId(source.getDefinition().getNodeId());

        final String archetypeId = source.getArchetypeId().getValue();

        state.pushArchetypeRoot(archetypeId, source);
        for (CAttribute sourceAttribute : source.getDefinition().getAttributes()) {
            CAttribute targetAttribute = makeClone(sourceAttribute);
            result.getAttributes().add(targetAttribute);

            resolveCAttributeReferences(targetAttribute, state);
        }
        state.popArchetypeRoot();

        return result;
    }


    private class State {
        // current archetype roots, including top-level archetype.
        private final Deque<Archetype> archetypeRoots = new ArrayDeque<>();
        // all archetypes contained in the template
        private final Map<String, Archetype> containedArchetypes = new LinkedHashMap<>();

        public void pushArchetypeRoot(String archetypeId, Archetype archetype) {
            containedArchetypes.put(archetypeId, archetype);
            archetypeRoots.push(archetype);
        }

        public Archetype popArchetypeRoot() {
            return archetypeRoots.pop();
        }
    }


    private static class LanguageRemovingVisitor extends AbstractAmVisitor<AmObject, AmVisitContext> {
        private final Set<String> existingLanguages;

        private LanguageRemovingVisitor(Set<String> existingLanguages) {
            this.existingLanguages = existingLanguages;
        }

        @Override
        public ArchetypeWalker.Action<? extends AmObject> preorderVisit(AmObject item, AmVisitContext context) {
            if (item instanceof CodeDefinitionSet) {
                CodeDefinitionSet cds = (CodeDefinitionSet) item;
                if (!existingLanguages.contains(cds.getLanguage())) {
                    return ArchetypeWalker.Action.remove();
                }
            }
            return super.preorderVisit(item, context);
        }
    }

    private class AnnotationAddingVisitor extends AbstractAmVisitor<AmObject, AmConstraintContext> {
        private final Template template;

        public AnnotationAddingVisitor(Template template) {
            this.template = template;
        }

        @Override
        public ArchetypeWalker.Action<? extends AmObject> preorderVisit(AmObject item, AmConstraintContext context) {
            if (!context.getAmParents().isEmpty() && item instanceof CArchetypeRoot) {
                CArchetypeRoot car = (CArchetypeRoot) item;
                Archetype archetype = archetypeProvider.getFlatArchetype(car.getArchetypeRef());
                String defaultLanguage = archetype.getOriginalLanguage().getCodeString();
                if (archetype.getAnnotations() != null) {
                    for (ResourceAnnotationNodes ran : archetype.getAnnotations().getItems()) {
                        if (!ran.getLanguage().equals(defaultLanguage)) continue;

                        ResourceAnnotationNodes templateRan = getOrCreateLanguageAnnotations(template,
                                ran.getLanguage());

                        for (ResourceAnnotationNodeItems rani : ran.getItems()) {
                            String path = context.getRmPath().toString() + rani.getPath();
                            ResourceAnnotationNodeItems templateRani = new ResourceAnnotationNodeItems();
                            templateRani.setPath(path);
                            templateRani.getItems().addAll(rani.getItems());
                            templateRan.getItems().add(templateRani);
                        }
                    }
                }
            }
            return ArchetypeWalker.Action.next();
        }

        private ResourceAnnotationNodes getOrCreateLanguageAnnotations(Template template, String language) {
            if (template.getAnnotations() == null) {
                template.setAnnotations(new ResourceAnnotations());
            }
            for (ResourceAnnotationNodes item : template.getAnnotations().getItems()) {
                if (item.getLanguage().equals(language)) {
                    return item;
                }
            }
            ResourceAnnotationNodes ran = new ResourceAnnotationNodes();
            ran.setLanguage(language);
            template.getAnnotations().getItems().add(ran);
            return ran;
        }
    }

}
