/*
 * Copyright (C) 2014 Marand
 */

package org.openehr.adl.am;

public enum OperatorKind {
    /**
     * Equals operator ("=" or "==")
     */
    OP_EQ(2001, "="),

    /**
     * Not equals operator ("!=" or "/=" or "<>")
     */
    OP_NE(2002, "!="),

    /**
     * Less-than or equals operator ("<=")
     */
    OP_LE(2003, "<="),

    /**
     * Less-than operator ("<")
     */
    OP_LT(2004, "<"),

    /**
     * Grater-than or equals operator (">=")
     */
    OP_GE(2005, ">="),

    /**
     * Grater-than operator (">")
     */
    OP_GT(2006, ">"),

    /**
     * Matches operator ("matches" or "is_in")
     */
    OP_MATCHES(2007, "matches"),

    /**
     * Not logical operator
     */
    OP_NOT(2010, "not"),

    /**
     * And logical operator
     */
    OP_AND(2011, "and"),

    /**
     * Or logical operator
     */
    OP_OR(2012, "or"),

    /**
     * Xor logical operator
     */
    OP_XOR(2013, "xor"),

    /**
     * Implies logical operator
     */
    OP_IMPLIES(2014, "implies"),

    /**
     * For-all quantifier operator
     */
    OP_FOR_ALL(2015, "for_all"),

    /**
     * Exists quantifier operator
     */
    OP_EXISTS(2016, "exists"),

    /**
     * Plus operator ("+")
     */
    OP_PLUS(2020, "+"),

    /**
     * Minus operator ("-")
     */
    OP_MINUS(2021, "-"),

    /**
     * Multiply operator ("*")
     */
    OP_MULTIPLY(2022, "*"),

    /**
     * Divide operator ("/")
     */
    OP_DIVIDE(2023, "/"),

    /**
     * Exponent operator ("^")
     */
    OP_EXP(2024, "^");

    private int value;
    private String sign;

    OperatorKind(int value, String sign) {
        this.value = value;
        this.sign = sign;
    }

    public int getValue() {
        return value;
    }

    public String toString() {
        return sign;
    }

    public static OperatorKind fromValue(int value) {
        for (OperatorKind e : OperatorKind.values()) {
            if (e.getValue() == value) {
                return e;
            }
        }
        return null;
    }
}