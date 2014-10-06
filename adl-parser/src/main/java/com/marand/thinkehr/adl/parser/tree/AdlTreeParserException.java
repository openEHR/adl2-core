/*
 * Copyright (C) 2014 Marand
 */

package com.marand.thinkehr.adl.parser.tree;

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
