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

import org.openehr.jaxb.am.Cardinality;

/**
 * @author markopi
 */
public class CardinalityMixin extends AbstractAmMixin<Cardinality> {
    public CardinalityMixin(Cardinality self) {
        super(self);
    }

    public boolean isEqualTo(Cardinality other) {
        if (other == null) return false;
        if (other.isIsOrdered() != self.isIsOrdered()) return false;
        if (other.isIsUnique() != self.isIsUnique()) return false;
        return AmMixins.of(other.getInterval()).isEqualTo(self.getInterval());
    }

}