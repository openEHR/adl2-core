package org.openehr.adl.serializer;

import com.google.common.collect.ImmutableList;
import org.openehr.jaxb.am.Archetype;
import org.openehr.jaxb.am.ArchetypeTerminology;
import org.openehr.jaxb.am.CObject;
import org.openehr.jaxb.rm.ResourceDescription;
import org.openehr.jaxb.rm.ResourceDescriptionItem;
import org.testng.annotations.Test;

import static org.openehr.adl.am.AmObjectFactory.*;
import static org.openehr.adl.rm.RmObjectFactory.*;
import static org.openehr.adl.serializer.ArchetypeSerializerTest.write;

/**
 * @author markopi
 */
public class ObjectArchetypeSerializerTest {
    @Test
    public void serializeObject() {
        Archetype archetype = new Archetype();
        archetype.setArchetypeId(newArchetypeId("openEHR-EHR-CLUSTER.test_serialization_from_objects.v1"));
        archetype.setRmRelease("1.0.2");
        archetype.setAdlVersion("2.0.5");

        archetype.setDescription(new ResourceDescription());
        archetype.getDescription().setLifecycleState("unmanaged");
        archetype.getDescription().setCopyright("2015");
        archetype.getDescription().getOriginalAuthor().addAll(ImmutableList.of(
                newStringDictionaryItem("organisation", "openehr"),
                newStringDictionaryItem("name", "tester")
        ));
        ResourceDescriptionItem rdi = new ResourceDescriptionItem();
        rdi.setLanguage(newCodePhrase(newTerminologyId("ISO_639-1"), "en"));
        rdi.setMisuse("anything but testing");
        rdi.setUse("testing");
        rdi.setPurpose("testing");
        archetype.getDescription().getDetails().add(rdi);
        archetype.setOriginalLanguage(newCodePhrase(newTerminologyId("ISO_639-1"), "en"));

        archetype.setDefinition(newCComplexObject("CLUSTER", null, "id1", ImmutableList.of(
                newCAttribute("items", null, null, ImmutableList.<CObject>of(
                        newCComplexObject("ELEMENT", null, "id2", ImmutableList.of(
                                newCAttribute("value", null, null, ImmutableList.<CObject>of(
                                        newCComplexObject("DV_CODED_TEXT", null, "id3", ImmutableList.of(
                                                newCAttribute("defining_code", null, null, ImmutableList.<CObject>of(
                                                        newCTerminologyCode("ac1", "at1")
                                                ))
                                        )),
                                        newCComplexObject("DV_DURATION", null, "id4", ImmutableList.of(
                                                newCAttribute("value", null, null, ImmutableList.<CObject>of(
                                                        newCDuration("PT5M", null, null)
                                                ))
                                        ))
                                ))
                        ))
                ))
        )));



        archetype.setTerminology(new ArchetypeTerminology());
        // add definition of value sets
        archetype.getTerminology().getValueSets().add(
                newValueSetItem("ac1", ImmutableList.of("at1", "at2", "at3"))
        );


        // add definitions for all used terms
        archetype.getTerminology().getTermDefinitions().addAll(ImmutableList.of(
                newCodeDefinitionSet("en", ImmutableList.of(
                        // nodes
                        newArchetypeTerm("id1", "Test Serialization"),
                        newArchetypeTerm("id2", "Choice"),
                        newArchetypeTerm("id3", "Choice - DvCodedText"),
                        newArchetypeTerm("id4", "Choice - DvDuration"),
                        // value sets
                        newArchetypeTerm("ac1", "Test value set"),
                        // terms
                        newArchetypeTerm("at1", "First member of test value set"),
                        newArchetypeTerm("at2", "Second member of test value set"),
                        newArchetypeTerm("at3", "Third member of test value set")
                ))
        ));

        String serialized = ArchetypeSerializer.serialize(archetype);
        write(serialized, "openEHR-EHR-CLUSTER.test_serialization_from_objects.v1.adls");
    }
}
