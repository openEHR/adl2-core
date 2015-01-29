package org.openehr.adl.flattener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import org.openehr.adl.parser.AdlDeserializer;
import org.openehr.adl.parser.BomSupportingReader;
import org.openehr.adl.rm.OpenEhrRmModel;
import org.openehr.jaxb.am.DifferentialArchetype;
import org.openehr.jaxb.am.FlatArchetype;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Created by Bjørn on 16/01/2015.
 */
public class ParentChildWithChoiceElementTest extends FlattenerTestBase{
    private AdlDeserializer deserializer;
    private ArchetypeFlattener flattener;
    @BeforeClass
    public void init(){

        final OpenEhrRmModel rmModel = new OpenEhrRmModel();
        deserializer = new AdlDeserializer(rmModel);
        flattener = new ArchetypeFlattener(rmModel);
    }

    protected String readArchetype(String file) throws IOException {
        return CharStreams.toString(new BomSupportingReader(
                getClass().getClassLoader().getResourceAsStream(file),
                Charsets.UTF_8));
    }



    private final String clusterAmount = "adl14/openEHR-EHR-CLUSTER.amount.v1.adl";
    private final String clusterAmountRange = "adl14/openEHR-EHR-CLUSTER.amount-range.v1.adl";
    @Test
    public void testFlattenClusterAmountRangeWithClusterAmount() throws IOException {
        DifferentialArchetype parent = deserializer.parse(readArchetype(clusterAmount));
        FlatArchetype flatParent = flattener.flatten(null, parent);
        assertThat(flatParent).isNotNull();

        DifferentialArchetype child = deserializer.parse(readArchetype(clusterAmountRange));
        FlatArchetype flatChild = flattener.flatten(flatParent, child);
        assertThat(flatChild).isNotNull();


    }
}
