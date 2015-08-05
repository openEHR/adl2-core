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

package org.openehr.adl.am.mixin;

import com.google.common.collect.ImmutableMap;
import org.openehr.jaxb.am.CAttribute;
import org.openehr.jaxb.am.Cardinality;
import org.openehr.jaxb.rm.*;

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
                .put(IntervalOfDate.class, IntervalOfDateMixin.class)
                .put(IntervalOfTime.class, IntervalOfTimeMixin.class)
                .put(IntervalOfDateTime.class, IntervalOfDateTimeMixin.class)
                .put(IntervalOfInteger.class, IntervalOfIntegerMixin.class)
                .put(IntervalOfReal.class, IntervalOfRealMixin.class)
                .put(MultiplicityInterval.class, MultiplicityIntervalMixin.class)
                .put(CAttribute.class, CAttributeMixin.class)
                .put(IntervalOfDuration.class, IntervalOfDurationMixin.class)
                .put(Cardinality.class, CardinalityMixin.class)
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
