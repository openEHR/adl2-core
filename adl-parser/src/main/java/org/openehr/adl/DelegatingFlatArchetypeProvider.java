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

package org.openehr.adl;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.UncheckedExecutionException;
import org.openehr.adl.flattener.ArchetypeFlattener;
import org.openehr.jaxb.am.DifferentialArchetype;
import org.openehr.jaxb.am.FlatArchetype;

/**
 * @author Marko Pipan
 */
public class DelegatingFlatArchetypeProvider implements FlatArchetypeProvider {
    private final ArchetypeProvider archetypeProvider;
    private final ArchetypeFlattener flattener;
    private final LoadingCache<String, FlatArchetype> flatArchetypeCache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .build(new CacheLoader<String, FlatArchetype>() {
                @Override
                public FlatArchetype load(String archetypeId) throws Exception {
                    DifferentialArchetype differentialArchetype = getDifferentialArchetype(archetypeId);
                    FlatArchetype flatParent = null;
                    if (differentialArchetype.getParentArchetypeId() != null) {
                        flatParent = getFlatArchetype(differentialArchetype.getParentArchetypeId().getValue());
                    }
                    return flattener.flatten(flatParent, differentialArchetype);
                }
            });

    public DelegatingFlatArchetypeProvider(ArchetypeProvider archetypeProvider, ArchetypeFlattener flattener) {
        this.archetypeProvider = archetypeProvider;
        this.flattener = flattener;
    }

    @Override
    public DifferentialArchetype getDifferentialArchetype(String archetypeId) {
        return archetypeProvider.getDifferentialArchetype(archetypeId);
    }

    @Override
    public FlatArchetype getFlatArchetype(String archetypeId) {
        try {
            return flatArchetypeCache.getUnchecked(archetypeId);
        } catch (UncheckedExecutionException e) {
            if (e.getCause() instanceof RuntimeException) {
                throw (RuntimeException) e.getCause();
            }
            throw e;
        }
    }
}
