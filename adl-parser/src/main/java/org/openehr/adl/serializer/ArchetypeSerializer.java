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

package org.openehr.adl.serializer;

import org.openehr.adl.serializer.constraints.*;
import org.openehr.jaxb.am.*;

import java.util.HashMap;
import java.util.Map;

import static org.openehr.adl.serializer.ArchetypeSerializeUtils.buildOccurrences;

/**
 * @author Marko Pipan
 */
public class ArchetypeSerializer {
    private final AdlStringBuilder builder;
    private final Archetype archetype;
    private final ArchetypeMapHolder archetypeMapHolder;
    private final String defaultLanguage;
    private final Map<Class, ConstraintSerializer> constraintSerializers;


    private ArchetypeSerializer(Archetype archetype) {
        this.builder = new AdlStringBuilder();
        this.archetype = archetype;
        this.archetypeMapHolder = new ArchetypeMapHolder(archetype);
        this.defaultLanguage = archetype.getOriginalLanguage().getCodeString();


        constraintSerializers = new HashMap<>();
        constraintSerializers.put(CString.class, new CStringSerializer(this));
        constraintSerializers.put(CReal.class, new CRealSerializer(this));
        constraintSerializers.put(CComplexObject.class, new CComplexObjectSerializer(this));
        constraintSerializers.put(CArchetypeRoot.class, new CArchetypeRootSerializer(this));
        constraintSerializers.put(CTerminologyCode.class, new CTerminologyCodeSerializer(this));
        constraintSerializers.put(CDateTime.class, new CDateTimeSerializer(this));
    }


    public static String serialize(Archetype archetype) {
        return new ArchetypeSerializer(archetype).serialize();
    }

    private String serialize() {
        if (archetype.isIsTemplate()) {
            builder.append("template");
        } else {
            if (archetype.isIsOverlay()) {
                builder.append("archetype_overlay");
            } else {
                builder.append("archetype");
            }
        }

        if (archetype.getAdlVersion() != null) {
            builder.append(" (adl_version=").append(archetype.getAdlVersion()).append(")");
        }
        builder.newIndentedline().append(archetype.getArchetypeId().getValue()).unindent().newline();

        if (archetype.getConcept() != null) {
            String comment = archetypeMapHolder.getTermText(defaultLanguage, archetype.getConcept());
            builder.newline().append("concept").newIndentedline()
                    .append("[").append(archetype.getConcept()).append("]").lineComment(comment)
                    .unindent().newline();
        }

        if (archetype.getOriginalLanguage() != null) {
            builder.newline().append("language").newIndentedline();
            builder.append("original_language = ").dadl(archetype.getOriginalLanguage()).newline();
            if (!archetype.getTranslations().isEmpty()) {
                builder.append("translations = ").dadl(archetype.getTranslations()).newline();
            }
            builder.unindent();
        }

        if (archetype.getDescription() != null) {
            builder.newline().append("description");
            builder.dadlBean(archetype.getDescription());
        }

        if (archetype.getDefinition() != null) {
            builder.newline().append("definition");
            buildCObject(archetype.getDefinition());
            builder.newline();
        }

        if (archetype.getOntology()!=null) {
            builder.newline().append("terminology").dadlBean(archetype.getOntology());
        }

        return builder.toString();
    }



    public void buildCObject(CObject cobj) {
        ConstraintSerializer serializer = constraintSerializers.get(cobj.getClass());
        if (serializer!=null) {
            serializer.serialize(cobj);
        } else {
            throw new AssertionError("Unsupported constraint: " + cobj.getClass().getName());
        }
    }


    public String getSimpleCommentText(CObject cobj) {
        ConstraintSerializer serializer = constraintSerializers.get(cobj.getClass());
        if (serializer==null) return null;
        return serializer.getSimpleCommentText(cobj);
    }

    public AdlStringBuilder getBuilder() {
        return builder;
    }

    public Archetype getArchetype() {
        return archetype;
    }

    public ArchetypeMapHolder getArchetypeMapHolder() {
        return archetypeMapHolder;
    }

    public String getDefaultLanguage() {
        return defaultLanguage;
    }
}
