package org.openehr.adl.template;

import org.openehr.adl.FlatArchetypeProvider;
import org.openehr.adl.TestingArchetypeProvider;
import org.openehr.jaxb.am.OperationalTemplate;
import org.openehr.jaxb.am.Template;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * @author markopi
 */
public class OperationalTemplateBuilderTest {

    @Test
    public void testBuild() throws Exception {
        FlatArchetypeProvider flatArchetypeProvider = new TestingArchetypeProvider("adl15/template");
        Template template = TemplateDeserializer.deserialize(getClass().getClassLoader().getResourceAsStream(
                "adl15/template/openEHR-EHR-COMPOSITION.mini_vitals.v1.adlt"));

        OperationalTemplate opt = OperationalTemplateBuilder.build(flatArchetypeProvider, template);
    }
}