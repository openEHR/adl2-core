/*
 * Copyright (C) 2014 Marand
 */

package com.marand.thinkehr.adl.util.walker;

import org.openehr.am.AmObject;

/**
 * @author markopi
 */
public interface AmSinglePhaseVisitor<T extends AmObject, C extends AmVisitContext> {
    ArchetypeWalker.Action<? extends T> visit(T item, C AmVisitContext);
}
