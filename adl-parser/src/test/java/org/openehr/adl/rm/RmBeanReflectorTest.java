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

import org.openehr.adl.ParserTestBase;
import org.openehr.jaxb.rm.*;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.openehr.adl.rm.RmObjectFactory.newIntervalOfInteger;
import static org.fest.assertions.Assertions.assertThat;

/**
 * @author markopi
 */
public class RmBeanReflectorTest extends ParserTestBase {
    @Test
    public void testListProperties() throws Exception {
        Iterable<RmBeanReflector.RmAttribute> properties = RmBeanReflector.listProperties(DvQuantity.class);
        List<String> names = new ArrayList<>();
        for (RmBeanReflector.RmAttribute property : properties) {
            names.add(property.getAttribute());
        }


        assertThat(names).containsOnly("accuracy", "accuracy_is_percent", "magnitude", "magnitude_status",
                "normal_range", "normal_status", "other_reference_ranges", "precision", "units");
    }

    @Test
    public void testRmAttributeMandatory() throws Exception {
        RmBeanReflector.RmAttribute numerator = RmBeanReflector.getRmAttribute(DvProportion.class, "numerator");

        assertRmAttribute(numerator, "numerator", "numerator", Float.TYPE, newIntervalOfInteger(1, 1));
    }

    @Test
    public void testRmAttributeOptional() throws Exception {
        RmBeanReflector.RmAttribute formatting = RmBeanReflector.getRmAttribute(DvText.class, "formatting");

        assertRmAttribute(formatting, "formatting", "formatting", String.class, newIntervalOfInteger(0, 1));
    }

    @Test
    public void testRmAttributeMulti() throws Exception {
        RmBeanReflector.RmAttribute items = RmBeanReflector.getRmAttribute(ItemList.class, "items");

        assertRmAttribute(items, "items", "items", Element.class, newIntervalOfInteger(0, null));
    }


    private void assertRmAttribute(RmBeanReflector.RmAttribute attr, String attributeName, String propertyName, Class<?> type,
            IntervalOfInteger occurrences) {
        assertThat(attr.getAttribute()).isEqualTo(attributeName);
        assertThat(attr.getProperty().getName()).isEqualTo(propertyName);
        assertThat(attr.getTargetType()).isEqualTo(type);

        assertThat(json(attr.getOccurrences())).isEqualTo(json(occurrences));
    }
}
