/*
 * Copyright (C) 2014 Marand
 */

package com.marand.thinkehr.adl.am.mixin;

import org.openehr.jaxb.rm.IntervalOfReal;

/**
 * @author markopi
 */
public class IntervalOfRealMixin<T extends IntervalOfReal> extends IntervalMixin<T, Float> {
    public IntervalOfRealMixin(T self) {
        super(self);
    }

    @Override
    protected Float getLower(IntervalOfReal interval) {
        return interval.getLower();
    }

    @Override
    protected Float getUpper(IntervalOfReal interval) {
        return interval.getUpper();
    }

}
