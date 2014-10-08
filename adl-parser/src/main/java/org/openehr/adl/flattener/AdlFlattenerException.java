/*
 * Copyright (C) 2014 Marand
 */

package org.openehr.adl.flattener;

import org.openehr.adl.AdlException;

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
