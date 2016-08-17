/*
 * ADL Designer
 * Copyright (c) 2013-2014 Marand d.o.o. (www.marand.com)
 *
 * This file is part of ADL2-tools.
 *
 * ADL2-tools is free software: you can redistribute it and/or modify
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

package org.openehr.adl.template;

import org.openehr.adl.FlatArchetypeProvider;
import org.openehr.adl.flattener.ArchetypeFlattener;
import org.openehr.adl.rm.RmModel;
import org.openehr.jaxb.am.Archetype;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Marko Pipan
 */
public class FlatArchetypeProviderOverlay implements FlatArchetypeProvider {
    private final FlatArchetypeProvider delegate;
    private final Map<String, Archetype> overlayArchetypeMap;
    private final ArchetypeFlattener flattener;


    public FlatArchetypeProviderOverlay(FlatArchetypeProvider delegate, List<Archetype> archetypes) {
        this.delegate = delegate;
        flattener = new ArchetypeFlattener();

        overlayArchetypeMap = new LinkedHashMap<>();
        for (Archetype archetype : archetypes) {
            overlayArchetypeMap.put(archetype.getArchetypeId().getValue(), archetype);
        }
    }


    @Override
    public Archetype getDifferentialArchetype(String archetypeId) {
        Archetype archetype = overlayArchetypeMap.get(archetypeId);
        if (archetype != null) return archetype;
        return delegate.getDifferentialArchetype(archetypeId);
    }

    @Override
    public Archetype getFlatArchetype(String archetypeId) {
        Archetype source = getDifferentialArchetype(archetypeId);
        Archetype parent = null;
        if (source.getParentArchetypeId() != null && source.getParentArchetypeId().getValue() != null) {
            parent = getFlatArchetype(source.getParentArchetypeId().getValue());
        }
        return flattener.flatten(parent, source);
    }
}
