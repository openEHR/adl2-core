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
import org.openehr.jaxb.am.CTerminologyCode;

/**
 * @author Marko Pipan
 */
public class CTerminologyCodeSerializer extends ConstraintSerializer<CTerminologyCode> {

    public CTerminologyCodeSerializer(ArchetypeSerializer serializer) {
        super(serializer);
    }

    @Override
    public void serialize(CTerminologyCode cobj) {
        boolean multiline = cobj.getCodeList().size() > 1;

        if (multiline) {
            serializeMultiline(cobj);
        } else {
            serializeSingleLine(cobj);
        }
    }

    private void serializeSingleLine(CTerminologyCode cobj) {
        builder.append("[");
        if (cobj.getTerminologyId() != null) {
            builder.append(cobj.getTerminologyId()).append("::");
        }
        builder.append(cobj.getCodeList().get(0));
        if (cobj.getAssumedValue()!=null) {
            builder.append("; ").append(cobj.getAssumedValue());
        }
        builder.append("]");
    }

    private void serializeMultiline(CTerminologyCode cobj) {
        builder.newIndentedline();
        builder.append("[");
        if (cobj.getTerminologyId() != null) {
            builder.append(cobj.getTerminologyId()).append("::");
        }
        for (int i = 0; i < cobj.getCodeList().size(); i++) {
            String code = cobj.getCodeList().get(i);
            builder.newline();
            builder.append(code);
            if (i < cobj.getCodeList().size() - 1) {
                builder.append(",");
            } else {
                builder.append(cobj.getAssumedValue() != null ? ";" : "]");
            }
            builder.lineComment(serializer.getArchetypeMapHolder().getTermText(code));
        }
        if (cobj.getAssumedValue() != null) {
            builder.newline();
            builder.append(cobj.getAssumedValue()).append("]");
        }
        builder.unindent().newline();
    }


    @Override
    public String getSimpleCommentText(CTerminologyCode cobj) {
        if (cobj.getCodeList().size() != 1) {
            return super.getSimpleCommentText(cobj);
        }
        String text = serializer.getArchetypeMapHolder().getConstraintDefinitionText(cobj.getCodeList().get(0));
        if (text==null) {
            text=serializer.getArchetypeMapHolder().getTermText(cobj.getCodeList().get(0));
        }
        return text;
    }
}
