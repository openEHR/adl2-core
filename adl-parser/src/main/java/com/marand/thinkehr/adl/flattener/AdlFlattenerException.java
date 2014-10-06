/*
 * Copyright (C) 2014 Marand
 */

package com.marand.thinkehr.adl.flattener;

import com.marand.thinkehr.adl.AdlException;

/**
 * @author markopi
 */
public class AdlFlattenerException extends AdlException {
    private static final long serialVersionUID = -6886012296433084504L;

    public AdlFlattenerException() {
    }

    public AdlFlattenerException(String message) {
        super(message);
    }

    public AdlFlattenerException(String message, Throwable cause) {
        super(message, cause);
    }

    public AdlFlattenerException(Throwable cause) {
        super(cause);
    }
}
