/*
 * Copyright (C) 2014 Marand
 */

package org.openehr.adl;

import org.openehr.jaxb.am.FlatArchetype;

/**
 * @author markopi
 */
public interface ArchetypeProvider {
    FlatArchetype getArchetype(String archetypeId);
}
