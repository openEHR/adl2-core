/*
 * Copyright (C) 2014 Marand
 */

package com.marand.thinkehr.adl.am.mixin;

/**
 * @author markopi
 */
abstract public class AbstractAmMixin<T> implements AmMixin<T> {
    protected final T self;

    public AbstractAmMixin(T self) {
        this.self = self;
    }
}
