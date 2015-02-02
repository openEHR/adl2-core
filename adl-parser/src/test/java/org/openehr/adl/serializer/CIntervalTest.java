package org.openehr.adl.serializer;

import org.openehr.adl.util.TestAdlParser;
import org.openehr.jaxb.am.Archetype;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import static org.fest.assertions.Assertions.assertThat;

/**
 * Test of Interval serialize
 */
public class CIntervalTest extends  CSerializer {


    @Test
    public void testSerializeIntervalOfQuantity(){
        Archetype archetype = TestAdlParser.parseAdl("adl14/openEHR-EHR-ELEMENT.test_interval_quantity.v1.adl");
    assertThat(archetype).isNotNull();

        String adl = ArchetypeSerializer.serialize(archetype);
        assertThat(adl).isNotEmpty();

        Archetype result = TestAdlParser.parseAdlFromString(adl);
        assertThat(result).isNotNull();
        write(adl, "openEHR-EHR-ELEMENT.test_interval_quantity.v1.adl");
    }

}
