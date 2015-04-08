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

package adl15.reference.features.specialisation;

import org.openehr.adl.flattener.ArchetypeFlattener;
import org.openehr.adl.rm.OpenEhrRmModel;
import org.openehr.adl.util.TestAdlParser;
import org.openehr.jaxb.am.DifferentialArchetype;
import org.openehr.jaxb.am.FlatArchetype;

/**
 * @author Marko Pipan
 */
abstract public class AbstractSpecializationTest {
    private static final String RESOURCE_PATTERN = "adl15/reference/features/specialisation/$.adls";

    protected ArchetypeFlattener FLATTENER = new ArchetypeFlattener(OpenEhrRmModel.getInstance());

    private DifferentialArchetype parse(String archetypeId) {
        String classpathResource = RESOURCE_PATTERN.replaceAll("\\$", archetypeId);
        return TestAdlParser.parseAdl(classpathResource);
    }

    protected FlatArchetype getArchetype(String archetypeId) {
        DifferentialArchetype source = parse(archetypeId);
        FlatArchetype parent = null;
        if (source.getParentArchetypeId() != null) {
            parent = getArchetype(source.getParentArchetypeId().getValue());
        }
        return FLATTENER.flatten(parent, source);
    }

}
