/*
 * Copyright (C) 2014 Marand
 */

package org.openehr.adl.am.mixin;

import com.google.common.collect.ImmutableMap;
import org.openehr.jaxb.am.CAttribute;
import org.openehr.jaxb.rm.IntervalOfInteger;
import org.openehr.jaxb.rm.IntervalOfReal;
import org.openehr.jaxb.rm.MultiplicityInterval;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author markopi
 */
class AmMixinsInternal {
    private static final Map<Class<?>, Class<? extends AmMixin>> configuration;
    private static final Map<Class<?>, Constructor<? extends AmMixin>> constructors;

    static {
        Map<Class<?>, Class<? extends AmMixin>> conf = ImmutableMap.<Class<?>, Class<? extends AmMixin>>builder()
                .put(IntervalOfInteger.class, IntervalOfIntegerMixin.class)
                .put(IntervalOfReal.class, IntervalOfRealMixin.class)
                .put(MultiplicityInterval.class, MultiplicityIntervalMixin.class)
                .put(CAttribute.class, CAttributeMixin.class)
                .build();

        configuration = ImmutableMap.copyOf(conf);

        constructors = new ConcurrentHashMap<>();
    }

    static <T, M extends AmMixin<?>> M create(T from) {
        Constructor<? extends AmMixin> mixinConstructor = getMixinClass(from.getClass());
        try {
            return (M) mixinConstructor.newInstance(from);
        } catch (ReflectiveOperationException e) {
            throw new IllegalArgumentException(
                    "Error constructing mixin class " + mixinConstructor.getDeclaringClass() + " for class " + from.getClass());
        }
    }

    private static Constructor<? extends AmMixin> getMixinClass(Class<?> fromClass) {
        Constructor<? extends AmMixin> result = constructors.get(fromClass);
        if (result == null) {
            Class<?> cls = fromClass;
            while (cls != null) {
                Class<? extends AmMixin> mixinClass = configuration.get(cls);
                if (mixinClass != null) {
                    try {
                        result = mixinClass.getConstructor(fromClass);
                    } catch (NoSuchMethodException e) {
                        throw new IllegalStateException(
                                "Missing mixin " + mixinClass.getName() + " constructor for " + fromClass.getName());
                    }
                    constructors.put(fromClass, result);
                    return result;
                }
                cls = cls.getSuperclass();
            }
            throw new IllegalArgumentException("No mixin defined for class " + fromClass);
        }
        return result;
    }


}
