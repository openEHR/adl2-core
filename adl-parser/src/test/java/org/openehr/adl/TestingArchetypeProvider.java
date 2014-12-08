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
        DifferentialArchetype differential = getDifferentialArchetype(archetypeId);
        FlatArchetype parent;
        if (differential.getParentArchetypeId() != null) {
            parent = getArchetype(differential.getParentArchetypeId().getValue());
        } else {
            parent = null;
        }

        return flattener.flatten(parent, differential);
    }


    @Override
    public DifferentialArchetype getDifferentialArchetype(String archetypeId) {
        String classpath = archetypeIdToClasspathMap.get(archetypeId);
        checkState(classpath != null, "No archetype with archetypeId=%s", archetypeId);
        return TestAdlParser.parseAdl(classpath);
    }

    @Override
    public FlatArchetype getArchetype(String archetypeId) {
        return archetypeIdToArchetypeMap.getUnchecked(archetypeId);
    }
}
