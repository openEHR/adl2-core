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

import java.util.List;

/**
 * Provides information about a given rm model
 *
 * @author markopi
 */
public interface RmModel {

    RmType getRmType(String rmType);


    /**
     * checks if a given rm type exists
     *
     * @param rmType type name
     * @return true if the type exists
     */
    boolean rmTypeExists(String rmType);

    /**
     * Returns information about a given rm attribute
     *
     * @param rmType    name of the containing rm type
     * @param attribute attribute name
     * @return attribute information
     * @throws org.openehr.adl.rm.RmModelException if no such attribute exists
     */
    RmTypeAttribute getRmAttribute(String rmType, String attribute);


    List<RmType> getAllTypes();

}
