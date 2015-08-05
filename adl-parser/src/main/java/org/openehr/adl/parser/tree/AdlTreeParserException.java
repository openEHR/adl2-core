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

import org.antlr.v4.runtime.Token;
import org.openehr.adl.parser.AdlParserException;

import javax.annotation.Nullable;

/**
 * @author markopi
 */
public class AdlTreeParserException extends AdlParserException {
    private static final long serialVersionUID = -913705312822104455L;

    public AdlTreeParserException(@Nullable Token location, String message, Object... params) {
        super(createMessage(location, message, params));
    }

    private static String createMessage(@Nullable Token location, String message, Object... params) {
        if (location != null) {
            return location.getLine() + ":" + (location.getCharPositionInLine() + 1) + " " + String.format(message, params);
        }
        return String.format(message, params);
    }
}
