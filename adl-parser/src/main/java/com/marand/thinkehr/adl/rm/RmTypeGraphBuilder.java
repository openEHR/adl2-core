/*
 * Copyright (C) 2014 Marand
 */

package com.marand.thinkehr.adl.rm;

import org.openehr.rm.RmObject;

import javax.xml.bind.annotation.XmlType;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.*;

import static com.google.common.base.Preconditions.checkState;

/**
 * @author markopi
 * @since 3.6.2013
 */
class RmTypeGraphBuilder {

    private final Map<String, RmTypeNode> rmTypeMappings = new HashMap<>();
    private final Map<Class<?>, RmTypeNode> rmClassMappings = new HashMap<>();
    private final Set<Class> nonRmClasses = new HashSet<>();


    public RmTypeGraph build() {

        Map<String, RmTypeNode> objectFactoryMappings = new HashMap<>();
        addObjectFactoryClasses(objectFactoryMappings, org.openehr.jaxb.rm.ObjectFactory.class);


        for (RmTypeNode rmType : objectFactoryMappings.values()) {
            checkState(!rmTypeMappings.containsKey(rmType.getRmType()));
            rmTypeMappings.put(rmType.getRmType(), rmType);
            for (Class<?> cls : rmType.getRmClasses()) {
                checkState(!rmClassMappings.containsKey(cls));
                rmClassMappings.put(cls, rmType);
            }
        }


        // Add primitives
        addRmType(RmTypes.BOOLEAN, Boolean.class, Boolean.TYPE);
        addRmType(RmTypes.DURATION, String.class);
        addRmType(RmTypes.DATE_TIME, String.class);
        addRmType(RmTypes.STRING, String.class);
        addRmType(RmTypes.INTEGER, Long.class, Long.TYPE, Integer.class, Integer.TYPE,
                Short.class, Short.TYPE, Byte.class, Byte.TYPE, BigInteger.class);
        addRmType(RmTypes.REAL, Double.class, Double.TYPE, Float.class, Float.TYPE);
        // Add other
        addRmType(RmTypes.LIST, List.class);
        addRmType(RmTypes.DATE, Date.class);
        addRmType(RmTypes.OBJECT, Object.class);


        buildAttributes();
        return new RmTypeGraph(rmTypeMappings, rmClassMappings);
    }

    private void buildAttributes() {
        final Set<RmTypeNode> processed = new HashSet<>();

        for (RmTypeNode rmTypeNode : rmTypeMappings.values()) {
            if (processed.contains(rmTypeNode) || nonRmClasses.contains(rmTypeNode.getMainRmClass())) continue;

            Iterable<RmBeanReflector.RmAttribute> attributes = RmBeanReflector.listProperties(rmTypeNode.getMainRmClass());
            List<RmTypeAttribute> resultAttributes = new ArrayList<>();
            for (RmBeanReflector.RmAttribute attribute : attributes) {
                RmTypeNode type = rmClassMappings.get(attribute.getProperty().getClass());

                resultAttributes.add(new RmTypeAttribute(attribute.getAttribute(), attribute.getProperty().getName(), rmTypeNode, type,
                        attribute.getOccurrences()));
            }
            rmTypeNode.setAttributes(resultAttributes);
            processed.add(rmTypeNode);
        }
    }

    // Adds a RmType to builder, checks for duplicates
    private void addRmType(String rmType, Class<?> rmMainClass, Class<?>... otherRmClasses) {
        // check removed because VERSION is both in rm and thinkehr model
        // checkState(!rmTypeMappings.containsKey(rmType), "RmType already exists: %s", rmType);

        RmTypeNode rmTypeNode = new RmTypeNode(rmType, rmMainClass, otherRmClasses);

        rmTypeMappings.put(rmType, rmTypeNode);
        for (Class<?> rmClass : rmTypeNode.getRmClasses()) {
            rmClassMappings.put(rmClass, rmTypeNode);
            nonRmClasses.add(rmClass);
        }
    }

    private void addObjectFactoryClasses(Map<String, RmTypeNode> target, Class objectFactoryClass) {
        Method[] methods = objectFactoryClass.getDeclaredMethods();
        for (Method m : methods) {
            if (m.getParameterTypes().length == 0) {
                Class<?> rmClass = m.getReturnType();
                addObjectFactoryClass(target, rmClass, null);
            }
        }
    }

    private void addObjectFactoryClass(Map<String, RmTypeNode> target, Class<?> rmClass, RmTypeNode child) {
        XmlType xmlType = rmClass.getAnnotation(XmlType.class);
        if (xmlType != null || rmClass == RmObject.class) {
            String rmType = xmlType != null ? xmlType.name() : RmTypes.RM_OBJECT;
            RmTypeNode node = target.get(rmType);
            if (node == null) {
                node = new RmTypeNode(rmType, rmClass);
                target.put(rmType, node);
            }
            if (child != null && !node.getChildren().contains(child)) {
                node.addChild(child);
            }
            addObjectFactoryClass(target, rmClass.getSuperclass(), node);
        }
    }

}
