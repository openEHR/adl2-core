/*
 * Copyright (C) 2014 Marand
 */

package com.marand.thinkehr.adl.am;

import com.marand.thinkehr.adl.rm.RmPath;
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
     * Gets the object on a path <code>path</code> relative to <code>obj</code>
     * If not found throws {@link IllegalStateException}.
     * The method returns first object found, and does not check if there are more than one matches.
     *
     * @param obj  Root object from which to search, Must be either an Archetype or CComplexObject
     * @param path path identifying the object to return.
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
     * Gets the object on a path <code>path</code> relative to <code>obj</code>
     * If not found returns null.
     * The method returns first object found, and does not check if there are more than one matches.
     *
     * @param obj  Root object from which to search, Must be either an Archetype or CComplexObject
     * @param path path identifying the object to return.
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
//            return findWithReflection(obj, attribute);
        }
    }

    @Nullable
    private static Segment findChildInAttributes(List<CAttribute> attributes, String attribute,
            @Nullable String nodeId) {
        for (CAttribute cAttribute : attributes) {
            if (cAttribute.getRmAttributeName().equals(attribute)) {
                for (CObject cObject : cAttribute.getChildren()) {
                    if (nodeId == null || nodeId.equals(cObject.getNodeId())) {
                        return new Segment(cAttribute, cObject);
                    }
                }
            }
        }
        return null;
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
