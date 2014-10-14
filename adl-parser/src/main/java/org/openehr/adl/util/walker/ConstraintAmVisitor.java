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

package org.openehr.adl.util.walker;

import org.openehr.am.AmObject;
import org.openehr.adl.rm.RmPath;
import org.openehr.jaxb.am.CAttribute;
import org.openehr.jaxb.am.CObject;

/**
 * @author markopi
 */

public class ConstraintAmVisitor<C extends AmConstraintContext> extends DispatchingAmVisitor<C> {

    public ConstraintAmVisitor() {
        add(AmObject.class, new CObjectAmVisitor());
    }


    private class CObjectAmVisitor extends AbstractAmVisitor<AmObject, C> {
        @Override
        public ArchetypeWalker.Action<? extends AmObject> preorderVisit(AmObject item, C context) {
            if (!context.getAmParents().isEmpty()) {
                if (item instanceof CObject) {
                    CObject cobj = (CObject) item;
                    final RmPath path = context.getRmPath().constrain(cobj.getNodeId());
                    context.getParentRmPaths().addLast(context.getRmPath());
                    context.setRmPath(path);
                } else if (item instanceof CAttribute) {
                    CAttribute attribute = (CAttribute) item;
                    final RmPath path;
                    if (attribute.getDifferentialPath() != null) {
                        path = context.getRmPath().resolve(attribute.getDifferentialPath());
                    } else {
                        path = context.getRmPath().resolve(attribute.getRmAttributeName(), null);
                    }
                    context.getParentRmPaths().addLast(context.getRmPath());
                    context.setRmPath(path);
                }
            }
            return super.preorderVisit(item, context);
        }

        @Override
        public ArchetypeWalker.Action<? extends AmObject> postorderVisit(AmObject item, C context,
                ArchetypeWalker.Action<? extends AmObject> action) {
            if (!context.getAmParents().isEmpty()) {
                if (item instanceof CObject || item instanceof CAttribute) {
                    context.setRmPath(context.getParentRmPaths().pollLast());
                }
            }
            return super.postorderVisit(item, context, action);
        }
    }
}
