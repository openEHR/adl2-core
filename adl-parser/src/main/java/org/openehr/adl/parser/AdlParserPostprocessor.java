/*
 * Copyright (C) 2014 Marand
 */

package org.openehr.adl.parser;

import org.openehr.am.AmObject;
import org.openehr.adl.am.AmQuery;
import org.openehr.adl.util.AdlUtils;
import org.openehr.adl.util.walker.AmConstraintContext;
import org.openehr.adl.util.walker.AmSinglePhaseVisitor;
import org.openehr.adl.util.walker.AmVisitors;
import org.openehr.adl.util.walker.ArchetypeWalker;
import org.openehr.jaxb.am.ArchetypeInternalRef;
import org.openehr.jaxb.am.CObject;
import org.openehr.jaxb.am.DifferentialArchetype;

/**
 * @author Marko Pipan
 */
public class AdlParserPostprocessor {

    public static void postprocess(final DifferentialArchetype archetype) {
        ArchetypeWalker.walkConstraints(AmVisitors.preorder(new AmSinglePhaseVisitor<AmObject, AmConstraintContext>() {
            @Override
            public ArchetypeWalker.Action<? extends AmObject> visit(AmObject item, AmConstraintContext context) {
                if (item instanceof ArchetypeInternalRef) {
                    ArchetypeInternalRef air = (ArchetypeInternalRef) item;
                    if (air.getTargetPath() != null) {
                        CObject target = AmQuery.find(archetype, air.getTargetPath());
                        if (target!=null) {
                            if (air.getOccurrences() == null) {
                                air.setOccurrences(AdlUtils.makeClone(target.getOccurrences()));
                            }
                        }
                    }
                }
                    return ArchetypeWalker.Action.next();
            }
        }), archetype, new AmConstraintContext());

    }


}
