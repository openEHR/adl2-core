/*
 * Copyright (C) 2014 Marand
 */

package adl15.reference.features.uid_metadata;

import org.openehr.adl.flattener.ArchetypeFlattener;
import org.openehr.adl.rm.OpenEhrRmModel;
import org.openehr.adl.util.TestAdlParser;
import org.openehr.jaxb.am.DifferentialArchetype;
import org.openehr.jaxb.am.FlatArchetype;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Marko Pipan
 */
public class MetadataFeaturesTest {
    private ArchetypeFlattener flattener;

    @BeforeClass
    public void init() {
        flattener = new ArchetypeFlattener(new OpenEhrRmModel());
    }

    @Test
    public void testUidAndOtherMetadata() {
        DifferentialArchetype parent = TestAdlParser.parseAdl(
                "adl15/reference/features/uid_metadata/openEHR-TEST_PKG-WHOLE.parent_with_uid_and_other_metadata.v1.adls");

        DifferentialArchetype child = TestAdlParser.parseAdl(
                "adl15/reference/features/uid_metadata/openEHR-TEST_PKG-WHOLE.child_with_uid_and_other_metadata.v1.adls");

        FlatArchetype flatParent = flattener.flatten(null, parent);
        FlatArchetype flatChild = flattener.flatten(flatParent, child);

        assertThat(flatChild.getUid().getValue()).isEqualTo("15E82D77-7DB7-4F70-8D8E-EED6FF241B2D");
    }

    @Test
    public void testOid() {
        DifferentialArchetype parent = TestAdlParser.parseAdl(
                "adl15/reference/features/uid_metadata/openEHR-TEST_PKG-WHOLE.parent_with_oid.v1.adls");

        DifferentialArchetype child = TestAdlParser.parseAdl(
                "adl15/reference/features/uid_metadata/openEHR-TEST_PKG-WHOLE.child_with_oid.v1.adls");

        FlatArchetype flatParent = flattener.flatten(null, parent);
        FlatArchetype flatChild = flattener.flatten(flatParent, child);

        assertThat(flatChild.getUid().getValue()).isEqualTo("2.4.34.666.7.2");
    }

    @Test
    public void testUid() {
        DifferentialArchetype parent = TestAdlParser.parseAdl(
                "adl15/reference/features/uid_metadata/openEHR-TEST_PKG-WHOLE.parent_with_uid.v1.adls");

        DifferentialArchetype child = TestAdlParser.parseAdl(
                "adl15/reference/features/uid_metadata/openEHR-TEST_PKG-WHOLE.child_with_uid.v1.adls");

        FlatArchetype flatParent = flattener.flatten(null, parent);
        FlatArchetype flatChild = flattener.flatten(flatParent, child);

        assertThat(flatChild.getUid().getValue()).isEqualTo("A22E8ED5-81B9-46CC-AFF1-612F428F5447");
    }

}
