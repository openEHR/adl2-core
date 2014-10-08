/*
 * Copyright (C) 2014 Marand
 */

package org.openehr.adl;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.openehr.adl.flattener.ArchetypeFlattener;
import org.openehr.adl.rm.OpenEhrRmModel;
import org.openehr.adl.util.TestAdlParser;
import org.apache.commons.io.FilenameUtils;
import org.openehr.jaxb.am.DifferentialArchetype;
import org.openehr.jaxb.am.FlatArchetype;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkState;

/**
 * @author markopi
 */
public class TestingArchetypeProvider implements ArchetypeProvider {
    private final Map<String, String> archetypeIdToClasspathMap;
    private final LoadingCache<String, FlatArchetype> archetypeIdToArchetypeMap = CacheBuilder.newBuilder()
            .build(new CacheLoader<String, FlatArchetype>() {
                @Override
                public FlatArchetype load(String archetypeId) throws Exception {
                    return parseAndFlattenArchetype(archetypeId);
                }
            });
    private final ArchetypeFlattener flattener;


    public TestingArchetypeProvider(String baseClasspath) throws IOException {
        archetypeIdToClasspathMap = buildArchetypeIdToClasspathMap(baseClasspath);
        flattener = new ArchetypeFlattener(new OpenEhrRmModel());

    }

    private Map<String, String> buildArchetypeIdToClasspathMap(final String classpath) throws IOException {
        final Map<String, String> result = new HashMap<>();
        String baseFile = getClass().getClassLoader().getResource(classpath).getFile();
        if (baseFile.contains(":") && baseFile.startsWith("/")) baseFile = baseFile.substring(1);
        final Path basePath = Paths.get(baseFile);
        Files.walkFileTree(basePath, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                String filename = file.getFileName().toString();
                String ext = FilenameUtils.getExtension(filename);
                if (ext.equalsIgnoreCase("adls") || ext.equalsIgnoreCase("adl")) {
                    String archetypeId = FilenameUtils.getBaseName(filename);
                    String relativePath = basePath.relativize(file).toString();
                    result.put(archetypeId, Paths.get(classpath).resolve(relativePath).toString());
                }
                return super.visitFile(file, attrs);
            }
        });

        return result;
    }


    private FlatArchetype parseAndFlattenArchetype(String archetypeId) {
        String classpath = archetypeIdToClasspathMap.get(archetypeId);
        checkState(classpath != null, "No archetype with archetypeId=%s", archetypeId);
        DifferentialArchetype differential = TestAdlParser.parseAdl(classpath);
        FlatArchetype parent;
        if (differential.getParentArchetypeId() != null) {
            parent = getArchetype(differential.getParentArchetypeId().getValue());
        } else {
            parent = null;
        }

        return flattener.flatten(parent, differential);
    }


    @Override
    public FlatArchetype getArchetype(String archetypeId) {
        return archetypeIdToArchetypeMap.getUnchecked(archetypeId);
    }
}
