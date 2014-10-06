/*
 * Copyright (C) 2014 Marand
 */

package com.marand.thinkehr.adl.parser;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.marand.thinkehr.adl.AdlException;
import com.marand.thinkehr.adl.parser.tree.AdlTreeParserException;
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