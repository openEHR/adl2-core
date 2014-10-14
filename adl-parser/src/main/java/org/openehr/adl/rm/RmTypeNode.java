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

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author markopi
 * @since 3.6.2013
 */
public final class RmTypeNode {
    private RmTypeNode parent;
    private ImmutableList<RmTypeNode> children;
    private String rmType;
    private Class mainRmClass;
    private List<Class> rmClasses;
    private Map<String, RmTypeAttribute> attributesMap = ImmutableMap.of();


    public RmTypeNode(String rmType, Class rmClass, Class... otherRmClasses) {
        this.rmType = rmType;

        this.mainRmClass = rmClass;
        this.rmClasses = new ImmutableList.Builder<Class>()
                .add(rmClass)
                .addAll(Arrays.asList(otherRmClasses))
                .build();

        children = ImmutableList.of();
    }

    void addChild(RmTypeNode child) {
        children = new ImmutableList.Builder<RmTypeNode>()
                .addAll(children)
                .add(child)
                .build();
        child.parent = this;
    }

    public List<RmTypeNode> getChildren() {
        return children;
    }

    private void buildDescendants(ImmutableList.Builder<RmTypeNode> result, boolean includeSelf) {
        if (includeSelf) {
            result.add(this);
        }
        for (RmTypeNode child : children) {
            child.buildDescendants(result, true);
        }
    }

    public List<RmTypeNode> getDescendants(boolean includeSelf) {
        ImmutableList.Builder<RmTypeNode> result = new ImmutableList.Builder<>();
        buildDescendants(result, includeSelf);
        return result.build();
    }

    public Class getMainRmClass() {
        return mainRmClass;
    }

    public RmTypeNode getParent() {
        return parent;
    }

    public List<Class> getRmClasses() {
        return rmClasses;
    }

    public String getRmType() {
        return rmType;
    }

    public Map<String, RmTypeAttribute> getAttributes() {
        return attributesMap;
    }


    public Object newValueInstance() {
        try {
            return mainRmClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Error creating new instance of type " + mainRmClass.getName(), e);
        }
    }

    void setAttributes(List<RmTypeAttribute> attributes) {
        Map<String, RmTypeAttribute> attributesMap = new LinkedHashMap<>();
        for (RmTypeAttribute attribute : attributes) {
            attributesMap.put(attribute.getAttributeName(), attribute);
        }
        this.attributesMap = ImmutableMap.copyOf(attributesMap);
    }
}
