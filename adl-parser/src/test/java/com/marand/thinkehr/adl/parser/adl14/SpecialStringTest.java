/*
 * Copyright (C) 2014 Marand
 */

package com.marand.thinkehr.adl.parser.adl14;

import com.marand.thinkehr.adl.ParserTestBase;
import org.openehr.jaxb.am.CAttribute;
import org.openehr.jaxb.am.CComplexObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;


public class SpecialStringTest extends ParserTestBase {
// ------------------------------ FIELDS ------------------------------

    private List attributeList;
    private List list;

// -------------------------- PUBLIC METHODS --------------------------

    @BeforeClass
    public void setUp() throws Exception {
        attributeList = parseArchetype("adl14/adl-test-entry.special_string.test.adl").getDefinition().getAttributes();
    }

    @Test
    public void testParseEscapedBackslash() throws Exception {
        list = getConstraints(0);
        assertCString(list.get(1), null, new String[]{"any\\thing"}, null);
    }

    @Test
    public void testParseEscapedDoubleQuote() throws Exception {
        list = getConstraints(0);
        assertCString(list.get(0), null, new String[]{"some\"thing"}, null);
    }

// -------------------------- OTHER METHODS --------------------------

    private List getConstraints(int index) {
        CAttribute ca = (CAttribute) attributeList.get(index);
        return ((CComplexObject) ca.getChildren().get(0)).getAttributes();
    }
}
