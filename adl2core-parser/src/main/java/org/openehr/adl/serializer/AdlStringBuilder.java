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

package org.openehr.adl.serializer;

import org.apache.commons.lang.StringUtils;

/**
 * @author Marko Pipan
 */
public class AdlStringBuilder {
    private final StringBuilder builder;
    private final DAdlSerializer dAdlSerializer;
    private int indentDepth=0;
    private boolean newLine=true;

    public AdlStringBuilder() {
        builder = new StringBuilder();
        dAdlSerializer = new DAdlSerializer(this);
    }

    public AdlStringBuilder append(Object str) {
        builder.append(str);
        newLine=false;
        return this;
    }

    public AdlStringBuilder text(String str) {
        String text = quoteText(str);
        return append(text);

    }

    private String quoteText(String str) {
        str = StringUtils.replace(str, "\\", "\\\\");
        str = StringUtils.replace(str, "\"", "\\\"");
        return "\""+str+"\"";
    }

    public AdlStringBuilder tryNewLine() {
        if (!newLine) {
            return newline();
        }
        return this;
    }

    public AdlStringBuilder newline() {
        builder.append(StringUtils.rightPad("\n", indentDepth*4+1));
        newLine=true;
        return this;
    }

    public AdlStringBuilder indent() {
        indentDepth++;
        return this;
    }

    public AdlStringBuilder newIndentedline() {
        return indent().newline();
    }

    public AdlStringBuilder unindent() {
        indentDepth--;
        if (indentDepth<0) throw new AssertionError();
        return this;
    }

    public AdlStringBuilder lineComment(String comment) {
        if (comment!=null) {
            append(StringUtils.rightPad("", 4)).append("-- ").append(comment);
        }
        return this;
    }

    public AdlStringBuilder dadl(Object obj) {
        dAdlSerializer.serialize(obj);
        return this;
    }
    public AdlStringBuilder dadlBean(Object obj) {
        dAdlSerializer.serializeBean(obj);
        return this;
    }

    public int mark() {
        return builder.length();
    }
    public AdlStringBuilder revert(int previousMark) {
        builder.setLength(previousMark);
        return this;
    }

    @Override
    public String toString() {
        return builder.toString();
    }
}
