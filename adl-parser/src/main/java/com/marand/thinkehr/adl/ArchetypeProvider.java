/*
 * Copyright (C) 2014 Marand
 */

package com.marand.thinkehr.adl;

import org.openehr.jaxb.am.FlatArchetype;

/**
 * @author markopi
 */
public interface ArchetypeProvider {
    FlatArchetype getArchetype(String archetypeId);
}
