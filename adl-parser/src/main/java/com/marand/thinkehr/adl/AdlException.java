/*
 * Copyright (C) 2014 Marand
 */

package com.marand.thinkehr.adl;

/**
 * @author markopi
 */
public class AdlException extends RuntimeException {

    private static final long serialVersionUID = 1374833190382297507L;

    public AdlException() {
    }

    public AdlException(String message) {
        super(message);
    }

    public AdlException(String message, Throwable cause) {
        super(message, cause);
    }

    public AdlException(Throwable cause) {
        super(cause);
    }
}
