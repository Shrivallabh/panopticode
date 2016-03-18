package org.panopticode.util;

import junit.framework.TestCase;

public class IndicatorUtilTest extends TestCase {

	public void testCompute() {
		assertEquals(0.9,IndicatorUtil.computeMaxCCNIndicator(25),0.01);
		assertEquals(0.9,IndicatorUtil.computeLinesChangedIndicator(40, 1),0.01);
		assertEquals(0.9,IndicatorUtil.computeChangeFrequencyIndicator(1, 1),0.01);
		assertEquals(0.9,IndicatorUtil.computeC3Indicator(0.9, 0.9, 0.1, 0.9),0.01);
	}
}
