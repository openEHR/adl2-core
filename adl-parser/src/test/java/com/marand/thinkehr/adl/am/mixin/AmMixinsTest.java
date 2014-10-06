/*
 * Copyright (C) 2014 Marand
 */

package com.marand.thinkehr.adl.am.mixin;

import org.openehr.jaxb.rm.IntervalOfInteger;
import org.testng.annotations.Test;

import static com.marand.thinkehr.adl.rm.RmObjectFactory.newIntervalOfInteger;
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
