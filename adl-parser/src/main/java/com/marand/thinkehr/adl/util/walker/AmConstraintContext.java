/*
 * Copyright (C) 2014 Marand
 */

package com.marand.thinkehr.adl.util.walker;

import com.marand.thinkehr.adl.rm.RmPath;

import java.util.LinkedList;

/**
 * @author markopi
 */
public class AmConstraintContext extends AmVisitContext {
    private RmPath rmPath = RmPath.ROOT;
    private LinkedList<RmPath> parentRmPaths = new LinkedList<>();

    public RmPath getRmPath() {
        return rmPath;
    }

    public void setRmPath(RmPath rmPath) {
        this.rmPath = rmPath;
    }

    public LinkedList<RmPath> getParentRmPaths() {
        return parentRmPaths;
    }
}
