/*
 * Copyright (C) 2014 Marand
 */

package com.marand.thinkehr.adl.util.walker;

import org.openehr.am.AmObject;

import java.util.LinkedList;

/**
 * @author markopi
 */
public class AmVisitContext {
    private LinkedList<AmObject> amParents = new LinkedList<>();


    public LinkedList<AmObject> getAmParents() {
        return amParents;
    }
}
