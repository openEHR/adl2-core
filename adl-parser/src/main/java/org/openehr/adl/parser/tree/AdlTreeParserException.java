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

package org.openehr.adl.parser.tree;

import org.antlr.runtime.Token;

import javax.annotation.Nullable;

/**
 * @author markopi
 */
public class AdlTreeParserException extends RuntimeException {
    private static final long serialVersionUID = -8043425736801540295L;

    private final int line;
    private final int charPositionInLine;
    private final String tokenName;


    public AdlTreeParserException(String message, @Nullable Token location) {
        this(message, location, null);

    }

    public AdlTreeParserException(String message, @Nullable Token location, @Nullable Exception cause) {
        super(message, cause);
        if (location != null) {
            line = location.getLine();
            charPositionInLine = location.getCharPositionInLine();
            tokenName = location.getText();
        } else {
            line = -1;
            charPositionInLine = -1;
            tokenName = null;
        }
    }

    public int getLine() {
        return line;
    }

    public int getCharPositionInLine() {
        return charPositionInLine;
    }

    public String getTokenName() {
        return tokenName;
    }
}
