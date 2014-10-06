/*
 * Copyright (C) 2014 Marand
 */

package com.marand.thinkehr.adl.util;

import com.google.common.base.CaseFormat;
import org.apache.commons.lang.SerializationUtils;
import org.openehr.jaxb.am.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author markopi
 */
public class AdlUtils {
    private static final String AM_PACKAGE_NAME = Archetype.class.getPackage().getName();

    private AdlUtils() {
    }

    public static Class<? extends CObject> getAmClass(@Nonnull String amTypeName) throws ClassNotFoundException {
        int genericsTypeIndex = amTypeName.indexOf('<');
        String name = genericsTypeIndex == -1 ? amTypeName : amTypeName.substring(0, genericsTypeIndex);
        String className = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name);
        //noinspection unchecked
        return (Class<CObject>) Class.forName(AM_PACKAGE_NAME + '.' + className);
    }

    public static <T> Collection<T> emptyIfNull(@Nullable Collection<T> details) {
        if (details == null) return Collections.emptyList();
        return details;
    }

    @Nullable
    public static Float doubleToFloat(@Nullable Double d) {
        return d != null ? d.floatValue() : null;
    }

    public static <T> List<T> front(List<T> list) {
        return list.subList(0, list.size() - 1);
    }

    public static <T> T last(List<T> list) {
        return list.get(list.size() - 1);
    }

    public static <T> T head(List<T> list) {
        return list.get(0);
    }

    public static <T> List<T> tail(List<T> list) {
        return list.subList(1, list.size());
    }

    /**
     * Creates a clone using java serialization
     *
     * @param from Object to be cloned
     * @param <T>  type of the cloned object
     * @return Clone of the object
     */
    @SuppressWarnings("unchecked")
    public static <T extends Serializable> T makeClone(T from) {
        return (T) SerializationUtils.clone(from);
    }


    public static FlatArchetype createFlatArchetypeClone(DifferentialArchetype source) {
        FlatArchetype result = new FlatArchetype();
        fillArchetypeFields(result, source);
        return makeClone(result);
    }

    public static Template createTemplateClone(FlatArchetype source) {
        Template result = new Template();
        fillArchetypeFields(result, source);

        return makeClone(result);
    }


    private static void fillArchetypeFields(FlatArchetype target, Archetype source) {
        target.setDefinition(source.getDefinition());
        target.setIsControlled(source.isIsControlled());
        target.setArchetypeId(source.getArchetypeId());
        target.setUid(source.getUid());
        target.setAdlVersion(source.getAdlVersion());
        target.setConcept(source.getConcept());
        target.setDescription(source.getDescription());
        target.setOntology(source.getOntology());
        target.setOriginalLanguage(source.getOriginalLanguage());
        target.setParentArchetypeId(source.getParentArchetypeId());
        target.setRevisionHistory(source.getRevisionHistory());

        target.getAnnotations().addAll(source.getAnnotations());
        target.getInvariants().addAll(source.getInvariants());
        target.getTranslations().addAll(source.getTranslations());
        target.setIsOverlay(source.isIsOverlay());
        target.setIsTemplate(source.isIsTemplate());
    }


    public static String getRmTypeName(@Nonnull Class<?> clazz) {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, clazz.getSimpleName());
    }
}
