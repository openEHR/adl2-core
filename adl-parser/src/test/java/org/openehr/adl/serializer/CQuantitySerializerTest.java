package org.openehr.adl.serializer;

import org.openehr.adl.util.TestAdlParser;
import org.openehr.jaxb.am.Archetype;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import static org.fest.assertions.Assertions.assertThat;

/**
 * Created by bna on 01.02.2015.
 */
public class CQuantitySerializerTest extends  CSerializer {



    @Test
    public void testParseQuantity(){
        Archetype archetype = TestAdlParser.parseAdl("adl14/openEHR-EHR-ELEMENT.test_quantity.v1.adl");
        assertThat(archetype).isNotNull();

        String adl = ArchetypeSerializer.serialize(archetype);
        assertThat(adl).isNotNull();
        assertThat(adl).isNotEmpty();
        write(adl, "openEHR-EHR-ELEMENT.test_quantity.v1.adl");

        Archetype result = TestAdlParser.parseAdlFromString(adl);
        assertThat(result).isNotNull();
    }
}
