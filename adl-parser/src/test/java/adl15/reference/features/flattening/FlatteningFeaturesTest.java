/*
 * Copyright (C) 2014 Marand
 */

package adl15.reference.features.flattening;

import com.marand.thinkehr.adl.am.AmQuery;
import com.marand.thinkehr.adl.flattener.ArchetypeFlattener;
import com.marand.thinkehr.adl.rm.OpenEhrRmModel;
import com.marand.thinkehr.adl.util.TestAdlParser;
import org.openehr.jaxb.am.CTerminologyCode;
import org.openehr.jaxb.am.DifferentialArchetype;
import org.openehr.jaxb.am.FlatArchetype;
import org.testng.annotations.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Marko Pipan
 */
public class FlatteningFeaturesTest {
    @Test
    public void testFlattening() {
        DifferentialArchetype parent = TestAdlParser.parseAdl("adl15/reference/features/flattening/openEHR-EHR-OBSERVATION.flattening_parent_1.v1.adls");
        DifferentialArchetype specialized = TestAdlParser.parseAdl("adl15/reference/features/flattening/openEHR-EHR-OBSERVATION.override_to_multiple.v1.adls");

        ArchetypeFlattener flattener = new ArchetypeFlattener(new OpenEhrRmModel());

        FlatArchetype flatParent = flattener.flatten(null, parent);
        FlatArchetype flattened = flattener.flatten(flatParent, specialized);

        CTerminologyCode cr = AmQuery.get(flattened, "/data[id2]/events[id3]/data[id4]/items[id5]/value/defining_code");
        assertThat(cr.getCodeList().get(0)).isEqualTo("at0.2");
    }
}
