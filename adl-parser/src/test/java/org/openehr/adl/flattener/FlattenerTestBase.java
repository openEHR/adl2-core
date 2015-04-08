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

package org.openehr.adl.flattener;

import org.openehr.adl.ParserTestBase;
import org.openehr.adl.rm.OpenEhrRmModel;
import org.openehr.adl.util.TestAdlParser;
import org.openehr.jaxb.am.DifferentialArchetype;
import org.openehr.jaxb.am.FlatArchetype;
import org.openehr.jaxb.rm.ResourceAnnotationNodeItems;
import org.openehr.jaxb.rm.ResourceAnnotationNodes;

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
    private final ArchetypeFlattener flattener = new ArchetypeFlattener(OpenEhrRmModel.getInstance());

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

    protected Map<String, ResourceAnnotationNodes> annotationSetToMap(List<ResourceAnnotationNodes> items) {
        Map<String, ResourceAnnotationNodes> result = new HashMap<>();
        for (ResourceAnnotationNodes item : items) {
            result.put(item.getLanguage(), item);
        }
        return result;
    }

    protected Map<String, ResourceAnnotationNodeItems> annotationToMap(List<ResourceAnnotationNodeItems> items) {
        Map<String, ResourceAnnotationNodeItems> result = new HashMap<>();
        for (ResourceAnnotationNodeItems item : items) {
            result.put(item.getPath(), item);
        }
        return result;
    }
}
