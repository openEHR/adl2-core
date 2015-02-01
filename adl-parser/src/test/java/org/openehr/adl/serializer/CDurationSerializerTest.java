package org.openehr.adl.serializer;

import org.openehr.adl.util.TestAdlParser;
import org.openehr.jaxb.am.Archetype;
import org.openehr.jaxb.am.CAttribute;
import org.openehr.jaxb.am.CComplexObject;
import org.testng.annotations.Test;
import static org.fest.assertions.Assertions.*;

/**
 * Created by bna on 01.02.2015.
 */
public class CDurationSerializerTest extends  CSerializer {

    public CDurationSerializerTest(){
        super(false);
    }

    @Test
    public void testSerializeDuration(){
        Archetype archetype = TestAdlParser.parseAdl("adl14/openEHR-EHR-ELEMENT.test_duration.v1.adl");

        String adl = ArchetypeSerializer.serialize(archetype);
        write(adl, "openEHR-EHR-ELEMENT.test_duration.v1.adl");

        Archetype result = TestAdlParser.parseAdlFromString(adl);
        assertThat(result).isNotNull();


    }
}
