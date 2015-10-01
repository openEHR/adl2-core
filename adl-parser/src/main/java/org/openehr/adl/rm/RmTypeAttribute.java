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

package org.openehr.adl.rm;

import org.openehr.jaxb.am.Cardinality;
import org.openehr.jaxb.rm.MultiplicityInterval;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author markopi
 * @since 20.6.2013
 */
public class RmTypeAttribute {
    private final String targetType;
    private final String attributeName;
    private final MultiplicityInterval existence;
    private final Cardinality cardinality;

    public RmTypeAttribute(String attributeName, String targetType,
                           MultiplicityInterval existence, Cardinality cardinality) {
        this.attributeName = attributeName;
        this.existence = existence;
        this.targetType = targetType;
        this.cardinality = cardinality;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public String getType() {
        return targetType;
    }

    public MultiplicityInterval getExistence() {
        return existence;
    }

    public Cardinality getCardinality() {
        return cardinality;
    }
}
