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

import com.google.common.base.Joiner;
import org.openehr.adl.serializer.ArchetypeSerializeUtils;
import org.openehr.adl.serializer.ArchetypeSerializer;
import org.openehr.jaxb.am.*;

import java.util.ArrayList;
import java.util.List;

import static org.openehr.adl.serializer.ArchetypeSerializeUtils.buildOccurrences;

/**
 * @author Marko Pipan
 */
public class CComplexObjectSerializer<T extends CComplexObject> extends ConstraintSerializer<T> {
    public CComplexObjectSerializer(ArchetypeSerializer serializer) {
        super(serializer);
    }

    @Override
    public void serialize(T cobj) {
        builder.indent().newline();

        builder.append(cobj.getRmTypeName());
        if (cobj.getNodeId() != null) {
            builder.append("[").append(cobj.getNodeId()).append("]");
        }
        builder.append(" ");
        if (cobj.getOccurrences() != null) {
            builder.append("occurrences matches {");
            buildOccurrences(builder, cobj.getOccurrences());
            builder.append("} ");
        }
        builder.append("matches {");
        if (cobj.getNodeId() != null) {
            builder.lineComment(serializer.getArchetypeWrapper().getTermText(cobj.getNodeId()));
        }

        buildAttributesAndTuples(cobj);
        builder.append("}");
        builder.unindent();
    }

    private void buildAttributesAndTuples(T cobj) {
        if (cobj.getAttributes().isEmpty() && cobj.getAttributeTuples().isEmpty()) {
            builder.append("*");
            return;
        }

        builder.indent().newline();
        for (CAttribute cattr : cobj.getAttributes()) {
            buildAttribute(cattr);
        }
        for (CAttributeTuple cAttributeTuple : cobj.getAttributeTuples()) {
            buildTuple(cAttributeTuple);
        }
        builder.unindent().newline();
    }


    private void buildAttribute(CAttribute cattr) {
        builder.tryNewLine();
        if (cattr.getRmAttributeName() != null) {
            builder.append(cattr.getRmAttributeName());
        } else {
            builder.append(cattr.getDifferentialPath());
        }
        builder.append(" ");
        if (cattr.getCardinality() != null) {
            builder.append("cardinality matches {");
            appendCardinality(cattr.getCardinality());
            builder.append("} ");
        }
        builder.append("matches ");
        buildAttributeChildConstraints(cattr);
        builder.append("");
    }

    private void buildTuple(CAttributeTuple cAttributeTuple) {
        builder.tryNewLine();
        builder.append("[");
        List<String> members = new ArrayList<>();
        for (CAttribute cAttribute : cAttributeTuple.getMembers()) {
            members.add(cAttribute.getRmAttributeName());
        }
        builder.append(Joiner.on(", ").join(members));
        builder.append("] matches {");
        builder.indent();
        for (int i = 0; i < cAttributeTuple.getChildren().size(); i++) {
            CObjectTuple cObjectTuple = cAttributeTuple.getChildren().get(i);
            builder.newline();
            builder.append("[");
            for (int j = 0; j < cObjectTuple.getMembers().size(); j++) {
                builder.append("{");
                serializer.buildCObject(cObjectTuple.getMembers().get(j));
                builder.append("}");
                if (j < cObjectTuple.getMembers().size() - 1) {
                    builder.append(", ");
                }
            }
            builder.append("]");
            if (i < cAttributeTuple.getChildren().size() - 1) {
                builder.append(", ");
            }

        }
        builder.unindent().newline();
        builder.append("}");
    }

    private void buildAttributeChildConstraints(CAttribute cattr) {
        boolean indent = !cattr.getChildren().isEmpty() &&
                         (cattr.getChildren().size() > 1 || !(cattr.getChildren().get(0) instanceof CPrimitiveObject));
        builder.append("{");
        for (CObject cObject : cattr.getChildren()) {
            serializer.buildCObject(cObject);
        }
        if (indent) {
            builder.newline();
        }
        builder.append("}");

        if (!indent && !cattr.getChildren().isEmpty()) {
            String commentText = serializer.getSimpleCommentText(cattr.getChildren().get(0));
            if (commentText != null) {
                builder.lineComment(commentText);
            }

        }
    }

    private void appendCardinality(Cardinality card) {
        ArchetypeSerializeUtils.buildOccurrences(builder, card.getInterval());
        List<String> tags = new ArrayList<>();
        if (!card.isIsOrdered()) {
            tags.add("unordered");
        }
        if (card.isIsUnique()) {
            tags.add("unique");
        }
        if (!tags.isEmpty()) {
            builder.append("; ").append(Joiner.on(", ").join(tags));
        }
    }
}
