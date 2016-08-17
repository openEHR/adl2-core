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

package org.openehr.adl.am.mixin;

import org.openehr.jaxb.rm.IntervalOfInteger;
import org.testng.annotations.Test;

import static org.openehr.adl.rm.RmObjectFactory.newIntervalOfInteger;
import static org.fest.assertions.Assertions.assertThat;

/**
 * @author markopi
 */
public class AmMixinsTest {
    @Test
    public void testIntervalOfInteger() throws Exception {
        IntervalOfInteger optional = newIntervalOfInteger(0, 1);
        IntervalOfInteger mandatory = newIntervalOfInteger(1, 1);
        IntervalOfInteger unbounded = newIntervalOfInteger(0, null);

        assertThat(AmMixins.of(optional).contains(mandatory)).isTrue();
        assertThat(AmMixins.of(mandatory).contains(optional)).isFalse();

        assertThat(AmMixins.of(unbounded).contains(optional)).isTrue();
        assertThat(AmMixins.of(optional).contains(unbounded)).isFalse();
    }
}
