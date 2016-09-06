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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author markopi
 * @since 3.6.2013
 */
public final class RmType {
    private RmType parent;
    private List<RmType> children = ImmutableList.of();
    private String rmType;
    private Map<String, RmTypeAttribute> attributes = ImmutableMap.of();

    public RmType(String rmType) {
        this.rmType = rmType;
        children = ImmutableList.of();
    }

    public List<RmType> getChildren() {
        return children;
    }

    public void addChild(RmType child) {
        this.children = ImmutableList.<RmType>builder()
                .addAll(this.children)
                .add(child)
                .build();
        child.parent = this;
    }

    private void buildDescendants(List<RmType> result, boolean includeSelf) {
        if (includeSelf) {
            result.add(this);
        }
        for (RmType child : children) {
            child.buildDescendants(result, true);
        }
    }

    public List<RmType> getDescendants(boolean includeSelf) {
        List<RmType> result = new ArrayList<>();
        buildDescendants(result, includeSelf);
        return result;
    }


    public RmType getParent() {
        return parent;
    }


    public String getRmType() {
        return rmType;
    }

    public Map<String, RmTypeAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, RmTypeAttribute> attributes) {
        this.attributes = attributes;
    }


    public RmTypeAttribute findAttribute(String attributeName) {
        RmTypeAttribute result = attributes.get(attributeName);
        if (result == null) {
            RmType p = getParent();
            while (p != null) {
                RmTypeAttribute attr = p.attributes.get(attributeName);
                if (attr != null) return attr;
                p = p.getParent();
            }
        }
        if (result == null) {
            // search for attribute on subclasses
            for (RmType subtype : getChildren()) {
                try {
                    return subtype.getAttribute(attributeName);
                } catch (RmModelException e) {
                    // no attribute on this subclass
                }
            }
        }
        return null;

    }

    public RmTypeAttribute getAttribute(String attributeName) {
        RmTypeAttribute result = findAttribute(attributeName);
        if (result == null) {
            throw new RmModelException(String.format("Rm type %s has no attribute %s", rmType, attributeName));
        }
        return result;
    }

    public boolean isSubclassOf(String rmType) {
        RmType type = this;
        while (type != null) {
            if (type.getRmType().equals(rmType)) return true;
            type = type.getParent();
        }
        return false;
    }

    public boolean isSubclassOf(RmType rmType) {
        return rmType != null && isSubclassOf(rmType.getRmType());
    }


}
