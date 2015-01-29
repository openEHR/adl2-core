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
import org.openehr.adl.util.ArchetypeWrapper;
import org.openehr.jaxb.am.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Marko Pipan
 */
public class ArchetypeSerializer {
    private final AdlStringBuilder builder;
    private final Archetype archetype;
    private final ArchetypeWrapper archetypeWrapper;
    private final String defaultLanguage;
    private final Map<Class, ConstraintSerializer> constraintSerializers;


    private ArchetypeSerializer(Archetype archetype) {
        this.builder = new AdlStringBuilder();
        this.archetype = archetype;
        this.archetypeWrapper = new ArchetypeWrapper(archetype);
        this.defaultLanguage = archetype.getOriginalLanguage().getCodeString();


        constraintSerializers = new HashMap<>();
        constraintSerializers.put(CString.class, new CStringSerializer(this));
        constraintSerializers.put(CReal.class, new CRealSerializer(this));
        constraintSerializers.put(CInteger.class, new CIntegerSerializer(this));
        constraintSerializers.put(CComplexObject.class, new CComplexObjectSerializer(this));
        constraintSerializers.put(CArchetypeRoot.class, new CArchetypeRootSerializer(this));
        constraintSerializers.put(CTerminologyCode.class, new CTerminologyCodeSerializer(this));
        constraintSerializers.put(CDateTime.class, new CDateTimeSerializer(this));
        constraintSerializers.put(CDate.class, new CDateSerializer(this));
        constraintSerializers.put(CBoolean.class, new CBooleanSerializer(this));
        constraintSerializers.put(CDuration.class, new CDurationSerializer(this));
        constraintSerializers.put(CTime.class, new CTimeSerializer(this));
        constraintSerializers.put(CDvQuantity.class, new CDvQuantitySerializer(this));
        constraintSerializers.put(CCodePhrase.class, new CCodePhraseSerializer (this));
constraintSerializers.put(CDvOrdinal.class, new CDvOrdinalSerializer(this));
        constraintSerializers.put(ArchetypeSlot.class, new ArchetypeSlotSerializer(this
        ));
        constraintSerializers.put(ArchetypeInternalRef.class, new ArchetypeInternalRefSerializer(this));
    }


    public static String serialize(Archetype archetype) {
        return new ArchetypeSerializer(archetype).serialize();
    }

    private String serialize() {
        if (archetype.isIsTemplate()) {
            builder.append("template");
        } else {
            if (archetype.isIsOverlay()) {
                builder.append("template_overlay");
            } else {
                builder.append("archetype");
            }
        }

        if (archetype.getAdlVersion() != null) {
            builder.append(" (adl_version=").append(archetype.getAdlVersion()).append(")");
        }
        builder.newIndentedline().append(archetype.getArchetypeId().getValue()).unindent().newline();

        if (archetype.getParentArchetypeId()!=null) {
            builder.newline().append("specialize").newIndentedline()
                    .append(archetype.getParentArchetypeId().getValue())
                    .unindent().newline();
        }

        if (archetype.getConcept() != null) {
            String comment = archetypeWrapper.getTermText(defaultLanguage, archetype.getConcept());
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
            builder.newline();
        }

        if ((archetype.getAnnotations()!=null && !archetype.getAnnotations().getItems().isEmpty())) {
            builder.newline().append("annotations");
            builder.dadlBean(archetype.getAnnotations());
            builder.newline();
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

    public ArchetypeWrapper getArchetypeWrapper() {
        return archetypeWrapper;
    }

    public String getDefaultLanguage() {
        return defaultLanguage;
    }
}
