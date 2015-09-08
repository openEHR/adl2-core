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
import org.openehr.jaxb.am.CDvQuantity;
import org.openehr.jaxb.am.CQuantityItem;
import org.openehr.jaxb.rm.DvQuantity;

import static com.google.common.base.MoreObjects.firstNonNull;

/**
 * Created by bna on 27.01.2015.
 */
public class CDvQuantitySerializer extends ConstraintSerializer<CDvQuantity> {
    public CDvQuantitySerializer(ArchetypeSerializer archetypeSerializer) {
        super(archetypeSerializer);
    }

    @Override
    public void serialize(CDvQuantity cobj) {
        builder
                .newIndentedline()
                .append("C_DV_QUANTITY <")
                .newIndentedline()
                .append("property = <[")
                .append(cobj.getProperty().getTerminologyId().getValue())
                .append("::")
                .append(cobj.getProperty().getCodeString())
                .append("]>")
                .newline();

        builder.tryNewLine().append("list = <").newIndentedline();
        int n = 0;
        for (CQuantityItem item : cobj.getList()) {
            n++;
            builder.append("[\"" + n + "\"] = <")
                    .newIndentedline();
            if (item.getUnits() != null) {
                builder.append("units = <\"").append(item.getUnits()).append("\">");
                builder.newline();
            }
            if (item.getMagnitude() != null) {
                builder.append("magnitude").append(" = ")
                        .append("<|").append(firstNonNull(item.getMagnitude().getLower(), "0"))
                        .append("..").append(firstNonNull(item.getMagnitude().getUpper(), "*"))
                        .append("|>");
                builder.newline();
            }
            if (item.getPrecision() != null) {
                builder.append("precision = ")
                        .append("<|").append(firstNonNull(item.getPrecision().getUpper(), "0")).append("|>");

                builder.newline();
            }
            builder.unindent()
                    .append(">")
                    .newline()
            ;


        }
        builder
                .tryNewLine()
                .append(">")
                .unindent()
                .unindent();
        DvQuantity assumedValue = cobj.getAssumedValue();
        if (assumedValue != null) {

            builder.newline()
                    .append("assumed_value = <")
                    .newIndentedline()
                    .append("magnitude = <").append(assumedValue.getMagnitude()).append(">")
                    .newline()
                    .append("units = <\"").append(assumedValue.getUnits()).append("\">")
                    .newline()
                    .append("precision = <").append(assumedValue.getPrecision()).append(">")
                    .newline()
                    .unindent()
                    .append(">")
        .newline()
            ;
        }
        builder.append(">");
        builder.unindent();
    }
}
