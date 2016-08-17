package org.openehr.adl.serializer;

import static org.fest.assertions.Assertions.*;
import org.openehr.adl.util.TestAdlParser;
import org.openehr.jaxb.am.Archetype;
import org.testng.annotations.Test;

/**
 * Created by bna on 01.02.2015.
 */
public class COrdinalTest extends  CSerializer{

    @Test
    public void testSerializeOrdinal(){
        Archetype archetype = TestAdlParser.parseAdl("adl14/openEHR-EHR-ELEMENT.test_ordinal.v1.adl");
        String adl = ArchetypeSerializer.serialize(archetype);
        write(adl, "openEHR-EHR-ELEMENT.test_ordinal.v1.adl");
        Archetype result = TestAdlParser.parseAdlFromString(adl);
        assertThat(result).isNotNull();

    }
}
