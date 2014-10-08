/*
 * Copyright (C) 2014 Marand
 */

package org.openehr.rm;

/**
 * @author Bostjan Lah
 */
public interface RangeParameters {
    Boolean isLowerIncluded();

    Boolean isUpperIncluded();

    boolean isLowerUnbounded();

    boolean isUpperUnbounded();
}
