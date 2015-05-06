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

package org.openehr.adl.serializer.constraints;

import org.openehr.adl.serializer.ArchetypeSerializer;
import org.openehr.jaxb.am.CArchetypeRoot;

/**
 * @author Marko Pipan
 */
public class CArchetypeRootSerializer extends ConstraintSerializer<CArchetypeRoot> {
    public CArchetypeRootSerializer(ArchetypeSerializer serializer) {
        super(serializer);
    }

    @Override
    public void serialize(CArchetypeRoot cobj) {
        builder.indent().newline();
        builder.append("use_archetype");
        builder.append(" ").append(cobj.getRmTypeName());
        builder.append("[");
        if (cobj.getSlotNodeId() != null) {
            builder.append(cobj.getSlotNodeId()).append(", ");
        }
        builder.append(cobj.getNodeId());
        builder.append("]");

        builder.unindent();
    }
}
