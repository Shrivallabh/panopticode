package org.panopticode.util;

import junit.framework.TestCase;

public class StringHelperTest extends TestCase {
    public void testIsEmpty() {
        assertTrue(StringHelper.isEmpty(null));
        assertTrue(StringHelper.isEmpty(""));
        assertTrue(StringHelper.isEmpty(" "));
        assertTrue(StringHelper.isEmpty("\t"));
        assertFalse(StringHelper.isEmpty("x"));
    }

    public void testNotEmpty() {
        assertFalse(StringHelper.notEmpty(null));
        assertFalse(StringHelper.notEmpty(""));
        assertFalse(StringHelper.notEmpty(" "));
        assertFalse(StringHelper.notEmpty("\t"));
        assertTrue(StringHelper.notEmpty("x"));
    }
}
