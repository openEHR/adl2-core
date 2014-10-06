/*
 * Copyright (C) 2014 Marand
 */

package com.marand.thinkehr.adl.parser.tree;

/**
 * @author markopi
 */
public class AdlTreeParserState {
    private String adlVersion;
    private boolean adlV15 = false;


    public void setAdlVersion(String adlVersion) {
        this.adlVersion = adlVersion;
        String[] tokens = adlVersion.split("\\.");
        if (tokens.length > 1) {
            try {
                adlV15 = Integer.parseInt(tokens[1]) >= 5;
            } catch (NumberFormatException e) {
                adlV15 = false;
            }
        }
    }

    public String getAdlVersion() {
        return adlVersion;
    }

    public boolean isAdlV15() {
        return adlV15;
    }
}
