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

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import org.openehr.jaxb.rm.MultiplicityInterval;
import org.openehr.rm.RmObject;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlType;
import java.lang.reflect.Method;
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

    private final Multimap<String, Class> rmTypeNameClasses = ArrayListMultimap.create();

    public RmTypeGraph build() {

        Map<String, RmType> objectFactoryMappings = new HashMap<>();
        addObjectFactoryClasses(objectFactoryMappings, org.openehr.jaxb.rm.ObjectFactory.class);


        for (RmType rmType : objectFactoryMappings.values()) {
            checkState(!rmTypeMappings.containsKey(rmType.getRmType()));
            rmTypeMappings.put(rmType.getRmType(), rmType);
            for (Class<?> cls : rmTypeNameClasses.get(rmType.getRmType())) {
                checkState(!rmClassMappings.containsKey(cls));
                rmClassMappings.put(cls, rmType);
            }
        }


        buildAttributes();
        return new RmTypeGraph(rmTypeMappings, rmClassMappings);
    }

    private void buildAttributes() {
        final Set<RmType> processed = new HashSet<>();

        for (RmType rmType : rmTypeMappings.values()) {
            Class mainRmClass = Iterables.getFirst(rmTypeNameClasses.get(rmType.getRmType()), null);
            if (processed.contains(rmType) || nonRmClasses.contains(mainRmClass)) continue;

            Iterable<RmBeanReflector.RmAttribute> attributes = RmBeanReflector.listProperties(mainRmClass);
            List<RmTypeAttribute> resultAttributes = new ArrayList<>();
            for (RmBeanReflector.RmAttribute attribute : attributes) {
                RmType type = rmClassMappings.get(attribute.getTargetType());

                String targetType = type != null ? type.getRmType() : null;
                resultAttributes.add(new RmTypeAttribute(
                        attribute.getAttribute(), targetType,
                        buildExistence(attribute.getOccurrences()), buildCardinality(attribute)));
            }
            rmType.setAttributes(resultAttributes);
            processed.add(rmType);
        }
    }

    private RmMultiplicity buildExistence(MultiplicityInterval occurrences) {
        return new RmMultiplicity(occurrences.getLower(), occurrences.getUpper());
    }

    @Nullable
    private RmCardinality buildCardinality(RmBeanReflector.RmAttribute attribute) {
        if (attribute.getOccurrences().getUpper() != null && attribute.getOccurrences().getUpper() == 1) {
            return null;
        }
        RmCardinality result = new RmCardinality();
        result.setInterval(new RmMultiplicity());
        result.setIsOrdered(true);
        result.setIsUnique(false);
        return result;
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
                node = new RmType(rmType);
                rmTypeNameClasses.put(rmType, rmClass);

                target.put(rmType, node);
            }
            if (child != null && !node.getChildren().contains(child)) {
                node.addChild(child);
            }
            addObjectFactoryClass(target, rmClass.getSuperclass(), node);
        }
    }

}
