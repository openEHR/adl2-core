/*
 * Copyright (C) 2014 Marand
 */

package com.marand.thinkehr.adl.parser.adl15;

import com.marand.thinkehr.adl.ParserTestBase;
import org.openehr.jaxb.am.Archetype;
import org.openehr.jaxb.am.CObject;
import org.testng.annotations.Test;

import java.util.List;

import static com.marand.thinkehr.adl.am.AmObjectFactory.newSiblingOrder;
import static org.fest.assertions.Assertions.assertThat;

/**
 * @author markopi
 */
public class SpecializedOrderingTest extends ParserTestBase {


    @Test
    public void testOrder() throws Exception {
        Archetype archetype = parseArchetype("adl15/ordering/openehr-ehr-OBSERVATION.ordered-specialized.v1.adls");

        List<CObject> objects = archetype.getDefinition().getAttributes().get(0).getChildren();

        assertThat(objects).hasSize(7);
        assertCObject(objects.get(0), "POINT_EVENT", "at0.1", MANDATORY, newSiblingOrder(false, "at0002"));
        assertCObject(objects.get(1), "POINT_EVENT", "at0.2", MANDATORY, newSiblingOrder(false, "at0003"));
        assertCObject(objects.get(2), "POINT_EVENT", "at0.3", MANDATORY, newSiblingOrder(true, "at0004"));
        assertCObject(objects.get(3), "POINT_EVENT", "at0.4", MANDATORY, newSiblingOrder(false, "at0003"));
        assertCObject(objects.get(4), "POINT_EVENT", "at0.5", MANDATORY, newSiblingOrder(true, "at0003"));
        assertCObject(objects.get(5), "POINT_EVENT", "at0.6", MANDATORY, newSiblingOrder(false, "at0004"));
        assertCObject(objects.get(6), "POINT_EVENT", "at0.7", MANDATORY, newSiblingOrder(true, "at0002"));
    }

}
