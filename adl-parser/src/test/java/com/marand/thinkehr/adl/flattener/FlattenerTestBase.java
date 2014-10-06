/*
 * Copyright (C) 2014 Marand
 */

package com.marand.thinkehr.adl.flattener;

import com.marand.thinkehr.adl.ParserTestBase;
import com.marand.thinkehr.adl.rm.OpenEhrRmModel;
import com.marand.thinkehr.adl.util.TestAdlParser;
import org.openehr.jaxb.am.DifferentialArchetype;
import org.openehr.jaxb.am.FlatArchetype;
import org.openehr.jaxb.rm.Annotation;
import org.openehr.jaxb.rm.AnnotationSet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author markopi
 */
abstract public class FlattenerTestBase extends ParserTestBase {
    private static final Pattern CLASSPATH_PATTERN = Pattern.compile("(.*/)[^/]+(\\.adls?)");
    private final ArchetypeFlattener flattener = new ArchetypeFlattener(new OpenEhrRmModel());

    protected FlatArchetype parseAndFlattenArchetype(String classpathResource) {
        DifferentialArchetype differential = TestAdlParser.parseAdl(classpathResource);
        FlatArchetype parent;
        if (differential.getParentArchetypeId() != null) {
            Matcher m = CLASSPATH_PATTERN.matcher(classpathResource);
            m.find();
            // parent archetype must be in the same package
            String parentClasspathResource = m.group(1) + differential.getParentArchetypeId().getValue() + m.group(2);
            parent = parseAndFlattenArchetype(parentClasspathResource);
        } else {
            parent = null;
        }

        return flattener.flatten(parent, differential);
    }

    protected Map<String, AnnotationSet> annotationSetToMap(List<AnnotationSet> items) {
        Map<String, AnnotationSet> result = new HashMap<>();
        for (AnnotationSet item : items) {
            result.put(item.getLanguage(), item);
        }
        return result;
    }

    protected Map<String, Annotation> annotationToMap(List<Annotation> items) {
        Map<String, Annotation> result = new HashMap<>();
        for (Annotation item : items) {
            result.put(item.getPath(), item);
        }
        return result;
    }
}
