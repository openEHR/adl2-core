/*
 * Copyright (C) 2014 Marand
 */

package com.marand.thinkehr.adl.am.mixin;

import org.openehr.jaxb.am.CAttribute;
import org.openehr.jaxb.rm.Interval;
import org.openehr.jaxb.rm.MultiplicityInterval;

import static com.marand.thinkehr.adl.am.mixin.AmMixinsInternal.create;

/**
 * @author markopi
 */
public class AmMixins {


    public static MultiplicityIntervalMixin of(MultiplicityInterval from) {
        return create(from);
    }

    public static <T extends Interval, V extends Comparable> IntervalMixin<T, V> of(T from) {
        return create(from);
    }

    public static CAttributeMixin of(CAttribute from) {
        return create(from);
    }

}
