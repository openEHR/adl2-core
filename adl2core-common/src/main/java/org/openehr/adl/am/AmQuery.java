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

package org.openehr.adl.am;

import org.openehr.adl.rm.RmPath;
import org.openehr.jaxb.am.Archetype;
import org.openehr.jaxb.am.CAttribute;
import org.openehr.jaxb.am.CComplexObject;
import org.openehr.jaxb.am.CObject;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;


/**
 * @author markopi
 */
public class AmQuery {

    /**
     * Gets the object on a path {@code path} relative to {@code obj}
     * If not found throws {@link IllegalStateException}.
     * The method returns first object found, and does not check if there are more than one matches.
     *
     * @param obj  Root object from which to search, Must be either an Archetype or CComplexObject
     * @param path path identifying the object to return.
     * @param <T>  type to remove required casting
     * @return The object matching the path relative to obj
     * @throws IllegalStateException if no object is found
     */
    public static <T extends CObject> T get(Object obj, String path) {
        T result = find(obj, RmPath.valueOf(path).segments());
        if (result == null) {
            throw new AmObjectNotFoundException("Object " + obj + " has no child on path " + path);
        }
        return result;
    }

    /**
     * Gets the object on a path {@code path} relative to {@code obj}
     * If not found returns null.
     * The method returns first object found, and does not check if there are more than one matches.
     *
     * @param obj  Root object from which to search, Must be either an Archetype or CComplexObject
     * @param path path identifying the object to return.
     * @param <T>  type to remove required casting
     * @return The object matching the path relative to obj, or null if not found
     */
    @Nullable
    public static <T extends CObject> T find(Object obj, String path) {
        return find(obj, RmPath.valueOf(path).segments());
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public static <T extends CObject> T find(final Object obj, Iterable<RmPath> path) {
        CObject node = (obj instanceof Archetype) ? ((Archetype) obj).getDefinition() : (CObject) obj;
        for (RmPath pathSegment : path) {
            Segment valueSegment = getChild(node, pathSegment);
            if (valueSegment == null) return null;
            node = valueSegment.getObject();
        }
        return (T) node;
    }

    public static CAttribute getAttribute(final Object obj, String path) {
        return getAttribute(obj, RmPath.valueOf(path).segments());
    }

    public static CAttribute getAttribute(final Object obj, List<RmPath> path) {
        CAttribute attribute = findAttribute(obj, path);
        if (attribute == null) {
            throw new AmObjectNotFoundException("Object " + obj + " has no attribute on path " + path);
        }
        return attribute;
    }

    @Nullable
    public static CAttribute findAttribute(final Object obj, String path) {
        return findAttribute(obj, RmPath.valueOf(path).segments());
    }

    @Nullable
    public static CAttribute findAttribute(final Object obj, List<RmPath> path) {
        if (path.isEmpty()) {
            throw new IllegalArgumentException("Bad rm path: " + path);
        }
        RmPath last = path.get(path.size() - 1);
        if (last.getNodeId() != null) {
            throw new IllegalArgumentException("Last path segment must not have nodeId: " + path);
        }

        CObject parent = find(obj, path.subList(0, path.size() - 1));

        if (parent instanceof CComplexObject) {
            return findAttribute(((CComplexObject) parent).getAttributes(), last.getAttribute());
        } else {
            return null;
        }

    }


    public static List<Segment> segments(Object obj, String path) {
        return segments(obj, RmPath.valueOf(path).segments());
    }

    public static List<Segment> segments(final Object obj, Iterable<RmPath> path) {
        CObject node = (obj instanceof Archetype) ? ((Archetype) obj).getDefinition() : (CObject) obj;
        List<Segment> result = new ArrayList<>();
        for (RmPath pathSegment : path) {
            Segment valueSegment = getChild(node, pathSegment);
            if (valueSegment == null) {
                throw new AmObjectNotFoundException("Object " + obj + " has no child matching " + pathSegment.toSegmentString());
            }
            node = valueSegment.getObject();
            result.add(valueSegment);
        }
        return result;
    }


    @Nullable
    private static Segment getChild(CObject obj, RmPath segment) {
        try {
            return findChild(obj, segment.getAttribute(), segment.getNodeId());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
    private static Segment findChild(CObject obj, String attribute, @Nullable String atCode) {
        if (obj instanceof CComplexObject) {
            return findChildInAttributes(((CComplexObject) obj).getAttributes(), attribute, atCode);
        } else {
            throw new AssertionError(obj);
        }
    }

    @Nullable
    private static CAttribute findAttribute(List<CAttribute> attributes, String attributeName) {
        for (CAttribute attribute : attributes) {
            if (attribute.getRmAttributeName().equals(attributeName)) {
                return attribute;
            }
        }
        return null;
    }

    @Nullable
    private static Segment findChildInAttributes(List<CAttribute> attributes, String attribute,
                                                 @Nullable String nodeId) {
        CAttribute cAttribute = findAttribute(attributes, attribute);
        if (cAttribute == null) return null;
        for (CObject cObject : cAttribute.getChildren()) {
            if (nodeIdMatches(cObject.getNodeId(), nodeId)) {
                return new Segment(cAttribute, cObject);
            }
        }
        return null;
    }

    private static boolean nodeIdMatches(String candidateNodeId, String nodeId) {
        return nodeId == null
                || nodeId.equals(candidateNodeId)
                || candidateNodeId != null && candidateNodeId.startsWith(nodeId + ".");
    }

    public static class Segment {
        private final CAttribute attribute;
        private final CObject object;

        public Segment(CAttribute attribute, CObject object) {
            this.attribute = attribute;
            this.object = object;
        }

        public CAttribute getAttribute() {
            return attribute;
        }

        public CObject getObject() {
            return object;
        }
    }
}
