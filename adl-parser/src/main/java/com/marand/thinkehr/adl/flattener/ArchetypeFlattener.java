/*
 * Copyright (C) 2014 Marand
 */

package com.marand.thinkehr.adl.flattener;

import com.marand.thinkehr.adl.rm.RmModel;
import org.openehr.jaxb.am.DifferentialArchetype;
import org.openehr.jaxb.am.FlatArchetype;

import javax.annotation.Nullable;

import static com.marand.thinkehr.adl.util.AdlUtils.createFlatArchetypeClone;

/**
 * Flattens a specialized archetype with a flat parent
 *
 * @author markopi
 */
public class ArchetypeFlattener {
    private final ArchetypeMerger merger;

    public ArchetypeFlattener(RmModel rmModel) {
        merger = new ArchetypeMerger(rmModel);
    }

    /**
     * Flattens a specialized source archetype
     *
     * @param flatParent   Parent archetype. Must already be flattened. Can be null if differentialArchetype is not specialized
     * @param differential Differential (source) archetype
     * @return Specialized archetype in flattened form
     */
    public FlatArchetype flatten(@Nullable FlatArchetype flatParent, DifferentialArchetype differential) {
        FlatArchetype result = createFlatArchetypeClone(differential);

        if (differential.getParentArchetypeId() != null) {
            if (flatParent == null || !flatParent.getArchetypeId().getValue().startsWith(differential.getParentArchetypeId().getValue())) {
                throw new AdlFlattenerException(String.format("Wrong or missing parent archetype: expected %s, got %s",
                        differential.getParentArchetypeId().getValue(),
                        flatParent != null ? flatParent.getArchetypeId().getValue() : null));
            }
            merger.merge(flatParent, result);
        }

        return result;
    }

}
