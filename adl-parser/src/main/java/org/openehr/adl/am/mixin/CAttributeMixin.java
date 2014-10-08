/*
 * Copyright (C) 2014 Marand
 */

package org.openehr.adl.am.mixin;

import org.openehr.jaxb.am.CAttribute;

/**
 * @author markopi
 */
public class CAttributeMixin extends AbstractAmMixin<CAttribute> {
    public CAttributeMixin(CAttribute self) {
        super(self);
    }

    public boolean isMultiple() {
        return self.getCardinality()!=null;
    }

    public boolean isSingle() {
        return self.getCardinality()==null;
    }
    public boolean isOrdered() {
        return isMultiple() && self.getCardinality().isIsOrdered();
    }

    public boolean isProhibited() {
        return AmMixins.of(self.getExistence()).isProhibited();
    }


}
