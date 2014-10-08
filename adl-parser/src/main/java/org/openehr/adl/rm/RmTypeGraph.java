/*
 * Copyright (C) 2014 Marand
 */

package org.openehr.adl.rm;

import com.google.common.base.Preconditions;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * @author markopi
 * @since 3.6.2013
 */
public class RmTypeGraph {

    private final Map<String, RmTypeNode> rmTypeMappings;
    private final Map<Class<?>, RmTypeNode> rmClassMappings;

    RmTypeGraph(Map<String, RmTypeNode> rmTypeMappings, Map<Class<?>, RmTypeNode> rmClassMappings) {
        this.rmClassMappings = rmClassMappings;
        this.rmTypeMappings = rmTypeMappings;
    }

    @Nullable
    public RmTypeNode tryGetNodeFromRmType(String rmType) {
        return rmTypeMappings.get(rmType);
    }

    public RmTypeNode getNodeFromRmType(String rmType) {
        RmTypeNode result = rmTypeMappings.get(rmType);
        Preconditions.checkArgument(result != null, "Unknown rmType: '%s'", rmType);
        return result;
    }

    @Nullable
    public RmTypeNode tryGetNodeFromRmClass(Class<?> rmClass) {
        return rmClassMappings.get(rmClass);
    }

    public RmTypeNode getNodeFromRmClass(Class<?> rmClass) {
        RmTypeNode result = rmClassMappings.get(rmClass);
        Preconditions.checkArgument(result != null, "Class %s is not part of rm model", rmClass.getName());
        return result;
    }

    public RmTypeAttribute getRmAttribute(Class<?> rmClass, String attributeName) {
        RmTypeNode rmTypeNode = getNodeFromRmClass(rmClass);
        return rmTypeNode.getAttributes().get(attributeName);
    }

    public Iterable<RmTypeNode> getAllNodes() {
        return rmTypeMappings.values();
    }
}
