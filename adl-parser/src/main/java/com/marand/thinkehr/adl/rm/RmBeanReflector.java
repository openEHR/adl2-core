/*
 * Copyright (C) 2014 Marand
 */

package com.marand.thinkehr.adl.rm;

import com.google.common.base.CaseFormat;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang.StringUtils;
import org.openehr.jaxb.rm.MultiplicityInterval;

import javax.annotation.Nonnull;
import javax.xml.bind.annotation.XmlElement;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.google.common.base.Preconditions.checkArgument;
import static com.marand.thinkehr.adl.rm.RmObjectFactory.newMultiplicityInterval;

/**
 * @author markopi
 * @since 13.6.2013
 */
class RmBeanReflector {
    private final Map<Class, Map<String, RmAttribute>> rmClassAttributeMap;

    private static final RmBeanReflector instance;

    static {
        instance = new RmBeanReflector();
    }

    private RmBeanReflector() {
        rmClassAttributeMap = new ConcurrentHashMap<>();
    }


    private Map<String, RmAttribute> getAttributes(Class beanClass) {
        Map<String, RmAttribute> properties = rmClassAttributeMap.get(beanClass);
        if (properties == null) {
            try {
                properties = buildAttributes(beanClass);
            } catch (ReflectiveOperationException | IntrospectionException e) {
                throw new IllegalStateException(e);
            }
        }
        return properties;
    }

    private Map<String, RmAttribute> buildAttributes(Class<?> beanClass) throws ReflectiveOperationException, IntrospectionException {
        Map<String, RmAttribute> properties;
        properties = new LinkedHashMap<>();
        BeanInfo info = Introspector.getBeanInfo(beanClass);

        PropertyDescriptor[] pds = info.getPropertyDescriptors();
        for (PropertyDescriptor pd : pds) {
            if (pd.getName().equals("class")) continue;
            ;

            String attribute = getAttributeForField(pd.getName());
            MultiplicityInterval occurrences = getOccurrences(beanClass, pd);
            final Class<?> targetType = List.class.isAssignableFrom(pd.getPropertyType())
                    ? extractGenericType(beanClass, pd)
                    : pd.getPropertyType();
            RmAttribute rp = new RmAttribute(attribute, pd, occurrences, targetType);
            properties.put(attribute, rp);
        }
        rmClassAttributeMap.put(beanClass, ImmutableMap.copyOf(properties));
        return properties;
    }

    private Field getField(Class<?> beanClass, PropertyDescriptor pd) throws ReflectiveOperationException {
        final Method readMethod;
        if (Boolean.class == pd.getPropertyType()) {
            readMethod = beanClass.getMethod("is" + StringUtils.capitalize(pd.getName()));
        } else {
            readMethod = pd.getReadMethod();
        }
        Class<?> declaringClass = readMethod.getDeclaringClass();
        return declaringClass.getDeclaredField(pd.getName());


    }

    private Class<?> extractGenericType(Class<?> beanClass, PropertyDescriptor pd) throws ReflectiveOperationException {
        checkArgument(List.class.isAssignableFrom(pd.getPropertyType()));

        Field field = getField(beanClass, pd);
        ParameterizedType stringListType = (ParameterizedType) field.getGenericType();
        return (Class<?>) stringListType.getActualTypeArguments()[0];
    }

    private MultiplicityInterval getOccurrences(Class<?> beanClass, PropertyDescriptor pd) throws ReflectiveOperationException {
        Field field = getField(beanClass, pd);
        if (field.getType().isPrimitive()) return newMultiplicityInterval(1, 1);


        XmlElement xmlElement = field.getAnnotation(XmlElement.class);
        final Integer lower;
        final Integer upper;
        if (xmlElement != null) {
            lower = xmlElement.required() ? 1 : 0;
        } else {
            lower = 0;
        }
        if (List.class.isAssignableFrom(field.getType())) {
            upper = null;
        } else {
            upper = 1;
        }

        return newMultiplicityInterval(lower, upper);
    }


    public static Iterable<RmAttribute> listProperties(Class beanClass) {
        return instance.getAttributes(beanClass).values();
    }

    public static RmAttribute getRmAttribute(Class beanClass, String attribute) {
        return instance.getAttributes(beanClass).get(attribute);
    }

    private static String getAttributeForField(@Nonnull String fieldName) {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, fieldName);
    }


    public static class RmAttribute {
        private final PropertyDescriptor property;
        private final String attribute;
        private final MultiplicityInterval occurrences;
        private final Class<?> targetType;

        public RmAttribute(String attribute, PropertyDescriptor property, MultiplicityInterval occurrences, Class<?> targetType) {
            this.attribute = attribute;
            this.property = property;
            this.occurrences = occurrences;
            this.targetType = targetType;
        }

        public String getAttribute() {
            return attribute;
        }

        public PropertyDescriptor getProperty() {
            return property;
        }

        public MultiplicityInterval getOccurrences() {
            return occurrences;
        }

        public Class<?> getTargetType() {
            return targetType;
        }
    }


}
