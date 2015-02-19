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
import org.openehr.jaxb.am.CReal;

import static com.google.common.base.MoreObjects.firstNonNull;

/**
 * @author Marko Pipan
 */
public class CRealSerializer extends ConstraintSerializer<CReal> {
    public CRealSerializer(ArchetypeSerializer serializer) {
        super(serializer);
    }

    @Override
    public void serialize(CReal cobj) {
        boolean constrained = false;

        if (cobj.getRange() != null) {
            builder.append("|")
                    .append(firstNonNull(cobj.getRange().getLower(), "0.0"))
                    .append("..")
                    .append(firstNonNull(cobj.getRange().getUpper(), "*"))
                    .append("|");
            constrained = true;
        }
        if (!cobj.getList().isEmpty()) {
            for (int i = 0; i < cobj.getList().size(); i++) {
                Float item = cobj.getList().get(i);
                builder.append(item);
                if (i < cobj.getList().size() - 1) {
                    builder.append(", ");
                }
            }
            constrained = true;
        }
        if (cobj.getAssumedValue() != null) {
            builder.append(";").append(cobj.getAssumedValue());
            constrained = true;
        }

        if (!constrained) {
            builder.append("*");
        }

    }
}
