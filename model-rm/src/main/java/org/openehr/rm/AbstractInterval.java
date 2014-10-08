/*
 * Copyright (C) 2014 Marand
 */

package org.openehr.rm;

/**
 * @author Bostjan Lah
 */
public abstract class AbstractInterval extends RmObject {
    public abstract Boolean isLowerIncluded();

    public abstract Boolean isUpperIncluded();

    public abstract boolean isLowerUnbounded();

    public abstract boolean isUpperUnbounded();
}
