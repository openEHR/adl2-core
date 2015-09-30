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
import org.openehr.jaxb.am.Archetype;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkState;

/**
 * @author markopi
 */
public class TestingArchetypeProvider implements FlatArchetypeProvider {
    private final Map<String, String> archetypeIdToClasspathMap;
    private final LoadingCache<String, Archetype> archetypeIdToArchetypeMap = CacheBuilder.newBuilder()
            .build(new CacheLoader<String, Archetype>() {
                @Override
                public Archetype load(String archetypeId) throws Exception {
                    return parseAndFlattenArchetype(archetypeId);
                }
            });
    private final ArchetypeFlattener flattener;


    public TestingArchetypeProvider(String baseClasspath) throws IOException {
        archetypeIdToClasspathMap = buildArchetypeIdToClasspathMap(baseClasspath);
        flattener = new ArchetypeFlattener(OpenEhrRmModel.getInstance());

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
                String ext = getExtension(filename);
                if (ext.equalsIgnoreCase("adls") || ext.equalsIgnoreCase("adl")) {
                    String archetypeId = getBaseName(filename);
                    String relativePath = basePath.relativize(file).toString();
                    result.put(archetypeId, Paths.get(classpath).resolve(relativePath).toString());
                }
                return super.visitFile(file, attrs);
            }
        });

        return result;
    }

    private String getBaseName(String filename) {
        String onlyFilename = Paths.get(filename).getFileName().toString();
        int lastPeriod = onlyFilename.lastIndexOf('.');
        if (lastPeriod > 0) { // not a bug
            return onlyFilename.substring(0, lastPeriod);
        } else {
            return onlyFilename;
        }
    }

    private String getExtension(String filename) {
        String onlyFilename = Paths.get(filename).getFileName().toString();
        int lastPeriod = onlyFilename.lastIndexOf(".");
        if (lastPeriod > 0) { // not a bug
            return onlyFilename.substring(lastPeriod + 1);
        } else {
            return "";
        }
    }


    private Archetype parseAndFlattenArchetype(String archetypeId) {
        Archetype differential = getDifferentialArchetype(archetypeId);
        Archetype parent;
        if (differential.getParentArchetypeId() != null) {
            parent = getFlatArchetype(differential.getParentArchetypeId().getValue());
        } else {
            parent = null;
        }

        return flattener.flatten(parent, differential);
    }


    @Override
    public Archetype getDifferentialArchetype(String archetypeId) {
        String classpath = archetypeIdToClasspathMap.get(archetypeId);
        checkState(classpath != null, "No archetype with archetypeId=%s", archetypeId);
        return TestAdlParser.parseAdl(classpath);
    }

    @Override
    public Archetype getFlatArchetype(String archetypeId) {
        return archetypeIdToArchetypeMap.getUnchecked(archetypeId);
    }
}
