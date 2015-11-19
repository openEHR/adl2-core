package org.openehr.adl.parser.adl15;

import com.google.common.collect.ImmutableMap;
import org.openehr.adl.ParserTestBase;
import org.openehr.jaxb.am.Archetype;
import org.openehr.jaxb.rm.ResourceDescription;
import org.testng.annotations.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author markopi
 */
public class ResourceDescriptionTest extends ParserTestBase {
    @Test
    public void parseResourceDescription() {
        Archetype archetype = parseArchetype("adl15/openEHR-EHR-COMPOSITION.resource_description.v1.adls");

        ResourceDescription desc = archetype.getDescription();

        assertThat(dictToMap(desc.getOriginalAuthor())).isEqualTo(ImmutableMap.of(
                "name", "Sam Heard",
                "organisation", "Ocean Informatics",
                "email", "sam.heard@oceaninformatics.com",
                "date", "23/03/2006"
        ));


        assertThat(desc.getLifecycleState()).isEqualTo("AuthorDraft");
        assertThat(desc.getOriginalNamespace()).isEqualTo("Original Namespace");
        assertThat(desc.getOriginalPublisher()).isEqualTo("Original Publisher");
        assertThat(desc.getCopyright()).isEqualTo("copyright");
        assertThat(desc.getLicence()).isEqualTo("licence");
        assertThat(desc.getCustodianNamespace()).isEqualTo("Custodian Namespace");
        assertThat(desc.getCustodianOrganisation()).isEqualTo("Custodian Organisation");
        assertThat(dictToMap(desc.getReferences())).isEqualTo(ImmutableMap.of(
                "one", "first",
                "two", "second"));

    }

}
