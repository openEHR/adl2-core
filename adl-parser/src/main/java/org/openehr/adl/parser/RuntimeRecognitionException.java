/*
 * Copyright (C) 2014 Marand
 */

package org.openehr.adl.parser;

import org.openehr.adl.antlr.AdlParser;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.Tree;

/**
 * @author markopi
 */
public class RuntimeRecognitionException extends RuntimeException {

    private static final long serialVersionUID = -2396026625136568094L;

    private final int line;
    private final int charPositionInLine;
    private final String tokenName;

    public RuntimeRecognitionException(RecognitionException e) {
        super(e.getMessage(), e);
        line = e.line;
        charPositionInLine = e.charPositionInLine;
        tokenName = e.token != null ?
                toTokenString(e.token.getType(), e.token.getText()) : e.getMessage();
    }

    private String toTokenString(int type, String text) {
        String typeStr = type > 0 ? AdlParser.tokenNames[type] : "(undefined)";
        if (typeStr.equals(text)) {
            return "[" + typeStr + "]";
        } else {
            return "[" + typeStr + ": " + text + "]";
        }
    }

    public RuntimeRecognitionException(Tree tree) {
        this("Unexpected tree node", tree);
    }

    public RuntimeRecognitionException(String message, Tree tree) {
        super(message);
        line = tree.getLine();
        charPositionInLine = tree.getCharPositionInLine();
        tokenName = toTokenString(tree.getType(), tree.getText());
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
