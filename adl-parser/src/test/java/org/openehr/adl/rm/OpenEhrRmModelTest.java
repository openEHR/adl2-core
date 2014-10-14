/*
 * ADL2-core
 * Copyright (c) 2013-2014 Marand d.o.o. (www.marand.com)
 *
 * This file is part of ADL2-core.
 *
 * ADL2-core is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openehr.adl.rm;

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
