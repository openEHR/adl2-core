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
        rmModel = OpenEhrRmModel.getInstance();
    }

    @Test
    public void testGetExisting() throws Exception {
        assertThat(rmModel.getRmType("DV_TEXT")).isNotNull();
    }

    @Test
    public void testGetHierarchy() throws Exception {
        assertThat(rmModel.getRmType("DV_AMOUNT").getChildren()).containsOnly(
                rmModel.getRmType("DV_COUNT"),
                rmModel.getRmType("DV_DURATION"),
                rmModel.getRmType("DV_QUANTITY"),
                rmModel.getRmType("DV_PROPORTION")
        );
        assertThat(rmModel.getRmType("DV_AMOUNT").getParent()).isEqualTo(rmModel.getRmType("DV_QUANTIFIED"));
    }

    @Test
    public void testGetAttributes() throws Exception {
        assertThat(rmModel.getRmType("DV_CODED_TEXT").getAttributes().keySet()).containsOnly(
                "defining_code", "encoding", "formatting", "hyperlink", "language", "mappings", "value");
    }

    @Test(expectedExceptions = RmModelException.class, expectedExceptionsMessageRegExp = "Unknown.*DV_NONE")
    public void testGetAbsent() throws Exception {
        rmModel.getRmType("DV_NONE");
    }
}
