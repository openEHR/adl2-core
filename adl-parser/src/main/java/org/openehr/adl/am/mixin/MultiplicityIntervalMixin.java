/*
 * Copyright (C) 2014 Marand
 */

package org.openehr.adl.am.mixin;

import org.openehr.jaxb.rm.MultiplicityInterval;

/**
 * @author markopi
 */
public class MultiplicityIntervalMixin extends IntervalOfIntegerMixin<MultiplicityInterval> {
    public MultiplicityIntervalMixin(MultiplicityInterval self) {
        super(self);
    }

    /**
     * @return true for [1,1]
     */
    public boolean isMandatory() {
        return self.getLower() != null && self.getLower() == 1 &&
               self.getUpper() != null && self.getUpper() == 1;
    }

    /**
     * @return true for [0,1]
     */
    public boolean isOptional() {
        return (self.getLower() == null || self.getLower() == 0) &&
               (self.getUpper() != null && self.getUpper() == 1);
    }

    /**
     * @return true if upper >= 2 or unbounded
     */
    public boolean isMultiple() {
        return self.getUpper() == null || self.getUpper() >= 2;
    }

    /**
     * @return true for [0,0]
     */
    public boolean isProhibited() {
        return self.getLower() != null && self.getLower() == 0;
    }
}
