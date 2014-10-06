/*
 * Copyright (C) 2014 Marand
 */

package org.openehr.rm;

import javax.xml.bind.annotation.XmlTransient;

/**
 * Base class for all RM package classes, including RM DataValue classes (not just Locatable).
 *
 * @author Saxo
 */
@XmlTransient
public abstract class RmObject {
    public static final String RM_VERSION = "1.0.1";
}