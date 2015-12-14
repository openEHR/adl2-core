package org.openehr.adl.am.mixin;

import org.openehr.jaxb.am.Archetype;
import org.openehr.jaxb.am.ArchetypeTerminology;
import org.openehr.jaxb.am.CTerminologyCode;
import org.openehr.jaxb.am.ValueSetItem;

import java.util.Optional;

/**
 * @author markopi
 */
public class CTerminologyCodeMixin extends CPrimitiveObjectMixin<CTerminologyCode> {
    public CTerminologyCodeMixin(CTerminologyCode self) {
        super(self);
    }

    public Optional<ValueSetItem> getValueSet(Archetype archetype) {
        return getValueSet(archetype.getTerminology());
    }

    public Optional<ValueSetItem> getValueSet(ArchetypeTerminology terminology) {
        String valueSetId = self.getConstraint();
        if (terminology == null || terminology.getValueSets().isEmpty()) {
            return Optional.empty();
        }
        for (ValueSetItem vsi : terminology.getValueSets()) {
            if (vsi.getId().equals(valueSetId)) {
                return Optional.of(vsi);
            }
        }
        return Optional.empty();
    }

}
