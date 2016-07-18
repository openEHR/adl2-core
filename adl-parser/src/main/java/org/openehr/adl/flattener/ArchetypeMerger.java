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

import com.google.common.collect.*;
import org.apache.commons.lang.SerializationUtils;
import org.openehr.adl.AdlException;
import org.openehr.adl.rm.RmPath;
import org.openehr.jaxb.am.*;
import org.openehr.jaxb.rm.MultiplicityInterval;
import org.openehr.jaxb.rm.ResourceAnnotations;

import javax.annotation.Nullable;
import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;
import static org.openehr.adl.util.AdlUtils.*;

/**
 * Merges a specialized differential specialized archetype with its flat parent to produce a flat version of the
 * specialized archetype.
 *
 * @author markopi
 */
class ArchetypeMerger {
    private final AnnotationsMerger annotationsMerger = new AnnotationsMerger();
//    private final RmModel rmModel;

    ArchetypeMerger() {
//        this.rmModel = rmModel;
    }

    private static boolean isEmptyInterval(MultiplicityInterval interval) {
        return Integer.valueOf(0).equals(interval.getLower()) && Integer.valueOf(0).equals(interval.getUpper());
    }

    /**
     * Merges a specialized archetype with its parent. Merge will be done in-place on the specialized parameter.
     *
     * @param flatParent  Flat parent archetype
     * @param specialized Specialized archetype
     */
    void merge(Archetype flatParent, Archetype specialized) {
        expandAttributeNodes(specialized.getDefinition());

        flattenCObject(RmPath.ROOT, null, flatParent.getDefinition(), specialized.getDefinition());


        mergeOntologies(flatParent.getTerminology(), specialized.getTerminology());
        if (flatParent.getAnnotations() != null) {
            if (specialized.getAnnotations() == null) {
                specialized.setAnnotations(new ResourceAnnotations());
            }
            annotationsMerger.merge(flatParent.getAnnotations().getItems(), specialized.getAnnotations().getItems());
        }
    }

    private void mergeOntologies(ArchetypeTerminology parent, ArchetypeTerminology specialized) {
        new OntologyFlattener(parent, specialized).flatten();
    }

    /* expands differential paths into actual nodes */
    private void expandAttributeNodes(CComplexObject sourceObject) {
        List<CAttribute> differentialAttributes = Lists.newArrayList();
        for (CAttribute cAttribute : sourceObject.getAttributes()) {
            if (cAttribute.getDifferentialPath() != null) {
                differentialAttributes.add(cAttribute);
            }

        }

        for (CAttribute specializedAttribute : differentialAttributes) {
            expandAttribute(sourceObject, specializedAttribute);
            sourceObject.getAttributes().remove(specializedAttribute);
        }

        for (CAttribute cAttribute : sourceObject.getAttributes()) {
            for (CObject cObject : cAttribute.getChildren()) {
                if (cObject instanceof CComplexObject) {
                    expandAttributeNodes((CComplexObject) cObject);
                }
            }
        }
    }

    private CAttribute expandAttribute(CComplexObject sourceObject, CAttribute specializedAttribute) {
        checkArgument(specializedAttribute.getDifferentialPath() != null);

        RmPath differentialPath = RmPath.valueOf(specializedAttribute.getDifferentialPath());

        CAttribute targetAttribute = makeClone(specializedAttribute);
        targetAttribute.setDifferentialPath(null);
        targetAttribute.setRmAttributeName(differentialPath.getAttribute());

        return expandAttribute(sourceObject, differentialPath.getParent().segments(), targetAttribute);
    }

    private CAttribute expandAttribute(CComplexObject sourceObject, List<RmPath> intermediateSegments, CAttribute targetAttribute) {
        if (intermediateSegments.isEmpty()) {
            sourceObject.getAttributes().add(targetAttribute);
            return targetAttribute;
        }

        RmPath segment = head(intermediateSegments);

        CAttribute existing = findAttribute(sourceObject.getAttributes(), segment.getAttribute());

        if (existing != null) { // attribute already exist, merge with it
            CComplexObject newSource = null;
            if (segment.getNodeId() == null && !existing.getChildren().isEmpty()) {
                newSource = (CComplexObject) existing.getChildren().get(0);
            } else {
                for (CObject cObject : existing.getChildren()) {
                    if (cObject.getNodeId() != null && cObject.getNodeId().equals(segment.getNodeId())) {
                        newSource = (CComplexObject) cObject;
                        break;
                    }
                }
                if (newSource == null) { // constraint on existing attribute does not exist, add it
                    newSource = new CComplexObject();
                    newSource.setNodeId(segment.getNodeId());
                    existing.getChildren().add(newSource);
                }
            }

            expandAttribute(newSource, tail(intermediateSegments), targetAttribute);
            return existing;
        } else { // attribute not found on sourceObject, create it

            CAttribute newAttribute = new CAttribute();
            newAttribute.setRmAttributeName(segment.getAttribute());

            CComplexObject newSource = new CComplexObject();
            newSource.setNodeId(segment.getNodeId());
            newAttribute.getChildren().add(newSource);

            sourceObject.getAttributes().add(newAttribute);

            expandAttribute(newSource, tail(intermediateSegments), targetAttribute);

            return newAttribute;
        }
    }

    private void flattenCComplexObject(RmPath path, CComplexObject flatParent, CComplexObject specialized) {
        flattenCComplexObjectAttributes(path, flatParent, specialized);
        flattenCComplexObjectTuples(flatParent, specialized);
    }

    private void flattenCComplexObjectTuples(CComplexObject flatParent, CComplexObject specialized) {
        // merge tuples
        Set<TreeSet<String>> tupleAttributes = new HashSet<>();
        for (CAttributeTuple attributeTuple : specialized.getAttributeTuples()) {
            tupleAttributes.add(makeTupleAttributeSet(attributeTuple.getMembers()));
        }

        for (CAttributeTuple attributeTuple : flatParent.getAttributeTuples()) {
            // skip tuples that are alread specialized
            if (tupleAttributes.contains(makeTupleAttributeSet(attributeTuple.getMembers()))) continue;
            // add from parent
            specialized.getAttributeTuples().add(makeClone(attributeTuple));
        }
    }

    private void flattenCComplexObjectAttributes(RmPath path, CComplexObject flatParent, CComplexObject specialized) {
        List<CAttribute> originalSpecializedAttributes = ImmutableList.copyOf(specialized.getAttributes());

        addCComplexObjectSpecializedAttributes(path, flatParent, specialized);
        addParentAttributesThatWereNotSpecialized(flatParent, specialized, originalSpecializedAttributes);

        Collections.sort(specialized.getAttributes(), new CAttributeComparator(flatParent));
    }

    private void addParentAttributesThatWereNotSpecialized(CComplexObject flatParent, CComplexObject specialized,
                                                           List<CAttribute> originalSpecializedAttributes) {
        for (CAttribute parentAttribute : flatParent.getAttributes()) {
            CAttribute specializedAttribute = findAttribute(originalSpecializedAttributes, parentAttribute.getRmAttributeName());
            if (specializedAttribute == null) {
                specialized.getAttributes().add(makeClone(parentAttribute));
            }
        }
    }

    private void addCComplexObjectSpecializedAttributes(RmPath path, CComplexObject flatParent, CComplexObject specialized) {
        for (Iterator<CAttribute> iterator = specialized.getAttributes().iterator(); iterator.hasNext(); ) {
            CAttribute specializedAttribute = iterator.next();
            CAttribute parentAttribute = findAttribute(flatParent.getAttributes(), specializedAttribute.getRmAttributeName());
            final RmPath attributePath = path.resolve(specializedAttribute.getRmAttributeName(), null);
            if (parentAttribute != null) {
                flattenCAttribute(attributePath, parentAttribute, specializedAttribute);
            }

//            if (specializedAttribute.getExistence() != null && isEmptyInterval(specializedAttribute.getExistence())) {
//                iterator.remove();
//            }
        }
    }

    private TreeSet<String> makeTupleAttributeSet(List<CAttribute> members) {
        TreeSet<String> result = new TreeSet<>();
        for (CAttribute member : members) {
            result.add(member.getRmAttributeName());
        }
        return result;
    }

    private void mergeAttribute(CAttribute parent, CAttribute result) {
        result.setExistence(first(result.getExistence(), parent.getExistence()));
        result.setCardinality(first(result.getCardinality(), parent.getCardinality()));
    }

    private void flattenCAttribute(RmPath path, CAttribute parent, CAttribute specialized) {

        mergeAttribute(parent, specialized);

//        List<Pair<CObject>> childPairs = getChildPairs(path, parent, specialized);

        List<CObject> conses = Lists.newArrayList(parent.getChildren());
        Set<CObject> specializedConses = ImmutableSet.copyOf(specialized.getChildren());
        addSpecializedChildren(conses, specialized.getChildren());

        Set<CObject> parentConsesToRemove = Sets.newHashSet();

        for (CObject specializedCons : conses) {
            if (!specializedConses.contains(specializedCons)) continue;

            if (specializedCons.getNodeId() != null) {
                CObject parentCons = findParentConstraintOfSpecializedNode(parent, specializedCons.getNodeId());

                if (parentCons != null) {
                    if (specializedCons instanceof CArchetypeRoot) {
                        // do not replace slot parent
                    } else {
                        final RmPath childPath = path.constrain(first(parentCons, specializedCons).getNodeId());
                        flattenCObject(childPath, specialized, parentCons, specializedCons);
                        parentConsesToRemove.add(parentCons);
                    }
                }
            } else if (specializedCons.getRmTypeName() != null) {
                CObject parentCons = findParentConstraintWithRmType(parent, specializedCons.getRmTypeName());
                if (parentCons != null) {
                    final RmPath childPath = path.constrain(first(parentCons, specializedCons).getNodeId());
                    flattenCObject(childPath, specialized, parentCons, specializedCons);
                    parentConsesToRemove.add(parentCons);

                }
            } else if (parent.getChildren().size() == 1) {
                CObject parentCons = parent.getChildren().get(0);
                parentConsesToRemove.add(parentCons);
            }
        }

        for (ListIterator<CObject> iterator = conses.listIterator(); iterator.hasNext(); ) {
            CObject cons = iterator.next();
            if (specializedConses.contains(cons)) continue;

            if (parentConsesToRemove.contains(cons)) {
                iterator.remove();
            } else {
                iterator.set(makeClone(cons));
            }
        }
        specialized.getChildren().clear();
        specialized.getChildren().addAll(conses);


//        List<CObject> result = new ArrayList<>();
//        for (Pair<CObject> pair : childPairs) {
//            if (pair.specialized != null) {
//                final RmPath childPath = path.constrain(first(pair.parent, pair.specialized).getNodeId());
//                if (pair.parent != null) {
//                    flattenCObject(childPath, specialized, pair.parent, pair.specialized);
//                }
//                if (pair.specialized instanceof CArchetypeRoot) {
//                    result.add(pair.parent);
//                }
//                result.add(pair.specialized);
//            } else {
//                result.add(makeClone(checkNotNull(pair.parent)));
//            }
//        }

//        specialized.getChildren().clear();
//        specialized.getChildren().addAll(result);
    }

    private void addSpecializedChildren(List<CObject> conses, List<CObject> children) {
        Multimap<String, CObject> moveOnParent = ArrayListMultimap.create();
        Multimap<String, CObject> moveOnAfter = ArrayListMultimap.create();
        for (CObject child : children) {
            if (child.getNodeId() != null) {
                if (child.getSiblingOrder() != null) {
                    int anchorIndex = findParentConstraintOfSpecializedNodeIndex(conses, child.getSiblingOrder().getSiblingNodeId());
                    if (anchorIndex < 0)
                        throw new AdlException("Node " + child.getNodeId() + " has an nonexisting sibling order node: " + child.getSiblingOrder().getSiblingNodeId());
                    if (child.getSiblingOrder().isIsBefore()) {
                        conses.add(anchorIndex, child);
                    } else {
                        conses.add(anchorIndex + 1 + moveOnAfter.get(child.getSiblingOrder().getSiblingNodeId()).size(), child);
                        moveOnAfter.put(child.getSiblingOrder().getSiblingNodeId(), child);
                    }
                } else {
                    int anchorIndex = findParentConstraintOfSpecializedNodeIndex(conses, child.getNodeId());
                    if (anchorIndex >= 0) {
                        String parentNodeId = unspecializeNodeId(child.getNodeId());
                        conses.add(anchorIndex + 1 + moveOnParent.get(parentNodeId).size(), child);
                        moveOnParent.put(parentNodeId, child);
                    } else {
                        conses.add(child);
                    }
                }
            } else {
                conses.add(child);
            }
        }
    }

    private String unspecializeNodeId(String nodeId) {
        int lastPeriod = nodeId.lastIndexOf('.');
        if (lastPeriod < 0) return nodeId;
        return nodeId.substring(0, lastPeriod);
    }

    /* Returns matching (parent,specialized) pairs of children of an attribute, in the order they should be present in
     the flattened model. One of the element may be null in case of no specialization or extension.  */
    private List<Pair<CObject>> getChildPairs(RmPath path, CAttribute parent, CAttribute specialized) {
        List<Pair<CObject>> result = new ArrayList<>();
        for (CObject parentChild : parent.getChildren()) {
            result.add(new Pair<>(parentChild, findSpecializedConstraintOfParentNode(specialized, parentChild)));
        }
        for (CObject specializedChild : specialized.getChildren()) {
            CObject parentChild = findParentConstraintOfSpecializedNode(parent, specializedChild.getNodeId());
            if (parentChild == null) {

                int index = getOrderIndex(path, result, specializedChild);

                if (index >= 0) {
                    result.add(index, new Pair<>(parentChild, specializedChild));
                } else {
                    result.add(new Pair<>(parentChild, specializedChild));
                }

            }
        }
        return result;
    }

    private int getOrderIndex(RmPath path, List<Pair<CObject>> result, CObject specializedChild) {
        if (specializedChild.getSiblingOrder() != null) {
            int index = Integer.MIN_VALUE;
            int i = 0;
            while (i < result.size()) {
                Pair<CObject> pair = result.get(i);
                if (pair.parent != null && atCodeMatchesOrSpecializes(
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
            return index;
        } else {
            return -1;
        }
    }

    private void flattenCObject(RmPath path, @Nullable CAttribute container, CObject parent, CObject specialized) {
        specialized.setNodeId(first(specialized.getNodeId(), parent.getNodeId()));
        specialized.setRmTypeName(first(specialized.getRmTypeName(), parent.getRmTypeName()));
        if (specialized.getNodeId() != null && specialized.getNodeId().equals(parent.getNodeId())) {
            specialized.setOccurrences(first(specialized.getOccurrences(), parent.getOccurrences()));
        }
        if (specialized instanceof ArchetypeSlot) {
            flattenCArchetypeSlot((ArchetypeSlot) parent, (ArchetypeSlot) specialized);
        }

        // todo what to do on bad rm type?
        if (specialized instanceof CComplexObject) {

            if (parent instanceof CComplexObject) {
                flattenCComplexObject(path, (CComplexObject) parent, (CComplexObject) specialized);
            }
        }

    }

    private void flattenCArchetypeSlot(ArchetypeSlot parent, ArchetypeSlot specialized) {
        if (specialized.getIncludes().isEmpty()) {
            for (Assertion assertion : parent.getIncludes()) {
                specialized.getIncludes().add((Assertion) SerializationUtils.clone(assertion));
            }
        }
        if (specialized.getExcludes().isEmpty()) {
            for (Assertion assertion : parent.getExcludes()) {
                specialized.getExcludes().add((Assertion) SerializationUtils.clone(assertion));
            }
        }
    }

    private <T> T first(@Nullable T first, @Nullable T second) {
        if (first != null) return first;
        if (second != null) return second;
        return null;
    }

    @Nullable
    private CAttribute findAttribute(List<CAttribute> attributes, String attributeName) {
        for (CAttribute attribute : attributes) {
            if (attribute.getRmAttributeName() != null && attribute.getRmAttributeName().equals(attributeName)) {
                return attribute;
            }
        }
        return null;
    }

    @Nullable
    private CObject findParentConstraintOfSpecializedNode(List<CObject> parentConses, @Nullable String nodeId) {
        int index = findParentConstraintOfSpecializedNodeIndex(parentConses, nodeId);
        return index >= 0 ? parentConses.get(index) : null;
    }

    private int findParentConstraintOfSpecializedNodeIndex(List<CObject> parentConses, @Nullable String nodeId) {
        for (int i = 0; i < parentConses.size(); i++) {
            CObject candidate = parentConses.get(i);
            if (atCodeMatchesOrSpecializes(nodeId, candidate.getNodeId())) return i;
        }
        return -1;
    }

    private CObject findParentConstraintOfSpecializedNode(CAttribute parent, @Nullable String nodeId) {
        return findParentConstraintOfSpecializedNode(parent.getChildren(), nodeId);
    }

    @Nullable
    private CObject findParentConstraintWithRmType(CAttribute parent, String rmType) {
        for (CObject object : parent.getChildren()) {
            if (object.getRmTypeName().equals(rmType)) {
                return object;
            }
        }
        return null;
    }

    @Nullable
    private CObject findSpecializedConstraintOfParentNode(CAttribute specializedAttribute, CObject parentConstraint) {
        for (CObject candidate : specializedAttribute.getChildren()) {
            if (!atCodeMatchesOrSpecializes(candidate.getNodeId(), parentConstraint.getNodeId())) continue;

            // rm type on the specialized constraint can only be null on intermediate nodes on differential path.
            // Just accept the parent constraint, since differential paths are only allowed on aom 1.5, where node_id
            // (already checked above) is always required.
//            if (candidate.getRmTypeName() != null && candidate instanceof CComplexObject) {
//                RmType specializedRmType = rmModel.getRmType(candidate.getRmTypeName());
//                if (!specializedRmType.isSubclassOf(parentConstraint.getRmTypeName())) continue;
//            }
            return candidate;
        }
        return null;
    }

    private boolean atCodeMatchesOrSpecializes(@Nullable String atCode, @Nullable String sameOrParentCode) {
        if (atCode == null) return true;
        if (sameOrParentCode == null) return true;

        if (atCode.equals(sameOrParentCode)) return true;
        if (atCode.startsWith(sameOrParentCode + ".")) {
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

    private static class CAttributeComparator implements Comparator<CAttribute> {
        private final Map<String, Integer> parentAttributeSortOrder;

        public CAttributeComparator(CComplexObject flatParent) {
            parentAttributeSortOrder = Maps.newHashMap();
            List<CAttribute> attributes = flatParent.getAttributes();
            for (int i = 0; i < attributes.size(); i++) {
                parentAttributeSortOrder.put(attributes.get(i).getRmAttributeName(), i);
            }
        }

        @Override
        public int compare(CAttribute o1, CAttribute o2) {
            Integer firstIndex = parentAttributeSortOrder.get(o1.getRmAttributeName());
            Integer secondIndex = parentAttributeSortOrder.get(o2.getRmAttributeName());
            if (firstIndex == null && secondIndex == null) return 0;
            if (firstIndex == null) return 1;
            if (secondIndex == null) return -1;

            return Integer.compare(firstIndex, secondIndex);
        }
    }
}
