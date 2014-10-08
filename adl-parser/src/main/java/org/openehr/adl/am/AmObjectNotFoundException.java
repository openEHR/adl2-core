/*
 * Copyright (C) 2014 Marand
 */

package org.openehr.adl.am;

import org.openehr.adl.AdlException;

/**
 * @author markopi
 */
public class AmObjectNotFoundException extends AdlException {
    private static final long serialVersionUID = 4992294214651899020L;


    public AmObjectNotFoundException(String message) {
        super(message);
    }
}
