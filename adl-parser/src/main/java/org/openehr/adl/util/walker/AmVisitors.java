/*
 * Copyright (C) 2014 Marand
 */

package org.openehr.adl.util.walker;

import org.openehr.am.AmObject;

/**
 * @author markopi
 */
public class AmVisitors {
    public static <T extends AmObject, C extends AmVisitContext> AmVisitor<T, C> preorder(AmSinglePhaseVisitor<T, C> visitor,
            Class<? extends T> onlyFor) {
        AmVisitor<T, C> adapter = preorder(visitor);
        DispatchingAmVisitor<C> dispatcher = new DispatchingAmVisitor<C>().add(onlyFor, adapter);
        //noinspection unchecked
        return (AmVisitor<T, C>) dispatcher;
    }

    public static <T extends AmObject, C extends AmVisitContext> AmVisitor<T, C> preorder(AmSinglePhaseVisitor<T, C> visitor) {
        return new AmSinglePhaseVisitorAdapter<>(true, visitor);
    }

    public static <T extends AmObject, C extends AmVisitContext> AmVisitor<T, C> postorder(AmSinglePhaseVisitor<T, C> visitor) {
        return new AmSinglePhaseVisitorAdapter<>(false, visitor);
    }
}
