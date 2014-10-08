/*
 * Copyright (C) 2014 Marand
 */

package org.openehr.adl.am.mixin;

import org.openehr.jaxb.rm.IntervalOfInteger;

/**
 * @author markopi
 */
public class IntervalOfIntegerMixin<T extends IntervalOfInteger> extends IntervalMixin<T, Integer> {
    public IntervalOfIntegerMixin(T self) {
        super(self);
    }

    @Override
    protected Integer getLower(IntervalOfInteger interval) {
        return interval.getLower();
    }

    @Override
    protected Integer getUpper(IntervalOfInteger interval) {
        return interval.getUpper();
    }

}
