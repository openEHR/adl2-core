/*
 * ADL2-core
 * Copyright (c) 2013-2014 Marand d.o.o. (www.marand.com)
 *
 * This file is part of ADL2-core.
 *
 * ADL2-core is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openehr.adl.parser;

import org.openehr.adl.am.AmQuery;
import org.openehr.adl.util.AdlUtils;
import org.openehr.adl.util.walker.AmConstraintContext;
import org.openehr.adl.util.walker.AmSinglePhaseVisitor;
import org.openehr.adl.util.walker.AmVisitors;
import org.openehr.adl.util.walker.ArchetypeWalker;
import org.openehr.am.AmObject;
import org.openehr.jaxb.am.Archetype;
import org.openehr.jaxb.am.ArchetypeInternalRef;
import org.openehr.jaxb.am.CObject;

/**
 * @author Marko Pipan
 */
public class AdlParserPostprocessor {

    public static void postprocess(final Archetype archetype) {
        ArchetypeWalker.walkConstraints(AmVisitors.preorder(new AmSinglePhaseVisitor<AmObject, AmConstraintContext>() {
            @Override
            public ArchetypeWalker.Action<? extends AmObject> visit(AmObject item, AmConstraintContext context) {
                if (item instanceof ArchetypeInternalRef) {
                    ArchetypeInternalRef air = (ArchetypeInternalRef) item;
                    if (air.getTargetPath() != null) {
                        CObject target = AmQuery.find(archetype, air.getTargetPath());
                        if (target != null) {
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
