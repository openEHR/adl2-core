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

package org.openehr.adl.parser;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.openehr.adl.AdlException;
import org.openehr.adl.parser.tree.AdlTreeParserException;
import org.antlr.runtime.RecognitionException;

import java.util.regex.Pattern;

/**
 * @author markopi
 */
public class AdlParserException extends AdlException {
    private static final Splitter LINE_SPLITER = Splitter.on(Pattern.compile("\n\r|\n"));
    private static final long serialVersionUID = 7337416899081632343L;

    public AdlParserException(String adl, RuntimeRecognitionException e) {
        super(formatMessage(adl, e.getMessage(), e.getLine(), e.getCharPositionInLine(), e.getTokenName()), e);
    }

    public AdlParserException(String adl, RecognitionException e) {
        super(formatMessage(adl, e.getMessage(), e.line, e.charPositionInLine, e.token.getText()));
    }

    public AdlParserException(String adl, AdlTreeParserException e) {
        super(formatMessage(adl, e.getMessage(), e.getLine(), e.getCharPositionInLine(), e.getTokenName()), e);
    }

    private static String formatMessage(String adl, String msg, int line, int charPositionInLine, String token) {
        StringBuilder message = new StringBuilder(String.format("%d:%d", line, charPositionInLine + 1));
        if (msg != null) {
            message.append(". ").append(msg);
        }
        if (token != null) {
            message.append(". Token: ").append(token);
        }

        if (line > 0 && charPositionInLine >= 0) {
            String adlLine = Lists.newArrayList(LINE_SPLITER.split(adl)).get(line - 1);

            message.append("; location: ");
            message.append((adlLine.substring(0, charPositionInLine) + "-->" + adlLine.substring(charPositionInLine)).trim());
        }
        return message.toString();
    }
}