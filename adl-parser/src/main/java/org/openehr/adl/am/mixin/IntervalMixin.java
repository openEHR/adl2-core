/*
 * Copyright (C) 2014 Marand
 */

package org.openehr.adl.am.mixin;

import org.openehr.jaxb.rm.Interval;

import java.util.Objects;

/**
 * @author markopi
 */
abstract public class IntervalMixin<T extends Interval, V extends Comparable> extends AbstractAmMixin<T> {

    public IntervalMixin(T self) {
        super(self);
    }

    abstract protected V getLower(T interval);

    abstract protected V getUpper(T interval);

    public boolean contains(T other) {
        return containsLower(other) && containsUpper(other);
    }

    private boolean containsLower(T other) {
        if (self.isLowerUnbounded()) return true;
        if (other.isLowerUnbounded()) return false;

        int cmp = getLower(self).compareTo(getLower(other));
        if (cmp < 0) return true;
        if (cmp > 0) return false;

        if (!self.isLowerIncluded() && other.isLowerIncluded()) return false;
        return true;
    }

    private boolean containsUpper(T other) {
        if (self.isUpperUnbounded()) return true;
        if (other.isUpperUnbounded()) return false;

        int cmp = getUpper(self).compareTo(getUpper(other));
        if (cmp > 0) return true;
        if (cmp < 0) return false;

        if (!self.isUpperIncluded() && other.isUpperIncluded()) return false;
        return true;
    }

    public boolean isEqualTo(T interval) {
        if (interval==null) return false;
        if (!Objects.equals(self.isLowerIncluded(), interval.isLowerIncluded())) return false;
        if (!Objects.equals(self.isUpperIncluded(), interval.isUpperIncluded())) return false;
        if (!Objects.equals(self.isLowerUnbounded(), interval.isLowerUnbounded())) return false;
        if (!Objects.equals(self.isUpperUnbounded(), interval.isUpperUnbounded())) return false;

        if (!Objects.equals(getLower(self), getLower(interval))) return false;
        if (!Objects.equals(getUpper(self), getUpper(interval))) return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(self.isLowerIncluded() ? "[" : "(");
        if (!self.isLowerUnbounded()) {
            result.append(getLower(self));
        }
        result.append(",");
        if (!self.isUpperUnbounded()) {
            result.append(getUpper(self));
        }
        result.append(self.isUpperIncluded() ? "]" : ")");
        return result.toString();
    }


}
