/*
 * Copyright (C) 2014 Marand
 */

package org.openehr.adl.rm;

/**
 * RM model representing OpenEHR objects
 * @author markopi
 */
public class OpenEhrRmModel implements RmModel {
    private final RmTypeGraph rmTypeGraph;

    public OpenEhrRmModel() {
        rmTypeGraph = new RmTypeGraphBuilder().build();
    }

    public Class<?> getRmClass(String rmType) {
        return getRmType(rmType).getMainRmClass();
    }

    @Override
    public String getRmTypeName(Class<?> rmClass) {
        return getRmType(rmClass).getRmType();
    }

    public RmTypeNode getRmType(Class<?> rmClass) {
        RmTypeNode result = rmTypeGraph.tryGetNodeFromRmClass(rmClass);
        if (result == null) {
            throw new RmModelException("Unknown RM class: " + rmClass.getName());
        }
        return result;
    }

    @Override
    public boolean rmTypeExists(String rmType) {
        return rmTypeGraph.tryGetNodeFromRmType(rmType)!=null;
    }

    public RmTypeNode getRmType(String rmType) {
        RmTypeNode node = rmTypeGraph.tryGetNodeFromRmType(rmType);
        if (node == null) {
            throw new RmModelException("Unknown RM type: " + rmType);
        }
        return node;
    }

    private RmTypeAttribute getRmAttribute(RmTypeNode rmType, String attribute) {
        RmTypeAttribute result = rmType.getAttributes().get(attribute);
        if (result == null) {
            throw new RmModelException("No attribute %s on rm type %s", attribute, rmType.getRmType());
        }
        return result;
    }

    @Override
    public RmTypeAttribute getRmAttribute(String rmType, String attribute) {
        return getRmAttribute(getRmType(rmType), attribute);
    }


}
