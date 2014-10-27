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

import org.openehr.jaxb.rm.MultiplicityInterval;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author markopi
 * @since 20.6.2013
 */
public class RmTypeAttribute {
    private final RmType owner;
    private final RmType targetType;
    private final String attributeName;
    private final String propertyName;
    private final MultiplicityInterval existence;

    public RmTypeAttribute(String attributeName, String propertyName, RmType owner, RmType targetType,
            MultiplicityInterval existence) {
        this.attributeName = attributeName;
        this.propertyName = propertyName;
        this.existence = existence;
        this.owner = checkNotNull(owner);
        this.targetType = targetType;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public RmType getOwner() {
        return owner;
    }

    public RmType getTargetType() {
        return targetType;
    }

    public Class<?> getType() {
        return targetType.getMainRmClass();
    }

    public MultiplicityInterval getExistence() {
        return existence;
    }
}
