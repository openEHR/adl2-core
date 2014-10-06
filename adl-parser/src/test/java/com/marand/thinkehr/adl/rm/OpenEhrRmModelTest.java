/*
 * Copyright (C) 2014 Marand
 */

package com.marand.thinkehr.adl.rm;

import org.openehr.jaxb.rm.DvAmount;
import org.openehr.jaxb.rm.DvText;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author markopi
 */
public class OpenEhrRmModelTest {
    private RmModel rmModel;

    @BeforeClass
    public void init() {
        rmModel = new OpenEhrRmModel();
    }

    @Test
    public void testGetExisting() throws Exception {
        assertThat(rmModel.getRmClass("DV_AMOUNT")).isEqualTo(DvAmount.class);
        assertThat(rmModel.getRmTypeName(DvText.class)).isEqualTo("DV_TEXT");
    }

    @Test(expectedExceptions = RmModelException.class, expectedExceptionsMessageRegExp = "Unknown.*DV_NONE")
    public void testGetAbsent() throws Exception {
        rmModel.getRmClass("DV_NONE");
    }
}
