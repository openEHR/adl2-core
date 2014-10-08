/*
 * Copyright (C) 2014 Marand
 */

package org.openehr.adl.rm;

import org.openehr.jaxb.rm.MultiplicityInterval;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author markopi
 * @since 20.6.2013
 */
public class RmTypeAttribute {
    private final RmTypeNode owner;
    private final RmTypeNode targetType;
    private final String attributeName;
    private final String propertyName;
    private final MultiplicityInterval existence;

    public RmTypeAttribute(String attributeName, String propertyName, RmTypeNode owner, RmTypeNode targetType,
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

    public RmTypeNode getOwner() {
        return owner;
    }

    public RmTypeNode getTargetType() {
        return targetType;
    }

    public Class<?> getType() {
        return targetType.getMainRmClass();
    }

    public MultiplicityInterval getExistence() {
        return existence;
    }
}
