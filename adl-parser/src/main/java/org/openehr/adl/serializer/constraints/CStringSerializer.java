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
import org.openehr.jaxb.am.CString;

/**
 * @author Marko Pipan
 */
public class CStringSerializer extends ConstraintSerializer<CString> {
    public CStringSerializer(ArchetypeSerializer serializer) {
        super(serializer);
    }

    @Override
    public void serialize(CString cobj) {
        boolean constrained = false;

        if (cobj.getPattern() != null) {
            char patternChar = findPatternWrapperChar(cobj.getPattern());

            builder.append(patternChar).append(cobj.getPattern()).append(patternChar);
            constrained = true;
        }
        if (!cobj.getList().isEmpty()) {
            for (int i = 0; i < cobj.getList().size(); i++) {
                String item = cobj.getList().get(i);
                builder.text(item);
                if (i < cobj.getList().size() - 1) {
                    builder.append(", ");
                }
            }
            constrained = true;
        }
        if (cobj.getAssumedValue() != null) {
            builder.append("; ").text(cobj.getAssumedValue());
            constrained = true;
        }
        if (!constrained) {
            builder.append("*");
        }
    }

    private char findPatternWrapperChar(String pattern) {
        if (!pattern.contains("/")) {
            return '/';
        }
        return '^';
    }
}
