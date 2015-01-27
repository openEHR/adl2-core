package org.openehr.adl.am.mixin;

import org.openehr.jaxb.rm.IntervalOfDuration;
import org.openehr.jaxb.rm.IntervalOfInteger;

/**
 * Created by bna on 27.01.2015.
 */
public class IntervalOfDurationMixin <T extends IntervalOfDuration> extends IntervalMixin<T, String> {

    public IntervalOfDurationMixin(T self) {
        super(self);
    }

    @Override
    protected String getLower(T interval) {
    return interval.getLower();
    }

    @Override
    protected String getUpper(T interval) {
        return interval.getUpper();
    }
}
