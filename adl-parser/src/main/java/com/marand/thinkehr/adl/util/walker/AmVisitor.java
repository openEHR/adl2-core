/*
 * Copyright (C) 2014 Marand
 */

package com.marand.thinkehr.adl.util.walker;

import org.openehr.am.AmObject;

/**
 * @author markopi
 */
public interface AmVisitor<T extends AmObject, C extends AmVisitContext> {
    /**
     * @param item    Item being visited.
     * @param context current context. context is mutable, so depth-based changes should be reverted in postorderVisit
     * @return Action to take with the node. Must be compatible with action returned by postorderVisit
     */
    ArchetypeWalker.Action<? extends T> preorderVisit(T item, C context);

    /**
     * @param item    Item being visited. If preorder action is replace, this is the replacement item
     * @param context current context
     * @param action  action to be taken that was returned by the preorderVisit
     * @return Action to take with the node. Must be compatible with action returned by preorderVisit
     */
    ArchetypeWalker.Action<? extends T> postorderVisit(T item, C context, ArchetypeWalker.Action<? extends T> action);

}
