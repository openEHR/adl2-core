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

/**
 * @author markopi
 */
public class AmVisitors {
    public static <T extends AmObject, C extends AmVisitContext> AmVisitor<T, C> preorder(AmSinglePhaseVisitor<T, C> visitor,
            Class<? extends T> onlyFor) {
        AmVisitor<T, C> adapter = preorder(visitor);
        DispatchingAmVisitor<C> dispatcher = new DispatchingAmVisitor<C>().add(onlyFor, adapter);
        //noinspection unchecked
        return (AmVisitor<T, C>) dispatcher;
    }

    public static <T extends AmObject, C extends AmVisitContext> AmVisitor<T, C> preorder(AmSinglePhaseVisitor<T, C> visitor) {
        return new AmSinglePhaseVisitorAdapter<>(true, visitor);
    }

    public static <T extends AmObject, C extends AmVisitContext> AmVisitor<T, C> postorder(AmSinglePhaseVisitor<T, C> visitor) {
        return new AmSinglePhaseVisitorAdapter<>(false, visitor);
    }
}
