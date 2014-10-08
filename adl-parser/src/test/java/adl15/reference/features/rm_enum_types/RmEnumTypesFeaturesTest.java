/*
 * Copyright (C) 2014 Marand
 */

package adl15.reference.features.rm_enum_types;

import org.openehr.adl.am.AmQuery;
import org.openehr.adl.util.TestAdlParser;
import org.openehr.jaxb.am.CComplexObject;
import org.openehr.jaxb.am.CInteger;
import org.openehr.jaxb.am.CObject;
import org.openehr.jaxb.am.DifferentialArchetype;
import org.testng.annotations.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Marko Pipan
 */
public class RmEnumTypesFeaturesTest {
    @Test
    public void testEnumType() {
        DifferentialArchetype sourceArchetype = TestAdlParser.parseAdl(
                "adl15/reference/features/rm_enum_types/openEHR-EHR-OBSERVATION.enum_type_1.v1.adls");

        CComplexObject c = AmQuery.find(sourceArchetype, "data[id2]/events[id3]/data[id4]");
        CObject c1 = AmQuery.find(c, "items[id5]/value");
        CObject c2 = AmQuery.find(c, "items[id7]/value");

        assertThat(((CInteger)AmQuery.find(c1, "type")).getList()).containsExactly(1);
        assertThat(((CInteger)AmQuery.find(c2, "type")).getList()).containsExactly(2,3);
    }

}
