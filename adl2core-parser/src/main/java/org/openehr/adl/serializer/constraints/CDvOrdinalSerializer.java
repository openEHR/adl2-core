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
import org.openehr.jaxb.am.CDvOrdinal;
import org.openehr.jaxb.rm.DvOrdinal;

import java.util.List;

/**
 * Created by bna on 27.01.2015.
 * ELEMENT[at0015] occurrences matches {0..1} matches {	-- Ordinal
 * value matches {
 * 0|[local::at0038], 	-- No pain
 * 1|[local::at0039], 	-- Slight pain
 * 2|[local::at0040], 	-- Mild pain
 * 5|[local::at0041], 	-- Moderate pain
 * 9|[local::at0042], 	-- Severe pain
 * 10|[local::at0043]  	-- Most severe pain imaginable
 * }
 * }
 */
public class CDvOrdinalSerializer extends ConstraintSerializer<CDvOrdinal> {
    public CDvOrdinalSerializer(ArchetypeSerializer serializer) {
        super(serializer);
    }

    @Override
    public void serialize(CDvOrdinal cobj) {
        boolean constrained=false;
        List<DvOrdinal> ordinalList = cobj.getList();
        int i = 0;
        for (DvOrdinal o : ordinalList) {
            i++;
            builder.append(o.getValue()).append("|")
                    .append("[")
                    .append(o.getSymbol().getDefiningCode().getTerminologyId().getValue())
                    .append("::")
                    .append(o.getSymbol().getDefiningCode().getCodeString())
                    .append("]");
            if (i < ordinalList.size()) {
                builder.append(",");
            }
            builder.newline();
            constrained=true;
        }

        if (!constrained) {
            builder.append("*");
        }
    }
}
