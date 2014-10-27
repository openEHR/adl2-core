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

package org.openehr.adl.rm;

import org.openehr.jaxb.rm.DataValue;
import org.openehr.jaxb.rm.Element;
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

    private final Map<String, RmType> rmTypeMappings = new HashMap<>();
    private final Map<Class<?>, RmType> rmClassMappings = new HashMap<>();
    private final Set<Class> nonRmClasses = new HashSet<>();


    public RmTypeGraph build() {

        Map<String, RmType> objectFactoryMappings = new HashMap<>();
        addObjectFactoryClasses(objectFactoryMappings, org.openehr.jaxb.rm.ObjectFactory.class);


        for (RmType rmType : objectFactoryMappings.values()) {
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
        final Set<RmType> processed = new HashSet<>();

        for (RmType rmType : rmTypeMappings.values()) {
            if (processed.contains(rmType) || nonRmClasses.contains(rmType.getMainRmClass())) continue;

            Iterable<RmBeanReflector.RmAttribute> attributes = RmBeanReflector.listProperties(rmType.getMainRmClass());
            List<RmTypeAttribute> resultAttributes = new ArrayList<>();
            for (RmBeanReflector.RmAttribute attribute : attributes) {
                RmType type = rmClassMappings.get(attribute.getTargetType());

                resultAttributes.add(new RmTypeAttribute(attribute.getAttribute(), attribute.getProperty().getName(), rmType, type,
                        attribute.getOccurrences()));
            }
            rmType.setAttributes(resultAttributes);
            processed.add(rmType);
        }
    }

    // Adds a RmType to builder, checks for duplicates
    private void addRmType(String rmType, Class<?> rmMainClass, Class<?>... otherRmClasses) {
        // check removed because VERSION is both in rm and thinkehr model
        // checkState(!rmTypeMappings.containsKey(rmType), "RmType already exists: %s", rmType);

        RmType rmTypeNode = new RmType(rmType, rmMainClass, otherRmClasses);

        rmTypeMappings.put(rmType, rmTypeNode);
        for (Class<?> rmClass : rmTypeNode.getRmClasses()) {
            rmClassMappings.put(rmClass, rmTypeNode);
            nonRmClasses.add(rmClass);
        }
    }

    private void addObjectFactoryClasses(Map<String, RmType> target, Class objectFactoryClass) {
        Method[] methods = objectFactoryClass.getDeclaredMethods();
        for (Method m : methods) {
            if (m.getParameterTypes().length == 0) {
                Class<?> rmClass = m.getReturnType();
                addObjectFactoryClass(target, rmClass, null);
            }
        }
    }

    private void addObjectFactoryClass(Map<String, RmType> target, Class<?> rmClass, RmType child) {
        XmlType xmlType = rmClass.getAnnotation(XmlType.class);
        if (xmlType != null || rmClass == RmObject.class) {
            String rmType = xmlType != null ? xmlType.name() : RmTypes.RM_OBJECT;
            RmType node = target.get(rmType);
            if (node == null) {
                node = new RmType(rmType, rmClass);

                if (Element.class.isAssignableFrom(rmClass)) {
                    node.setFinalType(true);
                    node.setDataAttribute("value");
                }

                target.put(rmType, node);
            }
            if (child != null && !node.getChildren().contains(child)) {
                node.addChild(child);
            }
            addObjectFactoryClass(target, rmClass.getSuperclass(), node);
        }
    }

}
