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
     * @return true if upper &gt;= 2 or unbounded
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
