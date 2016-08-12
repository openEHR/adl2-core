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

import com.google.common.collect.Lists;

import java.util.List;

/**
 * RM model representing OpenEHR objects
 *
 * @author markopi
 */
public class OpenEhrRmModel implements RmModel {
    private static OpenEhrRmModel instance;

    private final RmTypeGraph rmTypeGraph;


    /**
     * Use OpenEhrRmModel.getInstance() instead
     */
    private OpenEhrRmModel() {
        rmTypeGraph = new RmTypeGraphBuilder(org.openehr.jaxb.rm.ObjectFactory.class).build();
    }

    public static OpenEhrRmModel getInstance() {
        OpenEhrRmModel result = instance;
        if (result == null) {
            result = new OpenEhrRmModel();
            instance = result;
        }
        return result;
    }


    @Override
    public boolean rmTypeExists(String rmType) {
        return rmTypeGraph.tryGetNodeFromRmType(rmType) != null;
    }

    @Override
    public RmType getRmType(String rmType) {
        RmType node = rmTypeGraph.tryGetNodeFromRmType(rmType);
        if (node == null) {
            throw new RmModelException("Unknown RM type: " + rmType);
        }
        return node;
    }

    private RmTypeAttribute getRmAttribute(RmType rmType, String attribute) {
        RmTypeAttribute result = rmType.getAttributes().get(attribute);
        if (result == null) {
            throw new RmModelException("No attribute [%s] on rm type [%s]", attribute, rmType.getRmType());
        }
        return result;
    }

    @Override
    public RmTypeAttribute getRmAttribute(String rmType, String attribute) {
        return getRmAttribute(getRmType(rmType), attribute);
    }

    @Override
    public List<RmType> getAllTypes() {
        return Lists.newArrayList(rmTypeGraph.getAllNodes());
    }
}
