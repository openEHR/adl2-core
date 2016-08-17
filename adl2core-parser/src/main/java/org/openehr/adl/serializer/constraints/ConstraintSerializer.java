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

package org.openehr.adl.serializer.constraints;

import org.openehr.adl.serializer.AdlStringBuilder;
import org.openehr.adl.serializer.ArchetypeSerializer;
import org.openehr.jaxb.am.CObject;

/**
 * @author Marko Pipan
 */
abstract public class ConstraintSerializer<T extends CObject> {
    protected final ArchetypeSerializer serializer;
    protected final AdlStringBuilder builder;

    public ConstraintSerializer(ArchetypeSerializer serializer) {
        this.serializer=serializer;
        this.builder = serializer.getBuilder();
    }

    abstract public void serialize(T cobj);

    public String getSimpleCommentText(T cobj) {
        return null;
    }

    public boolean isEmpty(T cobj) {
        return false;
    }

    public int mark() {
        return builder.mark();
    }

    public void revert(int previousMark) {
        builder.revert(previousMark);
    }
}
