package org.openehr.adl.am.mixin;

import org.openehr.jaxb.am.CPrimitiveObject;

/**
 * @author markopi
 */
abstract public class CPrimitiveObjectMixin<T extends CPrimitiveObject> extends AbstractAmMixin<T> {
    public CPrimitiveObjectMixin(T self) {
        super(self);
    }
}
