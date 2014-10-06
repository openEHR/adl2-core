/*
 * Copyright (C) 2014 Marand
 */

package com.marand.thinkehr.adl.parser;

import org.openehr.am.AmObject;
import com.marand.thinkehr.adl.am.AmQuery;
import com.marand.thinkehr.adl.util.AdlUtils;
import com.marand.thinkehr.adl.util.walker.AmConstraintContext;
import com.marand.thinkehr.adl.util.walker.AmSinglePhaseVisitor;
import com.marand.thinkehr.adl.util.walker.AmVisitors;
import com.marand.thinkehr.adl.util.walker.ArchetypeWalker;
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
