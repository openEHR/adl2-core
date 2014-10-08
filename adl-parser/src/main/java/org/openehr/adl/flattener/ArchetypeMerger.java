/*
 * Copyright (C) 2014 Marand
 */

package org.openehr.adl.flattener;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.openehr.adl.am.AmObjectNotFoundException;
import org.openehr.adl.am.AmQuery;
import org.openehr.adl.rm.RmModel;
import org.openehr.adl.rm.RmPath;
import org.openehr.jaxb.am.*;
import org.openehr.jaxb.rm.MultiplicityInterval;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.openehr.adl.util.AdlUtils.front;
import static org.openehr.adl.util.AdlUtils.makeClone;

/**
 * Merges a specialized differential specialized archetype with its flat parent to produce a flat version of the
 * specialized archetype.
 *
 * @author markopi
 */
class ArchetypeMerger {
    private final AnnotationsMerger annotationsMerger = new AnnotationsMerger();
    private final RmModel rmModel;

    ArchetypeMerger(RmModel rmModel) {
        this.rmModel = rmModel;
    }


    /**
     * Merges a specialized archetype with its parent. Merge will be done in-place on the specialized parameter.
     *
     * @param flatParent  Flat parent archetype
     * @param specialized Specialized archetype
     */
    void merge(FlatArchetype flatParent, FlatArchetype specialized) {
        expandAttributeNodes(RmPath.ROOT, flatParent.getDefinition(), specialized.getDefinition());
        flattenCObject(RmPath.ROOT, null, flatParent.getDefinition(), specialized.getDefinition());

        mergeOntologies(flatParent.getOntology(), specialized.getOntology());
        annotationsMerger.merge(flatParent.getAnnotations(), specialized.getAnnotations());
    }

    private void mergeOntologies(ArchetypeOntology parent, ArchetypeOntology specialized) {
        new OntologyFlattener(parent, specialized).flatten();
    }


    /* expands differential paths into actual nodes */
    private void expandAttributeNodes(RmPath path, CComplexObject flatParent, CComplexObject specialized) {
        ListIterator<CAttribute> iterator = specialized.getAttributes().listIterator();
        while (iterator.hasNext()) {
            CAttribute specializedAttribute = iterator.next();
            CAttribute attribute;
            if (specializedAttribute.getDifferentialPath() != null) {
                attribute = expandAttribute(path, flatParent, specializedAttribute);
                iterator.set(attribute);
            } else {
                attribute = specializedAttribute;
            }

            CAttribute parentAttribute = findAttribute(flatParent.getAttributes(), attribute.getRmAttributeName());
            if (parentAttribute != null) {

                for (CObject cObject : attribute.getChildren()) {
                    if (cObject instanceof CComplexObject) {
                        CObject parentChild = findChild(parentAttribute, cObject.getNodeId());
                        RmPath childPath = path.resolve(attribute.getRmAttributeName(), cObject.getNodeId());
                        if (parentChild instanceof CComplexObject) {
                            expandAttributeNodes(childPath, (CComplexObject) parentChild, (CComplexObject) cObject);
                        }
                    }
                }
            }
        }
    }

    private void flattenCComplexObject(RmPath path, CComplexObject flatParent, CComplexObject specialized) {
        List<CAttribute> proginalSpecializedAttributes = ImmutableList.copyOf(specialized.getAttributes());
        // add specialized attributes
        for (Iterator<CAttribute> iterator = specialized.getAttributes().iterator(); iterator.hasNext(); ) {
            CAttribute specializedAttribute = iterator.next();
            CAttribute parentAttribute = findAttribute(flatParent.getAttributes(), specializedAttribute.getRmAttributeName());
            final RmPath attributePath = path.resolve(specializedAttribute.getRmAttributeName(), null);
            if (parentAttribute != null) {
                flattenCAttribute(attributePath, parentAttribute, specializedAttribute);
            }
            if (specializedAttribute.getExistence()!=null && isEmptyInterval(specializedAttribute.getExistence())) {
                iterator.remove();
            }
        }


        // add parent attributes that are not specialized
        for (CAttribute parentAttribute : flatParent.getAttributes()) {
            CAttribute specializedAttribute = findAttribute(proginalSpecializedAttributes, parentAttribute.getRmAttributeName());
            if (specializedAttribute == null) {
                specialized.getAttributes().add(makeClone(parentAttribute));
            }
        }

    }

//    private boolean isEmptyInterval(MultiplicityInterval interval) {
//        if (interval==null) return false;
//        return Integer.valueOf(0).equals(interval.getLower()) && Integer.valueOf(0).equals(interval.getUpper());
//    }


    private void mergeAttribute(CAttribute parent, CAttribute result) {
        result.setExistence(first(result.getExistence(), parent.getExistence()));
        result.setCardinality(first(result.getCardinality(), parent.getCardinality()));
    }

    private void flattenCAttribute(RmPath path, CAttribute parent, CAttribute specialized) {

        mergeAttribute(parent, specialized);

        List<Pair<CObject>> childPairs = getChildPairs(path, parent, specialized);

        List<CObject> result = new ArrayList<>();
        for (Pair<CObject> pair : childPairs) {
            if (pair.specialized != null) {
                final RmPath childPath = path.constrain(first(pair.parent, pair.specialized).getNodeId());
                if (pair.parent != null) {
                    flattenCObject(childPath, specialized, pair.parent, pair.specialized);
                }
                if (pair.specialized.getOccurrences()==null || !isEmptyInterval(pair.specialized.getOccurrences())) {
                    result.add(pair.specialized);
                }
            } else {
                result.add(makeClone(checkNotNull(pair.parent)));
            }
        }

        specialized.setMatchNegated(false);
        specialized.getChildren().clear();
        specialized.getChildren().addAll(result);
    }

    /* Returns matching (parent,specialized) pairs of children of an attribute, in the order they should be present in
     the flattened model. One of the element may be null in case of no specialization or extension.  */
    private List<Pair<CObject>> getChildPairs(RmPath path, CAttribute parent, CAttribute specialized) {
        List<Pair<CObject>> result = new ArrayList<>();
        for (CObject parentChild : parent.getChildren()) {
            result.add(new Pair<>(parentChild, findChild(specialized, parentChild.getNodeId())));
        }
        for (CObject specializedChild : specialized.getChildren()) {
            CObject parentChild = findChild(parent, specializedChild.getNodeId());
            if (parentChild == null) {

                if (specializedChild.getSiblingOrder() != null) {
                    int index = Integer.MIN_VALUE;
                    int i = 0;
                    while (i < result.size()) {
                        Pair<CObject> pair = result.get(i);
                        if (pair.parent != null && atCodeMatches(
                                specializedChild.getSiblingOrder().getSiblingNodeId(),
                                pair.parent.getNodeId())) {
                            index = i;
                            if (!specializedChild.getSiblingOrder().isIsBefore()) {
                                index = ++i;
                                while (i < result.size() && result.get(i).parent == null) {
                                    index = ++i;
                                }
                            }
                            break;
                        }
                        i++;
                    }
                    require(index >= 0, "Could order node with nodeId=%s: No child with nodeId=%s found in parent at %s",
                            specializedChild.getNodeId(), specializedChild.getSiblingOrder().getSiblingNodeId(), path);
                    result.add(index, new Pair<>(parentChild, specializedChild));
                } else {
                    result.add(new Pair<>(parentChild, specializedChild));
                }

            }
        }
        return result;
    }

    private void flattenCObject(RmPath path, @Nullable CAttribute container, CObject parent, CObject specialized) {
        specialized.setNodeId(first(specialized.getNodeId(), parent.getNodeId()));
        specialized.setRmTypeName(first(specialized.getRmTypeName(), parent.getRmTypeName()));
        specialized.setOccurrences(first(specialized.getOccurrences(), parent.getOccurrences()));

        // todo what to do on bad rm type?
        if (!rmModel.rmTypeExists(parent.getRmTypeName())) {
            return;
        }

        Class<?> parentRmClass = getRmClass(path, parent.getRmTypeName());
        Class<?> specializedRmClass = getRmClass(path, specialized.getRmTypeName());

        require(parentRmClass.isAssignableFrom(specializedRmClass), "Rm type %s is not a subclass of %s",
                specialized.getRmTypeName(), parent.getRmTypeName());


        if (specialized instanceof CComplexObject) {
            flattenCComplexObject(path, (CComplexObject) parent, (CComplexObject) specialized);
        }

        if (container != null && container.isMatchNegated()) {
            negateCObject(parent, specialized);
        }

    }

    private Class<?> getRmClass(RmPath path, String rmTypeName) {
        return rmModel.getRmClass(rmTypeName);
    }

    private void negateCObject(CObject parent, CObject specialized) {
        if (specialized instanceof CTerminologyCode) {
            CTerminologyCode target = (CTerminologyCode) specialized;
            List<String> newCodeList = Lists.newArrayList(((CTerminologyCode) parent).getCodeList());
            for (ListIterator<String> iterator = newCodeList.listIterator(); iterator.hasNext(); ) {
                String code = iterator.next();
                if (target.getCodeList().contains(code)) {
                    iterator.remove();
                }
            }
            target.getCodeList().clear();
            target.getCodeList().addAll(newCodeList);
        } else {
            throw new AdlFlattenerException("Could not negate CObject of type " + specialized.getClass());
        }
    }


    private CAttribute createIntermediateNodes(RmPath rmPath, List<AmQuery.Segment> parentSegments, CAttribute specialized) {

        CAttribute result = specialized;
        RmPath pathSegment = rmPath;
        for (int i = parentSegments.size() - 1; i >= 0; i--) {
            AmQuery.Segment parentSegment = parentSegments.get(i);

            result = createIntermediateNode(pathSegment, parentSegment, result);

            pathSegment = pathSegment.getParent();
        }
        return result;
    }

    private CAttribute createIntermediateNode(RmPath pathSegment, AmQuery.Segment parentSegment, CAttribute expandedAttribute) {
        CAttribute result = makeClone(parentSegment.getAttribute());

        // put expandedAttribute into its proper place in the result attribute
        for (CObject cObject : result.getChildren()) {
            if (atCodeMatches(pathSegment.getNodeId(), cObject.getNodeId())) {
                ListIterator<CAttribute> iterator = ((CComplexObject) cObject).getAttributes().listIterator();
                while (iterator.hasNext()) {
                    CAttribute parentAttribute = iterator.next();
                    if (parentAttribute.getRmAttributeName().equals(expandedAttribute.getRmAttributeName())) {
                        iterator.set(expandedAttribute);
                        return result;
                    }
                }
                throw new AdlFlattenerException("No parent attribute found for attribute " + expandedAttribute.getRmAttributeName());
            }
        }
        throw new AdlFlattenerException("No parent object found for nodeId " + pathSegment.getNodeId());
    }

    private CAttribute expandAttribute(RmPath path, CComplexObject flatParent, CAttribute differentialAttribute) {
        RmPath rmPath = RmPath.valueOf(differentialAttribute.getDifferentialPath());
        List<AmQuery.Segment> parentSegments;
        try {
            parentSegments = AmQuery.segments(flatParent, differentialAttribute.getDifferentialPath());
        } catch (AmObjectNotFoundException e) {
            throw new AdlFlattenerException(
                    String.format("Differential path not found: %s at %s", differentialAttribute.getDifferentialPath(), path));
        }


        CAttribute specialized = makeClone(differentialAttribute);
        specialized.setDifferentialPath(null);
        specialized.setRmAttributeName(rmPath.getAttribute());

        return createIntermediateNodes(rmPath.getParent(), front(parentSegments), specialized);
    }


    private <T> T first(@Nullable T first, @Nullable T second) {
        if (first != null) return first;
        if (second != null) return second;
        return null;
    }

    @Nullable
    private CAttribute findAttribute(List<CAttribute> attributes, String attributeName) {
        for (CAttribute attribute : attributes) {
            if (attribute.getRmAttributeName().equals(attributeName)) {
                return attribute;
            }
        }
        return null;
    }

    @Nullable
    private CObject findChild(CAttribute parent, @Nullable String nodeId) {
        for (CObject candidate : parent.getChildren()) {
            if (atCodeMatches(nodeId, candidate.getNodeId())) return candidate;
        }
        return null;
    }


    private boolean atCodeMatches(@Nullable String atCode, @Nullable String candidate) {
        if (atCode == null) return true;
        if (atCode.equals(candidate)) return true;
        if (candidate != null && candidate.startsWith(atCode + ".")) {
            return true;
        }
        return false;
    }

    private void fail(String message, Object... args) {
        throw new AdlFlattenerException(String.format(message, args));
    }

    private void require(boolean condition, String message, Object... args) {
        if (!condition) {
            fail(message, args);
        }
    }

    private static class Pair<T> {
        private final T parent;
        private final T specialized;

        private Pair(@Nullable T parent, @Nullable T specialized) {
            this.parent = parent;
            this.specialized = specialized;
        }
    }

    private static boolean isEmptyInterval(MultiplicityInterval interval) {
        return Integer.valueOf(0).equals(interval.getLower()) && Integer.valueOf(0).equals(interval.getUpper());
    }

}
