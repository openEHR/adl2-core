/*
 * Copyright (C) 2014 Marand
 */

package adl15.reference.features.specialisation;

import com.marand.thinkehr.adl.flattener.ArchetypeFlattener;
import com.marand.thinkehr.adl.rm.OpenEhrRmModel;
import com.marand.thinkehr.adl.util.TestAdlParser;
import org.openehr.jaxb.am.DifferentialArchetype;
import org.openehr.jaxb.am.FlatArchetype;

/**
 * @author Marko Pipan
 */
abstract public class AbstractSpecializationTest {
    private static final String RESOURCE_PATTERN = "adl15/reference/features/specialisation/$.adls";

    protected ArchetypeFlattener FLATTENER = new ArchetypeFlattener(new OpenEhrRmModel());

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
