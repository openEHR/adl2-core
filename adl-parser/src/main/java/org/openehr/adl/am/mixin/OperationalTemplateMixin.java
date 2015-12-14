package org.openehr.adl.am.mixin;

import org.openehr.jaxb.am.ArchetypeTerminology;
import org.openehr.jaxb.am.ArchetypeTerminologyItem;
import org.openehr.jaxb.am.OperationalTemplate;

import java.util.Optional;

/**
 * @author markopi
 */
public class OperationalTemplateMixin extends AbstractAmMixin<OperationalTemplate> {
    public OperationalTemplateMixin(OperationalTemplate self) {
        super(self);
    }


    public Optional<ArchetypeTerminology> getComponentTerminology(final String archetypeId) {
        for (ArchetypeTerminologyItem t : self.getComponentTerminologies()) {
            if (t.getCode().equals(archetypeId)) {
                return Optional.of(t.getValue());
            }
        }
        return Optional.empty();
    }
}
