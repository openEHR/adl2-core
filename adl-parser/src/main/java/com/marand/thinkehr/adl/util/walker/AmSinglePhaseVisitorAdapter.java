/*
 * Copyright (C) 2014 Marand
 */

package com.marand.thinkehr.adl.util.walker;

import org.openehr.am.AmObject;

/**
 * @author markopi
 */
public class AmSinglePhaseVisitorAdapter<T extends AmObject, C extends AmVisitContext> extends AbstractAmVisitor<T, C> {
    private final boolean preorder;
    private final AmSinglePhaseVisitor<T, C> visitor;

    public AmSinglePhaseVisitorAdapter(boolean preorder, AmSinglePhaseVisitor<T, C> visitor) {
        this.preorder = preorder;
        this.visitor = visitor;
    }

    @Override
    public ArchetypeWalker.Action<? extends T> preorderVisit(T item, C context) {
        if (preorder) {
            return visitor.visit(item, context);
        }
        return super.preorderVisit(item, context);
    }

    @Override
    public ArchetypeWalker.Action<? extends T> postorderVisit(T item, C context, ArchetypeWalker.Action<? extends T> action) {
        if (!preorder) {
            return visitor.visit(item, context);
        }
        return super.postorderVisit(item, context, action);
    }
}
