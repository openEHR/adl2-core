/*
 * Copyright (C) 2014 Marand
 */

package com.marand.thinkehr.adl.util.walker;

import org.openehr.am.AmObject;

/**
 * @author markopi
 */
abstract public class AbstractAmVisitor<T extends AmObject, C extends AmVisitContext> implements AmVisitor<T, C> {
    @Override
    public ArchetypeWalker.Action<? extends T> preorderVisit(T item, C context) {
        return ArchetypeWalker.Action.next();
    }

    @Override
    public ArchetypeWalker.Action<? extends T> postorderVisit(T item, C context, ArchetypeWalker.Action<? extends T> action) {
        return ArchetypeWalker.Action.next();
    }
}
