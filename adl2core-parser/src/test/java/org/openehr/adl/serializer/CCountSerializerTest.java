package org.openehr.adl.serializer;

import org.openehr.adl.util.TestAdlParser;
import org.openehr.jaxb.am.Archetype;
import org.testng.annotations.Test;

/**
 * Created by Bjørn on 01.02.2015.
 */
public class CCountSerializerTest extends  CSerializer{
    
    @Test
    public void testSerializeCountWithLowerOnly(){
        Archetype archetype = TestAdlParser.parseAdl("adl14/openEHR-EHR-ELEMENT.test_count_min_value.v1.adl");
        String adl = ArchetypeSerializer.serialize(archetype);
        write(adl, "openEHR-EHR-ELEMENT.test_count_min_value.v1.adl");
        Archetype resultArchetype = TestAdlParser.parseAdlFromString(adl);
        
    }
}
