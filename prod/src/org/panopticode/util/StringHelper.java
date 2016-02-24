package org.panopticode.util;

public class StringHelper {
    public static boolean notEmpty(String toCheck) {
        return !isEmpty(toCheck);
    }

    public static boolean isEmpty(String toCheck) {
        return toCheck == null || toCheck.trim().equals("");
    }
}
